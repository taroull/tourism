package es.ull.taro.tourism_core.utils;

import static es.ull.taro.tourism_core.domain.TDTResourceType.ACCOMMODATION;
import static es.ull.taro.tourism_core.domain.TDTResourceType.GASTRO;
import static es.ull.taro.tourism_core.domain.TDTResourceType.OFFICE;
import static es.ull.taro.tourism_core.domain.TDTResourceType.BEACH;
import static es.ull.taro.tourism_core.domain.TDTResourceType.NOTDT;
import static es.ull.taro.tourism_core.vocabularies.TurismoDeTenerife.ACCOMMODATION_BASE_URI;
import static es.ull.taro.tourism_core.vocabularies.TurismoDeTenerife.GASTRO_BASE_URI;
import static es.ull.taro.tourism_core.vocabularies.TurismoDeTenerife.OFFICE_BASE_URI;
import static es.ull.taro.tourism_core.vocabularies.TurismoDeTenerife.BEACH_BASE_URI;

import org.apache.commons.lang.StringUtils;

import es.ull.taro.tourism_core.domain.TDTResourceType;

public class Utils {

	public static TDTResourceType resolveTdtResourceType(String uri) {
		if (StringUtils.startsWith(uri, GASTRO_BASE_URI)) {
			return GASTRO;
		} else if (StringUtils.startsWith(uri, ACCOMMODATION_BASE_URI)) {
			return ACCOMMODATION;
		} else if (StringUtils.startsWith(uri, OFFICE_BASE_URI)) {
			return OFFICE;
		} else if (StringUtils.startsWith(uri, BEACH_BASE_URI)) {
			return BEACH;
		} else {
			return NOTDT;
		}
	}

	public static String getNameFromDbpediaURI(String dbpediaUri) {
		if (StringUtils.isNotBlank(dbpediaUri)) {
			String[] parts = dbpediaUri.split("/");
			return parts[parts.length - 1];
		}
		return StringUtils.EMPTY;
	}
}
