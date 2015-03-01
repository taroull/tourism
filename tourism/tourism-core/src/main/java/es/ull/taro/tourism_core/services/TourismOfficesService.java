package es.ull.taro.tourism_core.services;

import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Resource;

import es.ull.taro.tourism_core.domain.OfficeResource;

public interface TourismOfficesService {

	public static final String BEAN_ID = "tourismOfficesService";

	public List<es.ull.taro.tourism_core.domain.Resource> find(String name);

	public List<String> findTourismOfficesAround(float latitude, float longitude, int radiusInMeters);

	public Map<String, String> describeUri(String uri);

	public OfficeResource createOfficeResource(String uri);

	public Resource retrieve(String uri);
}
