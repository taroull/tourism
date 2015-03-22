package es.ull.taro.tourism_core.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;

public class PostalCodesMapping {

	private static final String COD_PROVINCIA = "CodProvincia";
	private static final String COD_MUNICIPIO = "CodMunicipio";
	private static final String CODIGO_POSTAL = "CodigoPostal";
	private static final String MUNICIPIO = "Municipio";

	private static PostalCodesMapping INSTANCE = null;

	private ImmutableMultimap<String, String> POSTAL_CODES_TO_MUNICIPALITY_CODES_MAP = null;
	private ImmutableMultimap<String, String> MUNICIPALITY_CODES_TO_POSTAL_CODES_MAP = null;

	private PostalCodesMapping() throws IOException {
		Builder<String, String> builder = ImmutableMultimap.<String, String> builder();
		Reader in = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("postal_codes_and_municipalities.csv"));
		Iterable<CSVRecord> records = CSVFormat.RFC4180.withDelimiter(';').withHeader(COD_PROVINCIA, COD_MUNICIPIO, CODIGO_POSTAL, MUNICIPIO).parse(in);
		for (CSVRecord record : records) {
			String provinceCode = record.get(COD_PROVINCIA);
			String municipalityCode = record.get(COD_MUNICIPIO);
			String postalCode = record.get(CODIGO_POSTAL);
			builder.put(provinceCode + municipalityCode, postalCode);
		}

		MUNICIPALITY_CODES_TO_POSTAL_CODES_MAP = builder.build();
		POSTAL_CODES_TO_MUNICIPALITY_CODES_MAP = MUNICIPALITY_CODES_TO_POSTAL_CODES_MAP.inverse();
	}

	private static void createInstance() throws IOException {
		if (INSTANCE == null) {
			synchronized (PostalCodesMapping.class) {
				if (INSTANCE == null) {
					INSTANCE = new PostalCodesMapping();
				}
			}
		}
	}

	public static PostalCodesMapping getInstance() {
		if (INSTANCE == null) {
			try {
				createInstance();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return INSTANCE;
	}

	public String getMunicipalityCode(String postalCode) {
		ImmutableList<String> municipalityCodes = POSTAL_CODES_TO_MUNICIPALITY_CODES_MAP.get(postalCode).asList();
		return municipalityCodes.size() > 0 ? municipalityCodes.get(0) : null;
	}

	public List<String> getPostalCodes(String municipalityCode) {
		ImmutableList<String> postalCodes = MUNICIPALITY_CODES_TO_POSTAL_CODES_MAP.get(municipalityCode).asList();
		return postalCodes;
	}
}