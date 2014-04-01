/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.listener;

import org.comet4j.core.event.CometContextEvent;
import org.comet4j.event.Listener;

/**
 * 上下文事件侦听抽象类
 */
public abstract class CometContextListener extends Listener<CometContextEvent> {

	@Override
	public boolean handleEvent(CometContextEvent event) {
		if (CometContextEvent.INITIALIZED == event.getSubType()) {
			return onInitialized(event);
		} else if (CometContextEvent.DESTROYED == event.getSubType()) {
			return onDestroyed(event);
		}
		return true;
	}

	public abstract boolean onInitialized(CometContextEvent event);

	public abstract boolean onDestroyed(CometContextEvent event);
}
