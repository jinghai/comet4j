/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.event;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.comet4j.core.CometEngine;
import org.comet4j.event.Event;

/**
 * 即将连接以前事件对象
 */

public class BeforeConnectEvent extends Event<CometEngine> {

	private HttpServletRequest request;
	private HttpServletResponse response;

	public BeforeConnectEvent(CometEngine target, HttpServletRequest anRequest, HttpServletResponse anResponse) {
		super(target);
		request = anRequest;
		response = anResponse;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void destroy() {
		super.destroy();
		request = null;
		response = null;
	}
}
