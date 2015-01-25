package es.ull.taro.tourism_core.services;

import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Resource;

import es.ull.taro.tourism_core.domain.OfficeResource;


public interface TourismOfficesService {

	public static final String BEAN_ID = "tourismOfficesService";
	
	public HashMap<String, String> find(String name);

	public List<String> findTourismOfficesAround(float latitude, float longitude, int radiusInMeters);

	public HashMap<String, String> describeUri(String uri);

	public OfficeResource createOfficeResource(String uri);

	public Resource retrieve(String uri);
}
