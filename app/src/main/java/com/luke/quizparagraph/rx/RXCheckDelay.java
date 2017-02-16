package com.luke.quizparagraph.rx;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;

/**
 * Created by cplu on 2016/12/7.
 * things will happen only if timeout elapsed
 */

public class RXCheckDelay {
	private Observable<Long> m_observable;
	private Subscription m_subscription;

	public RXCheckDelay(long durationInMs) {
		m_observable = Observable.timer(durationInMs, TimeUnit.MILLISECONDS);
//			Observable.create(new Observable.OnSubscribe<Integer>() {
//			@Override
//			public void call(Subscriber<? super Integer> subscriber) {
//				subscriber.onNext(1);
//				subscriber.onCompleted();
//			}
//		}).delay(durationInMs, TimeUnit.MILLISECONDS);
	}

	public void startDelayCheck() {
		stopDelayCheck();

		m_subscription = m_observable.subscribe();
	}

	public boolean isDelayElapsed() {
		return m_subscription == null || m_subscription.isUnsubscribed();
	}

	public void stopDelayCheck() {
		if(m_subscription != null && !m_subscription.isUnsubscribed()) {
			m_subscription.unsubscribe();
		}
	}
}
