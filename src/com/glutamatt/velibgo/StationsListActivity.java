package com.glutamatt.velibgo;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class StationsListActivity extends BaseActivity implements OnNavigationListener {

	protected static final String EXTRA_FIRST_STATION_ID = null;
	private ListView list;
	
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
		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(
				StationsListActivity.this,
				R.array.action_stations_list,
				android.R.layout.simple_spinner_dropdown_item);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(mSpinnerAdapter, this);
		
		loadStations();
	}
	
	private void loadStations() {
		
		class stationsLoader extends AsyncTask<Void, Void, List<Station>>
		{
			@Override
			protected List<Station> doInBackground(Void... params) {
				return DaoStation.getInstance(StationsListActivity.this).getAll();
			}
			
			@Override
			protected void onPostExecute(List<Station> result) {
				list.setAdapter(new StationsListAdapter<Station>(
						StationsListActivity.this,
						R.layout.activity_stations_list_item,
						R.id.station_item_text1,
						result
				));
			}
		}
		new stationsLoader().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_stations_list, menu);
		return true;
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
			Station station = (Station) stations.get(position);
			text2.setText(String.valueOf(station.getVelosDispo()) + " vélos dispo");
			return row_view;
		};
	}

	@Override
	public void onLocationChanged(Location l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdateStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStationsUpdated(List<Station> stations) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}
}
