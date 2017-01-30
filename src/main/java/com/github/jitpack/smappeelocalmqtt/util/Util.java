package com.github.jitpack.smappeelocalmqtt.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Util {
	public static Properties readConf(String configurationFile) throws IOException {
		Properties configuration = new Properties();
		try (InputStream in = new FileInputStream(configurationFile)) {
			configuration.load(in);
		}

		return configuration;
	}
}
