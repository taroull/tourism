package es.ull.taro.tourism_core.services;

import java.util.HashMap;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

import es.ull.taro.tourism_core.domain.HTOResource;

public abstract interface HTOService extends TDTService {
	
	public abstract Resource retrieve(String uri);
	
	public abstract HashMap<Literal, String> describeUri(String uri);

	public abstract HashMap<String, String> find(String query);
	
	public abstract HTOResource createHtoResource(String uri);
}
