package com.github.jitpack.smappeelocalmqtt.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jitpack.smappeelocalmqtt.dto.ControlDTO;
import com.github.jitpack.smappeelocalmqtt.dto.MeasurementsDTO;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmappeeClient {
	private Properties configuration;
	private static final String logonURL = "/gateway/apipublic/logon";
	private static final String logoffURL = "/gateway/apipublic/logoff";
	private static final String measurementsURL = "/gateway/apipublic/reportInstantaneousValues";
	private static final String controlURL = "/gateway/apipublic/commandControlPublic";
	private ObjectMapper mapper = new ObjectMapper();

	public SmappeeClient(Properties configuration) {
		this.configuration = configuration;
	}

	private void login() throws IOException {
		final Response response = Request.Post("http://" + configuration.getProperty("smappee.hub") + logonURL)
				.useExpectContinue()
				.version(HttpVersion.HTTP_1_1)
				.addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
				.bodyString(configuration.getProperty("smappee.password"), ContentType.DEFAULT_TEXT)
				.execute();
		if (response.returnResponse().getStatusLine().getStatusCode() != 200) {
			System.err.println(MessageFormat.format("Could not login to {0}.", configuration.getProperty("smappee.hub")));
		}
	}

	private void logout() throws IOException {
		final Response response = Request.Post("http://" + configuration.getProperty("smappee.hub") + logoffURL)
				.useExpectContinue()
				.version(HttpVersion.HTTP_1_1)
				.addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
				.bodyString(configuration.getProperty("smappee.password"), ContentType.DEFAULT_TEXT)
				.execute();
		if (response.returnResponse().getStatusLine().getStatusCode() != 200) {
			System.err.println(MessageFormat.format("Could not logout from {0}.", configuration.getProperty("smappee.hub")));
		}
	}

	public MeasurementsDTO getMeasurements() throws IOException {
		login();

		final String response = Request.Get("http://" + configuration.getProperty("smappee.hub") + measurementsURL)
				.useExpectContinue()
				.version(HttpVersion.HTTP_1_1)
				.addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
				.execute().returnContent().asString();
		String report = mapper.readTree(response).get("report").asText();

		MeasurementsDTO measurements = new MeasurementsDTO();

		/* Voltage */
		Matcher matcher = Pattern.compile(".*?voltage=(.*?) Vrms").matcher(report);
		if (matcher.find()) {
			measurements.setVoltage(matcher.group(1));
		}

		/* Phase 1 */
		matcher = Pattern.compile(".*?Phase 1.*?current=(.*?) A.*?activePower=(.*?) W.*?reactivePower=(.*?) var.*?apparentPower=(.*?) VA.*?cosfi=(.*?),").matcher(report);
		if (matcher.find()) {
			measurements.setPhase1_current(matcher.group(1));
			measurements.setPhase1_activePower(matcher.group(2));
			measurements.setPhase1_apparentPower(matcher.group(3));
			measurements.setPhase1_reactivePower(matcher.group(4));
			measurements.setPhase1_cosfi(matcher.group(5));
		}

		/* Phase 2 */
		matcher = Pattern.compile(".*?Phase 2.*?current=(.*?) A.*?activePower=(.*?) W.*?reactivePower=(.*?) var.*?apparentPower=(.*?) VA.*?cosfi=(.*?),").matcher(report);
		if (matcher.find()) {
			measurements.setPhase2_current(matcher.group(1));
			measurements.setPhase2_activePower(matcher.group(2));
			measurements.setPhase2_apparentPower(matcher.group(3));
			measurements.setPhase2_reactivePower(matcher.group(4));
			measurements.setPhase2_cosfi(matcher.group(5));
		}

		/* Phase 3 */
		matcher = Pattern.compile(".*?Phase 3.*?current=(.*?) A.*?activePower=(.*?) W.*?reactivePower=(.*?) var.*?apparentPower=(.*?) VA.*?cosfi=(.*?),").matcher(report);
		if (matcher.find()) {
			measurements.setPhase3_current(matcher.group(1));
			measurements.setPhase3_activePower(matcher.group(2));
			measurements.setPhase3_apparentPower(matcher.group(3));
			measurements.setPhase3_reactivePower(matcher.group(4));
			measurements.setPhase3_cosfi(matcher.group(5));
		}

		logout();

		return measurements;
	}

	public void setStatus(ControlDTO controlDTO) throws IOException {
		login();

		Request.Post("http://" + configuration.getProperty("smappee.hub") + controlURL)
				.useExpectContinue()
				.version(HttpVersion.HTTP_1_1)
				.addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
				.bodyString("control,controlId=" + (controlDTO.isStatus()  ? "1" : "0")
						+ "|" + controlDTO.getId(), ContentType.DEFAULT_TEXT)
				.execute();

		logout();
	}
}
