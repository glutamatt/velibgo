package com.glutamatt.velibgo.ui;

import java.util.List;

import com.glutamatt.velibgo.R;
import com.glutamatt.velibgo.models.Station;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.SparseArray;

public class StationMarkerManager {
	
	private static final float[] TRANSFORM_ALMOST_FULL = {
        2f, 0, 0, 0, 0,
        0, 1f, 0, 0, 0,
        0, 0, 1f, 0, 0,
        0, 0, 0, 1f, 0};
	private static final float[] TRANSFORM_TOTAL_FULL = {
		4f, 0, 0, 0, 0,
		0, 1f, 0, 0, 0,
		0, 0, 1f, 0, 0,
		0, 0, 0, 1f, 0};
	private static final float[] TRANSFORM_ALMOST_EMPTY = {
        1.1f, 0, 0, 0, 0,
        0, 1.3f, 0, 0, 0,
        0, 0, 1.3f, 0, 0,
        0, 0, 0, 1f, 0};
	private static final float[] TRANSFORM_TOTAL_EMPTY = {
        1.5f, 0, 0, 0, 0,
        0, 2f, 0, 0, 0,
        0, 0, 2f, 0, 0,
        0, 0, 0, 1f, 0};
	
	private static SparseArray<Marker> markers = new SparseArray<Marker>();
	
	
	public static Bitmap baseMarkerBitmap;
	
	public static void displayStationOnMap(final Station station, final GoogleMap map, final Resources res)
	{
		class ImageDecodeur extends AsyncTask<Bitmap, Void, Bitmap>
		{
			private Bitmap baseBitmap;

			@Override
			protected Bitmap doInBackground(Bitmap... params) {
				if(params[0] == null) {
					baseBitmap = BitmapFactory.decodeResource(res, R.drawable.marker_station);
				}
				else {
					baseBitmap = params[0];
				}
				Bitmap bitmap = Bitmap.createBitmap(baseBitmap.getWidth(), baseBitmap.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				canvas.drawBitmap(baseBitmap, 0, 0, getPaintToColor(station));
				Paint paint = new Paint();
				paint.setTextSize(20);
				paint.setColor(Color.BLACK);
				paint.setFlags(Paint.ANTI_ALIAS_FLAG);
				canvas.drawText(String.valueOf(station.getVelosDispo()), 63, 34, paint);
				canvas.drawText(String.valueOf(station.getPlacesDispo()), 63, 58, paint);
				return bitmap;
			}
			
			private Paint getPaintToColor(Station station) {
				float[] colorTransform = null;
				
				if(station.getPlacesDispo() < 3)
					colorTransform = TRANSFORM_ALMOST_FULL;
				if(station.getVelosDispo() < 3)
					colorTransform = TRANSFORM_ALMOST_EMPTY;
				if(station.getVelosDispo() == 0)
					colorTransform = TRANSFORM_TOTAL_EMPTY;
				if(station.getPlacesDispo() == 0)
					colorTransform = TRANSFORM_TOTAL_FULL;
				
				if(colorTransform == null)
					return null;

			    ColorMatrix colorMatrix = new ColorMatrix();
			    colorMatrix.setSaturation(0.5f);
			    colorMatrix.set(colorTransform);

			    ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
			    Paint paint = new Paint();
			    paint.setColorFilter(colorFilter);
				return paint;
			}

			@Override
			protected void onPostExecute(Bitmap bitmap) {
				setBaseMarkerBmp(baseBitmap);
				if (markers.get(station.getId()) != null)
					markers.get(station.getId()).remove();
				
				markers.put(station.getId(), map.addMarker(new MarkerOptions()
						.position(
								new LatLng(station.getLatitude(), station
										.getLongitude())).icon(
								BitmapDescriptorFactory.fromBitmap(bitmap))));
			}
			
		}
		new ImageDecodeur().execute(baseMarkerBitmap);
	}
	
	public static void setBaseMarkerBmp(Bitmap bmp)
	{
		baseMarkerBitmap = bmp;
	}

	public static void refreshMarkers(List<Station> stations, GoogleMap map, Resources res) {
		for(Station station : stations)
		{
			if(markers.get(station.getId()) != null)
				displayStationOnMap(station, map, res);
		}
	}

	public static void clearMarkers() {
		for(int i = 0; i < markers.size(); i++) {
			markers.get(markers.keyAt(i)).remove();
		}
		markers.clear();
	}
}
