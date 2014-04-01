/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.interfaces;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.comet4j.core.CometConnection;

public interface CometEngineInterfaces {

	// public ServerInfoDTO getServerInfo(HttpServletRequest request,
	// HttpServletResponse response);
	public void connect(HttpServletRequest request, HttpServletResponse response);

	public void revival(HttpServletRequest request, HttpServletResponse response);

	public void drop(HttpServletRequest request, HttpServletResponse response);

	public void timeout(HttpServletRequest request, HttpServletResponse response);

	public List<CometConnection> getConnections();

	public void send(String id);

	public void publish(String id);
}
