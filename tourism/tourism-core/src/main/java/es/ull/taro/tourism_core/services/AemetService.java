package es.ull.taro.tourism_core.services;

import java.util.HashMap;
import java.util.List;

public interface AemetService {

	public static final String BEAN_ID = "aemetService";

	public List<String> findWeatherStationsAround(float latitude, float longitude, int radiusInMeters);

	public HashMap<String, String> showWeatherStationProps(String uri);

	public Object getWeatherPrediction(String postalCode);
}
