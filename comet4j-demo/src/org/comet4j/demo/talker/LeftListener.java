/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.demo.talker;

import org.comet4j.core.CometConnection;
import org.comet4j.core.event.DropEvent;
import org.comet4j.core.listener.DropListener;
import org.comet4j.demo.talker.dto.LeftDTO;

/**
 * 下线侦听
 * @author jinghai.xiao@gmail.com
 * @date 2011-3-3
 */

public class LeftListener extends DropListener {

	/*
	 * (non-Jsdoc)
	 * @see org.comet4j.event.Listener#handleEvent(org.comet4j.event.Event)
	 */
	@Override
	public boolean handleEvent(DropEvent anEvent) {
		CometConnection conn = anEvent.getConn();
		if (conn != null) {
			String userName = AppStore.getInstance().get(conn.getId());
			LeftDTO dto = new LeftDTO(conn.getId(), userName);
			AppStore.getInstance().getMap().remove(conn.getId());
			anEvent.getTarget().sendToAll(Constant.APP_CHANNEL, dto);
		}
		return true;
	}

}
