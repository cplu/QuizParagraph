package com.luke.quizparagraph;

import android.app.Application;

import com.luke.quizparagraph.logger.UnivLogger;

/**
 * Created by cplu on 2015/10/22.
 */
public class QuizApplication extends Application {
//	public static boolean inForeground = true;
//	protected boolean m_isMainProcess = false; /// record if current running process is main process (main activity runs on this process)
//	private static final String SUB_PROCESS_SUFFIX = "_wft_subprocess";

	@Override
	public void onCreate() {
		super.onCreate();

		/// init logger level
		UnivLogger.initLogger(this, BuildConfig.DEBUG);
	}
}
