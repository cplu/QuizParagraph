package com.luke.quizparagraph.mvp.presenter;


import com.luke.quizparagraph.mvp.view.IActivityView;

import java.lang.ref.WeakReference;

/**
 * Created by cplu on 2016/7/26.
 */
public abstract class ActivityPresenter<ViewType extends IActivityView> {

	private WeakReference<ViewType> m_iView;
	protected ViewType m_dummy;
	protected abstract ViewType createDummy();

	protected ViewType getView() {
		ViewType view = m_iView.get();
		return view != null ? view : m_dummy;
	}

	/**
	 * @param view
	 */
	public void attach(ViewType view) {
		m_iView = new WeakReference<>(view);
		m_dummy = createDummy();

	}

	/**
	 */
	public void detach() {

		if (m_iView != null) {
			m_iView.clear();
//			m_iView = null;
		}
	}
}
