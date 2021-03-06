package com.glutamatt.velibgo.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;	
import android.view.animation.AccelerateInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;


public class CirclesOnMapDrawer {
	
	private static BitmapDescriptor circleBmp;

	public static void draw(final GoogleMap mapView, final LatLng latLng, final int radiusM, final Resources res)
	{
		class CircleDrawerTask extends AsyncTask<Void, Void, BitmapDescriptor>
		{
			@Override
			protected BitmapDescriptor doInBackground(Void... params) {
				int d = 1000; // diameter 
				Bitmap bm = Bitmap.createBitmap(d, d, Config.ARGB_8888);
				Canvas c = new Canvas(bm);
				Paint p = new Paint();
				p.setColor(android.graphics.Color.GREEN);
				p.setAlpha(100);
				c.drawCircle(d/2, d/2, d/2, p);
				return BitmapDescriptorFactory.fromBitmap(bm);
			}
			
			@Override
			protected void onPostExecute(BitmapDescriptor bmp) {
				displayBmpToCircleInMap(bmp, latLng, radiusM, mapView);
				circleBmp = bmp;
			}
		}
		
		if(circleBmp == null)
			new CircleDrawerTask().execute();
		else
			displayBmpToCircleInMap(circleBmp, latLng, radiusM, mapView);
	}
	
	private static void displayBmpToCircleInMap(BitmapDescriptor bmp, LatLng latLng, int radiusM, GoogleMap mapView) {
		GroundOverlayOptions options = new GroundOverlayOptions().
	            image(bmp).
	            position(latLng,radiusM*2,radiusM*2).
	            transparency(0.4f);
		final GroundOverlay circle = mapView.addGroundOverlay(options);
		fadeOutCircle(circle);
	}

	private static void fadeOutCircle(final GroundOverlay circle) {
		ValueAnimator animation = ValueAnimator.ofFloat(0, 1);
		animation.setDuration(1000*2);
		animation.setInterpolator(new AccelerateInterpolator(2.0f));
		animation.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Float value = (Float) animation.getAnimatedValue();
				if(value == 1 ) circle.remove();
				circle.setTransparency(value);
			}
		});
		animation.start();
	}
	
}
