/**
 * @(#)CometContextEventListener.java 2011-3-9 Copyright 2011 it.kedacom.com,
 *                                    Inc. All rights reserved.
 */

package org.comet4j.demo.eventmonitor;

import org.comet4j.core.event.CometContextEvent;
import org.comet4j.core.listener.CometContextListener;

/**
 * (用一句话描述类的主要功能)
 * @author xiaojinghai
 * @date 2011-3-9
 */

public class CometContextEventListener extends CometContextListener {

	/*
	 * (non-Jsdoc)
	 * @see
	 * org.comet4j.core.listener.CometContextListener#onInitialized(org.comet4j
	 * .core.event.CometContextEvent)
	 */
	@Override
	public boolean onInitialized(CometContextEvent event) {
		System.out.println("[CometContextEvent]:subType=" + event.getSubType());
		return false;
	}

	/*
	 * (non-Jsdoc)
	 * @see
	 * org.comet4j.core.listener.CometContextListener#onDestroyed(org.comet4j
	 * .core.event.CometContextEvent)
	 */
	@Override
	public boolean onDestroyed(CometContextEvent event) {
		// TODO 该方法尚未实现
		return false;
	}

}
