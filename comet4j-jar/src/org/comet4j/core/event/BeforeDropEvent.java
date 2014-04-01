/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.event;

import javax.servlet.http.HttpServletRequest;

import org.comet4j.core.CometEngine;
import org.comet4j.event.Event;

/**
 * 即将断开前的事件对象
 */

public class BeforeDropEvent extends Event<CometEngine> {

	private HttpServletRequest request;

	public BeforeDropEvent(CometEngine target, HttpServletRequest aRequest) {
		super(target);
		request = aRequest;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void destroy() {
		super.destroy();
		request = null;
	}
}
