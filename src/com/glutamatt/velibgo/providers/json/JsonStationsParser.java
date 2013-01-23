package com.glutamatt.velibgo.providers.json;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import com.glutamatt.velibgo.models.Station;

public class JsonStationsParser {
	
	public List<Station> decode(String jsonstring)
	{
		try {
			JSONArray jsonarray = new JSONArray(jsonstring);
			JSONObject jsonstation = null;
			ArrayList<Station> stations = new ArrayList<Station>();
			Station station;
			JSONObject jsonLoc;
			JSONObject jsonState;
			for (int i = 0; i < jsonarray.length(); i++) {
				jsonstation = jsonarray.getJSONObject(i);
				jsonLoc = jsonstation.getJSONObject("loc");
				jsonState = jsonstation.getJSONObject("state");
				station = new Station();
				station.setId(jsonstation.getInt("_id"));
				station.setAdresse(jsonstation.getString("address"));
				station.setNom(jsonstation.getString("name"));
				station.setLatitude(jsonLoc.getDouble("lat"));
				station.setLongitude(jsonLoc.getDouble("lon"));
				
				station.setPlacesDispo(jsonState.getInt("available_slots"));
				station.setVelosDispo(jsonState.getInt("available_bikes"));
				station.setPlacesLocked(jsonState.getInt("locked_slots"));
				station.setPlacesTotal(jsonState.getInt("total_slots"));
				
				stations.add(station);
			}
			return stations;
		} catch (Exception e) {
		}
		return null;
	}

}
