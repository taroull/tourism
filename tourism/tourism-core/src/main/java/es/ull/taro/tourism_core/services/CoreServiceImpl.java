package es.ull.taro.tourism_core.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.jsonldjava.core.JsonLdError;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

import es.ull.taro.tourism_core.domain.TDTResource;
import es.ull.taro.tourism_core.domain.TDTResourceType;
import es.ull.taro.tourism_core.utils.Utils;
import es.ull.taro.tourism_core.vocabularies.DBpedia;

@Service("coreService")
public class CoreServiceImpl implements CoreService {

	@Autowired
	private GastroService gastroService;

	@Autowired
	private AccommodationService accommodationService;

	@Autowired
	private DBpediaService dBpediaService;

	@Autowired
	private FlickrService flickrService;

	@Autowired
	private GeoLinkedDataService geoLinkedDataService;

	@Autowired
	private TourismOfficesService tourismOfficesService;

	@Autowired
	private PlacesService placesService;

	// @Override
	// public Object retrieve(String uri) throws JsonLdError {
	//
	// FileManager fileManager = FileManager.get();
	// fileManager.addLocatorURL();
	// Model model = fileManager.loadModel(uri);
	//
	// final JenaRDFParser parser = new JenaRDFParser();
	// return JsonLdProcessor.fromRDF(model, parser);
	// }

	@Override
	public Object retrieve(String uri) throws JsonLdError {

		TDTResourceType type = Utils.resolveTdtResourceType(uri);
		switch (type) {
		case GASTRO:
			return gastroService.describeUri(uri);
		case ACCOMMODATION:
			return accommodationService.describeUri(uri);
		case OFFICE:
			return tourismOfficesService.describeUri(uri);
		case BEACH:
			return placesService.describeUri(uri);
		default:
			return dBpediaService.describeUri(uri);
		}
	}

	@Override
	public List<String> retrieveMunicipalityInfo(String tdtResourceUri) throws JsonLdError {
		TDTResource tdtResource = buildTDTResource(tdtResourceUri);
		return tdtResource != null ? retrieveMunicipalityInfoFromTDTResource(tdtResource) : null;
	}

	@Override
	public List<String> retrieveMunicipalityPhotos(String htoResourceUri) throws JsonLdError {
		List<String> photosUrls = new ArrayList<String>();
		List<String> municipalityUris = retrieveMunicipalityInfo(htoResourceUri);
		for (String uri : municipalityUris) {
			if (StringUtils.startsWith(uri, DBpedia.RESOURCE)) {
				photosUrls.addAll(flickrService.findPhotos(Utils.getNameFromDbpediaURI(uri)));
			}
		}
		return photosUrls;
	}

	@Override
	public List<String> retrievePhotosAround(String tdtResourceUri, int radius) throws JsonLdError {
		TDTResource tdtResource = buildTDTResource(tdtResourceUri);
		return flickrService.findPhotosNear(tdtResource.getLatitude(), tdtResource.getLongitude(), radius);
	}

	@Override
	public List<String> retrievePlacesAround(String tdtResourceUri, int radius) {
		TDTResource tdtResource = buildTDTResource(tdtResourceUri);
		return geoLinkedDataService.retrievePlacesAround(tdtResource.getLatitude(), tdtResource.getLongitude(), radius);
	}

	private List<String> retrieveMunicipalityInfoFromTDTResource(TDTResource tdtResource) throws JsonLdError {
		List<String> result = new ArrayList<String>();
		result.addAll(dBpediaService.retrieveMunicipalityInfo(tdtResource.getPostalCode()));
		result.addAll(dBpediaService.retrieveMunicipalityInfoES(tdtResource.getPostalCode()));
		return result;
	}

	@Override
	public List<String> retrieveTourismOfficesAround(String tdtResourceUri, int radius) {
		TDTResource tdtResource = buildTDTResource(tdtResourceUri);
		return tourismOfficesService.findTourismOfficesAround(tdtResource.getLatitude(), tdtResource.getLongitude(), radius);
	}

