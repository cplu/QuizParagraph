package com.luke.quizparagraph.quiz.data;

import android.graphics.PointF;

import java.util.LinkedList;
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
	private LinkedList<Phrase> m_phraseParagraph;

	/**
	 * create empty Paragraph
	 */
	public Paragraph() {
		m_phraseParagraph = new LinkedList<>();
		m_lineWidth = 0;
		parseCurrentParagraph();
	}

	/**
	 * create a Paragraph object to deal with the question
	 *
	 * @param phraseList
	 * @param lineWidth  line width limit, which is shown in the view, in pixel
	 */
	public Paragraph(LinkedList<Phrase> phraseList, int lineWidth) {
		m_phraseParagraph = phraseList;
		m_lineWidth = lineWidth;
		parseCurrentParagraph();
	}

	public List<Phrase> getPhraseList() {
		return m_phraseParagraph;
	}

	public Phrase getPhraseByIndex(int indexFound) {
		if(indexFound >= 0 && indexFound < m_phraseParagraph.size()) {
			return m_phraseParagraph.get(indexFound);
		} else {
			return null;
		}
	}

	/**
	 * parse the current paragraph and return current phrase list as a paragraph
	 *
	 * @return
	 */
	private List<Phrase> parseCurrentParagraph() {
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

	/**
	 * get phrase by checking if it contains (x, y)
	 * @param x
	 * @param y
	 * @param outRelative       output the relative coordinates from (x, y) to the left-top corner of the Phrase
	 * @return
	 */
	public int getPhraseByPosition(float x, float y, PointF outRelative) {
		for (int i = 0; i < m_phraseParagraph.size(); i++) {
			if (m_phraseParagraph.get(i).contains(x, y, outRelative)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * move phrase by checking if (x, y) is attached to a Phrase
	 * @param x
	 * @param y
	 * @param phraseToMove
	 * @return  true if position changed, false if no changing at all
	 */
	public boolean movePhraseByPosition(float x, float y, Phrase phraseToMove) {
		int oldIndex = -1;  /// store index of phraseToMove
		int newIndex = -1;  /// store index of insertion
		for (int i = 0; i < m_phraseParagraph.size(); i++) {
			Phrase phrase = m_phraseParagraph.get(i);
			if (phraseToMove == phrase) {
				oldIndex = i;
			}
			if (phrase.toLeftOf(x, y)) {  /// current phrase is at the left of (x,y)
				newIndex = i + 1;
//				Logger.debug("find new index " + newIndex);
			} else if(phrase.getColumnPosition() == 0
				&& phrase.contains(x,y, new PointF())) {  /// current phrase is near (x, y), this is only checked when column is 0
				newIndex = i;
//				Logger.debug("find new index " + newIndex);
			}
		}
		if(newIndex == -1) {
			/// try to add to the last position
			Phrase lastPhrase = m_phraseParagraph.getLast();
			if(lastPhrase != null && lastPhrase.above(x, y)) {
				newIndex = m_phraseParagraph.size();
			}
		}

		if (oldIndex > 0 && oldIndex == newIndex) {
			/// the same index, no need to move
			return false;
		} else if (newIndex == -1) {
			/// try to remove old one because no position to dock
			if(m_phraseParagraph.remove(phraseToMove)) {
				parseCurrentParagraph();
				return true;
			} else {
				return false;
			}
		} else if (oldIndex == -1) {
			/// add new one
			m_phraseParagraph.add(newIndex, phraseToMove);
			parseCurrentParagraph();
			return true;
		} else {
			/// this is rare case, only when user "skip" from one phrase to another
			m_phraseParagraph.remove(phraseToMove);
			if(newIndex > oldIndex) {
				newIndex--;
			}
			m_phraseParagraph.add(newIndex, phraseToMove);
			parseCurrentParagraph();
			return true;
		}
	}

	/**
	 * make sure phraseToMove exists, otherwise add it to fallbackIndex
	 * @param phraseToMove
	 * @param fallbackIndex
	 * @return  true if phraseToMove already exists, false if a fallback docking is done
	 */
	public boolean assurePhraseExisted(Phrase phraseToMove, int fallbackIndex) {
		for (Phrase phrase : m_phraseParagraph) {
			if (phrase == phraseToMove) {
				return true;
			}
		}
		/// fall back docking
		m_phraseParagraph.add(fallbackIndex, phraseToMove);
		parseCurrentParagraph();
		return false;
	}

	public void addPhrase(Phrase phrase) {
		m_phraseParagraph.addLast(phrase);
		parseCurrentParagraph();
	}

	public int getPhraseCount() {
		return m_phraseParagraph.size();
	}

	public boolean isEmpty() {
		return m_phraseParagraph.isEmpty();
	}
}
