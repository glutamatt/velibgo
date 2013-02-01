package com.glutamatt.velibgo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.glutamatt.velibgo.models.Station;
import com.glutamatt.velibgo.storage.DaoStation;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class StationsListActivity extends BaseActivity {

	protected static final String EXTRA_FIRST_STATION_ID = null;
	private ListView list;
	private boolean showStared;
	private static Location currentLocation;
	private static StationsListAdapter<Station> listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stations_list);
		list = (ListView) findViewById(R.id.view_stations_list);
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adaptorView, View view, int position, long id) {
				Station station = (Station) adaptorView.getItemAtPosition(position);
				Intent intent = new Intent(StationsListActivity.this, MainActivity.class);
				intent.putExtra(MainActivity.EXTRA_FOCUS_STATION_ID, station.getId());
				StationsListActivity.this.startActivity(intent);
			}
		});
		
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(
				StationsListActivity.this,
				R.array.action_stations_list,
				android.R.layout.simple_spinner_dropdown_item);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				showStared = (itemPosition == 1)?true:false;
				loadStations();
				return false;
			}
		});
		
		if(listAdapter != null)
			list.setAdapter(listAdapter);
		else
			loadStations();
	}
	
	private void loadStations() {
		
		class stationsLoader extends AsyncTask<Void, Void, List<Station>>
		{
			@Override
			protected List<Station> doInBackground(Void... params) {
				List<Station> stations;
				if(showStared)
					stations = DaoStation.getInstance(StationsListActivity.this).getStared();
				else
					stations = DaoStation.getInstance(StationsListActivity.this).getAll();
				if(currentLocation != null)
					Collections.sort(stations, new Comparator<Station>() {
						@Override
						public int compare(Station lhs, Station rhs) {
							return stationLocationDistance(lhs) - stationLocationDistance(rhs);
						}
					});
				return stations;
			}

			@Override
			protected void onPostExecute(List<Station> result) {
				listAdapter = new StationsListAdapter<Station>(
						StationsListActivity.this,
						R.layout.activity_stations_list_item,
						R.id.station_item_text1,
						result
				);
				list.setAdapter(listAdapter);
			}
		}
		new stationsLoader().execute();
	}
	
	private static int stationLocationDistance(Station station) {
		if(currentLocation == null) return -1;
		return java.lang.Math.round(getDistances(station)[0]);
	}
	
	private static float[] getDistances(Station station)
	{
		float[] distances = new float[3];
		Location.distanceBetween(
				currentLocation.getLatitude(), currentLocation.getLongitude(),
				station.getLatitude(), station.getLongitude(), 
				distances);
		return distances;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_stations_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		case R.id.menu_refresh:
			syncService.pullFreshData();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private class StationsListAdapter<T> extends ArrayAdapter<T> {
		private List<T> stations;
		public StationsListAdapter(Context context, int resource,
				int textViewResourceId, List<T> result) {
			super(context, resource, textViewResourceId, result);
			stations = result;
		}
		
		public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
			View row_view = super.getView(position, convertView, parent);
			TextView text2 = (TextView) row_view.findViewById(R.id.station_item_text2);
			TextView text3 = (TextView) row_view.findViewById(R.id.station_item_text3);
			ImageView orientation = (ImageView) row_view.findViewById(R.id.station_item_bearing_image);
			CheckBox starCheckbox = (CheckBox) row_view.findViewById(R.id.checkbox_favoris);
			final Station station = (Station) stations.get(position);
			text2.setText(String.valueOf(station.getVelosDispo()) + " vélos dispo");
			if (currentLocation != null) {
				float[] dists = getDistances(station);
				text3.setText(String.valueOf(java.lang.Math.round(dists[0])) + " m");
				orientation.setRotation(dists[1]);
			}
			starCheckbox.setFocusable(false);
			starCheckbox.setChecked(station.isStared());
			starCheckbox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					boolean isChecked = ((CheckBox) arg0).isChecked();
					station.setStared(isChecked);
					DaoStation.getInstance(StationsListActivity.this).save(station);
				}
			});
			return row_view;
		}
	}

	@Override
	public void onLocationChanged(Location l) {
		currentLocation = l;
		loadStations();
	}

	@Override
	public void onUpdateStart() {
		Toast.makeText(getApplicationContext(), "Récupération des données", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStationsUpdated(List<Station> stations) {
		loadStations();
	}
}
