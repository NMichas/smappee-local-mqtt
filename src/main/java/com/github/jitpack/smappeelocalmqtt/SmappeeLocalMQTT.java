package com.github.jitpack.smappeelocalmqtt;

import com.github.jitpack.smappeelocalmqtt.beans.Control;
import com.github.jitpack.smappeelocalmqtt.beans.Polling;
import com.github.jitpack.smappeelocalmqtt.beans.SmappeeClient;
import lombok.extern.java.Log;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.apache.commons.lang3.StringUtils;

@Log
public class SmappeeLocalMQTT {

  public static void main(String[] args) throws Exception {
    SmappeeLocalMQTT app = new SmappeeLocalMQTT();
    app.boot();
  }

  public void boot() throws Exception {
    // Create Camel context.
    Main main = new Main();

    // Register beans.
    main.bind("smappeeClient", new SmappeeClient());
    main.bind("polling", new Polling());
    main.bind("control", new Control());

    // Create routes.
    main.addRouteBuilder(new AppRouteBuilder());

    // Startup and Shutdown hooks.
    main.addMainListener(new Events());

    // Start Camel context.
    main.run();
  }

  private static class AppRouteBuilder extends RouteBuilder {

    private String auth = StringUtils.isNoneEmpty(System.getenv("slm_mqtt_username"),
        System.getenv("slm_mqtt_password")) ?
        "&userName=" + System.getenv("slm_mqtt_username") +
            "&password=" + System.getenv("slm_mqtt_password") : "";

    @Override
    public void configure() {
      if (Boolean.parseBoolean(System.getenv("slm_polling"))) {
        from("timer:pollingTimer?period=" + System.getenv("slm_poller_freq"))
            .bean("polling")
            .to("mqtt:polling?" +
                "clientId=SmappeeLocalMQTT"
                + "&publishTopicName=" + System.getenv("slm_mqtt_polling_topic")
                + "&host=" + System.getenv("slm_mqtt_host")
                + auth);
      } else {
        log.info("Polling disabled.");
      }

      if (Boolean.parseBoolean(System.getenv("slm_control"))) {
        from(
            "mqtt:control?subscribeTopicName=" + System.getenv("slm_mqtt_control_topic")
                + "&host=" + System.getenv("slm_mqtt_host")
                + auth)
            .transform(body().convertToString())
            .to("bean:control");
      } else {
        log.info("Control disabled.");
      }
    }
  }

  public static class Events extends MainListenerSupport {

    @Override
    public void afterStart(MainSupport main) {
      log.info("SLM started.");
    }

    @Override
    public void beforeStop(MainSupport main) {
      log.info("Stopping SLM.");
    }
  }
}
