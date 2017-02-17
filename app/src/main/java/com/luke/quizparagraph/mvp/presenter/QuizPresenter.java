package com.luke.quizparagraph.mvp.presenter;

import android.graphics.Paint;
import android.graphics.PointF;
import android.text.TextUtils;

import com.luke.quizparagraph.mvp.view.IQuizView;
import com.luke.quizparagraph.quiz.data.Paragraph;
import com.luke.quizparagraph.quiz.data.Phrase;
import com.luke.quizparagraph.quiz.data.Word;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by cplu on 2017/2/15.
 */

public class QuizPresenter extends ActivityPresenter<IQuizView> {
	private Paragraph m_paragraph = new Paragraph();
	private Phrase m_phraseToMove;   /// temporary phrase entry that is moving
	private int m_indexFound = -1;
//	private boolean m_phraseDeleted;    /// whether phrase to move has been deleted from m_paragraph

	@Override
	protected IQuizView createDummy() {
		return IQuizView.dummy;
	}

	@Override
	public void attach(IQuizView view) {
		super.attach(view);
	}

	public List<Phrase> parseNewParagraph(List<String> phraseStringList, int lineWidth, Paint paint) {
		LinkedList<Phrase> phraseList = generatePhraseList(phraseStringList, paint, lineWidth);
		m_paragraph = new Paragraph(phraseList, lineWidth);
//		List<Phrase> parsedResult = m_paragraph.parseCurrentParagraph();
		getView().notifyPhraseResult(m_paragraph.getPhraseList());
		return phraseList;
	}

	/**
	 * select a Phrase who contains (x, y)
	 *
	 * @param x           x coordinate of touch point
	 * @param y           y coordinate of touch point
	 * @param outRelative output the relative coordinates from (x, y) to the left-top corner of the Phrase
	 * @return
	 */
	public Phrase selectPhraseByPosition(float x, float y, PointF outRelative) {
		m_indexFound = m_paragraph.getPhraseByPosition(x, y, outRelative);
		m_phraseToMove = m_paragraph.getPhraseByIndex(m_indexFound);
		return m_phraseToMove;
	}

	/**
	 * move selected Phrase to position (x, y)
	 *
	 * @param x x coordinate of left-top corner of current phrase
	 * @param y y coordinate of left-top corner of current phrase
	 */
	public void movePhraseByPosition(float x, float y) {
		if (m_phraseToMove == null) {
			return;
		}
		if (m_paragraph.movePhraseByPosition(x, y, m_phraseToMove)) {
			getView().notifyPhraseResult(m_paragraph.getPhraseList());
		}
	}

	/**
	 * dock the current moving Phrase if exists
	 * if no docking, fall back to the old position
	 */
	public void dockPhrase() {
		if (m_phraseToMove == null) {
			return;
		}
		if (m_indexFound >= 0) {
			m_paragraph.assurePhraseExisted(m_phraseToMove, m_indexFound);
			getView().notifyPhraseResult(m_paragraph.getPhraseList());
			m_indexFound = -1;
		}
		m_phraseToMove = null;
	}

	private LinkedList<Phrase> generatePhraseList(List<String> phraseStringList, Paint paint, int lineWidth) {

		LinkedList<Phrase> phraseList = new LinkedList<>();
		/// iterate by index, we need to use index to keep original indexes
		for (int i = 0; i < phraseStringList.size(); i++) {
			String phraseString = phraseStringList.get(i);
			phraseString = phraseString.trim();
			if (!TextUtils.isEmpty(phraseString) && phraseString.length() <= lineWidth) {
				List<Word> wordSet = createWordSetByStringSet(phraseString, paint);
				phraseList.add(new Phrase(phraseString, i, wordSet));
			} else {
				/// invalid input string (empty or null), should bypass, meaning that index in m_phraseParagraph needn't be consecutive
			}
		}
		return phraseList;
	}

	private List<Word> createWordSetByStringSet(String phraseString, Paint paint) {
		String[] wordStringSet = phraseString.trim().split(" +");
		List<Word> wordList = new ArrayList<>();
		for (String wordString : wordStringSet) {
			Word word = new Word(wordString, (int) Math.ceil(paint.measureText(wordString + Phrase.SPACE_STRING)));
			wordList.add(word);
		}
		return wordList;
	}

	/**
	 * add a new Phrase to the last of Paragraph
	 *
	 * @param phraseString
	 * @param paint
	 * @return true if successfull, false otherwise
	 */
	public boolean addPhrase(String phraseString, Paint paint, int lineWidth) {
		phraseString = phraseString.trim();
		if (!TextUtils.isEmpty(phraseString) && paint.measureText(phraseString) < lineWidth) {
			List<Word> wordSet = createWordSetByStringSet(phraseString, paint);
			int phraseCount = m_paragraph.getPhraseCount();
			Phrase phrase = new Phrase(phraseString, phraseCount, wordSet);
			m_paragraph.addPhrase(phrase);
			getView().notifyPhraseAdded(phrase);
			return true;
		} else {
			return false;
		}
	}
}
