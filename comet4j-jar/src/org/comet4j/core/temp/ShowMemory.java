/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.temp;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * @author jinghai.xiao@gmail.com
 * @date 2011-3-24
 */

public class ShowMemory {

	static String x;

	public static void main(String[] args) {
		ShowMemory sm = new ShowMemory();
		sm.show();
		x = "";
		for (int i = 0; i < 1000; i++)
			x += " string number " + i;
		sm.show();
		x = null;
		sm.show();
		MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
		mem.gc();
		sm.show();
	}

	void show() {
		MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
		MemoryUsage heap = mem.getHeapMemoryUsage();
		System.out.print("Heap committed=" + heap.getCommitted() + " init=" + heap.getInit() + " max=" + heap.getMax()
				+ " used=" + heap.getUsed());
		System.out.print(",total:" + Runtime.getRuntime().totalMemory());
		System.out.print(",free" + Runtime.getRuntime().freeMemory());
		System.out.println(",max" + Runtime.getRuntime().maxMemory() / 1024);
	}

}
