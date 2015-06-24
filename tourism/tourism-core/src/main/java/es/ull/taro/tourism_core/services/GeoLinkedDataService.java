package es.ull.taro.tourism_core.services;

import java.util.HashMap;
import java.util.List;

public interface GeoLinkedDataService {

	public static final String BEAN_ID = "geoLinkedDataService";

	public List<HashMap<String, String>> retrievePlacesAround(float latitude, float longitude, int radius);
}
