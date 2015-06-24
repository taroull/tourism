package es.ull.taro.tourism_core.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Resource;

import es.ull.taro.tourism_core.domain.BeachResource;

public interface PlacesService {

	public static final String BEAN_ID = "placesService";

	public List<es.ull.taro.tourism_core.domain.TDTResource> find(String name);

	public List<HashMap<String, String>> findBeachesAround(float latitude, float longitude, int radiusInMeters);

	public Map<String, String> describeUri(String uri);

	public BeachResource createBeachResource(String uri);

	public Resource retrieve(String uri);
}
