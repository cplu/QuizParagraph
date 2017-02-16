package com.luke.quizparagraph.rx;

import org.pmw.tinylog.Logger;

import rx.Subscriber;

/**
 * Created by cplu on 2016/7/26.
 */
public abstract class RXLocalSubscriber<Type> extends Subscriber<Type> {
	@Override
	public void onCompleted() {
		Logger.debug("subscriber(local) in onCompleted :" + Thread.currentThread().getName());
		Logger.debug("subscriber onCompleted");
	}

	@Override
	public void onError(Throwable throwable) {

	}
}
