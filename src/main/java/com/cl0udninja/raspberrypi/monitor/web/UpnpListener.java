package com.cl0udninja.raspberrypi.monitor.web;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Credit to @armzilla
 * 
 * https://github.com/armzilla/amazon-echo-ha-bridge/blob/master/src/main/java/com/armzilla/ha/upnp/UpnpListener.java
 */
@Component
@Slf4j
public class UpnpListener {

  private static final int UPNP_DISCOVERY_PORT = 1900;
  private static final String UPNP_MULTICAST_ADDRESS = "239.255.255.250";

  @Value("${upnp.response.port}")
  private int upnpResponsePort;

  @Value("${upnp.config.address}")
  private String responseAddress;

  @Value("${emulator.portbase}")
  private int portBase;
  @Value("${emulator.portcount}")
  private int portCount;

  @Value("${upnp.disable}")
  private boolean disable;

  @Autowired
  private ApplicationContext applicationContext;

  @Scheduled(fixedDelay = Integer.MAX_VALUE)
  public void startListening() {

    if (disable) {
      return;
    }

    log.info("Starting UPNP Discovery Listener");

    try (DatagramSocket responseSocket = new DatagramSocket(upnpResponsePort);
        MulticastSocket upnpMulticastSocket = new MulticastSocket(UPNP_DISCOVERY_PORT);) {
      InetSocketAddress socketAddress = new InetSocketAddress(UPNP_MULTICAST_ADDRESS, UPNP_DISCOVERY_PORT);
      Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();

      while (ifs.hasMoreElements()) {
        NetworkInterface xface = ifs.nextElement();
        Enumeration<InetAddress> addrs = xface.getInetAddresses();
        String name = xface.getName();
        int IPsPerNic = 0;

        while (addrs.hasMoreElements()) {
          InetAddress addr = addrs.nextElement();
          log.debug(name + " ... has addr " + addr);
          if (InetAddressUtils.isIPv4Address(addr.getHostAddress())) {
            IPsPerNic++;
          }
        }
        log.debug("Checking " + name + " to our interface set");
        if (IPsPerNic > 0) {
          upnpMulticastSocket.joinGroup(socketAddress, xface);
          log.debug("Adding " + name + " to our interface set");
        }
      }

      while (true) { // trigger shutdown here
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        upnpMulticastSocket.receive(packet);
        String packetString = new String(packet.getData());
        if (isSSDPDiscovery(packetString)) {
          log.trace("Got SSDP Discovery packet from " + packet.getAddress().getHostAddress() + ":" + packet.getPort());
          for (int i = 0; i < portCount; i++) {
            sendUpnpResponse(responseSocket, packet.getAddress(), packet.getPort(), portBase + i, i);
          }
        }
      }

    } catch (IOException e) {
      log.error("UpnpListener encountered an error. Shutting down", e);
      ConfigurableApplicationContext context = (ConfigurableApplicationContext) UpnpListener.this.applicationContext;
      context.close();

    }
    log.info("UPNP Discovery Listener Stopped");

  }

  /**
   * very naive ssdp discovery packet detection
   * 
   * @param body
   * @return
   */
  protected boolean isSSDPDiscovery(String body) {
    if (body != null && body.startsWith("M-SEARCH * HTTP/1.1") && body.contains("MAN: \"ssdp:discover\"")) {
      return true;
    }
    return false;
  }

  String discoveryTemplate = "HTTP/1.1 200 OK\r\n" + "CACHE-CONTROL: max-age=86400\r\n" + "EXT:\r\n"
      + "LOCATION: http://%s:%s/upnp/%s/setup.xml\r\n" + "OPT: \"http://schemas.upnp.org/upnp/1/0/\"; ns=01\r\n"
      + "01-NLS: %s\r\n" + "ST: urn:schemas-upnp-org:device:RaspberryPi:1\r\n"
      + "USN: uuid:Socket-1_0-221438K0100073::urn:RaspberryPi:device:**\r\n\r\n";

  protected void sendUpnpResponse(DatagramSocket socket, InetAddress requester, int sourcePort, int gatewayPort,
      int emulatorId) throws IOException {
    log.debug(String.format("sendUpnpResponse %s %s", requester.getHostAddress(), sourcePort));

    String discoveryResponse = String.format(discoveryTemplate, responseAddress, gatewayPort,
        "RaspberryPi" + emulatorId, getRandomUUIDString());
    log.trace(String.format("discoveryResponse: %s", discoveryResponse));
    DatagramPacket response = new DatagramPacket(discoveryResponse.getBytes(), discoveryResponse.length(), requester,
        sourcePort);
    socket.send(response);
  }

  protected String getRandomUUIDString() {
    return "F802908E-7FBC-4757-A0E6-938FA617C37A"; // https://xkcd.com/221/
  }
}