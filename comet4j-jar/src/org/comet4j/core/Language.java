/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core;

import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * 负责初始化和读取本地化字符串
 */

public class Language {

	private static String cnLanguageFile = "/language-cn.xml";
	private static String enLanguageFile = "/language-en.xml";

	private static Properties prop;

	private static void beforeGet() {
		if (prop == null) {
			Locale locale = CometContext.getInstance().getLocale();
			if (Locale.ENGLISH == locale) {
				InputStream in = Language.class.getResourceAsStream(enLanguageFile);
				prop = new Properties();
				try {
					prop.loadFromXML(in);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (Locale.CHINESE == locale) {
				InputStream in = Language.class.getResourceAsStream(cnLanguageFile);
				prop = new Properties();
				try {
					prop.loadFromXML(in);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 按Key获取本地化字符串
	 * @param key
	 * @return
	 */

	public static String get(String key) {
		beforeGet();
		return prop.getProperty(key);
	}

	/**
	 * 获取连接成功本地化字符串
	 * @return
	 */

	public static String getConnectSuccess() {
		beforeGet();
		return prop.getProperty("ConnectSuccess");
	}

	/**
	 * 获取连接失败本地化字符串
	 * @return
	 */

	public static String getConnectFailure() {
		beforeGet();
		return prop.getProperty("ConnectFailure");
	}

	public void destroy() {
		prop = null;
	}
}
