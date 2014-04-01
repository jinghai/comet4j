/**
 * @(#)BeforeDropEventListener.java 2011-3-9 Copyright 2011 it.kedacom.com, Inc.
 *                                  All rights reserved.
 */

package org.comet4j.demo.eventmonitor;

import org.comet4j.core.event.BeforeDropEvent;
import org.comet4j.core.listener.BeforeDropListener;

/**
 * (用一句话描述类的主要功能)
 * @author xiaojinghai
 * @date 2011-3-9
 */

public class BeforeDropEventListener extends BeforeDropListener {

	/*
	 * (non-Jsdoc)
	 * @see org.comet4j.event.Listener#handleEvent(org.comet4j.event.Event)
	 */
	@Override
	public boolean handleEvent(BeforeDropEvent anEvent) {
		System.out.println("[BeforeDropEvent]:cId=" + anEvent.getRequest().getParameter("cid"));
		return true;
	}

}
