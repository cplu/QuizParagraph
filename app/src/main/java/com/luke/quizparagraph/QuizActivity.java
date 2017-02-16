package com.luke.quizparagraph;

import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luke.quizparagraph.customview.PhraseTextView;
import com.luke.quizparagraph.mvp.MVPActivity;
import com.luke.quizparagraph.mvp.presenter.QuizPresenter;
import com.luke.quizparagraph.mvp.view.IQuizView;
import com.luke.quizparagraph.quiz.data.Phrase;

import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class QuizActivity extends MVPActivity<IQuizView, QuizPresenter>
	implements IQuizView {

	@BindView(R.id.layout_paragraph_main)
	RelativeLayout m_layoutParagraph;

	/**
	 * list of TextViews to be used for moving and placing
	 * the index in this list is also set in Phrase object, which means the m_originalIndex in Phrase is linked as index in this list
	 */
	private List<PhraseTextView> m_phraseItemViews = new ArrayList<>();
	private Paint m_txtPaint;
	private PhraseTextView m_textViewSelectedPhrase;

	@Override
	protected QuizPresenter createPresenter() {
		return new QuizPresenter();
	}

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
	protected void onPause() {
		super.onPause();
	}

	@OnClick(R.id.btn_add_phrase_main)
	public void onBtnAddPhraseClick() {

	}

	private PointF m_selectedPhrasePosRelativeToTouch = new PointF();

	@OnTouch(R.id.layout_paragraph_main)
	public boolean onLayoutParagraphTouch(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Phrase phrase = m_presenter.selectPhraseByPosition(event.getX(), event.getY(), m_selectedPhrasePosRelativeToTouch);
				if (phrase != null) {
					Logger.debug("selected phrase " + phrase.getOriginalIndex());
					m_textViewSelectedPhrase = m_phraseItemViews.get(phrase.getOriginalIndex());
					setPhraseSelected(m_textViewSelectedPhrase);
					m_textViewSelectedPhrase.setX(event.getX() - m_selectedPhrasePosRelativeToTouch.x);
					m_textViewSelectedPhrase.setY(event.getY() - m_selectedPhrasePosRelativeToTouch.y);
				} else {
					m_textViewSelectedPhrase = null;
				}
				return true;
			case MotionEvent.ACTION_MOVE:
				if (m_textViewSelectedPhrase != null) {
					m_textViewSelectedPhrase.setX(event.getX() - m_selectedPhrasePosRelativeToTouch.x);
					m_textViewSelectedPhrase.setY(event.getY() - m_selectedPhrasePosRelativeToTouch.y);
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				clearPhraseSelected();
				break;
			default:
				break;
		}
		return super.onTouchEvent(event);
	}

	private void clearPhraseSelected() {
		if (m_textViewSelectedPhrase != null) {
			m_textViewSelectedPhrase.setBackgroundResource(R.drawable.bg_text_phrase_normal);
			m_textViewSelectedPhrase.setVisibility(View.GONE);
		}
	}

	private void setPhraseSelected(PhraseTextView relatedView) {
		relatedView.setVisibility(View.VISIBLE);
		relatedView.clearSeparatorViews(m_layoutParagraph);
		relatedView.bringToFront();
		relatedView.setBackgroundResource(R.drawable.bg_text_phrase_selected);
	}

	@Override
	public void notifyPhraseResult(List<Phrase> parsedResult) {
		for (Phrase phrase : parsedResult) {
			/// get corresponding TextView
			PhraseTextView textViewAccordingly = m_phraseItemViews.get(phrase.getOriginalIndex());
			/// check if is the selected one
			if(textViewAccordingly == m_textViewSelectedPhrase) {
				/// do not change the selected phrase
				continue;
			}
			/// get new position for this view
			int lineNumber = phrase.getLineNumber();
			int columnPosition = phrase.getColumnPosition();
			int separatorIndex = phrase.getSeparatingPosition();
			if (separatorIndex != Phrase.NO_SEPARATING) {
				/// line separator, create separator views and show
				textViewAccordingly.setVisibility(View.GONE);
				TextView[] separatorViews = new TextView[]{
					createDefaultTextView(),
					createDefaultTextView()
				};
				textViewAccordingly.setSeparatorViews(m_layoutParagraph, Arrays.asList(separatorViews));
				separatorViews[0].setText(phrase.getSeparatorString());
				separatorViews[0].setX(columnPosition);
				separatorViews[0].setY(lineNumber * Phrase.LINE_HEIGHT);
				separatorViews[1].setText(phrase.getRemainingString());
				separatorViews[1].setX(0);
				separatorViews[1].setY((lineNumber + 1) * Phrase.LINE_HEIGHT);
			} else {
				/// no line separator
				textViewAccordingly.clearSeparatorViews(m_layoutParagraph);
				textViewAccordingly.setVisibility(View.VISIBLE);
				textViewAccordingly.setX(columnPosition);
				textViewAccordingly.setY(lineNumber * Phrase.LINE_HEIGHT);
			}
		}
	}

	private void createDefaultPhrases() {
		List<String> phraseStringList = Arrays.asList(
			getResources().getStringArray(R.array.default_phrase)
		);
		m_phraseItemViews.clear();
		m_layoutParagraph.removeAllViews();
		for (int i = 0; i < phraseStringList.size(); i++) {
			PhraseTextView txtView = createDefaultPhraseTextView();
			if (m_txtPaint == null) {
				m_txtPaint = txtView.getPaint();
			}
			txtView.setText(phraseStringList.get(i));
			m_phraseItemViews.add(i, txtView);
			m_layoutParagraph.addView(txtView);
		}
		parseParagraph(phraseStringList, m_txtPaint);
	}

	public List<Phrase> parseParagraph(List<String> phraseStringList, Paint paint) {
		return m_presenter.parseNewParagraph(phraseStringList, m_layoutParagraph.getWidth(), paint);
	}

	private PhraseTextView createDefaultPhraseTextView() {
		return (PhraseTextView) getLayoutInflater().inflate(R.layout.layout_phrase_text_view, m_layoutParagraph, false);
	}

	private TextView createDefaultTextView() {
		return (TextView) getLayoutInflater().inflate(R.layout.layout_text_view, m_layoutParagraph, false);
	}
}
