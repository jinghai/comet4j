/**
 * @(#)DropEventListener.java 2011-3-9 Copyright 2011 it.kedacom.com, Inc. All
 *                            rights reserved.
 */

package org.comet4j.demo.eventmonitor;

import org.comet4j.core.event.DropEvent;
import org.comet4j.core.listener.DropListener;

/**
 * (用一句话描述类的主要功能)
 * @author xiaojinghai
 * @date 2011-3-9
 */

public class DropEventListener extends DropListener {

	/*
	 * (non-Jsdoc)
	 * @see org.comet4j.event.Listener#handleEvent(org.comet4j.event.Event)
	 */
	@Override
	public boolean handleEvent(DropEvent anEvent) {
		System.out.println("[DropEvent]:cId=" + anEvent.getConn().getId());
		return false;
	}

}
