package com.luke.quizparagraph;

import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luke.quizparagraph.customview.PhraseTextView;
import com.luke.quizparagraph.mvp.MVPActivity;
import com.luke.quizparagraph.mvp.presenter.QuizPresenter;
import com.luke.quizparagraph.mvp.view.IQuizView;
import com.luke.quizparagraph.quiz.anim.AnimatorTool;
import com.luke.quizparagraph.quiz.data.Phrase;
import com.luke.quizparagraph.util.Common;

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

	@BindView(R.id.edit_input_phrase_main)
	EditText m_editNewPhrase;

	/**
	 * list of TextViews to be used for moving and placing
	 * the index in this list is also set in Phrase object, which means the m_originalIndex in Phrase is linked as index in this list
	 */
	private List<PhraseTextView> m_phraseItemViews = new ArrayList<>();
	private Paint m_txtPaint;
	private PhraseTextView m_textViewSelectedPhrase;
	private PhraseTextView m_textViewDottyPhrase;  /// show selected Phrase with dot slash border

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
		m_layoutParagraph.post(() -> {
			prepare();
			createDefaultPhrases();
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@OnClick(R.id.btn_add_phrase_main)
	public void onBtnAddPhraseClick() {
		String phrase = m_editNewPhrase.getText().toString().trim();
		if (TextUtils.isEmpty(phrase)) {
			Common.showToast(getString(R.string.please_input_phrase));
		} else {
			PhraseTextView txtView = createDefaultPhraseTextView();
			txtView.setText(phrase);
			m_phraseItemViews.add(txtView);
			m_layoutParagraph.addView(txtView);
			if (!m_presenter.addPhrase(phrase, m_phraseItemViews.size() - 1, m_txtPaint, m_layoutParagraph.getWidth())) {
				Common.showToast(getString(R.string.phrase_too_long));
			}
		}
		m_editNewPhrase.setText(null);
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
					float currentPhrasePositionX = event.getX() - m_selectedPhrasePosRelativeToTouch.x;
					float currentPhrasePositionY = event.getY() - m_selectedPhrasePosRelativeToTouch.y;
					m_textViewSelectedPhrase.setX(currentPhrasePositionX);
					m_textViewSelectedPhrase.setY(currentPhrasePositionY);
					m_presenter.movePhraseByPosition(currentPhrasePositionX, currentPhrasePositionY);
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (m_textViewSelectedPhrase != null) {
					clearPhraseSelected();
					m_textViewSelectedPhrase = null;
					m_presenter.dockPhrase();
				}
				break;
			default:
				break;
		}
		return super.onTouchEvent(event);
	}

	private void clearPhraseSelected() {
		if (m_textViewSelectedPhrase != null) {
			m_textViewSelectedPhrase.setBackgroundResource(R.drawable.bg_text_phrase_normal);
//			m_textViewSelectedPhrase.setVisibility(View.GONE);
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
		m_textViewDottyPhrase.clearSeparatorViews(m_layoutParagraph);
		m_textViewDottyPhrase.setVisibility(View.INVISIBLE);

		for (Phrase phrase : parsedResult) {
			/// get corresponding TextView
			PhraseTextView textViewAccordingly = m_phraseItemViews.get(phrase.getOriginalIndex());
			/// check if is the selected one
			if (textViewAccordingly == m_textViewSelectedPhrase) {
				/// draw a dotty borderly textview instead
				m_textViewDottyPhrase.setText(phrase.getOriginalString());
				drawPhraseTextView(m_textViewDottyPhrase, phrase, true);
			} else {
				drawPhraseTextView(textViewAccordingly, phrase, false);
			}
		}
	}

	@Override
	public void notifyPhraseAdded(Phrase phrase) {
		PhraseTextView textViewAccordingly = m_phraseItemViews.get(phrase.getOriginalIndex());
		drawPhraseTextView(textViewAccordingly, phrase, false);
	}

	private void drawPhraseTextView(PhraseTextView textViewAccordingly, Phrase phrase, boolean dotty) {
		/// get new position for this view
		int lineNumber = phrase.getLineNumber();
		int columnPosition = phrase.getColumnPosition();
		int separatorIndex = phrase.getSeparatingPosition();
		if (separatorIndex != Phrase.NO_SEPARATING) {
			/// line separator, create separator views and show
			textViewAccordingly.setVisibility(View.GONE);
			/// this is done before getting textViewAccordingly's position
			TextView[] separatorViews = new TextView[]{
				createDefaultSeparatorView(textViewAccordingly),
				createDefaultSeparatorView(textViewAccordingly)
			};
			/// although it is invisible, the position is useful for later animator
			textViewAccordingly.setX(columnPosition);
			textViewAccordingly.setY(lineNumber * Phrase.LINE_HEIGHT);
			textViewAccordingly.setSeparatorViews(m_layoutParagraph, Arrays.asList(separatorViews));
			separatorViews[0].setText(phrase.getSeparatorString());
			separatorViews[1].setText(phrase.getRemainingString());
			if(dotty) {
//				textViewAccordingly.setText(phrase.getOriginalString());
				separatorViews[0].setBackgroundResource(R.drawable.bg_text_separator_left_dot);
				separatorViews[1].setBackgroundResource(R.drawable.bg_text_separator_right_dot);
			} else {
				separatorViews[1].setBackgroundResource(R.drawable.bg_text_separator_right);
			}
			if (AnimatorTool.animatePhraseView(separatorViews[0], columnPosition,
				lineNumber * Phrase.LINE_HEIGHT)) {    /// this checks if animator is necessary
				AnimatorTool.animatePhraseView(separatorViews[1], 0, (lineNumber + 1) * Phrase.LINE_HEIGHT);
			} else {
				separatorViews[1].setX(0);
				separatorViews[1].setY((lineNumber + 1) * Phrase.LINE_HEIGHT);
			}
//				separatorViews[0].setX(columnPosition);
//				separatorViews[0].setY(lineNumber * Phrase.LINE_HEIGHT);
//				separatorViews[1].setX(0);
//				separatorViews[1].setY((lineNumber + 1) * Phrase.LINE_HEIGHT);
		} else {
			/// no line separator
			textViewAccordingly.clearSeparatorViews(m_layoutParagraph);
			textViewAccordingly.setVisibility(View.VISIBLE);
			AnimatorTool.animatePhraseView(textViewAccordingly, columnPosition, lineNumber * Phrase.LINE_HEIGHT);
//				textViewAccordingly.setX(columnPosition);
//				textViewAccordingly.setY(lineNumber * Phrase.LINE_HEIGHT);
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
			txtView.setText(phraseStringList.get(i));
			m_phraseItemViews.add(i, txtView);
			m_layoutParagraph.addView(txtView);
		}

		parseParagraph(phraseStringList, m_txtPaint);
	}

	private void prepare() {
		m_textViewDottyPhrase = createDefaultPhraseTextView();
		m_layoutParagraph.addView(m_textViewDottyPhrase);
		m_textViewDottyPhrase.setVisibility(View.INVISIBLE);   /// hide as default
		m_textViewDottyPhrase.setBackgroundResource(R.drawable.bg_text_phrase_normal_dot);
	}

	public List<Phrase> parseParagraph(List<String> phraseStringList, Paint paint) {
		return m_presenter.parseNewParagraph(phraseStringList, m_layoutParagraph.getWidth(), paint);
	}

	private PhraseTextView createDefaultPhraseTextView() {
		PhraseTextView phraseTextView = (PhraseTextView) getLayoutInflater().inflate(R.layout.layout_text_phrase_view, m_layoutParagraph, false);
		phraseTextView.setVisibility(View.INVISIBLE);   /// hide as default
		if (m_txtPaint == null) {
			m_txtPaint = phraseTextView.getPaint();
		}
		return phraseTextView;
	}

	private TextView createDefaultSeparatorView(PhraseTextView textViewAccordingly) {
		TextView separatorView = (TextView) getLayoutInflater().inflate(R.layout.layout_text_phrase_part_view, m_layoutParagraph, false);
		/// set original position to the same as its corresponding PhraseTextView
		/// this is used when animator occurs
		separatorView.setX(textViewAccordingly.getX());
		separatorView.setY(textViewAccordingly.getY());
		return separatorView;
	}
}
