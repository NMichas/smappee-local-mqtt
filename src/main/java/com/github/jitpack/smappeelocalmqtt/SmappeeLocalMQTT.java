package com.github.jitpack.smappeelocalmqtt;


import com.github.jitpack.smappeelocalmqtt.beans.Control;
import com.github.jitpack.smappeelocalmqtt.beans.Polling;
import com.github.jitpack.smappeelocalmqtt.beans.SmappeeClient;
import com.github.jitpack.smappeelocalmqtt.util.Util;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Properties;

public class SmappeeLocalMQTT {
	private static final String configurationFile = "smappee-local-mqtt.conf";
	private static Properties configuration;

	public SmappeeLocalMQTT() throws IOException {
		configuration = Util.readConf(configurationFile);
	}

	private static class AppRouteBuilder extends RouteBuilder {
		private String auth = StringUtils.isNoneEmpty(configuration.getProperty("mqtt.username")) ?
				"&userName=" + configuration.getProperty("mqtt.username") +
						"&password=" + configuration.getProperty("mqtt.password") : "";

		@Override
		public void configure() throws Exception {
			if (Boolean.parseBoolean(configuration.getProperty("polling"))) {
				from("timer:pollingTimer?period=" + configuration.getProperty("poller.freq"))
						.bean("polling")
						.to("mqtt:polling?" +
								"clientId=SmappeeLocalMQTT"
								+ "&publishTopicName=" + configuration.getProperty("mqtt.polling.topic")
								+ "&host=" + configuration.getProperty("mqtt.host")
								+ auth);
			}

			if (Boolean.parseBoolean(configuration.getProperty("control"))) {
				from("mqtt:control?subscribeTopicName=" + configuration.getProperty("mqtt.control.topic")
						+ "&host=" + configuration.getProperty("mqtt.host")
						+ auth)
						.transform(body().convertToString())
						.to("bean:control");
			}
		}
	}

	public static class Events extends MainListenerSupport {
		@Override
		public void afterStart(MainSupport main) {
			System.out.println("Starting Smappee poller.");
		}

		@Override
		public void beforeStop(MainSupport main) {
			System.out.println("Stopping Smappee poller.");
		}
	}

	public void boot() throws Exception {
		/* Create Camel context */
		Main main = new Main();

		/* Register beans */
		main.bind("smappeeClient", new SmappeeClient(Util.readConf(configurationFile)));
		main.bind("polling", new Polling());
		main.bind("control", new Control());

		/* Create routes */
		main.addRouteBuilder(new AppRouteBuilder());

		/* Startup and Shutdown hooks */
		main.addMainListener(new Events());

		/* Start Camel context */
		main.run();
	}

	public static void main(String[] args) throws Exception {
		SmappeeLocalMQTT app = new SmappeeLocalMQTT();
		app.boot();
	}
}
