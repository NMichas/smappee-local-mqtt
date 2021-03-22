package com.github.jitpack.smappeelocalmqtt.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jitpack.smappeelocalmqtt.dto.ControlDTO;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;

@Slf4j
public class Control {

  private ObjectMapper mapper = new ObjectMapper();

  public void handle(Exchange exchange) throws IOException {
    SmappeeClient smappeeClient = exchange.getContext().getRegistry()
        .lookupByNameAndType("smappeeClient", SmappeeClient.class);

    ControlDTO controlDTO = mapper.readValue(exchange.getIn()
        .getBody().toString(), ControlDTO.class);
    log.debug("Setting control value: {}", controlDTO.toString());
    smappeeClient.setStatus(controlDTO);
  }
}
