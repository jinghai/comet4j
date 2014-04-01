/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.event;

/**
 * 事件发生时所携带的相关数据
 * @author xiaojinghai@kedacom.com
 */
@SuppressWarnings("rawtypes")
public class Event<O extends Observable> {

	private boolean stoped = false;
	private boolean preventDefault = false;
	private O target;

	public Event(O target) {
		this.target = target;
	}

	/**
	 * 停止事件运行<br>
	 * 立即停止事件发出者的行为函数，用于在事件处理过程中终止事件发出者的动作， 用于在动作发生之前结束动作，避免动作的发生。<br>
	 * 注意：只能在动作发出前进行终止，例如：BeforeGo等此类事件中应用。
	 */
	public void stopEvent() {
		stoped = true;
	}

	/**
	 * 停止事件传播<br>
	 * 调用后，此将不再传播给其它侦听，防止此事件再被其它侦听函数所处理<br>
	 * 注意：后加入侦听的函数，在事件触发时会先执行。
	 */
	public void preventDefault() {
		preventDefault = true;
	}

	public boolean hasStoped() {
		return stoped;
	}

	public boolean hasPreventDefault() {
		return preventDefault;
	}

	/**
	 * 获取事件的发出者
	 * @return
	 */
	public O getTarget() {
		return target;
	}

	public void destroy() {
		target = null;
	}
}
