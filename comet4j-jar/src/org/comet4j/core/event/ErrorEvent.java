/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.core.event;

import org.comet4j.core.CometEngine;
import org.comet4j.event.Event;

public class ErrorEvent extends Event<CometEngine> {

	private Exception err;

	public ErrorEvent(CometEngine target, Exception anErr) {
		super(target);
		err = anErr;
	}

	public Exception getErr() {
		return err;
	}

	public void setErr(Exception err) {
		this.err = err;
	}

}
