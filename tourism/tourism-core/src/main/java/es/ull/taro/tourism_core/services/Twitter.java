package es.ull.taro.tourism_core.services;

import java.util.HashMap;
import java.util.List;

public interface Twitter {
	
	//public List<String> findTwitter(String name);
	public List<HashMap<String, String>> findTwittersNear(float latitude, float longitude, int radius);
}
