/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.demo.talker;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.comet4j.core.CometConnection;
import org.comet4j.core.event.ConnectEvent;
import org.comet4j.core.listener.ConnectListener;
import org.comet4j.demo.talker.dto.JoinDTO;

/**
 * 上线侦听
 * @author jinghai.xiao@gmail.com
 * @date 2011-3-3
 */

public class JoinListener extends ConnectListener {

	/*
	 * (non-Jsdoc)
	 * @see org.comet4j.event.Listener#handleEvent(org.comet4j.event.Event)
	 */
	@Override
	public boolean handleEvent(ConnectEvent anEvent) {
		CometConnection conn = anEvent.getConn();
		HttpServletRequest request = conn.getRequest();
		String userName = getCookieValue(request.getCookies(), "userName", "");
		try {
			userName = URLDecoder.decode(userName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		JoinDTO dto = new JoinDTO(conn.getId(), userName);
		AppStore.getInstance().put(conn.getId(), userName);
		anEvent.getTarget().sendToAll(Constant.APP_CHANNEL, dto);
		return true;
	}

	public String getCookieValue(Cookie[] cookies, String cookieName, String defaultValue) {
		String result = defaultValue;
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookieName.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return result;
	}

}
