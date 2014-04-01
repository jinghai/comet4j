/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.exception;

/**
 * 重复的应用通道标识
 */

public class DoubleAppModuleKeyException extends CometException {

	/** serialVersionUID */
	private static final long serialVersionUID = -679462669681889902L;

	/**
	 * @param message
	 */

	public DoubleAppModuleKeyException(String message) {
		super(message);
	}

}
