/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.comet4j.core.dto.ConnectionDTO;
import org.comet4j.core.event.BeforeConnectEvent;
import org.comet4j.core.event.BeforeDropEvent;
import org.comet4j.core.event.BeforeRemoveEvent;
import org.comet4j.core.event.ConnectEvent;
import org.comet4j.core.event.DropEvent;
import org.comet4j.core.event.DyingEvent;
import org.comet4j.core.event.ErrorEvent;
import org.comet4j.core.event.MessageEvent;
import org.comet4j.core.event.RemovedEvent;
import org.comet4j.core.event.RevivalEvent;
import org.comet4j.core.listener.BeforeConnectListener;
import org.comet4j.core.listener.BeforeDropListener;
import org.comet4j.core.listener.BeforeRemoveListener;
import org.comet4j.core.listener.ConnectListener;
import org.comet4j.core.listener.DropListener;
import org.comet4j.core.listener.DyingListener;
import org.comet4j.core.listener.MessageListener;
import org.comet4j.core.listener.RemovedListener;
import org.comet4j.core.listener.RevivalListener;
import org.comet4j.event.Observable;

/**
 * 引擎，负责管理和维持连接，并能够必要的发送服务
 */

@SuppressWarnings({
		"unchecked", "rawtypes"
})
public class CometEngine extends Observable {

	private CometConnector ct;
	private CometSender sender;

	public CometEngine() {
		this.addEvent(BeforeConnectEvent.class);
		this.addEvent(ConnectEvent.class);
		this.addEvent(BeforeDropEvent.class);
		this.addEvent(DropEvent.class);
		this.addEvent(DyingEvent.class);
		this.addEvent(RevivalEvent.class);
		this.addEvent(MessageEvent.class);
		this.addEvent(ErrorEvent.class);// TODO:
		CometContext cc = CometContext.getInstance();
		sender = new CometSender(cc.getCacheExpires(), cc.getCacheFrequency());
		ct = new CometConnector(cc.getConnExpires(), cc.getConnFrequency());
	}

