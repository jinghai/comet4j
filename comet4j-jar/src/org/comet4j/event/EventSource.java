/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.event;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件源 一个事件源代一个事件种类，并管理这种事件的侦听。 职责：对于侦听的管理和执行
 * @author xiaojinghai@kedacom.com
 */
@SuppressWarnings("rawtypes")
public class EventSource<E extends Event, L extends ListenerInterface<E>> {

	protected List<L> listeners = new ArrayList<L>();

	public EventSource() {

	}

	/**
	 * 触发所有侦听函数
	 * @param anEvent
	 * @return
	 */
	public boolean fire(E anEvent) {
		synchronized (listeners) {
			for (int i = listeners.size() - 1; i >= 0; i--) {
				L listener = listeners.get(i);
				if (anEvent.hasStoped()) {
					return false;
				}
				if (anEvent.hasPreventDefault()) {
					break;
				}
				if (!listener.handleEvent(anEvent)) { // 等同于hasStoped
					return false;
				}
			}
		}
		return anEvent.hasStoped() ? false : true;
	}

	/**
	 * 添加侦听函数
	 * @param aListener
	 */
	public void addListener(L aListener) {
		synchronized (listeners) {
			listeners.add(aListener);
		}

	}

	/**
	 * 删除指定的侦听函数
	 * @param aListener
	 */
	public void removeListener(L aListener) {
		synchronized (listeners) {
			listeners.remove(aListener);
		}

	}

	/**
	 * 删除所有侦听
	 */
	public void removeAllListeners() {
		synchronized (listeners) {
			listeners.clear();
		}
	}

	/**
	 * 得到某个侦听函数
	 * @param aListener
	 * @return
	 */
	public L getListener(L aListener) {
		for (L l : listeners) {
			if (l == aListener) {
				return l;
			}
		}
		return null;
	}

	/**
	 * 获取所有侦听函数
	 * @return
	 */
	public List<L> getListeners() {
		return listeners;
	}

	public void destroy() {
		removeAllListeners();
		listeners = null;
	}
}
