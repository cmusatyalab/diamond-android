package edu.cmu.cs.diamond.android.examples.facialrecognition;

import java.io.File;

import android.os.Environment;

public class Const {
	public static String SERVER_IP = "128.237.231.129";
	public static File ROOT_DIR = new File(Environment.getExternalStorageDirectory() + File.separator + "Gabriel" + File.separator);
	public static File RESULTS_FILE = new File(ROOT_DIR.getAbsolutePath() + File.separator + "app-results.yaml");
	public static int MAX_TOKEN_SIZE = 2;
	public static final int FRAMES_PER_ENCODING = 100;
	public static final boolean USE_TOKENS = true;
	public static final boolean USE_PRERECORDED = true;
	public static final File PRERECORDED_DIR = new File(
		Environment.getExternalStorageDirectory() + File.separator + "offloading" +
	        File.separator + "prerecorded"
	);

	public static int MIN_FPS = 2;
	public static int IMAGE_WIDTH = 320;
	public static int IMAGE_HEIGHT = 240;
}
