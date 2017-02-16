package com.luke.quizparagraph.rx;

import android.content.Intent;

import org.pmw.tinylog.Logger;

import rx.Subscriber;

/**
 * Created by cplu on 2016/7/26.
 */
public abstract class RXNetworkSubscriber extends Subscriber<Intent> {
	@Override
	public void onCompleted() {
		Logger.debug("subscriber(network) in onCompleted :" + Thread.currentThread().getName());
		Logger.debug("subscriber onCompleted");
	}

	@Override
	public void onError(Throwable throwable) {

	}
}
