/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.util;

import javax.servlet.http.HttpServletRequest;

/**
 * 浏览器工具类
 */

public class ExplorerUtil {

	public static Boolean isIE(HttpServletRequest request) {
		String regEx = "msie";
		String userAgent = request.getHeader("User-Agent").toLowerCase();
		return userAgent.indexOf(regEx) == -1 ? false : true;
	}

	public static Boolean isFirefox(HttpServletRequest request) {
		String regEx = "firefox";
		String userAgent = request.getHeader("User-Agent").toLowerCase();
		return userAgent.indexOf(regEx) == -1 ? false : true;
	}

	public static Boolean isOpera(HttpServletRequest request) {
		String regEx = "opera";
		String userAgent = request.getHeader("User-Agent").toLowerCase();
		return userAgent.indexOf(regEx) == -1 ? false : true;
	}

	public static Boolean isChrome(HttpServletRequest request) {
		String regEx = "\bchrome\b";
		String userAgent = request.getHeader("User-Agent").toLowerCase();
		return userAgent.indexOf(regEx) == -1 ? false : true;
	}

	public static Boolean isAir(HttpServletRequest request) {
		String regEx = "adobeair";
		String userAgent = request.getHeader("User-Agent").toLowerCase();
		return userAgent.indexOf(regEx) == -1 ? false : true;
	}

	public static Boolean isSafari(HttpServletRequest request) {
		String regEx = "safari";
		String userAgent = request.getHeader("User-Agent").toLowerCase();
		if (userAgent.indexOf(regEx) != -1 && !isChrome(request)) {
			return true;
		} else {
			return false;
		}
	}

	// 是否可以进行HTTP长连接
	public static Boolean canStreamingXHR(HttpServletRequest request) {
		return (isSafari(request) || isAir(request) || isChrome(request) || isFirefox(request)) ? true : false;
	}
}
