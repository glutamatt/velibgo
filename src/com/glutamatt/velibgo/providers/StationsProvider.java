package com.glutamatt.velibgo.providers;

import java.util.List;
import com.glutamatt.velibgo.io.Network;
import com.glutamatt.velibgo.models.Station;
import com.glutamatt.velibgo.providers.json.JsonStationsParser;
public class StationsProvider {
	
	private Network network;

	public StationsProvider(Network n) {
		network = n ;
	}
	
	public List<Station> getAllStations()
	{
		String jsonStations = network.downloadUrl("http://open-api.madebymonsieur.com/velib/stations?accept=application/json");
		JsonStationsParser parser = new JsonStationsParser();
		List<Station> stations = parser.decode(jsonStations);
		return stations;
	}

}
