package com.luke.quizparagraph.rx;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by cplu on 2016/9/6.
 */
public class RXTask {
	/**
	 * run a task on the calling thread
	 * @param task
	 * @param <Type>
	 * @return
	 */
	public static <Type> Subscription run(RXLocalSubscriber<Type> task) {
		return Observable.create(new Observable.OnSubscribe<Type>() {
			@Override
			public void call(Subscriber<? super Type> subscriber) {
				subscriber.onNext(null);
				subscriber.onCompleted();
			}
		}).subscribe(task);
	}
}
