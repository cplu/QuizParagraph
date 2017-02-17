package com.luke.quizparagraph.quiz.data;

import android.graphics.PointF;

import com.luke.quizparagraph.util.Common;

import java.util.List;

/**
 * Created by cplu on 2017/2/15.
 */

public class Phrase {
	public static final String SPACE_STRING = " ";
	public static final int LINE_HEIGHT = Common.dp2px(40);
	//	public static final int SPACE_LENGTH = 1;
	public static final int NO_SEPARATING = -1;
	private static final float POINT_NEAR_LIMIT = Common.dp2px(6);
//	public static final int PHRASE_LENGTH_LIMIT = 28;
	private final int m_originalIndex;  /// original index in input List<String>
	private final int m_width;         /// width of this phrase
	private final List<Word> m_wordSet;   /// word set of this phrase

	/// all following fields are temporarily set when parsing a paragraph
	private int m_separatingPosition = NO_SEPARATING;   /// temporary separating position in m_wordSet, NO_SEPARATING means no separating at this phrase
	private int m_lineNumber = 0;       /// temporary line number of this Phrase
	private int m_columnPosition = 0;   /// temporary column position of this Phrase
	private String m_separatorString;   /// separator string (temporarily)
	private String m_remainingString;   /// remaining string after separating position (temporarily)
	private int m_remainingWidth;       /// remaining width after separating position (temporarily)

	public Phrase(int originalIndex, List<Word> wordSet) {
//		m_originalIndex = originalIndex;
		int width = 0;
		/// calculate length of all words
		for (Word word : wordSet) {
			width += word.getWidth();
		}
		m_width = width;
		m_wordSet = wordSet;
		m_originalIndex = originalIndex;
	}
//
//	public int getOriginalIndex() {
//		return m_originalIndex;
//	}

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
		m_separatorString = null;
		m_remainingString = null;
		m_remainingWidth = 0;
	}

	/**
	 * calculate the separating position
	 *
	 * @param currentWidth current width in a line, excluding this Phrase, but including a space beforehand
	 * @param widthLimit   width limit in a line
	 * @param lineNumber   current line number
	 */
	public void calculateSeparatingPosition(int currentWidth, int widthLimit, int lineNumber) {
		/// default to NO_SEPARATING
		/// Since this method is called only when (currentLength + m_width > widthLimit), NO_SEPARATING is never the case after the for loop
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
		if (m_separatingPosition == 0) {
			/// this means the phrase is separated before the first word, so actually it should be moved to a new line
			m_columnPosition = 0;
			m_lineNumber = lineNumber + 1;
			m_separatingPosition = NO_SEPARATING;
		} else {
			m_lineNumber = lineNumber;
		}
		generateSeparatorString();
		generateRemainingString();
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
		m_remainingWidth = width;
		return width;
	}

	/**
	 * set line number
	 *
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
		return String.format("length: %d separating: %d line: %d col: %d"
			, m_width, m_separatingPosition, m_lineNumber, m_columnPosition);
	}

	public int getColumnPosition() {
		return m_columnPosition;
	}

	public void setColumnPosition(int position) {
		m_columnPosition = position;
	}

	/**
	 * get separator string before separating position
	 */
	private void generateSeparatorString() {
		if (m_separatingPosition == NO_SEPARATING) {
			m_separatorString = null;
		} else {
			StringBuilder resultSB = new StringBuilder();
			for (int i = 0; i < m_separatingPosition; i++) {
				resultSB.append(m_wordSet.get(i).getWordString());
				resultSB.append(SPACE_STRING);
			}
			m_separatorString = resultSB.toString().trim();
		}
	}

	private void generateRemainingString() {
		if (m_separatingPosition == NO_SEPARATING) {
			m_remainingString = null;
		} else {
			StringBuilder resultSB = new StringBuilder();
			for (int i = m_separatingPosition; i < m_wordSet.size(); i++) {
				resultSB.append(m_wordSet.get(i).getWordString());
				resultSB.append(SPACE_STRING);
			}
			m_remainingString = resultSB.toString().trim();
		}
	}

	public int getOriginalIndex() {
		return m_originalIndex;
	}

	/**
	 * check if this Phrase contains (x, y), be careful to handle separating
	 *
	 * @param x
	 * @param y
	 * @param outRelative
	 * @return
	 */
	public boolean contains(float x, float y, PointF outRelative) {
		if (m_separatingPosition != NO_SEPARATING) {
			/// check if in the remaining part if separating exists
			if (x >= 0 && x < m_remainingWidth
			    && y >= (m_lineNumber + 1) * LINE_HEIGHT
			    && y < (m_lineNumber + 2) * LINE_HEIGHT) {
				outRelative.set(m_width - m_remainingWidth + x
					, y - (m_lineNumber + 1) * LINE_HEIGHT);
				return true;
			}
		}
		/// check if in the main part, or in the separator part if separating exists
		outRelative.set(x - m_columnPosition
			, y - m_lineNumber * LINE_HEIGHT);
		return x >= m_columnPosition && x < (m_columnPosition + m_width - m_remainingWidth)
		       && y >= m_lineNumber * LINE_HEIGHT && y < (m_lineNumber + 1) * LINE_HEIGHT;
	}

	public String getSeparatorString() {
		return m_separatorString;
	}

	public String getRemainingString() {
		return m_remainingString;
	}

	/**
	 * check if this Phrase's left-top corner is near (x, y)
	 * Note that this only check the "first part" if separating exists
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isAttachedTo(float x, float y) {
		return isPointNear(x, y, m_columnPosition, m_lineNumber * LINE_HEIGHT);
	}

	private boolean isPointNear(float x1, float y1, float x2, float y2) {
		return Math.abs(x1 - x2) < POINT_NEAR_LIMIT
		       && Math.abs(y1 - y2) < POINT_NEAR_LIMIT;
	}
}
