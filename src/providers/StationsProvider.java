package providers;

import java.util.List;
import providers.json.JsonStationsParser;
import com.glutamatt.velibgo.io.Network;
import models.Station;
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
