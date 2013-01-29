package com.glutamatt.velibgo;

import java.util.List;

import com.glutamatt.velibgo.models.Station;
import com.glutamatt.velibgo.storage.DaoStation;
import com.glutamatt.velibgo.ui.CirclesOnMapDrawer;
import com.glutamatt.velibgo.ui.StationMarkerManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.content.Intent;
import android.graphics.Point;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements OnMapClickListener {
	
	public static final int SEARCH_SIGHT = 500;
	protected static final String EXTRA_FOCUS_STATION_ID = "glutamatt.velibgo.Main.extra.focusstationid";
	
	GoogleMap mMap;
	Marker locationMarker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.setOnMapClickListener(this);
		mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker clickedWindowMarker) {
				Intent intent = new Intent(MainActivity.this, StationsListActivity.class);
				intent.putExtra(StationsListActivity.EXTRA_FIRST_STATION_ID,
						StationMarkerManager.getMarkerStationId(clickedWindowMarker));
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume() {
		class LoadInit extends AsyncTask<Void, Void, List<Station>>{
			@Override
			protected List<Station> doInBackground(Void... params) {
				return DaoStation.getInstance(getApplicationContext()).getAll();
			}
			@Override
			protected void onPostExecute(List<Station> result) {
				onStationsUpdated(result);
				int focusId = getIntent().getIntExtra(EXTRA_FOCUS_STATION_ID, 0);
				if(focusId > 0)
				{
					Station station = DaoStation.getInstance(getApplicationContext()).find(focusId);
					if(station != null)
						centerMap(new LatLng(station.getLatitude(), station.getLongitude()));
				}
			}
		}
		new LoadInit().execute();
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_display_markers:
			displayStationsByLocation(mMap.getCameraPosition().target, true);
			break;
		case R.id.menu_locate_me:
			centerMapOnLocation();
			break;
		case R.id.menu_refresh:
			syncService.pullFreshData();
			break;
		case R.id.menu_clear:
			StationMarkerManager.clearMarkers();
		default:break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	private void centerMapOnLocation() {
		if(locationMarker == null) {
			Toast.makeText(getApplicationContext(), "Position non disponible", Toast.LENGTH_SHORT).show();
			return ;
		}
		centerMap(locationMarker.getPosition());
	}

	private void centerMap(LatLng location) {
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(
				location.latitude, location.longitude
				)).zoom(16).build()), 800, null);
		displayStationsByLocation(location, false);
	}

	@Override
	public void onLocationChanged(Location l) {
		Location location = l;
		LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
		if(locationMarker == null)
		{
			locationMarker = mMap.addMarker(new MarkerOptions()
				.position(position)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_location))
			);
		}
		animateMarker(locationMarker, position, false) ;
	}
	
	//http://stackoverflow.com/questions/13728041/move-markers-in-google-map-v2-android
	public void animateMarker(final Marker marker, final LatLng toPosition,
            final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

	@Override
	public void onStationsUpdated(List<Station> stations) {
		StationMarkerManager.refreshMarkers(stations,mMap, getResources());
	}

	@Override
	public void onUpdateStart() {
		Toast.makeText(MainActivity.this, "Récupération des données", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onMapClick(LatLng clickPos) {
		displayStationsByLocation(clickPos, true);
	}
	
	private void displayStationsByLocation(LatLng pos, boolean drawCircle)
	{
		class ShowNearByStationsTask extends AsyncTask<LatLng, Void, List<Station>>
		{
			@Override
			protected List<Station> doInBackground(LatLng... params) {
				return DaoStation.getInstance(MainActivity.this).getByCoordonnees(params[0], SEARCH_SIGHT);
			}
			@Override
			protected void onPostExecute(List<Station> result) {
				for(Station station: result)
					StationMarkerManager.displayStationOnMap(station, mMap, getResources());
			}
		}
		if(drawCircle)
			CirclesOnMapDrawer.draw(mMap, pos, SEARCH_SIGHT, getResources());
		new ShowNearByStationsTask().execute(pos);
	}
}
