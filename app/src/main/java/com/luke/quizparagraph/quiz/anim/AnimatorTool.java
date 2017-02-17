package com.luke.quizparagraph.quiz.anim;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.luke.quizparagraph.customview.PhraseTextView;

/**
 * Created by cplu on 2017/2/17.
 */

public class AnimatorTool {

	private static final long DURATION_PHRASE_ANIM = 200;

	/**
	 * animator that move Phrase from its current position to (x, y)
	 * @param view
	 * @param x
	 * @param y
	 * @return
	 */
	public static AnimatorSet startPhraseViewAnimator(PhraseTextView view, float x, float y) {
		AnimatorSet animatorSet = getDefaultAnimator(view, x, y);
		if(animatorSet == null) {
			return null;
		}
		view.cancelAnimator();
		view.setAnimator(animatorSet);
		animatorSet.start();
		return animatorSet;
	}

	public static AnimatorSet getDefaultAnimator(View view, float x, float y) {
		if(view.getX() == x && view.getY() == y) {
			/// same position, no animation at all
			return null;
		}
		ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "x", view.getX(), x);
		ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "y", view.getY(), y);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setDuration(DURATION_PHRASE_ANIM)
			.setInterpolator(new LinearInterpolator());
		animatorSet.playTogether(animatorX, animatorY);
//		animatorSet.start();
		return animatorSet;
	}
}
