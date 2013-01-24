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
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.SparseArray;

public class StationMarkerManager {
	
	private static SparseArray<Marker> markers = new SparseArray<Marker>();
	
	public static void displayStationOnMap(final Station station, final GoogleMap map, final Resources res)
	{
		class ImageDecodeur extends AsyncTask<Void, Void, Bitmap>
		{
			@Override
			protected Bitmap doInBackground(Void... params) {
				Bitmap baseBitmap = BitmapFactory.decodeResource(res, R.drawable.marker_station);
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
				
				if (markers.get(station.getId()) != null)
					markers.get(station.getId()).remove();
				
				markers.put(station.getId(), map.addMarker(new MarkerOptions()
						.position(
								new LatLng(station.getLatitude(), station
										.getLongitude())).icon(
								BitmapDescriptorFactory.fromBitmap(bitmap))));
			}
			
		}
		new ImageDecodeur().execute();
	}

	public static void refreshMarkers(List<Station> stations, GoogleMap map, Resources res) {
		for(Station station : stations)
		{
			if(markers.get(station.getId()) != null)
				displayStationOnMap(station, map, res);
		}
	}

}
