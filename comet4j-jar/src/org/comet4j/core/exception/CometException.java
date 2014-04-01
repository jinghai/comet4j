/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.exception;

/**
 * Comet4J异常
 */

public class CometException extends RuntimeException {

	private static final long serialVersionUID = 3048792398595051727L;
	private Throwable cause;

	public CometException(String message) {
		super(message);
	}

	public CometException(Throwable t) {
		super(t.getMessage());
		this.cause = t;
	}

	@Override
	public Throwable getCause() {
		return this.cause;
	}
}
