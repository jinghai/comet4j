/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.event;

import javax.servlet.ServletContext;

import org.comet4j.core.CometContext;
import org.comet4j.event.Event;
import org.comet4j.event.demo.mac.SubEventType;

/**
 * Comet4J上下文初始化完成事件对象
 */

public class CometContextEvent extends Event<CometContext> {

	public static SubEventType INITIALIZED = new SubEventType();
	public static SubEventType DESTROYED = new SubEventType();
	private SubEventType subType;
	private ServletContext servletContext;
	private CometContext cometContext;

	public CometContextEvent(CometContext target, SubEventType anSubType) {
		super(target);
		subType = anSubType;
	}

	public SubEventType getSubType() {
		return subType;
	}

	public void setSubType(SubEventType subType) {
		this.subType = subType;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public CometContext getCometContext() {
		return cometContext;
	}

	public void setCometContext(CometContext cometContext) {
		this.cometContext = cometContext;
	}

	@Override
	public void destroy() {
		super.destroy();
		subType = null;
		servletContext = null;
		cometContext = null;
	}
}
