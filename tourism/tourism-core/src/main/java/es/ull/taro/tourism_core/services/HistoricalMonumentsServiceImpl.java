package es.ull.taro.tourism_core.services;

import org.springframework.stereotype.Service;

import es.ull.taro.tourism_core.vocabularies.HTO;

@Service("historicalMonumentsService")
public class HistoricalMonumentsServiceImpl extends HTOServiceImpl implements HistoricalMonumentsService   {

	@Override
	protected String getRDFFileName() {
		return "monumentosHistoricos.RDF";
	}

	@Override
	protected String getResourceType() {
		// TODO Auto-generated method stub
		return HTO.ATTRACTION;
	}

}
