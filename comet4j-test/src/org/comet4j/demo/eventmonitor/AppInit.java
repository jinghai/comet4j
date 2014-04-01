package org.comet4j.demo.eventmonitor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.comet4j.core.CometContext;
import org.comet4j.core.CometEngine;
import org.comet4j.core.event.BeforeConnectEvent;
import org.comet4j.core.event.BeforeDropEvent;
import org.comet4j.core.event.BeforeRemoveEvent;
import org.comet4j.core.event.CometContextEvent;
import org.comet4j.core.event.ConnectEvent;
import org.comet4j.core.event.DropEvent;
import org.comet4j.core.event.DyingEvent;
import org.comet4j.core.event.MessageEvent;
import org.comet4j.core.event.RemovedEvent;
import org.comet4j.core.event.RevivalEvent;

/**
 * 应用初始化
 * @author jinghai.xiao@gmail.com
 * @date 2011-2-25
 */

public class AppInit implements ServletContextListener {

	// ServletContextListener
	@SuppressWarnings("unchecked")
	public void contextInitialized(ServletContextEvent event) {
		CometContext cc = CometContext.getInstance();
		cc.registChannel(Constant.AppChannel);
		cc.addListener(CometContextEvent.class, new CometContextEventListener());
		CometEngine engine = cc.getEngine();
		engine.addListener(BeforeConnectEvent.class, new BeforeConnectEventListener());
		engine.addListener(ConnectEvent.class, new ConnectEventListener());
		engine.addListener(DyingEvent.class, new DyingEventListener());
		engine.addListener(RevivalEvent.class, new RevivalEventListener());
		engine.addListener(MessageEvent.class, new MessageEventListener());
		engine.addListener(BeforeDropEvent.class, new BeforeDropEventListener());
		engine.addListener(BeforeRemoveEvent.class, new BeforeRemoveEventListener());
		engine.addListener(RemovedEvent.class, new RemovedEventListener());
		engine.addListener(DropEvent.class, new DropEventListener());
	}

	public void contextDestroyed(ServletContextEvent event) {

	}

}
