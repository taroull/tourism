package es.ull.taro.tourism_core.services;

import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Resource;

import es.ull.taro.tourism_core.domain.HTOResource;

public abstract interface HTOService extends TDTService {

	public abstract Resource retrieve(String uri);

	public abstract Map<String, String> describeUri(String uri);

	public abstract List<HTOResource> find(String query);

	public abstract HTOResource createHtoResource(String uri);
}
