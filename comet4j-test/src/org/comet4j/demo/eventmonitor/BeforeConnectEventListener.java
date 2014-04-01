package org.comet4j.demo.eventmonitor;

import org.comet4j.core.event.BeforeConnectEvent;
import org.comet4j.core.listener.BeforeConnectListener;

/**
 * (用一句话描述类的主要功能)
 * @author xiaojinghai
 * @date 2011-3-9
 */

public class BeforeConnectEventListener extends BeforeConnectListener {

	/*
	 * (non-Jsdoc)
	 * @see org.comet4j.event.Listener#handleEvent(org.comet4j.event.Event)
	 */
	@Override
	public boolean handleEvent(BeforeConnectEvent anEvent) {
		System.out.println("[BeforeConnectEvent]:");
		return true;
	}

}
