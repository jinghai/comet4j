/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * 消息发送器 负责消息的发送，主要由引擎类负责调用。
 */

public class CometSender {

	private ExpiresCache cacher;

	public CometSender(long timespan, long frequency) {
		cacher = new ExpiresCache(timespan, frequency);
	}

	/**
	 * 为一个连接发送一条消息
	 * @param c
	 * @param e
	 */
	synchronized void sendTo(CometConnection c, CometMessage msg) {
		if (c == null) {
			return;
		}
		if (CometProtocol.STATE_DYING.equals(c.getState()) || c.getResponse() == null) {
			cacher.push(c, msg);
			return;
		}
		try {
			writeData(c, msg);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 为一个连接发送多条消息
	 * @param c
	 * @param list
	 */
	void sendTo(CometConnection c, List<CometMessage> list) {
		if (c == null || list.isEmpty()) {
			return;
		}
		for (CometMessage msg : list) {
			sendTo(c, msg);
		}
	}

	/**
	 * 为多个连接发送一条消息
	 * @param list
	 * @param e
	 */
	void sendTo(List<CometConnection> list, CometMessage msg) {
		if (list == null || list.isEmpty()) {
			return;
		}
		for (CometConnection c : list) {
			sendTo(c, msg);
		}
	}

	private void writeData(CometConnection c, CometMessage msg) throws IOException {
		c.setDyingTime(System.currentTimeMillis());
		PrintWriter writer;
		HttpServletResponse response = c.getResponse();
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		writer = response.getWriter();
		writer.print(CometProtocol.encode(msg));
		close(c);

	}

	private void close(CometConnection c) throws IOException {
		if (c.getResponse() != null) {
			if (c.getWorkStyle().equals(CometProtocol.WORKSTYLE_LLOOP)) {
				c.setState(CometProtocol.STATE_DYING);
				try {
					c.getResponse().getWriter().close();
				} catch (Exception e) {
				}
				c.setResponse(null);
			} else {
				try {
					c.getResponse().flushBuffer();
				} catch (Exception e) {
				}
			}
		}

	}

	List<CometMessage> getCacheMessage(CometConnection conn) {
		return cacher.get(conn);
	}

	/**
	 * 发送连接的缓存数据
	 * @param conn
	 */

	void sendCacheMessage(CometConnection conn) {
		List<CometMessage> list = this.getCacheMessage(conn);
		if (list != null && !list.isEmpty()) {
			for (CometMessage msg : list) {
				sendTo(conn, msg);
			}
		}
	}

	public void destroy() {
		cacher.init = false;
		cacher = null;
	}
}
