/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.temp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.CometEvent;
import org.apache.catalina.CometProcessor;

/**
 * 
 */
public class StreamingXHRServlet extends HttpServlet implements CometProcessor {

	private static final long serialVersionUID = 1L;
	protected ArrayList<HttpServletResponse> connections = new ArrayList<HttpServletResponse>();
	protected MessageSender messageSender = null;
	protected Boolean debug = false;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public StreamingXHRServlet() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init() throws ServletException {

		messageSender = new MessageSender();
		Thread messageSenderThread = new Thread(messageSender, "MessageSender[" + getServletContext().getContextPath()
				+ "]");
		messageSenderThread.setDaemon(true);
		messageSenderThread.start();

		Thread timeSenderThread = new Thread(new TimeSender());
		timeSenderThread.setDaemon(true);
		timeSenderThread.start();

	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy() {
		connections.clear();
		messageSender.stop();
		messageSender = null;
		debug = null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.apache.catalina.CometProcessor#event(org.apache.catalina.CometEvent)
	 */
	@SuppressWarnings("unused")
	public void event(CometEvent event) throws IOException, ServletException {
		HttpServletRequest request = event.getHttpServletRequest();
		HttpServletResponse response = event.getHttpServletResponse();

		if (event.getEventType() == CometEvent.EventType.BEGIN) {
			// event.setTimeout(0);
			log("-->begin");
			synchronized (connections) {
				connections.add(response);
			}
			event.setTimeout(6000);
		} else if (event.getEventType() == CometEvent.EventType.ERROR) {
			log("-->error");
			synchronized (connections) {
				connections.remove(response);
			}
			if (event.getEventSubType() == CometEvent.EventSubType.TIMEOUT) {
				response.setStatus(408);
			}
			response.getWriter().close();
			event.close();

		} else if (event.getEventType() == CometEvent.EventType.END) {
			log("-->end");
			synchronized (connections) {
				connections.remove(response);
			}
			response.getWriter().close();
			event.close();
		} else if (event.getEventType() == CometEvent.EventType.READ) {
			log("-->read");
		}

	}

	class TimeSender implements Runnable {

		@SuppressWarnings("deprecation")
		public void run() {
			while (true) {
				messageSender.send("System", new Date().toLocaleString());
				log("send" + new Date().toLocaleString());
				try {
					Thread.sleep(5000);
				} catch (InterruptedException ignore) {
				}
			}
		}
	}

	public class MessageSender implements Runnable {

		protected boolean running = true;
		protected ArrayList<String> messages = new ArrayList<String>();

		public MessageSender() {
		}

		public void stop() {
			running = false;
		}

		public void send(String user, String message) {
			synchronized (messages) {
				messages.add("[" + user + "]: " + message);
				messages.notify();
			}
		}

		public void run() {

			while (running) {

				if (messages.size() == 0) {
					try {
						synchronized (messages) {
							messages.wait();
						}
					} catch (InterruptedException e) {
						// Ignore
					}
				}

				synchronized (connections) {
					String[] pendingMessages = null;
					synchronized (messages) {
						pendingMessages = messages.toArray(new String[0]);
						messages.clear();
					}
					// Send any pending message on all the open connections
					for (int i = 0; i < connections.size(); i++) {
						try {
							PrintWriter writer = connections.get(i).getWriter();
							for (int j = 0; j < pendingMessages.length; j++) {
								writer.println(pendingMessages[j] + "<br>");
								// writer.print("<{a:'aa',b:'bb'}>");
							}
							writer.flush();
							// log("Message Out:...");
						} catch (IOException e) {
							// log("IOExeption sending message", e);
						}
					}
				}

			}

		}

	}

}
