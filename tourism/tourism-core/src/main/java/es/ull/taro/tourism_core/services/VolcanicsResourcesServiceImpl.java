package es.ull.taro.tourism_core.services;

import org.springframework.stereotype.Service;

import es.ull.taro.tourism_core.vocabularies.HTO;

@Service("volcanicResourceService")
public class VolcanicsResourcesServiceImpl extends HTOServiceImpl implements VolcanicsResourcesService {

	@Override
	protected String getRDFFileName() {
		return "recursosVolcanicos.rdf";
	}

	@Override
	protected String getResourceType() {
		// TODO Auto-generated method stub
		return HTO.ATTRACTION;
	}

}
