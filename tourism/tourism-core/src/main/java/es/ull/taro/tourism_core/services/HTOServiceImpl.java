package es.ull.taro.tourism_core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import es.ull.taro.tourism_core.domain.HTOResource;
import es.ull.taro.tourism_core.vocabularies.HTO;

public abstract class HTOServiceImpl extends TDTServiceImpl implements HTOService {

	@Override
	public Resource retrieve(String uri) {
		Model model = loadRDFFile();
		return model.getResource(uri);
	}

	@Override
	public List<HTOResource> find(String query) {

		Model model = loadRDFFile();

		StringBuilder sparqlQuery = new StringBuilder();
		sparqlQuery.append("PREFIX hto: <").append(HTO.VOCABULARY).append(">");
		sparqlQuery.append("SELECT DISTINCT ?resource (SAMPLE(?text) AS ?text2) (SAMPLE(?lat) AS ?lat2) (SAMPLE(?long) AS ?long2) ?PostCode");
		sparqlQuery.append("{ ");
		sparqlQuery.append("?resource a ").append(getResourceType()).append(".");
		sparqlQuery.append("?resource hto:name ?multilanguageText. ");
		sparqlQuery.append("?multilanguageText a hto:MultiLanguageText. ");
		sparqlQuery.append("?multilanguageText hto:languageText ?languageText. ");
		sparqlQuery.append("?languageText a hto:LanguageText. ");
		sparqlQuery.append("?languageText hto:text ?text. ");
		
		sparqlQuery.append("OPTIONAL { ");
		sparqlQuery.append("?resource hto:organiser ?Organisation .");
		sparqlQuery.append("?Organisation hto:coordinates ?Coordinates . ");
		sparqlQuery.append("?Coordinates hto:address ?Address .");
		sparqlQuery.append("?Address hto:xy ?XY. ");
		sparqlQuery.append("?XY hto:latitude ?lat. ");
		sparqlQuery.append("?XY hto:longitude ?long. }");
		
		sparqlQuery.append(""
				+ "OPTIONAL {"
				+ "?resource hto:organiser ?B3_Organisation . "
				+ "?B3_Organisation hto:coordinates ?B3_Coordinates . "
				+ "?B3_Coordinates hto:address ?B3_Address . "
				+ "?B3_Address hto:postcode ?PostCode . }");
		
	
		sparqlQuery.append("FILTER regex(?text, \"").append(query).append("\", \"i\"). ");
		sparqlQuery.append("}");
		sparqlQuery.append("GROUP BY ?resource ?PostCode");

		QueryExecution qe = QueryExecutionFactory.create(sparqlQuery.toString(), model);

		List<HTOResource> resources = new ArrayList<HTOResource>();
		
		try {
			ResultSet results = qe.execSelect();
			for (; results.hasNext();) {
				QuerySolution sol = (QuerySolution) results.next();
				HTOResource htoResource = new HTOResource(sol.getResource("?resource").getURI().toString());
				htoResource.setName(sol.getLiteral("?text2").toString());
				if(sol.getLiteral("?PostCode") != null)
					htoResource.setPostalCode(sol.getLiteral("?PostCode").toString());
				if (!sol.getLiteral("?lat2").toString().isEmpty())
					htoResource.setLatitude(sol.getLiteral("?lat2").getFloat());
				if (!sol.getLiteral("?long2").toString().isEmpty())
					htoResource.setLongitude(sol.getLiteral("?long2").getFloat());
				resources.add(htoResource);
			}
		} finally {
			qe.close();
		}
		return resources;
	}
	
