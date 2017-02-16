package com.luke.quizparagraph.mvp.presenter;

import android.graphics.Paint;
import android.graphics.PointF;
import android.text.TextUtils;

import com.luke.quizparagraph.mvp.view.IQuizView;
import com.luke.quizparagraph.quiz.data.Paragraph;
import com.luke.quizparagraph.quiz.data.Phrase;
import com.luke.quizparagraph.quiz.data.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cplu on 2017/2/15.
 */

public class QuizPresenter extends ActivityPresenter<IQuizView> {
	private Paragraph m_paragraph;
	private Phrase m_phraseToMove;   /// temporary phrase entry that is moving

	@Override
	protected IQuizView createDummy() {
		return IQuizView.dummy;
	}

	public List<Phrase> parseNewParagraph(List<String> phraseStringList, int lineWidth, Paint paint) {
		List<Phrase> phraseList = generatePhraseList(phraseStringList, paint);
		m_paragraph = new Paragraph(phraseList, lineWidth);
		List<Phrase> parsedResult = m_paragraph.parseCurrentParagraph();
		getView().notifyPhraseResult(parsedResult);
		return phraseList;
	}

	/**
	 * select a Phrase who contains (x, y)
	 * @param x             x coordinate of touch point
	 * @param y             y coordinate of touch point
	 * @param outRelative   output the relative coordinates from (x, y) to the left-top corner of the Phrase
	 * @return
	 */
	public Phrase selectPhraseByPosition(float x, float y, PointF outRelative) {
		m_phraseToMove = m_paragraph.removePhraseByPosition(x, y, outRelative);
		if (m_phraseToMove != null) {
			List<Phrase> parsedResult = m_paragraph.parseCurrentParagraph();
			getView().notifyPhraseResult(parsedResult);
		}
		return m_phraseToMove;
	}

	private List<Phrase> generatePhraseList(List<String> phraseStringList, Paint paint) {

		List<Phrase> phraseList = new ArrayList<>();
		/// iterate by index, we need to use index to keep original indexes
		for (int i = 0; i < phraseStringList.size(); i++) {
			if (!TextUtils.isEmpty(phraseStringList.get(i))) {

				String[] wordStringSet = phraseStringList.get(i).split(" +");
				List<Word> wordSet = createWordSetByStringSet(wordStringSet, paint);
				phraseList.add(new Phrase(i, wordSet));
			} else {
				/// invalid input string (empty or null), should bypass, meaning that index in m_phraseParagraph needn't be consecutive
			}
		}
		return phraseList;
	}

	private List<Word> createWordSetByStringSet(String[] wordStringSet, Paint paint) {
		List<Word> wordList = new ArrayList<>();
		for (String wordString : wordStringSet) {
			Word word = new Word(wordString, (int) Math.ceil(paint.measureText(wordString + Phrase.SPACE_STRING)));
			wordList.add(word);
		}
		return wordList;
	}

}
