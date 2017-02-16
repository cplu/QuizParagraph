package com.luke.quizparagraph.quiz.data;

import java.util.List;

/**
 * Created by cplu on 2017/2/15.
 */

public class Phrase {
//	public static final int SPACE_LENGTH = 1;
	public static final int NO_SEPARATING = -1;
	private final int m_originalIndex;  /// original index in input List<String>
	private final int m_width;         /// width of this phrase
	private final List<Word> m_wordSet;   /// word set of this phrase
	private int m_separatingPosition = NO_SEPARATING;   /// temporary separating position in m_wordSet, NO_SEPARATING means no separating at this phrase
	private int m_lineNumber = 0;       /// temporary line number of this Phrase
	private int m_columnPosition = 0;   /// temporary column position of this Phrase

	public Phrase(int originalIndex, List<Word> wordSet) {
		m_originalIndex = originalIndex;
		int width = 0;
		/// calculate length of all words
		for (Word word : wordSet) {
			width += word.getWidth();
		}
		m_width = width;
		m_wordSet = wordSet;
	}

	public int getOriginalIndex() {
		return m_originalIndex;
	}

	public int getWidth() {
		return m_width;
	}

	public List<Word> getWordSet() {
		return m_wordSet;
	}

	public int getSeparatingPosition() {
		return m_separatingPosition;
	}

	/**
	 * reset separating position
	 */
	public void resetSeparatingPosition() {
		m_separatingPosition = NO_SEPARATING;
	}

	/**
	 * calcuate the separating position
	 *
	 * @param currentWidth  current width in a line, excluding this Phrase, but including a space beforehand
	 * @param widthLimit    width limit in a line
	 * @param lineNumber    current line number
	 */
	public void calculateSeparatingPosition(int currentWidth, int widthLimit, int lineNumber) {
		/// default to NO_SEPARATING, in fact, we usually not go here since this method is called only when (currentLength + m_width > widthLimit)
		m_separatingPosition = NO_SEPARATING;
		m_columnPosition = currentWidth;
		for (int i = 0; i < m_wordSet.size(); i++) {
			if (currentWidth + m_wordSet.get(i).getWidth() > widthLimit) {   /// include a space
				m_separatingPosition = i;
				break;
			}
			currentWidth += m_wordSet.get(i).getWidth();
		}
		/// check and set line number
		if(m_separatingPosition == 0) {
			/// this means the phrase is separated before the first word, so actually it should be moved to a new line
			m_columnPosition = 0;
			m_lineNumber = lineNumber + 1;
		} else {
			m_lineNumber = lineNumber;
		}
	}

	public int getRemainingLengthAfterSeparatingPosition() {
		if (m_separatingPosition == NO_SEPARATING) {
			return m_width;
		}
		/// calculate width after separating index
		int width = 0;
		for (int i = m_separatingPosition; i < m_wordSet.size(); i++) {
			width += m_wordSet.get(i).getWidth();
		}
		return width;
	}

	/**
	 * set line number
	 * @param linenumber
	 */
	public void setLineNumber(int linenumber) {
		m_lineNumber = linenumber;
	}

	public int getLineNumber() {
		return m_lineNumber;
	}

	@Override
	public String toString() {
		return String.format("original: %d length: %d separating: %d line: %d col: %d"
			, m_originalIndex, m_width, m_separatingPosition, m_lineNumber, m_columnPosition);
	}

	public int getColumnPosition() {
		return m_columnPosition;
	}

	public void setColumnPosition(int position) {
		m_columnPosition = position;
	}
}
