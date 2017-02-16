package com.luke.quizparagraph.rx;

import org.pmw.tinylog.Logger;

import rx.Subscriber;

/**
 * Created by cplu on 2016/8/1.
 */
public abstract class RXIntervalSubscriber extends Subscriber<Long> {
	@Override
	public void onCompleted() {
		Logger.debug("subscriber(interval) in onCompleted :" + Thread.currentThread().getName());
		Logger.debug("subscriber onCompleted");
	}

	@Override
	public void onError(Throwable throwable) {

	}
}
