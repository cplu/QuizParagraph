
package com.luke.quizparagraph.mvp;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.luke.quizparagraph.mvp.presenter.ActivityPresenter;
import com.luke.quizparagraph.mvp.view.IActivityView;


/**
 * Created by cplu on 2016/8/2.
 */
public abstract class MVPActivity<ViewType extends IActivityView, PresenterType extends ActivityPresenter<ViewType>>
		extends Activity implements IActivityView {

	protected PresenterType m_presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_presenter = createPresenter();
		m_presenter.attach((ViewType)this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		m_presenter.detach();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

		}

		return super.onKeyDown(keyCode, event);
	}

	protected abstract PresenterType createPresenter();
}
