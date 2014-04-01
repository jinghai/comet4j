/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.demo.talker;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.comet4j.core.CometContext;
import org.comet4j.core.CometEngine;

/**
 * 应用初始化
 * @author jinghai.xiao@gmail.com
 * @date 2011-2-25
 */

public class AppInit implements ServletContextListener {

	/**
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		CometContext cc = CometContext.getInstance();
		CometEngine engine = cc.getEngine();
		cc.registChannel(Constant.APP_CHANNEL);// 注册通道
		// 绑定事件侦听
		engine.addConnectListener(new JoinListener());
		engine.addDropListener(new LeftListener());
		// 启动系统监控信息发送器
		Thread healthSender = new Thread(new HealthSender(), "HealthSender");
		healthSender.setDaemon(true);
		healthSender.start();
	}

	/**
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO 该方法尚未实现

	}

}
