/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.comet4j.core.util.ExplorerUtil;
import org.comet4j.core.util.NetUtil;

/**
 * Comet连接，负责构造一个连接的实例
 */

public class CometConnection {

	private final String id;

	private final String clientIp;

	private final String clientVersion;

	private String workStyle;

	private String state = CometProtocol.STATE_ALIVE;

	private long dyingTime = 0L; // 连接超时后会进入dying(濒死)状态，此时间指进入dying状态的起始时间

	private HttpServletRequest request;

	private HttpServletResponse response;

	public CometConnection(HttpServletRequest anRequest, HttpServletResponse anResponse) {
		clientIp = NetUtil.getIpAddr(anRequest);
		clientVersion = anRequest.getParameter(CometProtocol.FLAG_CLIENTVERSION);
		if (CometContext.getInstance().getWorkStyle().equals(CometProtocol.WORKSTYLE_AUTO)) {
			if (ExplorerUtil.canStreamingXHR(anRequest)) {
				workStyle = CometProtocol.WORKSTYLE_STREAM;
			} else {
				workStyle = CometProtocol.WORKSTYLE_LLOOP;
			}
		} else {
			workStyle = CometContext.getInstance().getWorkStyle();
		}
		request = anRequest;
		response = anResponse;
		id = UUID.randomUUID().toString();
	}

	/**
	 * 获得连接ID
	 * @return
	 */

	public String getId() {
		return id;
	}

	/**
	 * 获得客户端IP
	 * @return
	 */

	public String getClientIp() {
		return clientIp;
	}

	/**
	 * 获得Comet客户端版本号
	 * @return
	 */

	public String getClientVersion() {
		return clientVersion;
	}

	/**
	 * 获得连接实际的工作模式，CometProtocol.WORKSTYLE_STREAM或CometProtocol.WORKSTYLE_LLOOP
	 * @return
	 */

	public String getWorkStyle() {
		return workStyle;
	}

	/**
	 * 获得连接状态,CometProtocol.STATE_ALIVE或CometProtocol.STATE_DYING
	 * @return
	 */

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	/**
	 * 获得连接最近一次转入CometProtocol.STATE_DYING状态的时间截
	 * @return
	 */

	public long getDyingTime() {
		return dyingTime;
	}

	/**
	 * 设置连接最近一次转入CometProtocol.STATE_DYING状态的时间截
	 * @param dyingTime
	 */

	public void setDyingTime(long dyingTime) {
		this.dyingTime = dyingTime;
	}

	/**
	 * 获取连接的Request对象
	 * @return
	 */

	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * 设置连接的Request对象
	 * @param request
	 */

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * 获取连接的Response对象
	 * @return
	 */

	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * 设置连接的Response对象
	 * @param response
	 */

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void destroy() {
		this.request = null;
		this.response = null;
	}

}
