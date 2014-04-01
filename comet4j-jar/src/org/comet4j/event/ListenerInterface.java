/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.event;

/**
 * 
 */

/**
 * 侦听接口 职责：规范事件侦听的执行方法
 * @author xiaojinghai@kedacom.com
 */

@SuppressWarnings("rawtypes")
public interface ListenerInterface<E extends Event> {

	/**
	 * 事件处理
	 * @param anEvent
	 * @return 
	 *         如果返回值为false则终被终止被终止执行此动作，避免动作的发生（如before类事件，一般为动作发生之前的事件才可以被终止执行，避免动作的发生
	 *         ）， 否则继续执行其它侦听函数。
	 */
	public abstract boolean handleEvent(E anEvent);
}
