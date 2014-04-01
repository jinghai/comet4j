/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.event;

import org.comet4j.core.CometConnection;
import org.comet4j.core.CometEngine;
import org.comet4j.event.Event;

/**
 * 连接濒死状态事件对象
 */

public class DyingEvent extends Event<CometEngine> {

	private CometConnection conn;

	public DyingEvent(CometEngine target, CometConnection aConn) {
		super(target);
		conn = aConn;
	}

	public CometConnection getConn() {
		return conn;
	}

	public void setConn(CometConnection conn) {
		this.conn = conn;
	}

	@Override
	public void destroy() {
		super.destroy();
		conn = null;
	}
}
