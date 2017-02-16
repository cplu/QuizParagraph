package com.luke.quizparagraph;

import android.graphics.Paint;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.TextView;

import com.luke.quizparagraph.quiz.data.Paragraph;
import com.luke.quizparagraph.quiz.data.Phrase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ParagraphTest {

	@Rule
	public ActivityTestRule<QuizActivity> mActivityRule = new ActivityTestRule<>(
		QuizActivity.class);

	@Test
	public void testParagraph() throws Exception {
//		Context appContext = InstrumentationRegistry.getTargetContext();
		TextView textView = (TextView) mActivityRule.getActivity().getLayoutInflater().inflate(R.layout.layout_phrase, null);
		Paint paint = textView.getPaint();

		List<String> phraseList = Arrays.asList(
			"We",
			"are",
			"in charge of",
			"the task",
			"We",
			"are",
			"in charge of",
			"the task"
		);
		Paragraph paragraph = new Paragraph(phraseList, 100, paint);
		List<Phrase> parsedList = paragraph.parseCurrent();
		for (Phrase phrase : parsedList) {
			System.out.println("ParagraphTest " + phrase.toString());
		}
		assertEquals(parsedList.get(2).getLineNumber(), 0);
		assertEquals(parsedList.get(2).getSeparatingPosition(), Phrase.NO_SEPARATING);
		assertEquals(parsedList.get(2).getColumnPosition(), 7);
		assertEquals(parsedList.get(3).getLineNumber(), 1);
		assertEquals(parsedList.get(3).getColumnPosition(), 0);
		assertEquals(parsedList.get(6).getLineNumber(), 1);
		assertEquals(parsedList.get(6).getSeparatingPosition(), 1);
		assertEquals(parsedList.get(6).getColumnPosition(), 16);
		assertEquals(parsedList.get(7).getLineNumber(), 2);
	}
}
