package com.glutamatt.velibgo.services;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service{
	
	private IBinder mBinder = new LocationBinder();
	private List<ILocationServiceListener> listeners = null;
	
	public class LocationBinder extends Binder
	{
		public LocationService getService(){
			return LocationService.this;
		}
	}
	
	public interface ILocationServiceListener
	{
		public void onLocationChanged(Location l); 
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location location;

	
	@Override
	public void onCreate() {
		listeners = new ArrayList<ILocationServiceListener>();
		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE) ;
		locationListener = new LocationListener() {
			@Override
			public synchronized void onStatusChanged(String provider, int status, Bundle extras) {}
			@Override
			public synchronized void onProviderEnabled(String provider) {}
			@Override
			public synchronized void onProviderDisabled(String provider) {}
			@Override
			public synchronized void onLocationChanged(Location plocation) {
				location = plocation;
				for (ILocationServiceListener listener : listeners) {
					listener.onLocationChanged(location);
				}
			}
		};
		if (locationManager.getProviders(false).contains(LocationManager.NETWORK_PROVIDER))
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 50, locationListener);
		if (locationManager.getProviders(false).contains(LocationManager.GPS_PROVIDER))
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
		super.onCreate();
	}
	
	public void addListener(ILocationServiceListener listener)
	{
		listeners.add(listener);
	}

}
