package com.luke.quizparagraph.customview;

import android.animation.AnimatorSet;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cplu on 2017/2/16.
 */

public class PhraseTextView extends TextView {
	private List<TextView> m_separatorViews = new ArrayList<>();    /// used for line separating
	private AnimatorSet m_phraseAnimator;

	public PhraseTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setSeparatorViews(ViewGroup parentView, List<TextView> separatorViews) {
		clearSeparatorViews(parentView);
		m_separatorViews = separatorViews;
		for (TextView separatorView : m_separatorViews) {
			parentView.addView(separatorView);
		}
	}

	public void clearSeparatorViews(ViewGroup parentView) {
		for (TextView separatorView : m_separatorViews) {
			parentView.removeView(separatorView);
		}
//		m_separatorViews.clear();
	}

	public void setAnimator(AnimatorSet animatorSet) {
		m_phraseAnimator = animatorSet;
	}

	public void cancelAnimator() {
		if(m_phraseAnimator != null) {
			m_phraseAnimator.cancel();
		}
	}
}
