package es.ull.taro.tourism_rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.ull.taro.tourism_core.domain.GeoResource;
import es.ull.taro.tourism_core.domain.HTOResource;
import es.ull.taro.tourism_core.domain.TDTResource;
import es.ull.taro.tourism_core.services.AccommodationService;
import es.ull.taro.tourism_core.services.AemetService;
import es.ull.taro.tourism_core.services.CoreService;
import es.ull.taro.tourism_core.services.DBpediaService;
import es.ull.taro.tourism_core.services.GastroService;
import es.ull.taro.tourism_core.services.HistoricalMonumentsService;
import es.ull.taro.tourism_core.services.NaturalMonumentsService;
import es.ull.taro.tourism_core.services.PlacesService;
import es.ull.taro.tourism_core.services.TourismOfficesService;
import es.ull.taro.tourism_core.services.VolcanicsResourcesService;

@Component
@Path("/")
public class TourismAPI {

	@Autowired
	protected DBpediaService dBpediaService;

	@Autowired
	protected CoreService coreService;

	@Autowired
	protected AccommodationService accommodationService;

	@Autowired
	protected GastroService gastroService;

	@Autowired
	protected PlacesService placesService;

	@Autowired
	protected TourismOfficesService tourismOfficesService;
	
	@Autowired
	protected NaturalMonumentsService naturalMonumentsService;
	
	@Autowired
	protected HistoricalMonumentsService historicalMonumentsService;
	
	@Autowired
	protected VolcanicsResourcesService volcanicsResourcesService;

	@Autowired
	protected AemetService aemetService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("resource")
	public Map<String, String> getResourceByUri(@QueryParam(value = "uri") String uri) throws Exception {
		return coreService.retrieve(uri);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("dbpediaResource")
	public List<GeoResource> findResourceByName(@QueryParam(value = "name") String name) throws Exception {
		return dBpediaService.find(name);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("accommodation")
	public List<HTOResource> findAccommodation(@QueryParam(value = "name") String name) throws Exception {
		return accommodationService.find(name);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("gastro")
	public List<HTOResource> findGastro(@QueryParam(value = "name") String name) throws Exception {
		return gastroService.find(name);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("municipalityInfo")
	public List<HashMap<String, String>> retrieveMunicipalityInfo(@QueryParam(value = "uri") String uri) throws Exception {
		return coreService.retrieveMunicipalityInfo(uri);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("municipalityPhotos")
	public List<String> retrieveMunicipalityPhotos(@QueryParam(value = "uri") String uri) throws Exception {
		return coreService.retrieveMunicipalityPhotos(uri);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("photosAround")
	public List<String> retrievePhotosAround(@QueryParam(value = "uri") String uri, @QueryParam(value = "radius") int radius) throws Exception {
		return coreService.retrievePhotosAround(uri, radius);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("placesAround")
	public List<HashMap<String, String>> retrievePlacesAround(@QueryParam(value = "uri") String uri, @QueryParam(value = "radius") int radius) throws Exception {
		return coreService.retrievePlacesAround(uri, radius);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("tourismOfficesAround")
	public List<HashMap<String, String>> retrieveTourismOfficesAround(@QueryParam(value = "uri") String uri, @QueryParam(value = "radius") int radius) throws Exception {
		return coreService.retrieveTourismOfficesAround(uri, radius);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("naturalMonumentsAround")
	public List<HashMap<String, String>> retrieveNaturalMonumentsAround(@QueryParam(value = "uri") String uri, @QueryParam(value = "radius") int radius) throws Exception {
		return coreService.retrieveNaturalMonumentsAround(uri, radius);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("historicalMonumentsAround")
	public List<HashMap<String, String>> retrieveHistoricalMonumentsAround(@QueryParam(value = "uri") String uri, @QueryParam(value = "radius") int radius) throws Exception {
		return coreService.retrieveHistoricalMonumentsAround(uri, radius);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("volcanicsResourcesAround")
	public List<HashMap<String, String>> retrieveVolcanicsResourcesAround(@QueryParam(value = "uri") String uri, @QueryParam(value = "radius") int radius) throws Exception {
		return coreService.retrieveVolcanicsResourcesAround(uri, radius);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("gastroAround")
	public List<HashMap<String, String>> retrieveGastroAround(@QueryParam(value = "uri") String uri, @QueryParam(value = "radius") int radius) throws Exception {
		return coreService.retrieveGastroAround(uri, radius);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("accommodationAround")
	public List<HashMap<String, String>> retrieveAccommodationAround(@QueryParam(value = "uri") String uri, @QueryParam(value = "radius") int radius) throws Exception {
		return coreService.retrieveAccommodationAround(uri, radius);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("placesNear")
	public List<HashMap<String, String>> findPlacesNear(@QueryParam(value = "uri") String uri, @QueryParam(value = "radius") int radius) throws Exception {
		return coreService.findPlacesNear(uri, radius);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("beachResource")
	public List<TDTResource> findBeachByName(@QueryParam(value = "name") String name) throws Exception {
		return placesService.find(name);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("beachAround")
	public List<HashMap<String, String>> retrieveBeachesAround(@QueryParam(value = "uri") String uri, @QueryParam(value = "radius") int radius) throws Exception {
		return coreService.retrieveBeachesAround(uri, radius);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("tourismOfficeResource")
	public List<TDTResource> findTourismOfficeByName(@QueryParam(value = "name") String name) throws Exception {
		return tourismOfficesService.find(name);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("naturalMonumentsResource")
	public List<HTOResource> findNaturalMonumentsByName(@QueryParam(value = "name") String name) throws Exception {
		return naturalMonumentsService.find(name);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("historicalMonumentsResource")
	public List<HTOResource> findHistoricalMonumentsByName(@QueryParam(value = "name") String name) throws Exception {
		return historicalMonumentsService.find(name);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("volcanicsResourcesResource")
	public List<HTOResource> findVolcanicsResourcesByName(@QueryParam(value = "name") String name) throws Exception {
		return volcanicsResourcesService.find(name);
	}


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("weatherPrediction")
	public Object weatherPrediction(@QueryParam(value = "uri") String uri) throws Exception {
		return coreService.getWeatherPrediction(uri);
	}
}
