package com.luke.quizparagraph.util;

import android.content.Context;

/**
 * Created by cplu on 2017/2/15.
 */

public class Common {
	public static String getAppName(Context context) {
		if (context.getApplicationInfo() != null) {
			int stringId = context.getApplicationInfo().labelRes;
			return stringId > 0 ? context.getString(stringId) : "Quiz";
		}
		return "Quiz";
	}
}
