package es.ull.taro.tourism_core.services;

import org.springframework.stereotype.Service;

import es.ull.taro.tourism_core.vocabularies.HTO;

@Service("gastroService")
public class GastroServiceImpl extends HTOServiceImpl implements GastroService {

	@Override
	protected String getRDFFileName() {
		return "tdt-restauracion.rdf";
	}

	@Override
	protected String getResourceType() {
		return HTO.GATRO;
	}
}