	@Override
	public List<String> retrieveBeachesAround(String tdtResourceUri, int radius) {
		TDTResource tdtResource = buildTDTResource(tdtResourceUri);
		return placesService.findBeachesAround(tdtResource.getLatitude(), tdtResource.getLongitude(), radius);
	}

	private TDTResource buildTDTResource(String tdtResourceUri) {
		TDTResourceType type = Utils.resolveTdtResourceType(tdtResourceUri);
		switch (type) {
		case GASTRO:
			return gastroService.createHtoResource(tdtResourceUri);
		case ACCOMMODATION:
			return accommodationService.createHtoResource(tdtResourceUri);
		case OFFICE:
			return tourismOfficesService.createOfficeResource(tdtResourceUri);
		case BEACH:
			return placesService.createBeachResource(tdtResourceUri);
		default:
			return null;
		}
	}

	@Override
	public HashMap<String, String> findPlacesNear(String uri, int radiusInMeters) throws IOException {

		TDTResource tdtResource = buildTDTResource(uri);
		return tdtResource != null ? spatialSearch(tdtResource.getLatitude(), tdtResource.getLongitude(), radiusInMeters) : null;

	}

	private HashMap<String, String> spatialSearch(float latitude, float longitude, int radiusInMeters) throws IOException {

		HashMap<String, String> result = new HashMap<String, String>();

		Model model = ModelFactory.createDefaultModel();
		InputStream inAcc = FileManager.get().open("tdtalojamientos.rdf");
		InputStream inRest = FileManager.get().open("tdt-restauracion.rdf");
		InputStream inOff = FileManager.get().open("tdtoficinasdeturismov1.2.0.rdf");
		InputStream inBeach = FileManager.get().open("playas.rdf");

		model.read(inAcc, "");
		model.read(inRest, "");
		model.read(inOff, "");
		model.read(inBeach, "");
		String placesDBP = "http://es.dbpedia.org/sparql?default-graph-uri=&query=%0D%0Aselect+%3Furi+%3Fname+%3Flatitude+%3Flongitude+%7B+%0D%0A++++%3Furi+rdfs%3Alabel+%3Fname+.%0D%0A++++%3Furi+geo%3Alat+%3Flatitude+.%0D%0A++++%3Furi+geo%3Along+%3Flongitude+.%0D%0A++++%3Furi+dcterms%3Asubject+%3Fprovince+.%0D%0A++++FILTER+regex%28%3Fprovince%2C+%22tenerife%22%2C+%22i%22%29.+%0D%0A+++%0D%0A%7D&format=text%2Fturtle&timeout=0&debug=on";
		RDFDataMgr.read(model, placesDBP);

		result = queryData(model, latitude, longitude, radiusInMeters);
		return result;
	}

