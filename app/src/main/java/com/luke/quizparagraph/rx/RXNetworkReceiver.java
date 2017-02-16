package com.luke.quizparagraph.rx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import org.pmw.tinylog.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by cplu on 2016/7/26.
 */
public class RXNetworkReceiver {
	private static Observable<Intent> s_networkObservable;
	private static Context m_ctx;

	public static void init(Context ctx) {
		m_ctx = ctx;

		s_networkObservable = Observable.create(new Observable.OnSubscribe<Intent>() {
			@Override
			public void call(final Subscriber<? super Intent> subscriber) {
				/// Note: subscriber here is a ReplaySubscriber, this call method is called only once
				Logger.debug("subscriber in OnSubscribe :" + Thread.currentThread().getName());
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
				m_ctx.registerReceiver(new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						String action = intent.getAction();
						if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
							subscriber.onNext(intent);
						}
					}
				}, intentFilter);
			}
		})
		.replay(1)
		.autoConnect();
	}

	public static Subscription register(RXNetworkSubscriber subscriber) {
		return s_networkObservable.subscribe(subscriber);
	}

	public static void unregister(Subscription subscription) {
		if (subscription != null && !subscription.isUnsubscribed()) {
			subscription.unsubscribe();
		}
	}
}
