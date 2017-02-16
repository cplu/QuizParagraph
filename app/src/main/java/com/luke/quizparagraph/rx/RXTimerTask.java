package com.luke.quizparagraph.rx;

import java.util.concurrent.TimeUnit;

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
 * Created by cplu on 2016/7/27.
 */
public class RXTimerTask {
	/**
	 * create a timer and start
	 * it is subscribed on io thread and run on main thread
	 *
	 * @param timespanMs time span in ms
	 * @param timerTask  task to be run by timer
	 * @return
	 */
	public static Subscription createAndStart(int timespanMs, Subscriber<Long> timerTask) {
		return Observable.interval(timespanMs, timespanMs, TimeUnit.MILLISECONDS)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(timerTask);
	}

	/**
	 * create a timer and start
	 * it is subscribed on io thread and run on scheduler
	 *
	 * @param timespanMs time span in ms
	 * @param timerTask  task to be run by timer
	 * @param scheduler  scheduler for running timer task
	 * @return
	 */
	public static Subscription createAndStart(int timespanMs, Subscriber<Long> timerTask, Scheduler scheduler) {
		return Observable.interval(timespanMs, timespanMs, TimeUnit.MILLISECONDS)
			.subscribeOn(Schedulers.io())
			.observeOn(scheduler)
			.subscribe(timerTask);
	}

	/**
	 * create a timer and start
	 * it is subscribed on io thread and run on main thread
	 * the Long value in onNext represents the timer count
	 *
	 * @param timespanMs time span in ms
	 * @param count      max times the task is run
	 * @param timerTask  task to be run by timer
	 * @return
	 */
	public static Subscription createAndStart(int timespanMs, int count, Subscriber<Long> timerTask) {
		return Observable.interval(timespanMs, timespanMs, TimeUnit.MILLISECONDS)
			.take(count)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(timerTask);
	}

	public static Subscription createAndStart(int timespanMs, int count, Scheduler scheduler, Subscriber<Long> timerTask) {
		return Observable.interval(timespanMs, timespanMs, TimeUnit.MILLISECONDS)
			.take(count)
			.subscribeOn(Schedulers.io())
			.observeOn(scheduler)
			.subscribe(timerTask);
	}

	/**
	 * create a timer and expect the first result that matches condition
	 * it is subscribed on io thread and run on main thread
	 * the Long value in onNext represents the timer count
	 *
	 * @param timespanMs    time span in ms
	 * @param count         max times the task is run
	 * @param condition     condition to match
	 * @param timerTask     task to be run on first result matching condition
	 * @return
	 */
	public static Subscription timerOnCondition(int timespanMs, int count, Func1 condition, Subscriber<Long> timerTask) {
		return Observable.interval(timespanMs, timespanMs, TimeUnit.MILLISECONDS)
			.take(count)
			.takeFirst(condition)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(timerTask);
	}

	/**
	 * run a single task after specific delay on main thread
	 *
	 * @param delayMs delay before task is run
	 * @param task    task to be run
	 * @return
	 */
	public static Subscription delaySingleTask(int delayMs, SingleSubscriber<Long> task) {
		return Single.just(1L)
			.delay(delayMs, TimeUnit.MILLISECONDS)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(task);
	}

	public static void stop(Subscription subscription) {
		if (subscription != null && !subscription.isUnsubscribed()) {
			subscription.unsubscribe();
		}
	}
}