	public HashMap<String, String> queryData(Model model, float latitude, float longitude, int radiusInMeters) {

		double convertedRadius = Double.valueOf(radiusInMeters) / 100000;

		HashMap<String, String> results = new HashMap<String, String>();
		StringBuilder query = new StringBuilder();
		query.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>");
		query.append("PREFIX res: <http://www.w3.org/2005/sparql-results#>");
		query.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		query.append("PREFIX hto: <http://protege.stanford.edu/rdf/HTOv4002#>");
		query.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");
		query.append("PREFIX org: <http://www.w3.org/TR/vocab-org/>");
		query.append("PREFIX vCard: <http://www.w3.org/TR/vcard-rdf/>");
		query.append("PREFIX tdt: <http://turismo-de-tenerife.org/def/turismo#>");

		query.append("SELECT ?resourceDBP ?NameDBP ?resourceHTO ?NameHTO ?resourceOff ?NameOff ?resourceBeach ?NameBeach { ");
		query.append("OPTIONAL {?B1 res:binding ?Uri . ?Uri res:variable \"uri\" . ?Uri res:value ?resourceDBP . ?B1 res:binding ?Name . ?Name res:variable \"name\" . ?Name res:value ?NameDBP . ");
		query.append("		              ?B1 res:binding ?Lat . ?Lat res:variable \"latitude\" . ?Lat res:value ?latDBP . ?B1 res:binding ?Long . ?Long res:variable \"longitude\" . ?Long res:value ?longDBP . ");
		query.append("		              FILTER(xsd:double(?latDBP) - xsd:double('" + latitude + "') <= " + convertedRadius);
		query.append("					     && xsd:double('" + latitude + "') - xsd:double(?latDBP) <= " + convertedRadius);
		query.append("  && xsd:double(?longDBP) - xsd:double('" + longitude + "') <= " + convertedRadius);
		query.append("  && xsd:double('" + longitude + "') - xsd:double(?longDBP) <= " + convertedRadius + ")}");
		query.append("OPTIONAL {?resourceHTO hto:name ?MultiLanguageText . ?MultiLanguageText hto:languageText ?LanguageText . ?LanguageText hto:text ?NameHTO .");
		query.append("?resourceHTO hto:organiser ?Organisation . ?Organisation hto:coordinates ?Coordinates . ?Coordinates hto:address ?Address . ?Address hto:xy ?XY . ?XY hto:latitude ?latHTO . ?XY hto:longitude ?longHTO . ");
		query.append("FILTER(xsd:double(?latHTO) - xsd:double('" + latitude + "') <= " + convertedRadius);
		query.append("  && xsd:double('" + latitude + "') - xsd:double(?latHTO) <= " + convertedRadius);
		query.append("  && xsd:double(?longHTO) - xsd:double('" + longitude + "') <= " + convertedRadius);
		query.append("  && xsd:double('" + longitude + "') - xsd:double(?longHTO) <= " + convertedRadius + " )} ");
		query.append("OPTIONAL {?resourceOff org:hasRegisteredSite ?Site . ?Site org:siteAddress ?Location . ?Location vCard:hasAddress ?Address . ?Address vCard:locality ?NameOff .");
		query.append("?Location geo:location ?Point . ?Point geo:lat ?latOff . ?Point geo:long ?longOff . ");
		query.append("FILTER(xsd:double(?latOff) - xsd:double('" + latitude + "') <= " + convertedRadius);
		query.append("  && xsd:double('" + latitude + "') - xsd:double(?latOff) <= " + convertedRadius);
		query.append("  && xsd:double(?longOff) - xsd:double('" + longitude + "') <= " + convertedRadius);
		query.append("  && xsd:double('" + longitude + "') - xsd:double(?longOff) <= " + convertedRadius + ")}");
		query.append("OPTIONAL {?resourceBeach tdt:ows_LinkTitle ?NameBeach . ?resourceBeach tdt:ows_Georeferencia ?GeoPoint . ?GeoPoint geo:lat ?latBeach . ?GeoPoint geo:long ?longBeach .");
		query.append("FILTER(xsd:double(?latBeach) - xsd:double('" + latitude + "') <= " + convertedRadius);
		query.append("  && xsd:double('" + latitude + "') - xsd:double(?latBeach) <= " + convertedRadius);
		query.append("  && xsd:double(?longBeach) - xsd:double('" + longitude + "') <= " + convertedRadius);
		query.append("  && xsd:double('" + longitude + "') - xsd:double(?longBeach) <= " + convertedRadius + ")}}");

		QueryExecution qe = QueryExecutionFactory.create(query.toString(), model);

		try {
			com.hp.hpl.jena.query.ResultSet rs = qe.execSelect();

			while (rs.hasNext()) {
				QuerySolution soln = rs.nextSolution();
				Resource solnHTO = soln.getResource("?resourceHTO");
				Resource solnDBP = soln.getResource("?resourceDBP");
				Resource solnOff = soln.getResource("?resourceOff");
				Resource solnBeach = soln.getResource("?resourceBeach");

				if (solnHTO != null)
					results.put(solnHTO.getURI(), soln.getLiteral("?NameHTO").toString());
				if (solnDBP != null)
					results.put(solnDBP.getURI(), soln.getLiteral("?NameDBP").toString());
				if (solnOff != null)
					results.put(solnOff.getURI(), soln.getLiteral("?NameOff").toString());
				if (solnBeach != null)
					results.put(solnBeach.getURI(), soln.getLiteral("?NameBeach").toString());
			}

		} finally {
			qe.close();
		}

		return results;
	}
}
