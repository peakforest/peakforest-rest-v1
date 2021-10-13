package fr.metabohub.peakforest.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import fr.metabohub.peakforest.model.AbstractDatasetObject;
import fr.metabohub.peakforest.model.compound.StructureChemicalCompound;
import fr.metabohub.peakforest.security.TokenManagementService;
import fr.metabohub.peakforest.services.compound.ChemicalCompoundManagementService;
import fr.metabohub.peakforest.services.compound.GenericCompoundManagementService;
import fr.metabohub.peakforest.services.compound.StructuralCompoundManagementService;
import fr.metabohub.peakforest.utils.PeakForestPruneUtils;
import fr.metabohub.peakforest.ws.struct.ObjectError;
import fr.metabohub.peakforest.ws.utils.JsonDumperTools;

/**
 * 
 * @author Nils Paulhe
 */
@Path("/compounds")
public class CompoundImpl extends SpectralDatabaseImpl {

	// entity scope
	public static final int SCOPE_ALL = 0;
	public static final int SCOPE_GENERIC = 100;
	public static final int SCOPE_CHEMICAL = 101;
	public static final int SCOPE_SUBSTRUCTURE = 11;

	// prop scope
	public static final int SCOPE_ID = 0;
	public static final int SCOPE_INCHI = 1;
	public static final int SCOPE_INCHIKEY = 2;
	public static final int SCOPE_NUMBER = 3;
	public static final int SCOPE_NAME = 4;

	// //////////////////////////////////////////////////////////////////////// ALL

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all")
	public String getAll(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_ALL, SCOPE_INCHIKEY);
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/ids")
	public String getAllIDs(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_ALL, SCOPE_ID);
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/inchi")
	public String getAllInChIs(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_ALL, SCOPE_INCHI);
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/inchikey")
	public String getAllInChIKeys(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_ALL, SCOPE_INCHIKEY);
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/count")
	public String getAllCount(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_ALL, SCOPE_NUMBER);
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/names")
	public String getAllNames(@QueryParam("callback") String callback, @QueryParam("molids") String molids,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json;
		if (molids != null && molids.trim().length() > 0)
			json = getByAllIDs(SCOPE_ALL, SCOPE_NAME, molids.split(","));
		else
			json = getByAllIDs(SCOPE_ALL, SCOPE_NAME);
		return JsonDumperTools.returnCallback(callback, json);
	}

	// ////////////////////////////////////////////////////////////////////////
	// Generic

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/generic/ids")
	public String getGenericIDs(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_GENERIC, SCOPE_ID);
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/generic/inchi")
	public String getGenericInChIs(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_GENERIC, SCOPE_INCHI);
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/generic/inchikey")
	public String getGenericInChIKeys(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_GENERIC, SCOPE_INCHIKEY);
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/generic/count")
	public String getGenericCount(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_GENERIC, SCOPE_NUMBER);
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/generic/names")
	public String getGenericNames(@QueryParam("callback") String callback, @QueryParam("molids") String molids,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json;
		if (molids != null && molids.trim().length() > 0)
			json = getByAllIDs(SCOPE_GENERIC, SCOPE_NAME, molids.split(","));
		else
			json = getByAllIDs(SCOPE_GENERIC, SCOPE_NAME);
		return JsonDumperTools.returnCallback(callback, json);
	}

