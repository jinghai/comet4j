/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.temp;

/**
 * 模拟发送信息的测试类
 * @author xiaojinghai
 * @date 2011-2-24
 */

public class SendMessageTest implements Runnable {

	public void run() {
		while (true) {
			try {
				Thread.sleep(5000);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			/*
			 * CometContext.getInstance().getEngine().sendTo(CometContext.
			 * getInstance ().getEngine().getConnections(), "Test Data");
			 */
		}
	}
}
