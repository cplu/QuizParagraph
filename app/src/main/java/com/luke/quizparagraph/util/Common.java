package com.luke.quizparagraph.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by cplu on 2017/2/15.
 */

public class Common {
	private static float s_deviceDensity = 0.0f;
	private static Context s_ctx;

	public static String getAppName(Context context) {
		if (context.getApplicationInfo() != null) {
			int stringId = context.getApplicationInfo().labelRes;
			return stringId > 0 ? context.getString(stringId) : "Quiz";
		}
		return "Quiz";
	}

	private static Common m_instance;

	private Common(Context ctx) {
		s_ctx = ctx;
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		s_deviceDensity = dm.density;
	}

	public static synchronized void init(Context ctx) {
		if (m_instance == null) {
			m_instance = new Common(ctx);
		}
	}

	public static Common instance() {
		return m_instance;
	}


	public static int dp2px(int value) {

		if(s_deviceDensity == 0.0f) {
			WindowManager wm = (WindowManager) s_ctx.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			DisplayMetrics dm = new DisplayMetrics();
			display.getMetrics(dm);
			s_deviceDensity = dm.density;
		}
		return (int) (value * s_deviceDensity + 0.5f);  /// add 0.5 for rounding
	}

	private static Toast s_toast;

	public static void showToast(String content) {
		if (s_ctx == null) {
			return;
		}
		if (s_toast == null) {
			s_toast = Toast.makeText(s_ctx, content, Toast.LENGTH_LONG);
		} else {
			//m_toast.cancel();
			s_toast.setText(content);
		}
		s_toast.show();
	}
}
