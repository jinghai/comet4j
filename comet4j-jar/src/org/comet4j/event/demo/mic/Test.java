/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.event.demo.mic;

import org.comet4j.event.Event;
import org.comet4j.event.Listener;
import org.comet4j.event.Observable;

public class Test {

	// 用法展示:微观事件模式
	public class SpeakEvent extends Event<Person> {

		public String words = "";

		public SpeakEvent(Person target, String aWords) {
			super(target);
			words = aWords;
		}
	}

	public abstract class SpeakListener extends Listener<SpeakEvent> {

	}

	public class GoEvent extends Event<Person> {

		public String where = "";

		public GoEvent(Person target, String where) {
			super(target);
			this.where = where;
		}
	}

	public abstract class GoListener extends Listener<GoEvent> {

	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	public class Person extends Observable {

		public Person() {
			this.addEvent(SpeakEvent.class);
			this.addEvent(GoEvent.class);
		}

		public void say(String aWords) {
			SpeakEvent e = new SpeakEvent(this, aWords);
			if (!this.fireEvent(e)) {
				return;
			}
			System.out.println("say:" + aWords);
		}

		public void go(String anPlace) {
			GoEvent e = new GoEvent(this, anPlace);
			if (!this.fireEvent(e)) {
				return;
			}
			System.out.println("go:" + anPlace);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Test().run();

	}

	@SuppressWarnings("unchecked")
	public void run() {
		Person person = new Person();
		person.addListener(SpeakEvent.class, new SpeakListener() {

			@Override
			public boolean handleEvent(SpeakEvent anEvent) {
				// anEvent.stopEvent();
				System.out.println("One person want to say:" + anEvent.words);
				return true;
			}

		});
		person.addListener(GoEvent.class, new GoListener() {

			@Override
			public boolean handleEvent(GoEvent anEvent) {
				// anEvent.stopEvent();
				System.out.println("One person want to go:" + anEvent.where);
				return true;
			}

		});
		person.say("Hello world!");
		person.go("Home!");
	}

}
