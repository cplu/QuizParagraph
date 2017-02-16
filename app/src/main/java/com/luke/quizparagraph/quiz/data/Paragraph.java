package com.luke.quizparagraph.quiz.data;

import android.graphics.Paint;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cplu on 2017/2/15.
 */

public class Paragraph {
	private static final String SPACE_STRING = " ";
	private final int m_lineWidth;
	/**
	 * Containing all phrases in paragraph.
	 * Two members of Pair are: length of phrase and word set of phrase
	 */
	private List<Phrase> m_phraseParagraph;

	/**
	 * create a Paragraph object to deal with the question
	 *
	 * @param phraseList
	 * @param lineWidth  line width limit, which is shown in the view, in pixel
	 * @param paint      paint used to measure text width
	 */
	public Paragraph(List<String> phraseList, int lineWidth, Paint paint) {
		m_phraseParagraph = new ArrayList<>();
		/// iterate by index, because we need index to keep original indexes
		for (int i = 0; i < phraseList.size(); i++) {
			if (!TextUtils.isEmpty(phraseList.get(i))) {
				String[] wordStringSet = phraseList.get(i).split(" +");
				List<Word> wordSet = createWordSetByStringSet(wordStringSet, paint);
				m_phraseParagraph.add(new Phrase(i, wordSet));
			} else {
				/// invalid input string (empty or null), should bypass, meaning that index in m_phraseParagraph needn't be consecutive
			}
		}
		m_lineWidth = lineWidth;
	}

	private List<Word> createWordSetByStringSet(String[] wordStringSet, Paint paint) {
		List<Word> wordList = new ArrayList<>();
		for (String wordString : wordStringSet) {
			Word word = new Word(wordString, (int) Math.ceil(paint.measureText(wordString + SPACE_STRING)));
			wordList.add(word);
		}
		return wordList;
	}

	/**
	 * parse the current paragraph and return current sequence with line separator
	 * depending on line width
	 *
	 * @return
	 */
	public List<Phrase> parseCurrent() {
		int width = 0; /// total width of current line
		int linenumber = 0;
		for (Phrase phrase : m_phraseParagraph) {
			if (width + phrase.getWidth() > m_lineWidth) {
				/// paragraph is separated at this phrase
				phrase.calculateSeparatingPosition(width, m_lineWidth, linenumber);
				width = phrase.getRemainingLengthAfterSeparatingPosition();
				linenumber++;
			} else {
				phrase.resetSeparatingPosition();   /// no paragraph separating here
				phrase.setLineNumber(linenumber);
				phrase.setColumnPosition(width);
				width += phrase.getWidth();
			}
		}
		return m_phraseParagraph;
	}
}
