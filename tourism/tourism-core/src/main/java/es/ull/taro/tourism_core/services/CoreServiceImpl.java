package es.ull.taro.tourism_core.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.jsonldjava.core.JsonLdError;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import es.ull.taro.tourism_core.domain.TDTResource;
import es.ull.taro.tourism_core.domain.TDTResourceType;
import es.ull.taro.tourism_core.utils.Utils;
import es.ull.taro.tourism_core.vocabularies.DBpedia;

@Service(CoreService.BEAN_ID)
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
	private Twitter twitterService;

	@Autowired
	private GeoLinkedDataService geoLinkedDataService;

	@Autowired
	private TourismOfficesService tourismOfficesService;
	
	@Autowired
	private NaturalMonumentsService naturalMonumentsService;
	
	@Autowired
	private VolcanicsResourcesService volcanicsResourcesService;
	
	@Autowired
	private HistoricalMonumentsService historicalMonumentsService;

	@Autowired
	private PlacesService placesService;

	@Autowired
	private AemetService aemetService;

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
	public Map<String, String> retrieve(String uri) throws JsonLdError {

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
		case NATURAL:
			return naturalMonumentsService.describeUri(uri);
		case VOLCANIC:
			return volcanicsResourcesService.describeUri(uri);
		case HISTORICAL:
			return historicalMonumentsService.describeUri(uri);
		default:
			return dBpediaService.describeUri(uri);
		}
	}

	@Override
	public List<HashMap<String, String>> retrieveMunicipalityInfo(String tdtResourceUri) throws JsonLdError {
		TDTResource tdtResource = buildTDTResource(tdtResourceUri);
		return tdtResource != null ? retrieveMunicipalityInfoFromTDTResource(tdtResource) : null;
	}

	@Override
	public List<String> retrieveMunicipalityPhotos(String htoResourceUri) throws JsonLdError {
		List<String> photosUrls = new ArrayList<String>();
		List<HashMap<String, String>> municipalityUris = retrieveMunicipalityInfo(htoResourceUri);
		for (HashMap<String, String> uri : municipalityUris) {
			if (StringUtils.startsWith(uri.get("uri"), DBpedia.RESOURCE)) {
				photosUrls.addAll(flickrService.findPhotos(Utils.getNameFromDbpediaURI(uri.get("uri"))));
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
	public List<HashMap<String, String>> retrieveTwittersAround(String tdtResourceUri, int radius) throws JsonLdError {
		TDTResource tdtResource = buildTDTResource(tdtResourceUri);
		return twitterService.findTwittersNear(tdtResource.getLatitude(), tdtResource.getLongitude(), radius);
	}

	@Override
	public List<HashMap<String, String>> retrievePlacesAround(String tdtResourceUri, int radius) {
		TDTResource tdtResource = buildTDTResource(tdtResourceUri);
		return geoLinkedDataService.retrievePlacesAround(tdtResource.getLatitude(), tdtResource.getLongitude(), radius);
	}
	
	@Override
	public List<HashMap<String, String>> findPlacesNear(String uri, int radius) throws IOException {
		TDTResource tdtResource = buildTDTResource(uri);
		List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		result.addAll(geoLinkedDataService.retrievePlacesAround(tdtResource.getLatitude(), tdtResource.getLongitude(),radius));
		result.addAll(dBpediaService.retrievePlacesAround(tdtResource.getLatitude(), tdtResource.getLongitude(),radius));
		result.addAll(tourismOfficesService.findTourismOfficesAround(tdtResource.getLatitude(), tdtResource.getLongitude(),radius));
		result.addAll(naturalMonumentsService.findAround(tdtResource.getLatitude(), tdtResource.getLongitude(),radius));
		result.addAll(historicalMonumentsService.findAround(tdtResource.getLatitude(), tdtResource.getLongitude(),radius));
		result.addAll(volcanicsResourcesService.findAround(tdtResource.getLatitude(), tdtResource.getLongitude(),radius));
		result.addAll(gastroService.findAround(tdtResource.getLatitude(), tdtResource.getLongitude(),radius));
		result.addAll(accommodationService.findAround(tdtResource.getLatitude(), tdtResource.getLongitude(),radius));
		result.addAll(placesService.findBeachesAround(tdtResource.getLatitude(), tdtResource.getLongitude(),radius));
		return result;
	}

	private List<HashMap<String, String>> retrieveMunicipalityInfoFromTDTResource(TDTResource tdtResource) throws JsonLdError {
		List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		result.addAll(dBpediaService.retrieveMunicipalityInfo(tdtResource.getPostalCode()));
		result.addAll(dBpediaService.retrieveMunicipalityInfoES(tdtResource.getPostalCode()));
		return result;
	}

	@Override
	public List<HashMap<String, String>> retrieveTourismOfficesAround(String tdtResourceUri, int radius) {
		TDTResource tdtResource = buildTDTResource(tdtResourceUri);
		return tourismOfficesService.findTourismOfficesAround(tdtResource.getLatitude(), tdtResource.getLongitude(), radius);
	}
	
	@Override
	public List<HashMap<String, String>> retrieveNaturalMonumentsAround(String htoResourceUri, int radius) {
		TDTResource tdtResource = buildTDTResource(htoResourceUri);
		return naturalMonumentsService.findAround(tdtResource.getLatitude(), tdtResource.getLongitude(), radius);
	}
	
	@Override
	public List<HashMap<String, String>> retrieveVolcanicsResourcesAround(String htoResourceUri, int radius) {
		TDTResource tdtResource = buildTDTResource(htoResourceUri);
		return volcanicsResourcesService.findAround(tdtResource.getLatitude(), tdtResource.getLongitude(), radius);
	}
	
	@Override
	public List<HashMap<String, String>> retrieveHistoricalMonumentsAround(String htoResourceUri, int radius) {
		TDTResource tdtResource = buildTDTResource(htoResourceUri);
		return historicalMonumentsService.findAround(tdtResource.getLatitude(), tdtResource.getLongitude(), radius);
	}
	
	@Override
	public List<HashMap<String, String>> retrieveGastroAround(String htoResourceUri, int radius) {
		TDTResource tdtResource = buildTDTResource(htoResourceUri);
		return gastroService.findAround(tdtResource.getLatitude(), tdtResource.getLongitude(), radius);
	}
	
	@Override
	public List<HashMap<String, String>> retrieveAccommodationAround(String htoResourceUri, int radius) {
		TDTResource tdtResource = buildTDTResource(htoResourceUri);
		return accommodationService.findAround(tdtResource.getLatitude(), tdtResource.getLongitude(), radius);
	}

	@Override
	public List<HashMap<String, String>> retrieveBeachesAround(String tdtResourceUri, int radius) {
		TDTResource tdtResource = buildTDTResource(tdtResourceUri);
		return placesService.findBeachesAround(tdtResource.getLatitude(), tdtResource.getLongitude(), radius);
	}

	private TDTResource buildTDTResource(String tdtResourceUri) {
		TDTResourceType type = Utils.resolveTdtResourceType(tdtResourceUri);
		if (TDTResourceType.BEACH.equals(type)) {
			return placesService.createBeachResource(tdtResourceUri);
		} else if (TDTResourceType.GASTRO.equals(type)) {
			return gastroService.createHtoResource(tdtResourceUri);
		} else if (TDTResourceType.ACCOMMODATION.equals(type)) {
			return accommodationService.createHtoResource(tdtResourceUri);
		} else if (TDTResourceType.NATURAL.equals(type)) {
			return naturalMonumentsService.createHtoResource(tdtResourceUri);
		} else if (TDTResourceType.HISTORICAL.equals(type)) {
			return historicalMonumentsService.createHtoResource(tdtResourceUri);
		} else if (TDTResourceType.VOLCANIC.equals(type)) {
			return volcanicsResourcesService.createHtoResource(tdtResourceUri);	
		} else if (TDTResourceType.OFFICE.equals(type)) {
			return tourismOfficesService.createOfficeResource(tdtResourceUri);
		}
		return null;
	}

	

/*	private List<es.ull.taro.tourism_core.domain.Resource> spatialSearch(float latitude, float longitude, int radiusInMeters) throws IOException {

		Model model = ModelFactory.createDefaultModel();
		InputStream inAcc = FileManager.get().open("tdtalojamientos.rdf");
		InputStream inRest = FileManager.get().open("tdt-restauracion.rdf");
		InputStream inOff = FileManager.get().open("oficinasdeturismo.rdf");
		InputStream inBeach = FileManager.get().open("playas.rdf");
		InputStream inNatural = FileManager.get().open("monumentosNaturales.rdf");
		InputStream inVolcanic = FileManager.get().open("recursosVolcanicos.rdf");
		InputStream inHistorical = FileManager.get().open("monumentosHistoricos.RDF");

		model.read(inAcc, EMPTY);
		model.read(inRest, EMPTY);
		model.read(inOff, EMPTY);
		model.read(inBeach, EMPTY);
		model.read(inNatural, EMPTY);
		model.read(inVolcanic, EMPTY);
		model.read(inHistorical, EMPTY);
		String placesDBP = "http://es.dbpedia.org/sparql?default-graph-uri=&query=%0D%0Aselect+%3Furi+%3Fname+%3Flatitude+%3Flongitude+%7B+%0D%0A++++%3Furi+rdfs%3Alabel+%3Fname+.%0D%0A++++%3Furi+geo%3Alat+%3Flatitude+.%0D%0A++++%3Furi+geo%3Along+%3Flongitude+.%0D%0A++++%3Furi+dcterms%3Asubject+%3Fprovince+.%0D%0A++++FILTER+regex%28%3Fprovince%2C+%22tenerife%22%2C+%22i%22%29.+%0D%0A+++%0D%0A%7D&format=text%2Fturtle&timeout=0&debug=on";
//		String placesGeoLinkedData = "http://geo.linkeddata.es/sparql?default-graph-uri=&query=PREFIX+xsd%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E+%0D%0APREFIX+geo%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2003%2F01%2Fgeo%2Fwgs84_pos%23%3E%0D%0A"
//				+ "SELECT++DISTINCT+%3Fsubject+%0D%0AWHERE+%7B+%0D%0A++%3Fsubject+geo%3Ageometry+%3Fg.++%0D%0A++%3Fg+geo%3Alat+%3Flat.+%0D%0A++%3Fg+geo%3Along+%3Flong.+%0D%0AFILTER%28xsd%3Adouble%28%3Flat%29+-+xsd%3Adouble%28%27" + latitude +  "%27%29+%3C%3D+" + radiusInMeters + "%0D%0A++%26%26+"
//				+ "xsd%3Adouble%28%27" + latitude + "%27%29+-+xsd%3Adouble%28%3Flat%29+%3C%3D+" + radiusInMeters + "%0D%0A++%26%26+xsd%3Adouble%28%3Flong%29+-+xsd%3Adouble%28%27" + longitude + "%27%29+%3C%3D+" + radiusInMeters +"%0D%0A++%26%26+xsd%3Adouble%28%27" + longitude + "%27%29+-+xsd%3Adouble%28%3Flong%29+%3C%3D+" + radiusInMeters + "+%29.+%0D%0A%7D&format=text%2Fplain&debug=on&timeout=";
//		RDFDataMgr.read (model, placesGeoLinkedData, Lang.NTRIPLES);
		RDFDataMgr.read(model, placesDBP);
		String uri = "http://geo.linkeddata.es/sparql?default-graph-uri=&query=PREFIX+xsd%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E+%0D%0APREFIX+geo%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2003%2F01%2Fgeo%2Fwgs84_pos%23%3E%0D%0ASELECT++DISTINCT+%3Fsubject+%0D%0AWHERE+%7B+%0D%0A++%3Fsubject+geo%3Ageometry+%3Fg.++%0D%0A++%3Fg+geo%3Alat+%3Flat.+%0D%0A++%3Fg+geo%3Along+%3Flong.+%0D%0AFILTER%28xsd%3Adouble%28%3Flat%29+-+xsd%3Adouble%28%2728.4091309%27%29+%3C%3D+1000%0D%0A++%26%26+xsd%3Adouble%28%2728.4091309%27%29+-+xsd%3Adouble%28%3Flat%29+%3C%3D+1000%0D%0A++%26%26+xsd%3Adouble%28%3Flong%29+-+xsd%3Adouble%28%27-16.5440964%27%29+%3C%3D+1000%0D%0A++%26%26+xsd%3Adouble%28%27-16.5440964%27%29+-+xsd%3Adouble%28%3Flong%29+%3C%3D+1000+%29.+%0D%0A%7D&format=text%2Fplain&debug=on&timeout=";
	//	String uri = "http://geo.linkeddata.es/sparql?default-graph-uri=&query=SELECT++DISTINCT+%3Fsubject%0D%0AWHERE+%7B+%0D%0A%3Fsubject+geo%3Ageometry+%3Fg.%0D%0A%3Fg+geo%3Alat+%3Flat.+%0D%0A%3Fg+geo%3Along+%3Flong.+%0D%0A%0D%0AFILTER%28xsd%3Adouble%28%3Flat%29+-+xsd%3Adouble%28%2728.4091309%27%29+%3C%3D+%2810000%29+%26%26+xsd%3Adouble%28%2728.4091309%27%29+-+xsd%3Adouble%28%3Flat%29+%3C%3D+%2810000%29+%26%26+xsd%3Adouble%28%3Flong%29+-+xsd%3Adouble%28%27-16.5440964%27%29+%3C%3D+%2810000%29+%26%26+xsd%3Adouble%28%27-16.5440964%27%29+-+xsd%3Adouble%28%3Flong%29+%3C%3D+%2810000%29%29.+%7D&format=application%2Fsparql-results%2Bjson&debug=on&timeout=";
	//	String uri = "http://geo.linkeddata.es/sparql?default-graph-uri=&query=prefix+rdf%3A%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0A%0D%0ASELECT++DISTINCT+%3Fsubject%0D%0AWHERE+%7B+%0D%0A%3Fsubject+geo%3Ageometry+%3Fg.%0D%0A%3Fg+geo%3Alat+%3Flat.+%0D%0A%3Fg+geo%3Along+%3Flong.+%0D%0A%0D%0AFILTER%28xsd%3Adouble%28%3Flat%29+-+xsd%3Adouble%28%2728.4091309%27%29+%3C%3D+%2810000%29+%26%26+xsd%3Adouble%28%2728.4091309%27%29+-+xsd%3Adouble%28%3Flat%29+%3C%3D+%2810000%29+%26%26+xsd%3Adouble%28%3Flong%29+-+xsd%3Adouble%28%27-16.5440964%27%29+%3C%3D+%2810000%29+%26%26+xsd%3Adouble%28%27-16.5440964%27%29+-+xsd%3Adouble%28%3Flong%29+%3C%3D+%2810000%29%29.+%7D&format=text%2Fplain&debug=on&timeout=";
		RDFDataMgr.read(model, uri, org.apache.jena.riot.Lang.NTRIPLES);

		return queryData(model, latitude, longitude, radiusInMeters);
	}*/

	public List<es.ull.taro.tourism_core.domain.Resource> queryData(Model model, float latitude, float longitude, int radiusInMeters) {

		double convertedRadius = Double.valueOf(radiusInMeters) / 100000;
		StringBuilder query = new StringBuilder();
		query.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>");
		query.append("PREFIX res: <http://www.w3.org/2005/sparql-results#>");
		query.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ");
		query.append("PREFIX hto: <http://protege.stanford.edu/rdf/HTOv4002#>");
		query.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");
		query.append("PREFIX org: <http://www.w3.org/TR/vocab-org/>");
		query.append("PREFIX vCard: <http://www.w3.org/TR/vcard-rdf/>");
		query.append("PREFIX tdt: <http://turismo-de-tenerife.org/def/turismo#>");

		query.append("SELECT DISTINCT ?resourceDBP ?NameDBP ?resourceHTO ?NameHTO ?resourceOff ?NameOff ?resourceBeach ?NameBeach { ");
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

		Set<es.ull.taro.tourism_core.domain.Resource> resources = new HashSet<es.ull.taro.tourism_core.domain.Resource>();

		try {
			com.hp.hpl.jena.query.ResultSet rs = qe.execSelect();

			while (rs.hasNext()) {
				QuerySolution soln = rs.nextSolution();
				Resource solnHTO = soln.getResource("?resourceHTO");
				Resource solnDBP = soln.getResource("?resourceDBP");
				Resource solnOff = soln.getResource("?resourceOff");
				Resource solnBeach = soln.getResource("?resourceBeach");

				if (solnHTO != null)
					resources.add(new es.ull.taro.tourism_core.domain.Resource(solnHTO.getURI(), soln.getLiteral("?NameHTO").toString()));
				if (solnDBP != null)
					resources.add(new es.ull.taro.tourism_core.domain.Resource(solnDBP.getURI(), soln.getLiteral("?NameDBP").toString()));
				if (solnOff != null)
					resources.add(new es.ull.taro.tourism_core.domain.Resource(solnOff.getURI(), soln.getLiteral("?NameOff").toString()));
				if (solnBeach != null)
					resources.add(new es.ull.taro.tourism_core.domain.Resource(solnBeach.getURI(), soln.getLiteral("?NameBeach").toString()));
			}
		} finally {
			qe.close();
		}

		return new ArrayList<es.ull.taro.tourism_core.domain.Resource>(resources);
	}

	@Override
	public Object getWeatherPrediction(String uri) {
		TDTResource tdtResource = buildTDTResource(uri);
		return aemetService.getWeatherPrediction(tdtResource.getPostalCode());
	}
}
