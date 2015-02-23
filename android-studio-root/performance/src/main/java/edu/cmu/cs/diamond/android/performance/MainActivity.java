package edu.cmu.cs.diamond.android.performance;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import edu.cmu.cs.diamond.android.Filter;
import edu.cmu.cs.diamond.android.FilterException;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {
    private final String TAG = this.getClass().getSimpleName();

    private Context context;

    private Thread processingThread = new Thread() {
        public void run() {
            InputStream ocvXmlIS = context.getResources().openRawResource(R.raw.haarcascade_frontalface);
            InputStream exampleImagesIS = context.getResources().openRawResource(R.raw.filters_zip);
            Filter rgbFilter, dogFilter, gaborFilter, imgDiffFilter, faceFilter, rgbHistFilter,
                    shinglingFilter;
            try {
                rgbFilter = new Filter(R.raw.rgbimg, context, "RGB", null, null);

                byte[] ocvXml = IOUtils.toByteArray(ocvXmlIS);
                byte[] exampleImages = IOUtils.toByteArray(exampleImagesIS);

                // scale, box_width, box_height, step, min_matches, max_distance, num_channels, metric
                String[] dogArgs = {"0.5", "24", "24", "2", "1", "0.5", "3", "pairwise"};
                dogFilter = new Filter(R.raw.dog_texture, context, "DoG", dogArgs, exampleImages);

                // xdim, ydim, step, min_matches, max_distance,
                //   num_angles, num_freq, radius, max_freq, min_freq
                String[] gaborArgs = {"24", "24", "2", "1", "0.5", "3", "2", "2", "2", "0.5", "0.5"};
                gaborFilter = new Filter(R.raw.gabor_texture, context, "gabor", gaborArgs, exampleImages);

                // TODO: imgDiff

                // xsize, ysize, stride, support
                String[] faceFilterArgs = {"1.2", "24", "24", "1", "2"};
                faceFilter = new Filter(R.raw.ocv_face, context, "OCVFace",
                        faceFilterArgs, ocvXml);

                // TODO: rgbHist

                // TODO: shingling
            } catch (IOException e1) {
                Log.e(TAG, "Unable to create filter subprocess.");
                e1.printStackTrace();
                return;
            }
            Filter[] profilingFilters = {faceFilter, dogFilter}; //gaborFilter};

            PrintWriter results;
            try {
                results = new PrintWriter("/sdcard/diamond-performance.csv");
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Unable to create result writer.");
                e.printStackTrace();
                return;
            }

            File imgDir = new File("/sdcard/public-domain-landscapes/");
            ArrayList<byte[]> rgbImages = new ArrayList<byte[]>();
            ArrayList<Long> durations = new ArrayList<Long>();
            ArrayList<String> imgNames = new ArrayList<String>();
            final Map<String,byte[]> m = new HashMap<String,byte[]>();
            for (String relImgStr : imgDir.list()) {
                try {
                    String imgStr = imgDir.getAbsolutePath()+"/"+relImgStr;
                    byte[] imgBytes = FileUtils.readFileToByteArray(new File(imgStr));
                    long start = System.currentTimeMillis();
                    m.put("",imgBytes);
                    rgbFilter.process(m);
                    byte[] rgbImage = m.get("_rgb_image.rgbimage");
                    rgbImages.add(rgbImage);
                    imgNames.add(relImgStr);
                    durations.add(System.currentTimeMillis()-start);
                    break; // TODO
                } catch (Exception e) {
                    Log.e(TAG, "Unable to read file: "+relImgStr);
                    e.printStackTrace();
                    return;
                }
            }

            String resultStr = imgNames.toString();
            resultStr = resultStr.substring(1,resultStr.length()-1);
            results.println("imgNames, "+resultStr);
            resultStr = durations.toString();
            resultStr = resultStr.substring(1,resultStr.length()-1);
            results.println("rgbImg, "+resultStr);

            durations.clear();
            for (Filter filter : profilingFilters) {
                for (byte[] img : rgbImages) {
                    try {
                        m.put("_rgb_image.rgbimage", img);
                        long start = System.currentTimeMillis();
                        filter.process(m);
                        durations.add(System.currentTimeMillis()-start);
                    } catch (Exception e) {
                        Log.e(TAG, "Error running filter");
                        e.printStackTrace();
                        return;
                    }
                }
                resultStr = durations.toString();
                resultStr = resultStr.substring(1,resultStr.length()-1);
                results.println(filter.name+", "+resultStr);
            }
            results.close();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();

        if (!processingThread.isAlive()) processingThread.start();
    }
}
