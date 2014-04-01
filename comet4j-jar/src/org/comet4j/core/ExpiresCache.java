/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 过期缓存器 可自动将过期内容清除，检测频率、过期时间 线程安全 一个键可以存放多个值 需改进：变成范型
 */

public class ExpiresCache {

	private final long timespan;
	private final long frequency;
	boolean init = false;
	private long size = 0l; // 临时保留缓存数量
	private final Map<CometConnection, List<CometMessage>> cache = Collections
			.synchronizedMap(new WeakHashMap<CometConnection, List<CometMessage>>());

	/**
	 * 构造
	 * @param aTimespan 过期时长，毫秒
	 * @param aFrequency 过期检测频率，毫秒
	 */

	public ExpiresCache(long aTimespan, long aFrequency) {
		init = true;
		frequency = aFrequency;
		timespan = aTimespan;
		Thread cleanerThread = new Thread(new CacheCleaner(), "Comet4J-MessageCleaner");
		cleanerThread.setDaemon(true);
		cleanerThread.start();

	}

	public synchronized void push(CometConnection c, CometMessage e) {
		if (c == null) return;
		List<CometMessage> list = cache.get(c);
		if (list == null) {
			list = new ArrayList<CometMessage>();
			cache.put(c, list);
		}
		list.add(e);
	}

	public synchronized void push(CometConnection c, List<CometMessage> e) {
		if (c == null) return;
		List<CometMessage> list = cache.get(c);
		if (list == null) {
			list = new ArrayList<CometMessage>();
			cache.put(c, list);
		}
		list.addAll(e);
	}

	/**
	 * 获取连接的失败消息列表，取出后将此连接及信息从缓存清除
	 * @param c 连接
	 * @return 如果没有连接对应的消息返回null，若取到此边接的消息列表
	 */
	public synchronized List<CometMessage> get(CometConnection c) {
		List<CometMessage> list = cache.get(c);
		if (list != null) {
			cache.remove(c);
		}
		return list;
	}

	// ----------定时清理过期信息---------------
	class CacheCleaner implements Runnable {

		private final List<CometConnection> toDeleteList = new ArrayList<CometConnection>();
		private final List<CometMessage> toDeleteMessageList = new ArrayList<CometMessage>();

		public void run() {
			while (init) {
				try {
					CometContext.getInstance().log("【CometDebug】开始清理消息缓存");
					Thread.sleep(frequency);
					checkExpires();
					CometContext.getInstance().log("【CometDebug】开始清理消息缓存完毕");
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		}

		// 过期检查
		private void checkExpires() {
			size = 0;
			synchronized (cache) {
				for (CometConnection o : cache.keySet()) {
					List<CometMessage> list = cache.get(o);
					size += list.size();
					if (list.size() > 0) {
						// 寻找过期消息条目
						synchronized (list) {
							for (CometMessage msg : list) {
								long expireMillis = msg.getTime() + timespan;
								if (expireMillis < System.currentTimeMillis()) {
									toDeleteMessageList.add(msg);
								}
							}
							if (!toDeleteMessageList.isEmpty()) {
								for (CometMessage msg : toDeleteMessageList) {
									list.remove(msg);
								}
								toDeleteMessageList.clear();
							}
						}
					}
					if (list.size() == 0) {
						toDeleteList.add(o);
					}
				}
				if (!toDeleteList.isEmpty()) {
					for (CometConnection c : toDeleteList) {
						cache.remove(c);
					}
					toDeleteList.clear();
				}
			}
			CometContext.getInstance().log("【CometDebug】缓存数量:" + size);
		}
	}

	public void destroy() {
		init = false;
		for (Object o : cache.keySet()) {
			cache.get(o).clear();
		}
		cache.clear();
	}
}
