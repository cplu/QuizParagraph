package com.luke.quizparagraph.rx;

import org.pmw.tinylog.Logger;

import rx.Subscriber;

/**
 * Created by cplu on 2016/11/23.
 */

public abstract class RXSubscriber<Type> extends Subscriber<Type> {
	@Override
	public void onCompleted() {
		Logger.debug("subscriber(local) in onCompleted :" + Thread.currentThread().getName());
		Logger.debug("subscriber onCompleted");
	}
}
