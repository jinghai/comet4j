/**
 * @(#)DelayTaskEngine.java 2011-7-25 Copyright 2011 it.kedacom.com, Inc. All
 *                          rights reserved.
 */

package org.comet4j.core.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 延迟任务管理器，用于执行延迟任务，当任务在执行以前可以分配新的任务，原任务将取消执行，不同的任务组使用taskId加以区分。
 * @author xiaojinghai
 * @date 2011-7-25
 */

public class DelayTask {

	private final int cpuCoreNumber = Runtime.getRuntime().availableProcessors();
	private final ScheduledExecutorService scheduler;
	private final Map<String, ScheduledFuture<Runnable>> taskHandles = new ConcurrentHashMap<String, ScheduledFuture<Runnable>>();

	public DelayTask() {
		scheduler = Executors.newScheduledThreadPool(cpuCoreNumber);
	}

	public DelayTask(int cpuCoreNumber) {
		scheduler = Executors.newScheduledThreadPool(cpuCoreNumber);
	}

	/**
	 * 延迟执行某任务，此方法可以多次重复调用，若之前的任务还没有执行，那么会取消原任务的执行，改为执行新任务。
	 * @param taskId 任务Id,用于区分不同类别的延迟任务
	 * @param task 需要执行的任务
	 * @param delay 延迟时间
	 * @param unit 延迟的时间单位
	 */

	@SuppressWarnings("unchecked")
	public void delay(String taskId, Runnable task, long delay, TimeUnit unit) {
		ScheduledFuture<Runnable> taskHandle = taskHandles.get(taskId);
		if (taskHandle == null || taskHandle.isDone()) {
			taskHandle = (ScheduledFuture<Runnable>) scheduler.schedule(task, delay, unit);
		} else {
			taskHandle.cancel(true);
			taskHandle = (ScheduledFuture<Runnable>) scheduler.schedule(task, delay, unit);
		}
		taskHandles.put(taskId, taskHandle);
	}

	/**
	 * 取消指定延迟任务的执行
	 * @param taskId 任务Id
	 */

	public void cancel(String taskId) {
		ScheduledFuture<Runnable> taskHandle = taskHandles.get(taskId);
		if (taskHandle != null && !taskHandle.isDone()) {
			taskHandle.cancel(true);
		}
	}

	/**
	 * 取消所有延迟任务的执行
	 */

	public void cancelAllTask() {
		for (String taskId : taskHandles.keySet()) {
			ScheduledFuture<Runnable> taskHandle = taskHandles.get(taskId);
			if (taskHandle != null && !taskHandle.isDone()) {
				taskHandle.cancel(true);
			}
		}
	}

	/**
	 * 在所有已安排的任务都执行完毕后结束并清除线程池。
	 */
	public void shutdown() {
		scheduler.shutdown();
	}

}
