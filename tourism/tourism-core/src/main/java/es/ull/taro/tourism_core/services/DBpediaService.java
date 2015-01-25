package es.ull.taro.tourism_core.services;

import java.util.HashMap;
import java.util.List;

import com.github.jsonldjava.core.JsonLdError;

import es.ull.taro.tourism_core.domain.GeoResource;

public interface DBpediaService extends BaseService {

	public static final String BEAN_ID = "dBpediaService";

	public List<String> retrieveMunicipalityInfo(String postalCode) throws JsonLdError;

	public List<String> retrieveMunicipalityInfoES(String postalCode) throws JsonLdError;

	public List<GeoResource> find(String query) throws JsonLdError;

	public HashMap<String, String> describeUri(String uri);
}
