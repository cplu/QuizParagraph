package com.luke.quizparagraph;

import android.graphics.Paint;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luke.quizparagraph.mvp.MVPActivity;
import com.luke.quizparagraph.mvp.presenter.QuizPresenter;
import com.luke.quizparagraph.mvp.view.IQuizView;
import com.luke.quizparagraph.quiz.data.Phrase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizActivity extends MVPActivity<IQuizView, QuizPresenter>
	implements IQuizView {

	@BindView(R.id.layout_paragraph_main)
	RelativeLayout m_layoutParagraph;

	/**
	 * list of TextViews to be used for moving and placing
	 * the index in this list is also set in Phrase object, which means the m_originalIndex in Phrase is linked as index in this list
	 */
	private List<TextView> m_phraseItemViews = new ArrayList<>();
	private Paint m_txtPaint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz);
		ButterKnife.bind(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		m_layoutParagraph.post(this::createDefaultPhrases);
	}

	@Override
	protected QuizPresenter createPresenter() {
		return new QuizPresenter();
	}

	private void createDefaultPhrases() {
		List<String> phraseList = Arrays.asList(
			"How",
			"are",
			"you?",
			"Make sure",
			"what",
			"is",
			"going on"
		);

		for (int i = 0; i < phraseList.size(); i++) {
			TextView txtView = (TextView) getLayoutInflater().inflate(R.layout.layout_phrase, m_layoutParagraph, false);
			if (m_txtPaint == null) {
				m_txtPaint = txtView.getPaint();
			}
			txtView.setText(phraseList.get(i));
			m_phraseItemViews.add(i, txtView);
			m_layoutParagraph.addView(txtView);

		}
		m_presenter.parseNewParagraph(phraseList, m_layoutParagraph.getWidth(), m_txtPaint);
	}

	@Override
	public void notifyPhraseResult(List<Phrase> parsedResult) {
		for (Phrase phrase : parsedResult) {
			/// get corresponding TextView
			TextView textViewAccordingly = m_phraseItemViews.get(phrase.getOriginalIndex());
			/// get new position for this view
			int lineNumber = phrase.getLineNumber();
			int columnPosition = phrase.getColumnPosition();
			textViewAccordingly.setX(columnPosition);
			textViewAccordingly.setY(lineNumber * 50);
		}
	}
}
