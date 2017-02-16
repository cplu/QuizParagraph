package com.luke.quizparagraph.logger;

import android.content.Context;
import android.os.Environment;

import com.luke.quizparagraph.rx.RXThreadTask;
import com.luke.quizparagraph.rx.SingleTask;
import com.luke.quizparagraph.util.Common;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.policies.SizePolicy;
import org.pmw.tinylog.writers.LogcatWriter;
import org.pmw.tinylog.writers.RollingFileWriter;

import java.io.File;
import java.io.IOException;

import rx.schedulers.Schedulers;

public class UnivLogger {

	//    private static Logger logger;
//	public static boolean s_isDebug = false;
	private static String s_appName;
	private static File s_logFilePath;

//    private UnivLogger() {
//    }
//
//    /**
//     * 返回唯一的logger对象
//     *
//     * @return
//     */
//    public static Logger instance() {
//        if (logger == null) {
//            initLog();
//            logger = Logger.getLogger(UnivLogger.class);
//            // logger.debug("init log4j log\n");
//        }
//        return logger;
//    }

	private static void deleteFiles(final String path) {
		RXThreadTask.run(Schedulers.io(), new SingleTask() {
			@Override
			public void onSuccess(Object o) {
				File file = new File(path);
				if (file.exists()) {
					String deleteCmd = "rm -r " + path;
					Runtime runtime = Runtime.getRuntime();
					try {
						runtime.exec(deleteCmd);
					} catch (IOException e) {
					}
				}
			}
		});
	}

	private static void initLog(Context ctx, boolean isDebug) {
		/// remove log dir generated by version 17 or lower
		File old_path = Environment.getExternalStorageDirectory();
		if (old_path != null) {
			String old_log_file = Environment.getExternalStorageDirectory()
			                      + File.separator + s_appName;
			deleteFiles(old_log_file);
		}

		/// create log dir for version 18 or later
		File cacheDir = ctx.getExternalCacheDir();
		if (cacheDir == null) {
			cacheDir = ctx.getCacheDir();
		}
		s_logFilePath = new File(cacheDir, "logs");
		if (!s_logFilePath.exists()) {
			s_logFilePath.mkdirs();
		}

		File log_file = new File(s_logFilePath, s_appName + ".txt");
		if (!log_file.exists()) {
			try {
				log_file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		try {
			Configurator configurator = Configurator.defaultConfig()
				.writer(new RollingFileWriter(log_file.getAbsolutePath(), 3,
					new SizePolicy(1024 * 1024 * 1)  /// File size is limited to 1 MB
				))
				.level(isDebug ? Level.DEBUG : Level.INFO)
				.formatPattern("{date:yyyy-MM-dd HH:mm:ss} [{thread_id}] {class_name}.{method}() {level}: {message}")
				.maxStackTraceElements(15);  /// The maximum number of lines of output stack traces
			if (isDebug) {
				configurator.addWriter(new LogcatWriter(), Level.DEBUG, "[{thread_id}] {class_name}.{method}() {message}");
			}
			configurator.activate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void cleanLogs() {
		if (s_logFilePath != null) {
			for (File f : s_logFilePath.listFiles()) {
				if (f.isFile()) {
					String name = f.getName();
					if (name.startsWith(s_appName)) {
						// do something
						f.delete();
					}
				}
			}
		}
	}

	public static File getLatestLogFile() {
		if (s_logFilePath == null || !s_logFilePath.exists()) {
			return null;
		}

		File log_file = new File(s_logFilePath, s_appName + ".txt");
		if (!log_file.exists()) {
			return null;
		}
		return log_file;
	}

	/**
	 * init logger, should be called after setting iCommon.isDebug
	 *
	 * @param ctx
	 */
	public static void initLogger(Context ctx, boolean isDebug) {
		s_appName = Common.getAppName(ctx);
		initLog(ctx, isDebug);
	}
}
