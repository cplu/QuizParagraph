package com.luke.quizparagraph.mvp.view;

import com.luke.quizparagraph.quiz.data.Phrase;

import java.util.List;

/**
 * Created by cplu on 2017/2/15.
 */

public interface IQuizView extends IActivityView {
	IQuizView dummy = new IQuizView() {
		@Override
		public void notifyPhraseResult(List<Phrase> parsedResult) {

		}

		@Override
		public void notifyPhraseAdded(Phrase phrase) {

		}
	};

	/**
	 * notify all phrases to ui
	 * @param parsedResult
	 */
	void notifyPhraseResult(List<Phrase> parsedResult);

	/**
	 * notify phrase added to paragraph (added to last)
	 * @param phrase
	 */
	void notifyPhraseAdded(Phrase phrase);
}
