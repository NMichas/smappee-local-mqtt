package com.github.jitpack.smappeelocalmqtt.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.apache.camel.Exchange;

import java.io.IOException;

@Log
public class Polling {
	private ObjectMapper mapper = new ObjectMapper();

	public String handle(Exchange exchange) throws IOException {
		SmappeeClient smappeeClient = exchange.getContext().getRegistry()
				.lookupByNameAndType("smappeeClient", SmappeeClient.class);
		String serialisedMeasurements = mapper.writeValueAsString(smappeeClient.getMeasurements());
		log.log(Level.FINE, "Polled value: {0}", serialisedMeasurements);

		return serialisedMeasurements;
	}
}
