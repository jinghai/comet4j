/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.demo.talker.dto;

import org.comet4j.demo.talker.Constant;

/**
 * 系统健康传输对象
 * @author jinghai.xiao@gmail.com
 * @date 2011-4-7
 */

public class HealthDTO {

	private final String type = Constant.HEALTH;
	private String totalMemory;
	private String freeMemory;
	private String maxMemory;
	private String usedMemory;
	private String connectorCount;
	private String startup;
	private final long divider = 1024 * 1024;

	public String getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(long totalMemory) {
		Long result = totalMemory / divider;
		this.totalMemory = result.toString();
	}

	public String getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(long freeMemory) {
		Long result = freeMemory / divider;
		this.freeMemory = result.toString();
	}

	public String getMaxMemory() {
		return maxMemory;
	}

	public void setMaxMemory(long maxMemory) {
		Long result = maxMemory / divider;
		this.maxMemory = result.toString();
	}

	public String getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(long usedMemory) {
		Long result = usedMemory / divider;
		this.usedMemory = result.toString();
	}

	public String getConnectorCount() {
		return connectorCount;
	}

	public void setConnectorCount(String connectorCount) {
		this.connectorCount = connectorCount;
	}

	public String getType() {
		return type;
	}

	public String getStartup() {

		return startup;
	}

	public void setStartup(String startup) {
		this.startup = startup;
	}

}
