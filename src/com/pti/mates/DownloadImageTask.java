package com.pti.mates;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;


public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
  int MAX_IMAGE_SIZE = 320;
  ImageView bmImage;
  Context context;

  public DownloadImageTask(ImageView iv, Context ctx) {
      this.bmImage = iv;
      context=ctx;
  }
  
  protected Bitmap doInBackground(String... urls) {
      String urldisplay = urls[0];
      Bitmap mIcon11 = null;
      try {
        InputStream in = new java.net.URL(urldisplay).openStream();
        mIcon11 = BitmapFactory.decodeStream(in);
      } catch (Exception e) {
          Log.e("Error", e.getMessage());
          e.printStackTrace();
      }
      return mIcon11;
  }

  protected void onPostExecute(Bitmap result) {
	  /*WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	  Display display = wm.getDefaultDisplay();
	  Point size = new Point();
	  display.getSize(size);
	  int width = size.x;
	  int height = size.y;
	  Bitmap scaledBitmap = scaleDown(result, width, true);*/
      bmImage.setImageBitmap(result);
      bmImage.setVisibility(View.VISIBLE);
      
  }
  
  public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
	        boolean filter) {
	    float ratio = Math.min(
	            (float) maxImageSize / realImage.getWidth(),
	            (float) maxImageSize / realImage.getHeight());
	    int width = Math.round((float) ratio * realImage.getWidth());
	    int height = Math.round((float) ratio * realImage.getHeight());

	    Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
	            height, filter);
	    return newBitmap;
	}
}