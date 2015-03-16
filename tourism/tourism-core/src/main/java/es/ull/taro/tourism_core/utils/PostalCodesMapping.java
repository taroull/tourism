package es.ull.taro.tourism_core.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class PostalCodesMapping {

	private static final String COD_PROVINCIA = "CodProvincia";
	private static final String COD_MUNICIPIO = "CodMunicipio";
	private static final String CODIGO_POSTAL = "CodigoPostal";
	private static final String MUNICIPIO = "Municipio";

	private static PostalCodesMapping INSTANCE = null;

	private Map<String, String> postalCodesMap = new HashMap<String, String>();

	private PostalCodesMapping() throws IOException {

		Reader in = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("postal_codes_and_municipalities.csv"));

		Iterable<CSVRecord> records = CSVFormat.RFC4180.withDelimiter(';').withHeader(COD_PROVINCIA, COD_MUNICIPIO, CODIGO_POSTAL, MUNICIPIO).parse(in);
		for (CSVRecord record : records) {
			String provinceCode = record.get(COD_PROVINCIA);
			String municipalityCode = record.get(COD_MUNICIPIO);
			String postalCode = record.get(CODIGO_POSTAL);
			postalCodesMap.put(postalCode, provinceCode + municipalityCode);
		}
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return INSTANCE;
	}

	public String getMunicipalityCode(String postalCode) {
		return postalCodesMap.get(postalCode);
	}
}