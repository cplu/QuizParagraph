package com.luke.quizparagraph.mvp.presenter;

import android.graphics.Paint;

import com.luke.quizparagraph.mvp.view.IQuizView;
import com.luke.quizparagraph.quiz.data.Paragraph;
import com.luke.quizparagraph.quiz.data.Phrase;

import java.util.List;

/**
 * Created by cplu on 2017/2/15.
 */

public class QuizPresenter extends ActivityPresenter<IQuizView> {
	private Paragraph m_paragraph;

	@Override
	protected IQuizView createDummy() {
		return IQuizView.dummy;
	}

	public void parseNewParagraph(List<String> phraseList, int lineWidth, Paint paint) {
		m_paragraph = new Paragraph(phraseList, lineWidth, paint);
		List<Phrase> parsedResult = m_paragraph.parseCurrent();
		getView().notifyPhraseResult(parsedResult);
	}
}
