package com.glutamatt.velibgo.services;

import java.util.ArrayList;
import java.util.List;


import com.glutamatt.velibgo.io.Network;
import com.glutamatt.velibgo.models.Station;
import com.glutamatt.velibgo.providers.StationsProvider;
import com.glutamatt.velibgo.storage.DaoStation;
import com.glutamatt.velibgo.storage.DatabaseOpenHelper;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

public class SyncService extends Service{

	private IBinder mBinder = new SyncBinder();
	public class SyncBinder extends Binder
	{
		public SyncService getService()
		{
			return SyncService.this;
		}
	}
	
	List<ISyncServerListener> listeners = new ArrayList<SyncService.ISyncServerListener>();
	public interface ISyncServerListener
	{
		public void onUpdateStart();
		public void onStationsUpdated(List<Station> stations);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public void addListener(ISyncServerListener listener)
	{
		listeners.add(listener);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		pullFreshData();
	}

	private static boolean pulling;
	public synchronized void pullFreshData() {
		if(pulling) return;
		pulling = true ;
		class Refresh extends AsyncTask<Void, Void, List<Station>>
		{
			@Override
			protected List<Station> doInBackground(Void... params) {
				Network network = new Network(getApplicationContext());
				if(network.checkNetwork())
				{
					StationsProvider provider = new StationsProvider(network);
					List<Station> stared = DaoStation.getInstance(getApplicationContext()).getStared();
					List<Integer> staredIds = new ArrayList<Integer>();
					List<Station> freshStations = provider.getAllStations();
					if(null == freshStations)
						return DaoStation.getInstance(getApplicationContext()).getAll();
					for(Station star : stared)
						staredIds.add(star.getId());
					for(Station station : freshStations)
						if(staredIds.contains(station.getId()))
								station.setStared(true);
					return freshStations;
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<Station> stations) {
				if(stations == null) return ;
				for (ISyncServerListener listener : listeners) {
					listener.onStationsUpdated(stations);
				}
				persistStations(stations);
				super.onPostExecute(stations);
				pulling = false ;
			}
		}
		for (ISyncServerListener listener : listeners) {
			listener.onUpdateStart();
		}
		new Refresh().execute();
	}
	
	protected static AsyncTask<Station, Void, Station> persistTask;
	private void persistStations(final List<Station> stations) {
		class PersistStation extends AsyncTask<Station, Void, Station>
		{
			@Override
			protected Station doInBackground(Station... params) {
				DaoStation.getInstance(getApplicationContext()).save(params[0]);
				return params[0];
			}
			 @Override
			protected void onPostExecute(Station result) {
				 int i = stations.indexOf(result);
				 if(++i < stations.size())
					 persistTask = new PersistStation().execute(stations.get(i));
			}
		}
		if(persistTask != null)
			persistTask.cancel(true);
		persistTask = new PersistStation().execute(stations.get(0));
	}
	
	@Override
	public void onDestroy() {
		DatabaseOpenHelper.getInstance(getApplicationContext()).close();
		super.onDestroy();
	}
}
