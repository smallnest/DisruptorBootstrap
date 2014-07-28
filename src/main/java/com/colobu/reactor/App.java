package com.colobu.reactor;

import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.event.Event;

import com.colobu.ObjectEvent;

import static reactor.event.selector.Selectors.$;

/**
 * A simple spring Reactor example.
 *
 */
public class App {

	public static void handleEvent(Event<ObjectEvent> ev) {
		System.out.println(ev.getData().getObject());
	}

	public static void main(String[] args) throws InterruptedException {
		Environment env = new Environment();
		Reactor reactor = Reactors.reactor().env(env).dispatcher(Environment.THREAD_POOL).get();

		// topic: parse
		reactor.on($("parse"), App::handleEvent);

		// Notify consumers of the 'parse' topic that data is ready
		// by passing a Supplier<Event<T>> in the form of a lambda

		for (long l = 0; true; l++) {
			String obj = "Test-" + l;
			reactor.notify("parse", Event.wrap(new ObjectEvent().setObject(obj)));
			Thread.sleep(1000);
		}

	}
}
