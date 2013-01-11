package com.glutamatt.velibgo.ui;

import com.glutamatt.velibgo.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import models.Station;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;

public class StationMarker {
	
	Station station;
	private Context context;
	
	public StationMarker(Station pstation, Context pcontext) {
		station = pstation;
		context = pcontext;
	}
	
	public void displayOnMap(final GoogleMap map)
	{
		class ImageDecodeur extends AsyncTask<Void, Void, Bitmap>
		{
			@Override
			protected Bitmap doInBackground(Void... params) {
				Bitmap baseBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_station);
				Bitmap bitmap = Bitmap.createBitmap(baseBitmap.getWidth(), baseBitmap.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				canvas.drawBitmap(baseBitmap, 0, 0, null);
				Paint paint = new Paint();
				paint.setTextSize(20);
				paint.setColor(Color.BLACK);
				paint.setFlags(Paint.ANTI_ALIAS_FLAG);
				canvas.drawText(String.valueOf(station.getVelosDispo()), 63, 34, paint);
				canvas.drawText(String.valueOf(station.getPlacesDispo()), 63, 58, paint);
				return bitmap;
			}
			
			@Override
			protected void onPostExecute(Bitmap bitmap) {
				map.addMarker(new MarkerOptions()
		        	.position(new LatLng(station.getLatitude(), station.getLongitude()))
		        	.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
		        );
			}
			
		}
		new ImageDecodeur().execute();
	}

}