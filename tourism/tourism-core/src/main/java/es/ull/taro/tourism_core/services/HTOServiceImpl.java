package es.ull.taro.tourism_core.services;

import java.util.HashMap;

import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
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
	public HashMap<String, String> find(String query) {

		Model model = loadRDFFile();

		StringBuilder sparqlQuery = new StringBuilder();
		sparqlQuery.append("PREFIX hto: <").append(HTO.VOCABULARY).append(">");
		sparqlQuery.append("SELECT ?resource ?text");
		sparqlQuery.append("{ ");
		sparqlQuery.append("?resource a ").append(getResourceType()).append(".");
		sparqlQuery.append("?resource hto:name ?multilanguageText. ");
		sparqlQuery.append("?multilanguageText a hto:MultiLanguageText. ");
		sparqlQuery.append("?multilanguageText hto:languageText ?languageText. ");
		sparqlQuery.append("?languageText a hto:LanguageText. ");
		sparqlQuery.append("?languageText hto:text ?text. ");
		sparqlQuery.append("FILTER regex(?text, \"").append(query).append("\", \"i\"). ");
		sparqlQuery.append("}");

		QueryExecution qe = QueryExecutionFactory.create(sparqlQuery.toString(), model);

		HashMap<String, String> resourceURIs = new HashMap<String, String>();

		try {
			ResultSet results = qe.execSelect();
			for (; results.hasNext();) {
				QuerySolution sol = (QuerySolution) results.next();
				String resource = sol.getResource("?resource").getURI().toString();
				String name = sol.getLiteral("?text").toString();
				resourceURIs.put(resource, name);

			}
		} finally {
			qe.close();
		}

		return resourceURIs;
	}

	@Override
	public HashMap<Literal, String> describeUri(String uri) {

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
		sparqlQuery2
				.append("SELECT ?Name ?AccommodationType ?Description ?PostCode ?City ?StreetName ?CountryCode ?TelNumber ?FaxNumber ?Email ?Url ?GastroType ?Timeline ?ProfileName ?ProfileValue ?FacilityValue ?HowToGet");
		sparqlQuery2
				.append("{ ?resource hto:name ?B1_MultiLanguageText . ?B1_MultiLanguageText hto:languageText ?B1_LanguageText . ?B1_LanguageText hto:text ?Name . ");
		sparqlQuery2
				.append(" OPTIONAL {?resource hto:description ?B2_Description . ?B2_Description hto:longDescription ?B2_MultiLanguageText . ?B2_MultiLanguageText hto:languageText ?B2_LanguageText . ?B2_LanguageText hto:text ?Description .}");
		sparqlQuery2
				.append("?resource hto:organiser ?B3_Organisation . ?B3_Organisation hto:coordinates ?B3_Coordinates . ?B3_Coordinates hto:address ?B3_Address . ?B3_Address hto:postcode ?PostCode . ");
		sparqlQuery2
				.append("?B3_Address hto:city ?B4_MultiLanguageText . ?B4_MultiLanguageText hto:languageText ?B4_LanguageText . ?B4_LanguageText hto:text ?City . ");
		sparqlQuery2.append("?B3_Address hto:streetAddress ?B5_StreetAddress . ?B5_StreetAddress hto:streetName ?StreetName . ");
		sparqlQuery2
				.append("?B3_Coordinates hto:telecoms ?B6_Telecoms . ?B6_Telecoms hto:telephone ?B6_TelecomNumber . ?B6_TelecomNumber hto:countryCode ?CountryCode . ");
		sparqlQuery2.append("?B6_TelecomNumber hto:number ?TelNumber . ");
		sparqlQuery2.append(" OPTIONAL {?B6_Telecoms hto:fax ?B7_TelecomNumber . ?B7_TelecomNumber hto:number ?FaxNumber . }");
		sparqlQuery2.append(" OPTIONAL {?B6_Telecoms hto:email ?Email . }");
		sparqlQuery2
				.append(" OPTIONAL {?B6_Telecoms hto:url ?B8_MultiLanguageText . ?B8_MultiLanguageText hto:languageText ?B8_LanguageText . ?B8_LanguageText hto:text ?Url .} ");
		sparqlQuery2
				.append(" OPTIONAL {?resource hto:gastroType ?B9_ListValue . ?B9_ListValue hto:referencedValue ?B9_ReferencedValue . ?B9_ReferencedValue hto:domainName \"tdt\" . ?B9_ReferencedValue hto:domainValue ?GastroType . }");
		sparqlQuery2
				.append(" OPTIONAL {?resource hto:schedule ?B10_Timeline . ?B10_Timeline tdt:timeText ?B10_MultiLanguageText . ?B10_MultiLanguageText hto:languageText ?B10_LanguageText . ?B10_LanguageText hto:text ?Timeline . } ");
		sparqlQuery2
				.append("?resource hto:profile ?B9_Profile . ?B9_Profile hto:profileField ?B9_ProfileField . ?B9_ProfileField hto:domainName \"tdt\" . ?B9_ProfileField hto:fieldName ?ProfileName . ?B9_ProfileField hto:fieldValue ?ProfileValue .");
		sparqlQuery2
				.append(" OPTIONAL {?resource hto:facility ?B10_Facility . ?B10_Facility hto:facilityName ?B10_ListValue . ?B10__ListValue hto:referencedValue ?B10_ReferencedValue . ?B10_ReferencedValue hto:domainName \"tdt\" . ?B10_ReferencedValue hto:domainValue ?FacilityValue . }");
		sparqlQuery2
				.append(" OPTIONAL {?resource hto:accommodationType ?B11_ListValue . ?B11_ListValue hto:referencedValue ?B11_ReferencedValue . ?B11_ReferencedValue hto:domainName \"tdt\" . ?B11_ReferencedValue hto:domainValue ?AccommodationType .}");
		sparqlQuery2
				.append(" OPTIONAL {?resource tdt:howArriveText ?B12_MultiLanguageText . ?B12_MultiLanguageText hto:languageText ?B12_LanguageText . ?B12_LanguageText hto:text ?HowToGet . }}");

		QueryExecution qe2 = QueryExecutionFactory.create(sparqlQuery2.toString(), resultModel);
		HashMap<Literal, String> results = new HashMap<Literal, String>();
		try {
			com.hp.hpl.jena.query.ResultSet ns = qe2.execSelect();
			while (ns.hasNext()) {
				QuerySolution soln = ns.nextSolution();
				results.put(soln.getLiteral("?Name"), "Nombre");
				if (soln.getLiteral("?AccommodationType") != null)
					results.put(soln.getLiteral("?AccommodationType"), "Tipo de alojamiento");
				if (soln.getLiteral("?Description") != null)
					results.put(soln.getLiteral("?Description"), "Description");
				results.put(soln.getLiteral("?PostCode"), "Código Postal");
				results.put(soln.getLiteral("?City"), "Ciudad");
				results.put(soln.getLiteral("?StreetName"), "Calle");
				results.put(soln.getLiteral("?CountryCode"), "Código del país");
				results.put(soln.getLiteral("?TelNumber"), "Teléfono");
				if (soln.getLiteral("?FaxNumber") != null && !soln.getLiteral("?FaxNumber").toString().isEmpty())
					results.put(soln.getLiteral("?FaxNumber"), "Fax");
				if (soln.getLiteral("?Email") != null && !soln.getLiteral("?Email").toString().isEmpty())
					results.put(soln.getLiteral("?Email"), "Correo");
				if (soln.getLiteral("?Url") != null && !soln.getLiteral("?Url").toString().isEmpty())
					results.put(soln.getLiteral("?Url"), "URL");
				if (soln.getLiteral("?GastroType") != null)
					results.put(soln.getLiteral("?GastroType"), "Tipo de actividad");
				if (soln.getLiteral("?Timeline") != null)
					results.put(soln.getLiteral("?Timeline"), "Horario");
				results.put(soln.getLiteral("?ProfileValue"), soln.getLiteral("?ProfileName").toString());
				if (soln.getLiteral("?FacilityValue") != null)
					results.put(soln.getLiteral("?FacilityValue"), "facility-" + soln.getLiteral("?FacilityValue"));
				if (soln.getLiteral("?HowToGet") != null)
					results.put(soln.getLiteral("?HowToGet"), "Cómo llegar");
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
		String postalCode = address.getProperty(m.createProperty("http://protege.stanford.edu/rdf/HTOv4002#postcode")).getLiteral()
				.getString();
		Statement xy = address.getProperty(m.createProperty("http://protege.stanford.edu/rdf/HTOv4002#xy"));
		try {
			float latitude = xy.getProperty(m.createProperty("http://protege.stanford.edu/rdf/HTOv4002#latitude")).getLiteral().getFloat();
			float longitude = xy.getProperty(m.createProperty("http://protege.stanford.edu/rdf/HTOv4002#longitude")).getLiteral()
					.getFloat();
			htoResource.setLatitude(latitude);
			htoResource.setLongitude(longitude);
		} catch (NumberFormatException e) {
			// TODO no nothing
		}
		htoResource.setPostalCode(postalCode);

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