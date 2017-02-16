package com.luke.quizparagraph.rx;

import org.pmw.tinylog.Logger;

import rx.SingleSubscriber;

/**
 * Created by cplu on 2016/8/19.
 */
public abstract class SingleTask<Type> extends SingleSubscriber<Type> {

	@Override
	public void onError(Throwable throwable) {
		Logger.debug(throwable);
	}
}
