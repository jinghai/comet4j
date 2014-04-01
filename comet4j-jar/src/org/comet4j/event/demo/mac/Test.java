/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package org.comet4j.event.demo.mac;

import org.comet4j.event.Event;
import org.comet4j.event.Listener;
import org.comet4j.event.Observable;

public class Test {

	// 用法展示:宏观事件模式
	public class PersonEvent extends Event<Person> {

		private SubEventType subType;
		public String sayWords = "";
		public String goWhere = "";

		public PersonEvent(Person target) {
			super(target);
		}

		public PersonEvent(Person target, SubEventType subtype) {
			super(target);
			this.subType = subtype;
		}

		public SubEventType getSubType() {
			return subType;
		}
	}

	public abstract class PersonListener extends Listener<PersonEvent> {

		@Override
		public boolean handleEvent(PersonEvent pe) {
			if (PersonEventSubType.BEFORESAY == pe.getSubType()) {
				return onBeforeSay(pe);
			} else if (PersonEventSubType.BEFOREGO == pe.getSubType()) {
				return onBeforeGo(pe);
			}
			return true;
		}

		public abstract boolean onBeforeSay(PersonEvent pe);

		public abstract boolean onBeforeGo(PersonEvent pe);
	}

	public class PersonListener1 extends PersonListener {

		@Override
		public boolean onBeforeSay(PersonEvent pe) {
			System.out.println("1:One person want to say:" + pe.sayWords);
			return true;
		}

		@Override
		public boolean onBeforeGo(PersonEvent pe) {
			System.out.println("1:One person want to go:" + pe.goWhere);
			return true;
		}
	}

	public class Person extends Observable<PersonEvent, PersonListener> {

		public Person() {
			this.addEvent(PersonEvent.class);
		}

		public void say(String aWords) {
			PersonEvent e = new PersonEvent(this, PersonEventSubType.BEFORESAY);
			e.sayWords = aWords;
			if (!this.fireEvent(e)) {
				return;
			}
			System.out.println("say:" + aWords);
		}

		public void go(String where) {
			PersonEvent e = new PersonEvent(this, PersonEventSubType.BEFOREGO);
			e.goWhere = where;
			if (!this.fireEvent(e)) {
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
	public void run() {
		Person person = new Person();

		person.addListener(PersonEvent.class, new PersonListener1());

		// 行间加入侦听
		person.addListener(PersonEvent.class, new PersonListener() {

			@Override
			public boolean onBeforeSay(PersonEvent pe) {
				// pe.stopEvent();
				// pe.preventDefault();
				System.out.println("2:One person want to say:" + pe.sayWords);
				return true;
			}

			@Override
			public boolean onBeforeGo(PersonEvent pe) {
				pe.stopEvent();
				// pe.preventDefault();
				System.out.println("2:One person want to go:" + pe.goWhere);
				return true;
			}
		});

		person.say("Hello world!");
		person.go("Home");

	}

}
