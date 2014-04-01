/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.dto;

import java.util.List;

/**
 * 连接成功后所发送的数据对象
 */
public class ConnectionDTO {

	public ConnectionDTO(String cId, String ws, List<String> channels, int timeout) {
		this.cId = cId;
		this.ws = ws;
		this.channels = channels;
		this.timeout = timeout;
	}

	/**
	 * 连接ID
	 */
	private String cId;
	/**
	 * 工作模式workStyle
	 */
	private String ws;
	/**
	 * 应用模块列表appModuesList
	 */
	private List<String> channels;
	/**
	 * 服务器超时时间
	 */
	private int timeout;

	public String getcId() {
		return cId;
	}

	public void setcId(String cId) {
		this.cId = cId;
	}

	public List<String> getChannels() {
		return channels;
	}

	public void setChannels(List<String> channels) {
		this.channels = channels;
	}

	public String getWs() {
		return ws;
	}

	public void setWs(String ws) {
		this.ws = ws;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}
