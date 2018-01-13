package com.cl0udninja.raspberrypi.monitor.web;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cl0udninja.raspberrypi.monitor.service.RaspberryPiStatusService;
import com.cl0udninja.raspberrypi.monitor.web.dto.SystemInfoDTO;

@RestController("/api/pi")
public class RaspberryPiStatusController {

  @Autowired
  private RaspberryPiStatusService svc;

  @GetMapping
  public ResponseEntity<SystemInfoDTO> getStatus()
      throws UnsupportedOperationException, IOException, InterruptedException, ParseException {
    return ResponseEntity.ok(svc.compileStatusInformation());
  }

}
