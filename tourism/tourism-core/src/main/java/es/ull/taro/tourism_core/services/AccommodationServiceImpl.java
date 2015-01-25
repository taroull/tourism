package es.ull.taro.tourism_core.services;

import org.springframework.stereotype.Service;

import es.ull.taro.tourism_core.vocabularies.HTO;

@Service("accommodationService")
public class AccommodationServiceImpl extends HTOServiceImpl implements AccommodationService {

	@Override
	protected String getRDFFileName() {
		return "tdtalojamientos.rdf";
	}

	@Override
	protected String getResourceType() {
		return HTO.ACCOMMODATION;
	}
}
