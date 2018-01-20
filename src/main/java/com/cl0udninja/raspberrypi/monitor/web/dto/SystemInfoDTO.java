package com.cl0udninja.raspberrypi.monitor.web.dto;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pi4j.system.SystemInfo.BoardType;

import lombok.Data;

@Data
public class SystemInfoDTO {

  @JsonProperty
  private Float cpuTemperature;
  @JsonProperty
  private String serialNumber;
  @JsonProperty
  private Float cpuCoreVoltage;
  @JsonProperty
  private String modelName;
  @JsonProperty
  @JsonSerialize(using = SystemInfoDTO.BoardTypeSerializer.class)
  private BoardType boardType;
  @JsonProperty
  private Long totalMemory;
  @JsonProperty
  private Long usedMemory;
  @JsonProperty
  private Long freeMemory;
  @JsonProperty
  private Long sharedMemory;
  @JsonProperty
  private String javaVersion;
  @JsonProperty
  private String hostname;
  @JsonProperty
  private Set<String> ipAddresses;
  @JsonProperty
  private Long cpuFrequency;
  @JsonProperty
  private String cpuPart;

  public static class BoardTypeSerializer extends JsonSerializer<BoardType> {

    @Override
    public void serialize(BoardType boardType, JsonGenerator json, SerializerProvider provider) throws IOException {
      json.writeString(boardType.name());
    }

  }
}
