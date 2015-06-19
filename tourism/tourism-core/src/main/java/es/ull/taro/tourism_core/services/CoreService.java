package es.ull.taro.tourism_core.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.github.jsonldjava.core.JsonLdError;

import es.ull.taro.tourism_core.domain.Resource;

public interface CoreService {

	public static final String BEAN_ID = "coreService";

	public Map<String, String> retrieve(String uri) throws JsonLdError;

	public List<String> retrieveMunicipalityInfo(String htoResourceUri) throws JsonLdError;

	public List<String> retrieveMunicipalityPhotos(String htoResourceUri) throws JsonLdError;

	public List<String> retrievePhotosAround(String htoResourceUri, int radius) throws JsonLdError;

	public List<String> retrievePlacesAround(String htoResourceUri, int radius);

	public List<String> retrieveTourismOfficesAround(String htoResourceUri, int radius);
	
	public List<String> retrieveNaturalMonumentsAround(String htoResourceUri, int radius);
	
	public List<String> retrieveHistoricalMonumentsAround(String htoResourceUri, int radius);
	
	public List<String> retrieveVolcanicsResourcesAround(String htoResourceUri, int radius);
	
	public List<String> retrieveGastroAround(String htoResourceUri, int radius);
	
	public List<String> retrieveAccommodationAround(String htoResourceUri, int radius);

	public List<String> findPlacesNear(String uri, int radiusInMeters) throws IOException;

	public List<String> retrieveBeachesAround(String htoResourceUri, int radius);

	public Object getWeatherPrediction(String uri);

}
