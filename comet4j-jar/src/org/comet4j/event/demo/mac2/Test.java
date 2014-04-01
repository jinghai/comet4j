/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.event.demo.mac2;

import java.util.HashMap;
import java.util.Map;

import org.comet4j.core.util.JSONUtil;
import org.comet4j.event.Event;
import org.comet4j.event.Listener;
import org.comet4j.event.Observable;

public class Test {

	public class SpeakEvent extends Event<Person> {

		public String words = "";

		public SpeakEvent(Person target, String aWords) {
			super(target);
			words = aWords;
		}
	}

	public class GoEvent extends Event<Person> {

		public String where = "";

		public GoEvent(Person target, String where) {
			super(target);
			this.where = where;
		}
	}

	@SuppressWarnings("rawtypes")
	public abstract class PersonListener extends Listener {

		@Override
		public boolean handleEvent(Event anEvent) {
			if (anEvent instanceof SpeakEvent) {
				return onSay((SpeakEvent) anEvent);
			}
			if (anEvent instanceof GoEvent) {
				return onGo((GoEvent) anEvent);
			}
			return true;
		}

		public abstract boolean onSay(SpeakEvent anEvent);

		public abstract boolean onGo(GoEvent anEvent);

	}

	public class PersonListener1 extends PersonListener {

		@Override
		public boolean onSay(SpeakEvent anEvent) {
			System.out.println("1:One person want to speak");
			return true;
		}

		@Override
		public boolean onGo(GoEvent anEvent) {
			System.out.println("1:One person want to go");
			return true;
		}
	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	public class Person extends Observable {

		public Person() {
			addEvent(SpeakEvent.class);
			addEvent(GoEvent.class);
		}

		public void say(String aWords) {
			SpeakEvent e = new SpeakEvent(this, aWords);
			if (!fireEvent(e)) {
				return;
			}
			System.out.println("say:" + aWords);
		}

		public void go(String where) {
			GoEvent e = new GoEvent(this, where);
			if (!fireEvent(e)) {
				return;
			}
			System.out.println("go:" + where);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Test().run();

	}

	// 用法
	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	public void run() {
		Person person = new Person();
		PersonListener1 ls = new PersonListener1();
		person.addListener(SpeakEvent.class, ls);
		person.addListener(GoEvent.class, ls);
		Map m = new HashMap();
		m.put("a", "aaa");
		m.put("b", true);
		System.out.println(JSONUtil.object2json(m));

	}

}