	@Override
	public List<String> findAround(float latitude, float longitude, int radiusInMeters) {
		Model model = loadRDFFile();
		
		double convertedRadius = Double.valueOf(radiusInMeters) / 100000;

		StringBuilder sparqlQuery = new StringBuilder();
		
		sparqlQuery.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		sparqlQuery.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> ");
		sparqlQuery.append("PREFIX hto: <").append(HTO.VOCABULARY).append(">");
		sparqlQuery.append("SELECT DISTINCT ?resource ?lat ?long");
		sparqlQuery.append("WHERE { ");
		sparqlQuery.append("?resource hto:organiser ?Organisation .");
		sparqlQuery.append("?Organisation hto:coordinates ?Coordinates . ");
		sparqlQuery.append("?Coordinates hto:address ?Address .");
		sparqlQuery.append("?Address hto:xy ?XY. ");
		sparqlQuery.append("?XY hto:latitude ?lat. ");
		sparqlQuery.append("?XY hto:longitude ?long.");
		sparqlQuery.append("FILTER(xsd:double(?lat) - xsd:double('" + latitude + "') <= " + convertedRadius);
		sparqlQuery.append("  && xsd:double('" + latitude + "') - xsd:double(?lat) <= " + convertedRadius);
		sparqlQuery.append("  && xsd:double(?long) - xsd:double('" + longitude + "') <= " + convertedRadius);
		sparqlQuery.append("  && xsd:double('" + longitude + "') - xsd:double(?long) <= " + convertedRadius + " )} ");
				
		List<String> uris = new ArrayList<String>();

		QueryExecution qe = QueryExecutionFactory.create(sparqlQuery.toString(), model);
		try {
			ResultSet results = qe.execSelect();
			for (; results.hasNext();) {
				QuerySolution sol = (QuerySolution) results.next();
				
				Resource resource = sol.getResource("?resource");
				
				uris.add(resource.getURI());
			}
		} finally {
			qe.close();
		}

		return uris;
	}
		