	// ////////////////////////////////////////////////////////////////////////
	// Chemical

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/chemical/ids")
	public String getChemicalIDs(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_CHEMICAL, SCOPE_ID);
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/chemical/inchi")
	public String getChemicalInChIs(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_CHEMICAL, SCOPE_INCHI);
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/chemical/inchikey")
	public String getChemicalInChIKeys(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_CHEMICAL, SCOPE_INCHIKEY);
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/chemical/count")
	public String getChemicalCount(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_CHEMICAL, SCOPE_NUMBER);
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/chemical/names")
	public String getChemicalNames(@QueryParam("callback") String callback, @QueryParam("molids") String molids,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json;
		if (molids != null && molids.trim().length() > 0)
			json = getByAllIDs(SCOPE_CHEMICAL, SCOPE_NAME, molids.split(","));
		else
			json = getByAllIDs(SCOPE_CHEMICAL, SCOPE_NAME);
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list/{ids}")
	public String getByIDs(@PathParam("ids") String ids, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByIDs(ids);
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public String getByID(@PathParam("id") String id, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByID(id);
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/byInChI")
	public String getByInChI(@QueryParam("InChI") String inChI, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (!TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		final String json = getByInChI(inChI);
		return JsonDumperTools.returnCallback(callback, json);
	}

	// ////////////////////////////////////////////////////////////////////////
	// fetcher

	private String getByAllIDs(int scopeEntity, int scopeProp) {
		return getByAllIDs(scopeEntity, scopeProp, null);
	}

	private String getByAllIDs(int scopeEntity, int scopeProp, String[] molids) {

		// boolean molidsIDs = false;
		// boolean molidsInChIKeys = false;
		List<Long> listIDmatch = new ArrayList<Long>();
		List<String> listInChIKeymatch = new ArrayList<String>();
		if (molids != null)
			for (String id : molids)
				try {
					listIDmatch.add(Long.parseLong(id));
				} catch (NumberFormatException e) {
					listInChIKeymatch.add(id);
				}

		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret = "";
		try {
			ArrayList<Long> listOfIDs = new ArrayList<Long>();
			ArrayList<String> listOfInChIs = new ArrayList<String>();
			ArrayList<String> listOfInChIKeys = new ArrayList<String>();
			ArrayList<String> listOfNames = new ArrayList<String>();
			// ArrayList<String> listOfNamesSorter = new ArrayList<String>();
			HashMap<Long, String> mapIDtoName = new HashMap<Long, String>();
			HashMap<String, String> mapInChIKeytoName = new HashMap<String, String>();
			// get all Compounds
			List<StructureChemicalCompound> listOfStructCC = new ArrayList<StructureChemicalCompound>();
			switch (scopeEntity) {
			case SCOPE_ID | SCOPE_NUMBER:
			default:
				if (scopeProp == SCOPE_NAME)
					listOfStructCC = StructuralCompoundManagementService.readAllWithNames();
				else
					listOfStructCC = StructuralCompoundManagementService.readAll();
				break;
			case SCOPE_GENERIC:
				if (scopeProp == SCOPE_NAME)
					listOfStructCC.addAll(GenericCompoundManagementService.readAllWithNames());
				else
					listOfStructCC.addAll(GenericCompoundManagementService.readAll());
				break;
			case SCOPE_CHEMICAL:
				if (scopeProp == SCOPE_NAME)
					listOfStructCC.addAll(ChemicalCompoundManagementService.readAllWithNames());
				else
					listOfStructCC.addAll(ChemicalCompoundManagementService.readAll());
				break;
			}

			switch (scopeProp) {
			case SCOPE_ID | SCOPE_NUMBER:
			default:
				for (StructureChemicalCompound scc : listOfStructCC)
					listOfIDs.add(scc.getId());
				// return
				if (scopeProp == SCOPE_NUMBER)
					ret = mapper.writeValueAsString(listOfIDs.size());
				else
					ret = mapper.writeValueAsString(listOfIDs);
				break;
			case SCOPE_INCHI:
				for (StructureChemicalCompound scc : listOfStructCC)
					listOfInChIs.add(scc.getInChI());
				// return
				ret = mapper.writeValueAsString(listOfInChIs);
				break;
			case SCOPE_INCHIKEY:
				for (StructureChemicalCompound scc : listOfStructCC)
					listOfInChIKeys.add(scc.getInChIKey());
				// return
				ret = mapper.writeValueAsString(listOfInChIKeys);
				break;
			case SCOPE_NAME:
				// List<Long> listIDmatch = new ArrayList<Long>();
				// List<String> listInChIKeymatch = new ArrayList<String>();
				if (!listIDmatch.isEmpty()) {
					for (StructureChemicalCompound scc : listOfStructCC)
						if (listIDmatch.contains(scc.getId())) {
							listOfNames.add(scc.getMainName());
							mapIDtoName.put(scc.getId(), scc.getMainName());
						}
				} else if (!listInChIKeymatch.isEmpty()) {
					for (StructureChemicalCompound scc : listOfStructCC)
						if (listInChIKeymatch.contains(scc.getInChIKey())) {
							listOfNames.add(scc.getMainName());
							mapInChIKeytoName.put(scc.getInChIKey(), scc.getMainName());
						}
				} else
					for (StructureChemicalCompound scc : listOfStructCC)
						listOfNames.add(scc.getMainName());

				// molids id
				if (!listInChIKeymatch.isEmpty() || !listIDmatch.isEmpty()) {
					listOfNames = new ArrayList<String>();
					if (molids != null)
						for (String id : molids)
							try {
								long idL = Long.parseLong(id);
								if (mapIDtoName.containsKey(idL))
									listOfNames.add(mapIDtoName.get(idL));
								else
									listOfNames.add(null);
							} catch (NumberFormatException e) {
								if (mapInChIKeytoName.containsKey(id))
									listOfNames.add(mapInChIKeytoName.get(id));
								else
									listOfNames.add(null);
							}
				}

				// return
				ret = mapper.writeValueAsString(listOfNames);
				break;

			}

		} catch (Exception e) {
			// Error
			ObjectError err = new ObjectError(e.getMessage());
			try {
				ret = mapper.writeValueAsString(err);
			} catch (IOException e1) {
				ret = "{ success: false, error: \"" + e1.getMessage() + "\"}";
			}
		}
		return ret;
	}

	private String getByIDs(String ids) {
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret = "";
		try {
			List<Long> idsL = new ArrayList<Long>();
			for (String id : ids.split(","))
				idsL.add(Long.parseLong(id));
			// search
			try {
				List<AbstractDatasetObject> compoundRef = new ArrayList<AbstractDatasetObject>();
				compoundRef.addAll(ChemicalCompoundManagementService.read(idsL));
				compoundRef.addAll(GenericCompoundManagementService.read(idsL));
				// prune
				compoundRef = PeakForestPruneUtils.prune(compoundRef);
				// return
				ret = mapper.writeValueAsString(compoundRef);
			} catch (Exception e) {
				// Error
				ObjectError err = new ObjectError(e.getMessage());
				try {
					ret = mapper.writeValueAsString(err);
				} catch (IOException e1) {
					ret = "{ success: false, error: \"" + e1.getMessage() + "\"}";
				}
			}
		} catch (NumberFormatException e) {
			try {
				List<AbstractDatasetObject> compoundRef = new ArrayList<AbstractDatasetObject>();
				compoundRef.addAll(ChemicalCompoundManagementService.readByInChIKeys(Arrays.asList(ids.split(","))));
				compoundRef.addAll(GenericCompoundManagementService.readByInChIKeys(Arrays.asList(ids.split(","))));
				// prune
				compoundRef = PeakForestPruneUtils.prune(compoundRef);
				// return
				ret = mapper.writeValueAsString(compoundRef);
			} catch (Exception eb) {
				// Error
				ObjectError err = new ObjectError(eb.getMessage());
				try {
					ret = mapper.writeValueAsString(err);
				} catch (IOException e1) {
					ret = "{ success: false, error: \"" + e1.getMessage() + "\"}";
				}
			}
		}
		return ret;
	}

	private String getByID(String id) {
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			long idL = Long.parseLong(id);
			// search
			try {
				StructureChemicalCompound compoundRef = ChemicalCompoundManagementService.read(idL);
				if (compoundRef == null)
					compoundRef = GenericCompoundManagementService.read(idL);
				// prune
				compoundRef = (StructureChemicalCompound) PeakForestPruneUtils.prune(compoundRef);
				// return
				ret = mapper.writeValueAsString(compoundRef);
			} catch (Exception e) {
				// Error
				ObjectError err = new ObjectError(e.getMessage());
				try {
					ret = mapper.writeValueAsString(err);
				} catch (IOException e1) {
					ret = "{ success: false, error: \"" + e1.getMessage() + "\"}";
				}
			}
		} catch (NumberFormatException e) {
			return getByInChIKey(id);
		}
		return ret;
	}

	private String getByInChIKey(String inChIKey) {
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			StructureChemicalCompound compoundRef = StructuralCompoundManagementService.readByInChIKey(inChIKey);
			// prune
			compoundRef = (StructureChemicalCompound) PeakForestPruneUtils.prune(compoundRef);
			// return
			ret = mapper.writeValueAsString(compoundRef);
		} catch (Exception e) {
			// Error
			ObjectError err = new ObjectError(e.getMessage());
			try {
				ret = mapper.writeValueAsString(err);
			} catch (IOException e1) {
				ret = "{ success: false, error: \"" + e1.getMessage() + "\"}";
			}
		}
		return ret;
	}

	private String getByInChI(String inChI) {
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			StructureChemicalCompound compoundRef = StructuralCompoundManagementService.readByInChI(inChI);
			// prune
			compoundRef = (StructureChemicalCompound) PeakForestPruneUtils.prune(compoundRef);
			// return
			ret = mapper.writeValueAsString(compoundRef);
		} catch (Exception e) {
			// Error
			ObjectError err = new ObjectError(e.getMessage());
			try {
				ret = mapper.writeValueAsString(err);
			} catch (IOException e1) {
				ret = "{ success: false, error: \"" + e1.getMessage() + "\"}";
			}
		}
		return ret;
	}

}
