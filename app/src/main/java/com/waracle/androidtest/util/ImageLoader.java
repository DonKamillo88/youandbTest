package com.waracle.androidtest.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;

/**
 * Created by Riad on 20/05/2015.
 */
public class ImageLoader {

    private static final String TAG = ImageLoader.class.getSimpleName();

    private static MemoryCache memoryCache = new MemoryCache();

    public ImageLoader() { /**/ }

    /**
     * Simple function for loading a bitmap image from the web
     *
     * @param url       image url
     * @param imageView view to set image too.
     */
    public void load(final String url, final ImageView imageView) {
        if (TextUtils.isEmpty(url)) {
            throw new InvalidParameterException("URL is empty!");
        }

        Bitmap bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    return loadImageData(url);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                Bitmap bmp = convertToBitmap((byte[]) o);
                memoryCache.put(url, bmp);
                setImageView(imageView, bmp);
            }
        };
        task.execute();

    }


    private static byte[] loadImageData(String url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        InputStream inputStream = null;
        try {
            try {
                // Read data from workstation
                inputStream = connection.getInputStream();
            } catch (IOException e) {
                // Read the error from the workstation
                inputStream = connection.getErrorStream();
            }

            // Can you think of a way to make the entire
            // HTTP more efficient using HTTP headers??


            return StreamUtils.readUnknownFully(inputStream);
        } finally {
            // Close the input stream if it exists.
            StreamUtils.close(inputStream);

            // Disconnect the connection
            connection.disconnect();
        }
    }

    private static Bitmap convertToBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private static void setImageView(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}