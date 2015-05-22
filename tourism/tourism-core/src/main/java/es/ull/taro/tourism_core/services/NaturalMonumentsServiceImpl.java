package es.ull.taro.tourism_core.services;

import org.springframework.stereotype.Service;

import es.ull.taro.tourism_core.vocabularies.HTO;

@Service("naturalMonumentsService")
public class NaturalMonumentsServiceImpl extends HTOServiceImpl implements NaturalMonumentsService {

	@Override
	protected String getRDFFileName() {
		return "monumentosNaturales.rdf";
	}

	@Override
	protected String getResourceType() {
		// TODO Auto-generated method stub
		return HTO.ATTRACTION;
	}


}
