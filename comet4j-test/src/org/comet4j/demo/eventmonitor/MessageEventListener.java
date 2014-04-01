/**
 * @(#)MessageEventListener.java 2011-3-9 Copyright 2011 it.kedacom.com, Inc.
 *                               All rights reserved.
 */

package org.comet4j.demo.eventmonitor;

import org.comet4j.core.event.MessageEvent;
import org.comet4j.core.listener.MessageListener;
import org.comet4j.core.util.JSONUtil;

/**
 * (用一句话描述类的主要功能)
 * @author xiaojinghai
 * @date 2011-3-9
 */

public class MessageEventListener extends MessageListener {

	/*
	 * (non-Jsdoc)
	 * @see org.comet4j.event.Listener#handleEvent(org.comet4j.event.Event)
	 */
	@Override
	public boolean handleEvent(MessageEvent anEvent) {
		System.out.println("[MessageEvent]:cId=" + anEvent.getConn().getId() + "\ndata="
				+ JSONUtil.object2json(anEvent.getData()));
		return false;
	}

}
