package com.cl0udninja.raspberrypi.monitor.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pi4j.system.SystemInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * Credits to @armzilla
 * https://github.com/armzilla/amazon-echo-ha-bridge/blob/master/src/main/java/com/armzilla/ha/upnp/UpnpSettingsResource.java
 */
@Controller
@Slf4j
@RequestMapping("/upnp")
public class UpnpSettingsController implements InitializingBean {
  @Value("${emulator.portcount}")
  private int portCount;

  private String modelName;
  private String modelNumber;
  private String hueTemplate;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.modelName = SystemInfo.getBoardType().name();
    this.modelNumber = SystemInfo.getModelName();
    this.hueTemplate = "<?xml version=\"1.0\"?>\n" + "<root xmlns=\"urn:schemas-upnp-org:device-1-0\">\n"
        + "<specVersion>\n" + "<major>1</major>\n" + "<minor>0</minor>\n" + "</specVersion>\n"
        + "<URLBase>http://%s:%s/</URLBase>\n" + // hostname string
        "<device>\n" + "<deviceType>urn:schemas-upnp-org:device:RaspberryPi:1</deviceType>\n"
        + "<friendlyName>Raspberry Pi (%s)</friendlyName>\n" + "<manufacturer>Raspberry Pi Foundation</manufacturer>\n"
        + "<manufacturerURL>https://www.raspberrypi.org/</manufacturerURL>\n"
        + "<modelDescription>Raspberry Pi SpringBoot Endpoint</modelDescription>\n" + "<modelName>" + modelName
        + "</modelName>\n" + "<modelNumber>" + modelNumber + "</modelNumber>\n"
        + "<modelURL>https://www.raspberrypi.org/products/raspberry-pi-3-model-b/</modelURL>\n"
        + "<serialNumber>%s</serialNumber>\n" + "<UDN>uuid:%s</UDN>\n" + "<serviceList>\n" + "<service>\n"
        + "<serviceType>(null)</serviceType>\n" + "<serviceId>(null)</serviceId>\n"
        + "<controlURL>(null)</controlURL>\n" + "<eventSubURL>(null)</eventSubURL>\n" + "<SCPDURL>(null)</SCPDURL>\n"
        + "</service>\n" + "</serviceList>\n" + "<presentationURL>index.html</presentationURL>\n" + "<iconList>\n"
        + "<icon>\n" + "<mimetype>image/png</mimetype>\n" + "<height>48</height>\n" + "<width>48</width>\n"
        + "<depth>24</depth>\n" + "<url>http://storage.googleapis.com/storage.cl0ud.ninja/raspberry-pi-logo.png</url>\n"
        + "</icon>\n" + "<icon>\n" + "<mimetype>image/png</mimetype>\n" + "<height>120</height>\n"
        + "<width>120</width>\n" + "<depth>24</depth>\n"
        + "<url>http://storage.googleapis.com/storage.cl0ud.ninja/raspberry-pi-logo.png</url>\n" + "</icon>\n"
        + "</iconList>\n" + "</device>\n" + "</root>\n";
  }

  @RequestMapping(value = "/{deviceId}/setup.xml", method = RequestMethod.GET, produces = "application/xml")
  public ResponseEntity<String> getUpnpConfiguration(@PathVariable(value = "deviceId") String deviceId,
      HttpServletRequest request) {
    log.debug("upnp device settings requested: " + deviceId + " from " + request.getRemoteAddr());
    String hostName = request.getLocalAddr();
    String filledTemplate = String.format(hueTemplate, hostName, request.getLocalPort(), hostName, deviceId, deviceId);

    return ResponseEntity.ok(filledTemplate);
  }

}