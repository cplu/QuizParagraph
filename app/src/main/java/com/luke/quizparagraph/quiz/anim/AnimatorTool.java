package com.luke.quizparagraph.quiz.anim;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by cplu on 2017/2/17.
 */

public class AnimatorTool {

	private static final long DURATION_PHRASE_ANIM = 200;

	/**
	 * move Phrase from its current position to (x, y)
	 * @param view
	 * @param x
	 * @param y
	 * @return  true if animation occurs
	 */
	public static boolean animatePhraseView(View view, float x, float y) {
		if(view.getX() == x && view.getY() == y) {
			/// same position, no animation at all
			return false;
		}
		ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "x", view.getX(), x);
		ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "y", view.getY(), y);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setDuration(DURATION_PHRASE_ANIM)
			.setInterpolator(new AccelerateInterpolator());
		animatorSet.playTogether(animatorX, animatorY);
		animatorSet.start();
		return true;
	}
}
