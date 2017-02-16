package com.luke.quizparagraph.quiz.data;

import android.graphics.PointF;

import java.util.List;

/**
 * Created by cplu on 2017/2/15.
 */

public class Paragraph {
	private final int m_lineWidth;
	/**
	 * Containing all phrases in paragraph.
	 * Map original index to a Phrase.
	 * Original index is passed in through constructor as an input
	 */
	private List<Phrase> m_phraseParagraph;

	/**
	 * create a Paragraph object to deal with the question
	 *
	 * @param phraseList
	 * @param lineWidth  line width limit, which is shown in the view, in pixel
	 */
	public Paragraph(List<Phrase> phraseList, int lineWidth) {
		m_phraseParagraph = phraseList;
		m_lineWidth = lineWidth;
	}

	/**
	 * parse the current paragraph and return current phrase list as a paragraph
	 *
	 * @return
	 */
	public List<Phrase> parseCurrentParagraph() {
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

	/**
	 * remove a phrase by index and return what is removed as a SimpleEntry
	 *
	 * @param phraseIndexToMove
	 * @return
	 */
	public Phrase removePhrase(int phraseIndexToMove) {
		/// remove the selected one
		Phrase phraseToMove = m_phraseParagraph.remove(phraseIndexToMove);
		return phraseToMove;
	}

	public Phrase removePhraseByPosition(float x, float y, PointF outRelative) {
		for (int i = 0; i < m_phraseParagraph.size(); i++) {
			if (m_phraseParagraph.get(i).contains(x, y, outRelative)) {
				return m_phraseParagraph.remove(i);
			}
		}
		return null;
	}
}
