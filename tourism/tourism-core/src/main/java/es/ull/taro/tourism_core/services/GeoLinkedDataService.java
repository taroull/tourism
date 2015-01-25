package es.ull.taro.tourism_core.services;

import java.util.List;

public interface GeoLinkedDataService {

	public static final String BEAN_ID = "geoLinkedDataService";

	public List<String> retrievePlacesAround(float latitude, float longitude, int radius);
}
