package com.github.jitpack.smappeelocalmqtt.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jitpack.smappeelocalmqtt.dto.ControlDTO;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.apache.camel.Exchange;

import java.io.IOException;

@Log
public class Control {
	private ObjectMapper mapper = new ObjectMapper();

	public void handle(Exchange exchange) throws IOException {
		SmappeeClient smappeeClient = exchange.getContext().getRegistry()
				.lookupByNameAndType("smappeeClient", SmappeeClient.class);

		ControlDTO controlDTO = mapper.readValue(exchange.getIn()
				.getBody().toString(), ControlDTO.class);
		log.log(Level.FINE, "Setting control value: {0}", controlDTO.toString());
		smappeeClient.setStatus(controlDTO);
	}
}
