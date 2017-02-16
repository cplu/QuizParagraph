package com.luke.quizparagraph.rx;

import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by cplu on 2016/8/4.
 */
public class RXThreadTask {
	/**
	 * run a task on <code>subscribeOn</code> and a subscriber is attached
	 * @param subscribeOn     the scheduled thread for the task
	 * @param task          task to be run
	 * @param subscriber    onSuccess is run on ui thread
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <Type> Subscription run(Scheduler subscribeOn, final Task task, SingleSubscriber subscriber) {
		return Single.create(new Single.OnSubscribe<Type>(){
			@Override
			public void call(SingleSubscriber<? super Type> singleSubscriber) {
				singleSubscriber.onSuccess(task.<Type>runTask());
			}
		})
				.subscribeOn(subscribeOn)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(subscriber);
	}

	/**
	 * run a task on <code>observeOn</code>
	 * @param observeOn        observeOn the task to be run
	 * @param subscriber       run a task
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Subscription run(Scheduler observeOn, final SingleTask subscriber) {
		return Single.just(null)
			.subscribeOn(Schedulers.io())
			.observeOn(observeOn)
			.subscribe(subscriber);
	}

	/**
	 * run a task on the current thread
	 * @param alternate         alternative result if task returns null
	 * @param task              task to be run
	 * @param <Type>
	 * @return                  observable for the task or alternate, for later to be subscribed on
	 */
	public static <Type> Observable createTask(final Observable<Type> alternate, final Task task) {
		return Observable.create(new Observable.OnSubscribe<Type>() {
			@Override
			public void call(Subscriber<? super Type> subscriber) {
				Type result = task.runTask();
				subscriber.onNext(result);
				subscriber.onCompleted();
			}
		}).flatMap(new Func1<Type, Observable<Type>>() {
			@Override
			public Observable<Type> call(Type type) {
				if(type != null) {
					return Observable.just(type);
				} else {
					return alternate;
				}
			}
		});
	}

	public interface Task {
		<Type> Type runTask();
	}
}
