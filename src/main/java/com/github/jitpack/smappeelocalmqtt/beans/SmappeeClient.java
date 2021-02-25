package com.github.jitpack.smappeelocalmqtt.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jitpack.smappeelocalmqtt.dto.ControlDTO;
import com.github.jitpack.smappeelocalmqtt.dto.MeasurementsDTO;
import java.math.BigDecimal;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmappeeClient {

  private static final String logonURL = "/gateway/apipublic/logon";
  private static final String logoffURL = "/gateway/apipublic/logoff";
  private static final String measurementsURL = "/gateway/apipublic/reportInstantaneousValues";
  private static final String controlURL = "/gateway/apipublic/commandControlPublic";
  private ObjectMapper mapper = new ObjectMapper();

  private void login() throws IOException {
    final Response response =
        Request.Post("http://" + System.getenv("slm_smappee_hub") + logonURL)
            .useExpectContinue()
            .version(HttpVersion.HTTP_1_1)
            .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .bodyString(System.getenv("slm_smappee_password"), ContentType.DEFAULT_TEXT)
            .execute();
    if (response.returnResponse().getStatusLine().getStatusCode() != 200) {
      System.err.println(MessageFormat.format("Could not login to {0}.", System.getenv(
          "slm_smappee_hub")));
    }
  }

  private void logout() throws IOException {
    final Response response =
        Request.Post("http://" + System.getenv("slm_smappee_hub") + logoffURL)
            .useExpectContinue()
            .version(HttpVersion.HTTP_1_1)
            .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .bodyString(System.getenv("slm_smappee_password"), ContentType.DEFAULT_TEXT)
            .execute();
    if (response.returnResponse().getStatusLine().getStatusCode() != 200) {
      System.err.println(MessageFormat.format("Could not logout from {0}.", System.getenv(
          "slm_smappee_hub")));
    }
  }

  public MeasurementsDTO getMeasurements() throws IOException {
    login();

    final String response =
        Request.Get("http://" + System.getenv("slm_smappee_hub") + measurementsURL)
            .useExpectContinue()
            .version(HttpVersion.HTTP_1_1)
            .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .execute().returnContent().asString();
    String report = mapper.readTree(response).get("report").asText();

    MeasurementsDTO measurements = new MeasurementsDTO();

    // Voltage.
    Matcher matcher = Pattern.compile(".*?voltage=(.*?) Vrms").matcher(report);
    if (matcher.find()) {
      measurements.setVoltage(new BigDecimal(matcher.group(1)));
    }

    // Phase 1.
    matcher = Pattern.compile(
        ".*?Phase 1.*?current=(.*?) A.*?activePower=(.*?) W.*?reactivePower=(.*?) var.*?apparentPower=(.*?) VA.*?cosfi=(.*?),")
        .matcher(report);
    if (matcher.find()) {
      measurements.setPhase1_current(new BigDecimal(matcher.group(1)));
      measurements.setPhase1_activePower(new BigDecimal(matcher.group(2)).intValue());
      measurements.setPhase1_apparentPower(new BigDecimal(matcher.group(3)).intValue());
      measurements.setPhase1_reactivePower(new BigDecimal(matcher.group(4)).intValue());
      measurements.setPhase1_cosfi(Integer.parseInt(matcher.group(5)));
    }

    // Phase 2.
    matcher = Pattern.compile(
        ".*?Phase 2.*?current=(.*?) A.*?activePower=(.*?) W.*?reactivePower=(.*?) var.*?apparentPower=(.*?) VA.*?cosfi=(.*?),")
        .matcher(report);
    if (matcher.find()) {
      measurements.setPhase2_current(new BigDecimal(matcher.group(1)));
      measurements.setPhase2_activePower(new BigDecimal(matcher.group(2)).intValue());
      measurements.setPhase2_apparentPower(new BigDecimal(matcher.group(3)).intValue());
      measurements.setPhase2_reactivePower(new BigDecimal(matcher.group(4)).intValue());
      measurements.setPhase2_cosfi(Integer.parseInt(matcher.group(5)));
    }

    // Phase 3.
    matcher = Pattern.compile(
        ".*?Phase 3.*?current=(.*?) A.*?activePower=(.*?) W.*?reactivePower=(.*?) var.*?apparentPower=(.*?) VA.*?cosfi=(.*?),")
        .matcher(report);
    if (matcher.find()) {
      measurements.setPhase3_current(new BigDecimal(matcher.group(1)));
      measurements.setPhase3_activePower(new BigDecimal(matcher.group(2)).intValue());
      measurements.setPhase3_apparentPower(new BigDecimal(matcher.group(3)).intValue());
      measurements.setPhase3_reactivePower(new BigDecimal(matcher.group(4)).intValue());
      measurements.setPhase3_cosfi(Integer.parseInt(matcher.group(5)));
    }

    // Totals.
    measurements.setTotal_current(new BigDecimal("0")
        .add(measurements.getPhase1_current())
        .add(measurements.getPhase2_current())
        .add(measurements.getPhase3_current()));
    measurements.setTotal_activePower(
        measurements.getPhase1_activePower()
        + measurements.getPhase2_activePower()
        + measurements.getPhase3_activePower());
    measurements.setTotal_reactivePower(
        measurements.getPhase1_reactivePower()
        + measurements.getPhase2_reactivePower()
        + measurements.getPhase3_reactivePower());
    measurements.setTotal_apparentPower(
        measurements.getPhase1_apparentPower()
        + measurements.getPhase2_apparentPower()
        + measurements.getPhase3_apparentPower());

    logout();

    return measurements;
  }

  public void setStatus(ControlDTO controlDTO) throws IOException {
    login();

    Request.Post("http://" + System.getenv("slm_smappee_hub") + controlURL)
        .useExpectContinue()
        .version(HttpVersion.HTTP_1_1)
        .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .bodyString("control,controlId=" + (controlDTO.isStatus() ? "1" : "0")
            + "|" + controlDTO.getId(), ContentType.DEFAULT_TEXT)
        .execute();

    logout();
  }
}