	/**
	 * 建立用户连接
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	void connect(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String uId = request.getParameter("uid");
		CometContext.getInstance().log("【CometDebug】-->【connect】-->uid:" + uId);

		CometConnection conn = new CometConnection(request, response);
		BeforeConnectEvent be = new BeforeConnectEvent(this, request, response);
		if (!this.fireEvent(be)) {
			conn.getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
			conn.getResponse().getWriter().close();
			return;
		}
		ct.addConnection(conn);
		CometContext cc = CometContext.getInstance();
		ConnectionDTO cdto = new ConnectionDTO(conn.getId(), conn.getWorkStyle(), cc.getAppModules(), cc.getTimeout());
		sendTo(CometProtocol.SYS_CHANNEL, conn, cdto);
		try {// 强制关闭长连接工作模式下的输出
			conn.getResponse().getWriter().close();
			conn.setState(CometProtocol.STATE_DYING);
			conn.setResponse(null);
			conn.setDyingTime(System.currentTimeMillis());
		} catch (Exception ex) {
		} finally {
			ConnectEvent e = new ConnectEvent(this, conn);
			this.fireEvent(e);
		}

	}

	void dying(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// response.setStatus(CometProtocol.HTTPSTATUS_TIMEOUT);
		String uId = request.getParameter("uid");
		String cId = request.getParameter("cid");
		CometContext.getInstance().log("【CometDebug】-->【dying】-->cid:" + cId + "," + "uid:" + uId);

		CometConnection conn = ct.getConnection(request);
		if (conn != null) {
			CometContext.getInstance().getEngine().sendTo(CometProtocol.SYS_CHANNEL, conn, CometProtocol.STATE_DYING);
		}
		try {
			conn.getResponse().getWriter().close();
		} catch (Exception exc) {
			try {
				response.getWriter().close();
			} catch (Exception excp) {
				excp.printStackTrace();
			}

		}
		if (conn != null) {
			conn.setState(CometProtocol.STATE_DYING);
			conn.setResponse(null);
			conn.setDyingTime(System.currentTimeMillis());
			DyingEvent e = new DyingEvent(this, conn);
			this.fireEvent(e);
		}
	}

	void revival(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String cId = getConnectionId(request);

		String uId = request.getParameter("uid");
		CometContext.getInstance().log("【CometDebug】-->【revival】-->cid:" + cId + "," + "uid:" + uId);

		if (cId == null) {
			drop(request, response);
			// throw new CometException("无法复活，断开连接。");
		}
		CometConnection conn = ct.getConnection(cId);
		if (conn != null /* && CometProtocol.STATE_DYING.equals(conn.getState()) */) {
			conn.setRequest(request);
			conn.setResponse(response);
			conn.setDyingTime(System.currentTimeMillis());
			conn.setState(CometProtocol.STATE_ALIVE);
			RevivalEvent e = new RevivalEvent(this, conn);
			this.fireEvent(e);
			sender.sendCacheMessage(conn);
		} else {
			drop(request, response);
			// throw new CometException("非正常复活，断开连接。conn=" + conn);
		}
	}

	/**
	 * 断开一个连接
	 * @param request
	 * @param response
	 * @throws IOException
	 */

	public void drop(HttpServletRequest request, HttpServletResponse response) throws IOException {

		BeforeDropEvent be = new BeforeDropEvent(this, request);
		if (!this.fireEvent(be)) {
			return;
		}
		String cId = getConnectionId(request);

		String uId = request.getParameter("uid");
		CometContext.getInstance().log("【CometDebug】-->【drop】-->cid:" + cId + "," + "uid:" + uId);

		CometConnection conn = null;
		if (cId != null) {
			conn = ct.getConnection(cId);
		} else {
			conn = ct.getConnection(request);
		}
		if (conn != null) {
			remove(conn);
		}
		// response.setStatus(CometProtocol.HTTPSTATUS_ERROR);
		response.getWriter().close();
	}

	void remove(CometConnection aConn) {

		BeforeRemoveEvent be = new BeforeRemoveEvent(this, aConn);
		if (!this.fireEvent(be)) {
			return;
		}
		sender.getCacheMessage(aConn);
		ct.removeConnection(aConn);
		// Fixed nio endpoint exception
		/*
		 * try { aConn.getResponse().getWriter().close(); } catch (Exception
		 * exc) { // 连接有可能是dying，此时getResponse为空是正常的，这里仅保证对有效的Response做出回应 }
		 */
		RemovedEvent re = new RemovedEvent(this, aConn);
		this.fireEvent(re);
		DropEvent de = new DropEvent(this, aConn);
		this.fireEvent(de);

	}

	/**
	 * 按ID获取已有
	 * @param id
	 * @return
	 */

	public CometConnection getConnection(String id) {
		return ct.getConnection(id);
	}

	/**
	 * 按Request对象获得连接对象
	 * @param request
	 * @return
	 */

	public CometConnection getConnection(HttpServletRequest request) {
		String cId = getConnectionId(request);
		CometConnection conn = null;
		if (cId != null) {
			conn = ct.getConnection(cId);
		} else {
			conn = ct.getConnection(request);
		}
		return conn;
	}

	/**
	 * 获得所有连接对象
	 * @return
	 */

	public List<CometConnection> getConnections() {
		return ct.getConnections();
	}

	/**
	 * 向连接发送消息
	 * @param channel 应用通道标识
	 * @param c 连接对象
	 * @param data 数据
	 */

	public void sendTo(String channel, CometConnection c, Object data) {
		CometMessage msg = new CometMessage(data, channel);
		sender.sendTo(c, msg);
		MessageEvent e = new MessageEvent(this, c, msg);
		this.fireEvent(e);
	}

	/**
	 * 向连接发送批量数据
	 * @param channel 应用通道标识
	 * @param c 连接对象
	 * @param data 数据列表
	 */

	public void sendTo(String channel, CometConnection c, List<Object> data) {
		for (Object o : data) {
			sendTo(channel, c, o);
		}
	}

	/**
	 * 向多个连接发送数据
	 * @param channel 应用通道标识
	 * @param list 连接对象列表
	 * @param data 数据
	 */

	public void sendTo(String channel, List<CometConnection> list, Object data) {
		if (list.isEmpty()) {
			return;
		}
		for (CometConnection c : list) {
			sendTo(channel, c, data);
		}
	}

	/**
	 * 向所有连接发送数据
	 * @param channel 应用通道标识
	 * @param data 数据
	 */

	public void sendToAll(String channel, Object data) {
		List<CometConnection> list = this.getConnections();
		if (list == null) {
			return;
		}
		synchronized (list) {
			for (CometConnection c : list) {
				sendTo(channel, c, data);
			}
		}
	}

	/**
	 * 从Request对象中获取连接ID
	 * @param request
	 * @return
	 */

	public String getConnectionId(HttpServletRequest request) {
		String id = request.getParameter(CometProtocol.FLAG_ID);
		if (id == null || "".equals(id)) {
			id = null;
		}
		return id;
	}

	/**
	 * 增加即将连接事件侦听，此事件动作可以被终止。
	 * @param li
	 */

	public void addBeforeConnectListener(BeforeConnectListener li) {
		this.addListener(BeforeConnectEvent.class, li);
	}

	/**
	 * 移除即将连接事件侦听，此事件动作可以被终止。
	 * @param li
	 */

	public void removeBeforeConnectListener(BeforeConnectListener li) {
		this.removeListener(BeforeConnectEvent.class, li);
	}

	/**
	 * 增加即将断开事件侦听，此事件动作可以被终止。
	 * @param li
	 */

	public void addBeforeDropListener(BeforeDropListener li) {
		this.addListener(BeforeDropEvent.class, li);
	}

	/**
	 * 移除即将断开事件侦听，此事件动作可以被终止。
	 * @param li
	 */

	public void removeBeforeDropListener(BeforeDropListener li) {
		this.removeListener(BeforeDropEvent.class, li);
	}

	/**
	 * 增加连接即将移除事件侦听，此事件动作可以被终止。
	 * @param li
	 */

	public void addBeforeRemoveListener(BeforeRemoveListener li) {
		this.addListener(BeforeRemoveEvent.class, li);
	}

	/**
	 * 移除连接即将移除事件侦听，此事件动作可以被终止。
	 * @param li
	 */

	public void removeBeforeRemoveListener(BeforeRemoveListener li) {
		this.removeListener(BeforeRemoveEvent.class, li);
	}

	/**
	 * 增加连接事件侦听
	 * @param li
	 */

	public void addConnectListener(ConnectListener li) {
		this.addListener(ConnectEvent.class, li);
	}

	/**
	 * 移除连接事件侦听
	 * @param li
	 */

	public void removeConnectListener(ConnectListener li) {
		this.removeListener(ConnectEvent.class, li);
	}

	/**
	 * 增加连接断开事件侦听
	 * @param li
	 */

	public void addDropListener(DropListener li) {
		this.addListener(DropEvent.class, li);
	}

	/**
	 * 移除连接断开事件侦听
	 * @param li
	 */

	public void removeDropListener(DropListener li) {
		this.removeListener(DropEvent.class, li);
	}

	/**
	 * 增加连接变为濒死状态事件侦听
	 * @param li
	 */

	public void addDyingListener(DyingListener li) {
		this.addListener(DyingEvent.class, li);
	}

	/**
	 * 移除连接变为濒死状态事件侦听
	 * @param li
	 */

	public void removeDyingListener(DyingListener li) {
		this.removeListener(DyingEvent.class, li);
	}

	/**
	 * 增加发送消息事件侦听
	 * @param li
	 */

	public void addMessageListener(MessageListener li) {
		this.addListener(MessageEvent.class, li);
	}

	/**
	 * 移除发送消息事件侦听
	 * @param li
	 */

	public void removeMessageListener(MessageListener li) {
		this.removeListener(MessageEvent.class, li);
	}

	/**
	 * 增加连接已删除事件侦听
	 * @param li
	 */

	public void addRemovedListener(RemovedListener li) {
		this.addListener(RemovedEvent.class, li);
	}

	/**
	 * 移除连接已删除事件侦听
	 * @param li
	 */

	public void removeRemovedListener(RemovedListener li) {
		this.removeListener(RemovedEvent.class, li);
	}

	/**
	 * 增加连接变为复活状态事件侦听
	 * @param li
	 */

	public void addRevivalListener(RevivalListener li) {
		this.addListener(RevivalEvent.class, li);
	}

	/**
	 * 移除连接变为复活状态事件侦听
	 * @param li
	 */
	public void removeRevivalListener(RevivalListener li) {
		this.removeListener(RevivalEvent.class, li);
	}

	@Override
	public void destroy() {
		super.destroy();
		ct.init = false;
		ct.destroy();
		sender.destroy();
		ct = null;
		sender = null;

	}

}
