package com.glutamatt.velibgo;

import com.glutamatt.velibgo.services.LocationService;
import com.glutamatt.velibgo.services.LocationService.ILocationServiceListener;
import com.glutamatt.velibgo.services.LocationService.LocationBinder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity implements ILocationServiceListener {
	
	GoogleMap mMap;
	Location location;
	Marker locationMarker;
	
	boolean mLocationServiceBound = false;
	LocationService mLocationService;
	private ServiceConnection mLocationServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mLocationServiceBound = false;
		}
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocationBinder binder = (LocationBinder) service;
			mLocationService = binder.getService();
			mLocationServiceBound = true ;
			mLocationService.addListener(MainActivity.this);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent locationServiceIntent = new Intent(this, LocationService.class);
		bindService(locationServiceIntent, mLocationServiceConnection, BIND_AUTO_CREATE);
		
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if(mLocationServiceBound) unbindService(mLocationServiceConnection);
		mLocationServiceBound = false;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private void centerMapOnLocation() {
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(
				location.getLatitude(), location.getLongitude()
		)).zoom(17).build()));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_locate_me:
			centerMapOnLocation();
			break;
		default:break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onLocationChanged(Location l) {
		location = l;
		LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
		if(locationMarker == null)
		{
			locationMarker = mMap.addMarker(new MarkerOptions()
				.position(position)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_location))
			);
		}
		locationMarker.setPosition(position);
	}

}
