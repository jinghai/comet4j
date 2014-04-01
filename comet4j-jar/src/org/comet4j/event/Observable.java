/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 被观察对象，此对象线程安全，内部实现使用乐观锁机制，即get方法不加锁，add,remove会有锁 职责：管理事件源对象、代理履行事件源职责
 * @author xiaojinghai@kedacom.com
 */
// TODO:考虑这里的Map是否可换为List，一个事件源对应于一种事件
@SuppressWarnings("rawtypes")
public class Observable<E extends Event, L extends ListenerInterface<E>> {

	private Map<Class<E>, EventSource<E, L>> eventSources = new HashMap<Class<E>, EventSource<E, L>>();
	private List<InterceptorInterface<E>> interceptors = new ArrayList<InterceptorInterface<E>>(0);
	private boolean suspend = false;

	/**
	 * 增加一个事件拦截器 此拦截器的interceptor方法在本对象所有事件发生之前执行，如有多个拦截器则按倒序逐个执行。
	 * 当某个拦截器返回false则停止事件广播、终止动作的发生并不再执行其它拦截器，否则继续执行。
	 * 当一个对象同时具备EventInterceptor和eventIntercept方法时，则先执行eventIntercept.
	 * @param anInterceptor
	 */
	@SuppressWarnings("unchecked")
	public void addEventInterceptor(InterceptorInterface anInterceptor) {
		synchronized (interceptors) {
			interceptors.add(anInterceptor);
		}
	}

	/**
	 * 移除一个事件拦截器
	 * @param anInterceptor
	 */
	public void removeEventInterceptor(L anInterceptor) {
		synchronized (interceptors) {
			interceptors.remove(anInterceptor);
		}
	}

	/**
	 * 移除所有事件拦截器
	 */
	public void removeAllEventInterceptor() {
		synchronized (interceptors) {
			interceptors.clear();
		}
	}

	/**
	 * 事件拦截 此方法在此对象所有事件发生之前执行，可以拦截发生在对象上的所有事件，默认不做任何处理，只返回true，子类可以重写此方法进行特殊处理。
	 * 当一个对象同时具备eventIntercept方法和EventInterceptor时，则先执行eventIntercept.
	 * @param anEvent
	 * @return 返回false,则停止事件广播并终止动作的发生，否则继续执行。
	 */
	public boolean eventIntercept(E anEvent) {
		return true;
	}

	/**
	 * 暂停事件的广播，但并不终止动作的发生，直到调用 resumeEvents方法
	 */
	public void suspendEvents() {
		suspend = true;
	}

	/**
	 * 恢复事件广播，suspendEvents方法后使用。
	 */
	public void resumeEvents() {
		suspend = false;
	}

	/**
	 * 触发事件广播
	 * @param anEvent
	 * @return
	 */
	protected boolean fireEvent(E anEvent) {
		if (eventIntercept(anEvent) == false) {
			return false;
		}
		if (interceptors.size() > 0) {
			for (int i = interceptors.size() - 1; i >= 0; i--) {
				InterceptorInterface<E> interceptor = interceptors.get(i);
				if (interceptor.intercept(anEvent) == false) {
					return false;
				}
			}
		}
		if (!suspend) {
			EventSource<E, L> es = null;
			synchronized (eventSources) {
				es = eventSources.get(anEvent.getClass());
			}
			if (es != null) {
				return es.fire(anEvent);
			}
		}
		return true;
	}

	/**
	 * 添加一种事件
	 * @param clazz 事件类型
	 */
	protected void addEvent(Class<E> clazz) {
		EventSource<E, L> es = eventSources.get(clazz);
		if (es == null) {
			synchronized (eventSources) {
				eventSources.put(clazz, new EventSource<E, L>());
			}
		}
	}

	/**
	 * 删除一种事件及其所有侦听
	 * @param clazz 事件类型
	 */
	protected void removeEvent(Class<E> clazz) {
		synchronized (eventSources) {
			EventSource<E, L> es = eventSources.get(clazz);
			if (es != null) {
				es.removeAllListeners();
			}
			eventSources.remove(clazz);
		}
	}

	/**
	 * 删除所有事件及所有侦听
	 */
	protected void removeAllEvent() {
		synchronized (eventSources) {
			for (Class<E> esn : eventSources.keySet()) {
				EventSource<E, L> es = eventSources.get(esn);
				if (es != null) {
					es.removeAllListeners();
				}
			}
			eventSources.clear();
		}
	}

	/**
	 * 为事件增加侦听
	 * @param clazz
	 * @param aListener
	 */
	public void addListener(Class<E> clazz, L aListener) {
		EventSource<E, L> es = eventSources.get(clazz);
		if (es != null) {
			es.addListener(aListener);
		}
	}

	/**
	 * 删除事件的某个侦听
	 * @param clazz
	 * @param aListener
	 */
	public void removeListener(Class<E> clazz, L aListener) {
		EventSource<E, L> es = eventSources.get(clazz);
		if (es != null) {
			es.removeListener(aListener);
		}
	}

	/**
	 * 删除事件的所有侦听
	 * @param clazz
	 */
	public void removeListener(Class<E> clazz) {
		EventSource<E, L> es = eventSources.get(clazz);
		if (es != null) {
			es.removeAllListeners();
		}
	}

	/**
	 * 删除所有侦听
	 */
	public void removeAllListener() {
		synchronized (eventSources) {
			for (Class<E> esn : eventSources.keySet()) {
				EventSource<E, L> es = eventSources.get(esn);
				if (es != null) {
					es.removeAllListeners();
				}
			}
		}
	}

	/**
	 * 获取事件的所有侦听
	 * @param clazz
	 * @return 未找到返回null
	 */
	public List<L> getListeners(Class<E> clazz) {
		EventSource<E, L> es = eventSources.get(clazz);
		if (es != null) {
			return es.getListeners();
		} else {
			return new ArrayList<L>(0);
		}
	}

	/**
	 * 获取事件的事件源对象
	 * @param clazz
	 * @return 未找到返回null
	 */
	public EventSource<E, L> getEventSource(Class<E> clazz) {
		return eventSources.get(clazz);
	}

	/**
	 * 获取所有事件源
	 * @return 未找到返回空列表
	 */
	public List<EventSource<E, L>> getAllEventSource() {
		List<EventSource<E, L>> result = new ArrayList<EventSource<E, L>>();
		for (Class<E> esn : eventSources.keySet()) {
			EventSource<E, L> es = eventSources.get(esn);
			result.add(es);
		}
		return result;
	}

	public void destroy() {
		this.removeAllEvent();
		eventSources = null;
		interceptors.clear();
		interceptors = null;
	}

}
