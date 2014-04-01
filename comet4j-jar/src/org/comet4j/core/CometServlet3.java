package org.comet4j.core;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "comet4j-connector", urlPatterns = {
	"/comet"
}, asyncSupported = true)
public class CometServlet3 extends HttpServlet {

	private static final long serialVersionUID = 5406891954492689797L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) {
		if (request.isAsyncSupported() && !request.isAsyncStarted()) {
			AsyncContext actx = request.getAsyncContext();
			actx.setTimeout(CometContext.getInstance().getTimeout());
			actx.addListener(new AsyncListener() {

				public void onStartAsync(AsyncEvent event) throws IOException {
					HttpServletRequest request = (HttpServletRequest) event.getAsyncContext().getRequest();
					HttpServletResponse response = (HttpServletResponse) event.getAsyncContext().getResponse();
					String action = request.getParameter(CometProtocol.FLAG_ACTION);

					if (CometProtocol.CMD_CONNECT.equals(action)) {

						CometContext.getInstance().getEngine().connect(request, response);
					} else if (CometProtocol.CMD_REVIVAL.equals(action)) {

						CometContext.getInstance().getEngine().revival(request, response);

					} else if (CometProtocol.CMD_DROP.equals(action)) {
						CometContext.getInstance().getEngine().drop(request, response);
					}
				}

				public void onComplete(AsyncEvent event) throws IOException {
					HttpServletRequest request = (HttpServletRequest) event.getAsyncContext().getRequest();
					HttpServletResponse response = (HttpServletResponse) event.getAsyncContext().getResponse();
					CometContext.getInstance().getEngine().dying(request, response);
				}

				public void onTimeout(AsyncEvent event) throws IOException {
					HttpServletRequest request = (HttpServletRequest) event.getAsyncContext().getRequest();
					HttpServletResponse response = (HttpServletResponse) event.getAsyncContext().getResponse();
					CometContext.getInstance().getEngine().dying(request, response);
				}

				public void onError(AsyncEvent event) throws IOException {
					HttpServletRequest request = (HttpServletRequest) event.getAsyncContext().getRequest();
					HttpServletResponse response = (HttpServletResponse) event.getAsyncContext().getResponse();
					CometContext.getInstance().getEngine().drop(request, response);
				}

			});
			request.startAsync(request, response);

		} else {
			System.out.println("Async not supported");
			request.startAsync(request, response);

		}

	}
}
