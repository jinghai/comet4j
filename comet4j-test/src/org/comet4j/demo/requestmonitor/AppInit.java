package org.comet4j.demo.requestmonitor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.comet4j.core.CometContext;
import org.comet4j.core.CometProtocol;

/**
 * 应用初始化
 * @author jinghai.xiao@gmail.com
 * @date 2011-2-25
 */

public class AppInit implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener,
		ServletRequestListener {

	// ServletContextListener
	public void contextInitialized(ServletContextEvent event) {
		CometContext cc = CometContext.getInstance();
		cc.registChannel(Constant.AppChannel);
	}

	public void contextDestroyed(ServletContextEvent event) {

	}

	// HttpSessionListener
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		// System.out.println("Session:" + session.getId() + "创建了");
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		// System.out.println("Session:" + session.getId() + "销毁了");
	}

	// HttpSessionAttributeListener
	public void attributeAdded(HttpSessionBindingEvent event) {
		// System.out.println("Session中增加了:" + event.getName() + "属性");
	}

	public void attributeRemoved(HttpSessionBindingEvent event) {
		// System.out.println("Session中删除了:" + event.getName() + "属性");
	}

	public void attributeReplaced(HttpSessionBindingEvent event) {
		// System.out.println("Session中修改了:" + event.getName() + "属性");
	}

	// ServletRequestListener
	public void requestInitialized(ServletRequestEvent event) {
		HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
		System.out.println("请求:" + request.getRequestURI() + ","+CometProtocol.FLAG_ACTION+":" + request.getParameter(CometProtocol.FLAG_ACTION) + ",cId:"
				+ request.getParameter("cid"));

	}

	public void requestDestroyed(ServletRequestEvent event) {
		HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
		// System.out.println("请求完毕:" + request.getRequestURI());
	}

}
