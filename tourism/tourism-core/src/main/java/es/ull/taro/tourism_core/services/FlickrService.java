package es.ull.taro.tourism_core.services;

import java.util.List;

public interface FlickrService {

	public static final String BEAN_ID = "flickrService";

	public List<String> findPhotos(String name);

	public List<String> findPhotosNear(float latitude, float longitude, int radius);
}
