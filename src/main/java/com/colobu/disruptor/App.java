package com.colobu.disruptor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.colobu.ObjectEvent;
import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * a simple example to show applying Disruptor.
 */
public class App {
	public static void handleEvent1(ObjectEvent event, long sequence, boolean endOfBatch) {
		System.out.println("handler-1: " + event.getObject());
	}
	public static void handleEvent2(ObjectEvent event, long sequence, boolean endOfBatch) {
		System.out.println("handler-2: " + event.getObject());
	}
	
	private static void produceEvents(Disruptor<ObjectEvent> disruptor) throws InterruptedException {
		RingBuffer<ObjectEvent> ringBuffer = disruptor.getRingBuffer();
		for (long l = 0; true; l++) {
			String obj = "Test-" + l;
			ringBuffer.publishEvent((event, sequence) -> event.setObject(obj));
			Thread.sleep(1000);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws InterruptedException {
		//Executor that will be used to construct new threads for consumers
		Executor executor = Executors.newCachedThreadPool();
		//Specify the size of the ring buffer, must be power of 2.
		int bufferSize = 1024;
		//Disruptor<ObjectEvent> disruptor = new Disruptor<>(ObjectEvent::new, bufferSize, executor);
		Disruptor<ObjectEvent> disruptor = new Disruptor<>(ObjectEvent::new, bufferSize, executor, 
				ProducerType.SINGLE, new LiteBlockingWaitStrategy());

		disruptor.handleEventsWith(App::handleEvent1);
		disruptor.handleEventsWith(App::handleEvent2);
		
		//disruptor.handleEventsWith((event, sequence, endOfBatch) -> System.out.println("Event: " + event.getObject()));
		disruptor.start();

		produceEvents(disruptor);
	}

	
}
