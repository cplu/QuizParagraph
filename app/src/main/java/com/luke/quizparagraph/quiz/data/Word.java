package com.luke.quizparagraph.quiz.data;

/**
 * Created by cplu on 2017/2/16.
 * Note：
 *   Word contains a space suffix to it. Space is not included in m_wordString,
 *   But it is measured when measureText is called, so m_width contains the space
 */

public class Word {
	private String m_wordString;  /// word string
	private int m_width;         /// width of the word, in pixel, NOT IN STRING LENGTH!!!

	public Word(String wordString, int width) {
		m_wordString = wordString;
		m_width = width;
	}

	public String getWordString() {
		return m_wordString;
	}

	public void setWordString(String wordString) {
		this.m_wordString = wordString;
	}

	public int getWidth() {
		return m_width;
	}

	public void setWidth(int width) {
		this.m_width = width;
	}
}
