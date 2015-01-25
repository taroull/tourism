package es.ull.taro.tourism_core.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.github.jsonldjava.core.JsonLdError;

public interface CoreService {

	public static final String BEAN_ID = "coreService";

	public Object retrieve(String uri) throws JsonLdError;
	
	public Object retrieve2(String uri) throws JsonLdError;

	public List<String> retrieveMunicipalityInfo(String htoResourceUri) throws JsonLdError;

	public List<String> retrieveMunicipalityPhotos(String htoResourceUri) throws JsonLdError;

	public List<String> retrievePhotosAround(String htoResourceUri, int radius) throws JsonLdError;

	public List<String> retrievePlacesAround(String htoResourceUri, int radius);

	public List<String> retrieveTourismOfficesAround(String htoResourceUri, int radius);
	
	public HashMap<String, String> findPlacesNear(String uri, int radiusInMeters) throws IOException; 

	public List<String> retrieveBeachesAround(String htoResourceUri, int radius);

}
