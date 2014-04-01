/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * WEB容器启动侦听，负责框架的启动
 */

public class CometAppListener implements ServletContextListener {

	/*
	 * 容器初始化
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
	 * .ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		CometContext cct = CometContext.getInstance();
		cct.init(event);
	}

	/*
	 * 容器销毁
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
		CometContext.getInstance().destroy();
	}

}
