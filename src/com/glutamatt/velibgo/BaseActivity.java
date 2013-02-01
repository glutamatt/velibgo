package com.glutamatt.velibgo;

import com.glutamatt.velibgo.services.LocationService;
import com.glutamatt.velibgo.services.LocationService.ILocationServiceListener;
import com.glutamatt.velibgo.services.SyncService;
import com.glutamatt.velibgo.services.LocationService.LocationBinder;
import com.glutamatt.velibgo.services.SyncService.ISyncServerListener;
import com.glutamatt.velibgo.services.SyncService.SyncBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public abstract class BaseActivity extends Activity implements ILocationServiceListener, ISyncServerListener {
	
	boolean mLocationServiceBound = false;
	LocationService mLocationService;
	protected ServiceConnection mLocationServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mLocationServiceBound = false;
		}
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocationBinder binder = (LocationBinder) service;
			mLocationService = binder.getService();
			mLocationServiceBound = true ;
			mLocationService.addListener(BaseActivity.this);
		}
	};
	
	SyncService syncService;
	boolean mSyncServiceBound = false;
	protected ServiceConnection mSyncServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mSyncServiceBound = false;
		}
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			syncService = ((SyncBinder) service).getService();
			mSyncServiceBound = true;
			syncService.addListener(BaseActivity.this);
		}
	};
	
	protected void onResume() {
		Intent locationServiceIntent = new Intent(this, LocationService.class);
		bindService(locationServiceIntent, mLocationServiceConnection, BIND_AUTO_CREATE);
		
		Intent syncServiceIntent = new Intent(this, SyncService.class);
		bindService(syncServiceIntent, mSyncServiceConnection, BIND_AUTO_CREATE);
		super.onResume();
	};
	
	protected void onStop() {
		super.onStop();
		if(mLocationServiceBound) unbindService(mLocationServiceConnection);
		mLocationServiceBound = false;
		super.onStop();
	}
	
	protected void onPause() {
		if(mLocationServiceBound)
		{
			mLocationServiceBound = false;
			unbindService(mLocationServiceConnection);
		}
		/*
		if(mSyncServiceBound)
		{
			unbindService(mSyncServiceConnection);
			mSyncServiceBound = false;
		}*/
		super.onPause();
	}
}
