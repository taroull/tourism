package es.ull.taro.tourism_core.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.jsonldjava.core.JsonLdError;

public interface CoreService {

	public static final String BEAN_ID = "coreService";

	public Map<String, String> retrieve(String uri) throws JsonLdError;

	public List<HashMap<String, String>> retrieveMunicipalityInfo(String htoResourceUri) throws JsonLdError;

	public List<String> retrieveMunicipalityPhotos(String htoResourceUri) throws JsonLdError;

	public List<String> retrievePhotosAround(String htoResourceUri, int radius) throws JsonLdError;

	public List<HashMap<String, String>> retrievePlacesAround(String htoResourceUri, int radius);

	public List<HashMap<String, String>> retrieveTourismOfficesAround(String htoResourceUri, int radius);
	
	public List<HashMap<String, String>> retrieveNaturalMonumentsAround(String htoResourceUri, int radius);
	
	public List<HashMap<String, String>> retrieveHistoricalMonumentsAround(String htoResourceUri, int radius);
	
	public List<HashMap<String, String>> retrieveVolcanicsResourcesAround(String htoResourceUri, int radius);
	
	public List<HashMap<String, String>> retrieveGastroAround(String htoResourceUri, int radius);
	
	public List<HashMap<String, String>> retrieveAccommodationAround(String htoResourceUri, int radius);

	public List<HashMap<String, String>> findPlacesNear(String uri, int radiusInMeters) throws IOException;

	public List<HashMap<String, String>> retrieveBeachesAround(String htoResourceUri, int radius);

	public Object getWeatherPrediction(String uri);

}
