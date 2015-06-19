package es.ull.taro.tourism_core.services;

import java.util.List;
import java.util.Map;

import com.github.jsonldjava.core.JsonLdError;

import es.ull.taro.tourism_core.domain.GeoResource;

public interface DBpediaService extends BaseService {

	public static final String BEAN_ID = "dBpediaService";

	public List<String> retrieveMunicipalityInfo(String postalCode) throws JsonLdError;

	public List<String> retrieveMunicipalityInfoES(String postalCode) throws JsonLdError;

	public List<GeoResource> find(String query) throws JsonLdError;

	public Map<String, String> describeUri(String uri);

	public List<String> retrievePlacesAround(float latitude, float longitude, int radius);
	
}
