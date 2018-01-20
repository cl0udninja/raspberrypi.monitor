package com.cl0udninja.raspberrypi.monitor.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.cl0udninja.raspberrypi.monitor.web.dto.SystemInfoDTO;
import com.pi4j.system.NetworkInfo;
import com.pi4j.system.SystemInfo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RaspberryPiStatusService {

  @SuppressWarnings("unchecked")
  public SystemInfoDTO compileStatusInformation()
      throws IOException, InterruptedException, UnsupportedOperationException, ParseException {
    log.debug("Querying system information");
    SystemInfoDTO systemInfo = new SystemInfoDTO();

    systemInfo.setCpuTemperature(SystemInfo.getCpuTemperature());
    systemInfo.setSerialNumber(SystemInfo.getSerial());
    systemInfo.setCpuCoreVoltage(SystemInfo.getCpuVoltage());
    systemInfo.setModelName(SystemInfo.getModelName());
    systemInfo.setBoardType(SystemInfo.getBoardType());
    systemInfo.setTotalMemory(SystemInfo.getMemoryTotal());
    systemInfo.setUsedMemory(SystemInfo.getMemoryUsed());
    systemInfo.setFreeMemory(SystemInfo.getMemoryFree());
    systemInfo.setSharedMemory(SystemInfo.getMemoryShared());
    systemInfo.setJavaVersion(SystemInfo.getJavaVersion());
    systemInfo.setHostname(NetworkInfo.getHostname());
    systemInfo.setIpAddresses(new HashSet<String>(CollectionUtils.<String>arrayToList(NetworkInfo.getIPAddresses())));
    systemInfo.setCpuFrequency(SystemInfo.getClockFrequencyArm());
    systemInfo.setCpuPart(SystemInfo.getCpuPart());

    log.debug(String.format("System information: %s", systemInfo));
    return systemInfo;
  }

}
