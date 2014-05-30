package com.pti.mates;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PhotoGallery extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_photo_gallery);
	
    ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
    ImagePagerAdapter adapter = new ImagePagerAdapter();
    viewPager.setAdapter(adapter);
  }

  private class ImagePagerAdapter extends PagerAdapter {
    private int[] mImages = new int[] {
        R.drawable.uno,
        R.drawable.dos,
        R.drawable.tres,
        R.drawable.cuatro
    };

    @Override
    public int getCount() {
      return mImages.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      Context context = PhotoGallery.this;
      ImageView imageView = new ImageView(context);
      int padding = context.getResources().getDimensionPixelSize(
          R.dimen.padding_medium);
      imageView.setPadding(padding, padding, padding, padding);
      imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      imageView.setImageResource(mImages[position]);
      ((ViewPager) container).addView(imageView, 0);
      
      return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      ((ViewPager) container).removeView((ImageView) object);
    }
  }
 

}

