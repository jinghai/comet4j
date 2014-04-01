/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.temp;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * @author jinghai.xiao@gmail.com
 * @date 2011-3-24
 */

public class ShowSystem {

	static String x;

	public static void main(String[] args) {
		OperatingSystemMXBean op = ManagementFactory.getOperatingSystemMXBean();
		System.out.println("Architecture: " + op.getArch());
		System.out.println("Processors: " + op.getAvailableProcessors());
		System.out.println("System name: " + op.getName());
		System.out.println("System version: " + op.getVersion());
		System.out.println("Last minute load: " + op.getSystemLoadAverage());
	}

}