	@Override
	public Map<String, String> describeUri(String uri) {

		Model model = loadRDFFile();

		StringBuilder sparqlQuery = new StringBuilder();
		sparqlQuery.append("PREFIX hto: <").append(HTO.VOCABULARY).append(">");
		sparqlQuery.append("DESCRIBE ").append("<").append(uri).append(">");

		QueryExecution qe = QueryExecutionFactory.create(sparqlQuery.toString(), model);
		Model resultModel;
		try {
			resultModel = qe.execDescribe();
		} finally {
			qe.close();
		}
		StringBuilder sparqlQuery2 = new StringBuilder();
		sparqlQuery2.append("PREFIX hto: <http://protege.stanford.edu/rdf/HTOv4002#>");
		sparqlQuery2.append("PREFIX tdt: <http://turismo-de-tenerife.org/def/turismo#>");
		
		sparqlQuery2.append(""
				+ "SELECT DISTINCT ?Name ?AccommodationType ?AttractionType ?Imagen ?Description ?PostCode ?City ?StreetName "
				+ "?CountryCode ?TelNumber ?FaxNumber ?Email ?Url ?GastroType ?Timeline ?ProfileName ?ProfileValue "
				+ "?FacilityValue ?HowToGet ?resource");
		sparqlQuery2.append("{ "
				+ "?resource hto:name ?B1_MultiLanguageText . "
				+ "?B1_MultiLanguageText hto:languageText ?B1_LanguageText . "
				+ "?B1_LanguageText hto:text ?Name . ");
		sparqlQuery2.append(" "
				+ "OPTIONAL {"
				+ "?resource hto:description ?B2_Description . "
				+ "?B2_Description hto:longDescription ?B2_MultiLanguageText . "
				+ "?B2_MultiLanguageText hto:languageText ?B2_LanguageText . "
				+ "?B2_LanguageText hto:text ?Description .}");
		sparqlQuery2.append(""
				+ "?resource hto:organiser ?B3_Organisation . "
				+ "?B3_Organisation hto:coordinates ?B3_Coordinates . "
				+ "?B3_Coordinates hto:address ?B3_Address . "
				+ "?B3_Address hto:postcode ?PostCode . ");
		sparqlQuery2.append(""
				+ "?B3_Address hto:city ?B4_MultiLanguageText . "
				+ "?B4_MultiLanguageText hto:languageText ?B4_LanguageText . "
				+ "?B4_LanguageText hto:text ?City . ");
		sparqlQuery2.append(""
				+ "?B3_Address hto:streetAddress ?B5_StreetAddress . "
				+ "?B5_StreetAddress hto:streetName ?StreetName . ");
		sparqlQuery2.append(""
				+ "?B3_Coordinates hto:telecoms ?B6_Telecoms . "
				+ "?B6_Telecoms hto:telephone ?B6_TelecomNumber . "
				+ "?B6_TelecomNumber hto:countryCode ?CountryCode . ");
		sparqlQuery2.append(""
				+ "?B6_TelecomNumber hto:number ?TelNumber . ");
		sparqlQuery2.append(" "
				+ "OPTIONAL {"
				+ "?B6_Telecoms hto:fax ?B7_TelecomNumber . "
				+ "?B7_TelecomNumber hto:number ?FaxNumber . }");
		sparqlQuery2.append(" "
				+ "OPTIONAL {"
				+ "?B6_Telecoms hto:email ?Email . }");
		sparqlQuery2.append(" "
				+ "OPTIONAL {"
				+ "?B6_Telecoms hto:url ?B8_MultiLanguageText . "
				+ "?B8_MultiLanguageText hto:languageText ?B8_LanguageText . "
				+ "?B8_LanguageText hto:text ?Url .} ");
		sparqlQuery2.append(" "
				+ "OPTIONAL {"
				+ "?resource hto:gastroType ?B9_ListValue . "
				+ "?B9_ListValue hto:referencedValue ?B9_ReferencedValue . "
				+ "?B9_ReferencedValue hto:domainName \"tdt\" . "
				+ "?B9_ReferencedValue hto:domainValue ?GastroType . }");
		sparqlQuery2.append(" "
				+ "OPTIONAL {"
				+ "?resource hto:schedule ?B10_Timeline . "
				+ "?B10_Timeline tdt:timeText ?B10_MultiLanguageText . "
				+ "?B10_MultiLanguageText hto:languageText ?B10_LanguageText . "
				+ "?B10_LanguageText hto:text ?Timeline . } ");
		sparqlQuery2.append(""
				+ "?resource hto:profile ?B9_Profile . "
				+ "?B9_Profile hto:profileField ?B9_ProfileField . "
				+ "?B9_ProfileField hto:domainName \"tdt\" . "
				+ "?B9_ProfileField hto:fieldName ?ProfileName . "
				+ "?B9_ProfileField hto:fieldValue ?ProfileValue .");
		sparqlQuery2.append(" "
				+ "OPTIONAL {"
				+ "?resource hto:facility ?B10_Facility . "
				+ "?B10_Facility hto:facilityName ?B10_ListValue . "
				+ "?B10__ListValue hto:referencedValue ?B10_ReferencedValue . "
				+ "?B10_ReferencedValue hto:domainName \"tdt\" . "
				+ "?B10_ReferencedValue hto:domainValue ?FacilityValue . }");
		sparqlQuery2.append(" "
				+ "OPTIONAL {"
				+ "?resource hto:accommodationType ?B11_ListValue . "
				+ "?B11_ListValue hto:referencedValue ?B11_ReferencedValue . "
				+ "?B11_ReferencedValue hto:domainName \"tdt\" . "
				+ "?B11_ReferencedValue hto:domainValue ?AccommodationType .}");
		sparqlQuery2.append(" "
				+ "OPTIONAL {"
				+ "?resource hto:attractionType ?B13_ListValue . "
				+ "?B13_ListValue hto:referencedValue ?B13_ReferencedValue . "
				+ "?B13_ReferencedValue hto:domainName \"tdt\" . "
				+ "?B13_ReferencedValue hto:domainValue ?AttractionType .}");
		sparqlQuery2.append(" "
				+ "OPTIONAL {"
				+ "?resource hto:mmiType ?B14_ListValue . "
				+ "?B14_ListValue hto:referencedValue ?B14_ReferencedValue . "
				+ "?B14_ReferencedValue hto:domainName \"hto\" . "
				+ "?B14_ReferencedValue hto:domainValue ?Imagen  . }");
		sparqlQuery2.append(" "
				+ "OPTIONAL {"
				+ "?resource tdt:howArriveText ?B12_MultiLanguageText . "
				+ "?B12_MultiLanguageText hto:languageText ?B12_LanguageText . "
				+ "?B12_LanguageText hto:text ?HowToGet . }}");

		QueryExecution qe2 = QueryExecutionFactory.create(sparqlQuery2.toString(), resultModel);

		Map<String, String> results = new HashMap<String, String>();
		try {
			com.hp.hpl.jena.query.ResultSet ns = qe2.execSelect();
			while (ns.hasNext()) {
				QuerySolution soln = ns.nextSolution();
				
				results.put("Nombre", soln.getLiteral("?Name").toString());
				
				if (soln.getLiteral("?AccommodationType") != null)
					results.put("Tipo de alojamiento", soln.getLiteral("?AccommodationType").toString());
				
				if (soln.getLiteral("?Description") != null)
					results.put("Description", soln.getLiteral("?Description").toString());
				
				if (soln.getLiteral("?AttractionType") != null)
					results.put("Tipo de Atracción Turística", soln.getLiteral("?AttractionType").toString());
				
				results.put("Código Postal", soln.getLiteral("?PostCode").toString());
				
				results.put("Ciudad", soln.getLiteral("?City").toString());
				
				results.put("Calle", soln.getLiteral("?StreetName").toString());
				
				results.put("Código del país", soln.getLiteral("?CountryCode").toString());
				
				results.put("Teléfono", soln.getLiteral("?TelNumber").toString());
				
				if (soln.getLiteral("?FaxNumber") != null && !soln.getLiteral("?FaxNumber").toString().isEmpty()) {
					results.put("Fax", soln.getLiteral("?FaxNumber").toString());
				}
				if (soln.getLiteral("?Email") != null && !soln.getLiteral("?Email").toString().isEmpty()) {
					results.put("Correo", soln.getLiteral("?Email").toString());
				}
				if (soln.getLiteral("?Url") != null && !soln.getLiteral("?Url").toString().isEmpty()) {
					results.put("URL", soln.getLiteral("?Url").toString());
				}
				if (soln.getLiteral("?GastroType") != null) {
					results.put("Tipo de actividad", soln.getLiteral("?GastroType").toString());
				}
				if (soln.getLiteral("?Timeline") != null) {
					results.put("Horario", soln.getLiteral("?Timeline").toString());
				}
				results.put(soln.getLiteral("?ProfileName").toString(), soln.getLiteral("?ProfileValue").toString());
				if (soln.getLiteral("?FacilityValue") != null) {
					results.put("facility-" + soln.getLiteral("?FacilityValue").toString(), soln.getLiteral("?FacilityValue").toString());
				}
				if (soln.getLiteral("?Imagen") != null) {
					results.put("Imagen", soln.getLiteral("?Imagen").toString());
			    }
				if (soln.getLiteral("?HowToGet") != null) {
					results.put("Cómo llegar", soln.getLiteral("?HowToGet").toString());
				}
			}

		} finally {
			qe2.close();
		}
		return results;
	}

