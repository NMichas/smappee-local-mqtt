package com.github.jitpack.smappeelocalmqtt.beans;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;

import java.io.IOException;

public class Polling {
	private ObjectMapper mapper = new ObjectMapper();

	public String handle(Exchange exchange) throws IOException {
		SmappeeClient smappeeClient = exchange.getContext().getRegistry()
				.lookupByNameAndType("smappeeClient", SmappeeClient.class);

		return mapper.writeValueAsString(smappeeClient.getMeasurements());
	}
}
