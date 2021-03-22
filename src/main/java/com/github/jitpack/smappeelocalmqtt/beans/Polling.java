package com.github.jitpack.smappeelocalmqtt.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;

@Slf4j
public class Polling {

  private ObjectMapper mapper = new ObjectMapper();

  public String handle(Exchange exchange) throws IOException {
    log.debug("Polling...");
    SmappeeClient smappeeClient = exchange.getContext().getRegistry()
        .lookupByNameAndType("smappeeClient", SmappeeClient.class);
    String serialisedMeasurements = mapper.writeValueAsString(smappeeClient.getMeasurements());
    log.debug("Polled value: {}", serialisedMeasurements);

    return serialisedMeasurements;
  }
}