	@Override
	public HTOResource createHtoResource(String uri) {
		Resource resource = retrieve(uri);
		HTOResource htoResource = new HTOResource();
		Model m = loadRDFFile();

		htoResource.setUri(resource.getURI());
		
		Statement organiser = resource.getProperty(m.createProperty("http://protege.stanford.edu/rdf/HTOv4002#organiser"));
		Statement coordinates = organiser.getProperty(m.createProperty("http://protege.stanford.edu/rdf/HTOv4002#coordinates"));
		Statement address = coordinates.getProperty(m.createProperty("http://protege.stanford.edu/rdf/HTOv4002#address"));
		String postalCode = address.getProperty(m.createProperty("http://protege.stanford.edu/rdf/HTOv4002#postcode")).getLiteral().getString();
		Statement xy = address.getProperty(m.createProperty("http://protege.stanford.edu/rdf/HTOv4002#xy"));
		
	//		;
		
		if(postalCode != null)
			htoResource.setPostalCode(postalCode);
		if(xy.getProperty(m.createProperty("http://protege.stanford.edu/rdf/HTOv4002#latitude")).getLiteral() != null && 
				!xy.getProperty(m.createProperty("http://protege.stanford.edu/rdf/HTOv4002#latitude")).getLiteral().getLexicalForm().isEmpty()) {
			float latitude = xy.getProperty(m.createProperty("http://protege.stanford.edu/rdf/HTOv4002#latitude")).getLiteral().getFloat();
			htoResource.setLatitude(latitude);
		}
		if (xy.getProperty(m.createProperty("http://protege.stanford.edu/rdf/HTOv4002#longitude")).getLiteral() != null && 
				!xy.getProperty(m.createProperty("http://protege.stanford.edu/rdf/HTOv4002#longitude")).getLiteral().getLexicalForm().isEmpty()) { 
			float longitude = xy.getProperty(m.createProperty("http://protege.stanford.edu/rdf/HTOv4002#longitude")).getLiteral().getFloat();
			htoResource.setLongitude(longitude);
		}
				
		return htoResource;
	}

	protected Model loadRDFFile() {
		return RDFDataMgr.loadModel(getRDFFileName());
	}

	@Override
	public String getVocabulary() {
		return HTO.VOCABULARY;
	}

	@Override
	public String getLatitudeProperty() {
		return HTO.LATITUDE;
	}

	@Override
	public String getLongitudeProperty() {
		return HTO.LONGITUDE;
	}

	protected abstract String getRDFFileName();

	protected abstract String getResourceType();
}
