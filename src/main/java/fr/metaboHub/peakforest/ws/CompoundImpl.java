package fr.metaboHub.peakforest.ws;

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

import fr.metaboHub.peakforest.security.TokenManagementService;
import fr.metaboHub.peakforest.ws.struct.ObjectError;
import fr.metabohub.peakforest.model.AbstractDatasetObject;
import fr.metabohub.peakforest.model.compound.StructureChemicalCompound;
import fr.metabohub.peakforest.services.compound.ChemicalCompoundManagementService;
import fr.metabohub.peakforest.services.compound.GenericCompoundManagementService;
import fr.metabohub.peakforest.services.compound.StructuralCompoundManagementService;
import fr.metabohub.peakforest.utils.Utils;

/**
 * 
 * @author Nils Paulhe
 * 
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

	/**
	 * @param callback
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all")
	public String getAll(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_ALL, SCOPE_INCHIKEY);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param callback
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/ids")
	public String getAllIDs(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_ALL, SCOPE_ID);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/inchi")
	public String getAllInChIs(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_ALL, SCOPE_INCHI);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/inchikey")
	public String getAllInChIKeys(@QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_ALL, SCOPE_INCHIKEY);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/count")
	public String getAllCount(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_ALL, SCOPE_NUMBER);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/names")
	public String getAllNames(@QueryParam("callback") String callback, @QueryParam("molids") String molids,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json;
		if (molids != null && molids.trim().length() > 0)
			json = getByAllIDs(SCOPE_ALL, SCOPE_NAME, molids.split(","));
		else
			json = getByAllIDs(SCOPE_ALL, SCOPE_NAME);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	// //////////////////////////////////////////////////////////////////////// Generic
	/**
	 * @param callback
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/generic/ids")
	public String getGenericIDs(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_GENERIC, SCOPE_ID);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/generic/inchi")
	public String getGenericInChIs(@QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_GENERIC, SCOPE_INCHI);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/generic/inchikey")
	public String getGenericInChIKeys(@QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_GENERIC, SCOPE_INCHIKEY);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/generic/count")
	public String getGenericCount(@QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_GENERIC, SCOPE_NUMBER);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/generic/names")
	public String getGenericNames(@QueryParam("callback") String callback,
			@QueryParam("molids") String molids, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json;
		if (molids != null && molids.trim().length() > 0)
			json = getByAllIDs(SCOPE_GENERIC, SCOPE_NAME, molids.split(","));
		else
			json = getByAllIDs(SCOPE_GENERIC, SCOPE_NAME);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	// //////////////////////////////////////////////////////////////////////// Chemical
	/**
	 * @param callback
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/chemical/ids")
	public String getChemicalIDs(@QueryParam("callback") String callback, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_CHEMICAL, SCOPE_ID);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/chemical/inchi")
	public String getChemicalInChIs(@QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_CHEMICAL, SCOPE_INCHI);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/chemical/inchikey")
	public String getChemicalInChIKeys(@QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_CHEMICAL, SCOPE_INCHIKEY);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/chemical/count")
	public String getChemicalCount(@QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByAllIDs(SCOPE_CHEMICAL, SCOPE_NUMBER);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/chemical/names")
	public String getChemicalNames(@QueryParam("callback") String callback,
			@QueryParam("molids") String molids, @QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json;
		if (molids != null && molids.trim().length() > 0)
			json = getByAllIDs(SCOPE_CHEMICAL, SCOPE_NAME, molids.split(","));
		else
			json = getByAllIDs(SCOPE_CHEMICAL, SCOPE_NAME);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	// //////////////////////////////////////////////////////////////////////// fetcher

	/**
	 * @param scopeEntity
	 * @param scopeProp
	 * @return
	 */
	private String getByAllIDs(int scopeEntity, int scopeProp) {
		return getByAllIDs(scopeEntity, scopeProp, null);
	}

	/**
	 * @param scopeEntity
	 * @param scopeProp
	 * @param molids
	 * @return
	 */
	private String getByAllIDs(int scopeEntity, int scopeProp, String[] molids) {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");

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
					listOfStructCC = StructuralCompoundManagementService.readAllWithNames(dbName, login,
							password);
				else
					listOfStructCC = StructuralCompoundManagementService.readAll(dbName, login, password);
				break;
			case SCOPE_GENERIC:
				if (scopeProp == SCOPE_NAME)
					listOfStructCC.addAll(
							GenericCompoundManagementService.readAllWithNames(dbName, login, password));
				else
					listOfStructCC.addAll(GenericCompoundManagementService.readAll(dbName, login, password));
				break;
			case SCOPE_CHEMICAL:
				if (scopeProp == SCOPE_NAME)
					listOfStructCC.addAll(
							ChemicalCompoundManagementService.readAllWithNames(dbName, login, password));
				else
					listOfStructCC.addAll(ChemicalCompoundManagementService.readAll(dbName, login, password));
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

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list/{ids}")
	public String getByIDs(@PathParam("ids") String ids, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByIDs(ids);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param ids
	 * @return
	 */
	private String getByIDs(String ids) {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
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
				compoundRef.addAll(ChemicalCompoundManagementService.read(idsL, dbName, login, password));
				compoundRef.addAll(GenericCompoundManagementService.read(idsL, dbName, login, password));
				// prune
				compoundRef = Utils.prune(compoundRef);
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
				compoundRef.addAll(ChemicalCompoundManagementService
						.readByInChIKeys(Arrays.asList(ids.split(",")), dbName, login, password));
				compoundRef.addAll(GenericCompoundManagementService
						.readByInChIKeys(Arrays.asList(ids.split(",")), dbName, login, password));
				// prune
				compoundRef = Utils.prune(compoundRef);
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

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public String getByID(@PathParam("id") String id, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByID(id);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param id
	 * @return
	 */
	private String getByID(String id) {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			long idL = Long.parseLong(id);
			// search
			try {
				StructureChemicalCompound compoundRef = ChemicalCompoundManagementService.read(idL, dbName,
						login, password);
				if (compoundRef == null)
					compoundRef = GenericCompoundManagementService.read(idL, dbName, login, password);
				// prune
				compoundRef = (StructureChemicalCompound) Utils.prune(compoundRef);
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

	// /**
	// * @param inChIKey
	// * @param callback
	// * @return
	// */
	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	// @Path("/byInChIKey/{InChIKey}")
	// public String getByInChIKey(@PathParam("InChIKey") String inChIKey,
	// @QueryParam("callback") String callback) {
	// String json = getByInChIKey(inChIKey);
	// // returning JSON or JSONP
	// if (callback != null && callback.trim().length() > 0)
	// return callback + "(" + json + ")";
	// return json;
	// }

	/**
	 * @param inChIKey
	 * @return
	 */
	private String getByInChIKey(String inChIKey) {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			StructureChemicalCompound compoundRef = StructuralCompoundManagementService
					.readByInChIKey(inChIKey, dbName, login, password);
			// prune
			compoundRef = (StructureChemicalCompound) Utils.prune(compoundRef);
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

	/**
	 * @param query
	 * @param max
	 * @param callback
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/byInChI")
	public String getByInChI(@QueryParam("InChI") String inChI, @QueryParam("callback") String callback,
			@QueryParam("token") String token) {
		// check token
		if (token == null || token.trim().length() == 0 || !TokenManagementService.isTokenValide(token)) {
			return TokenManagementService.tokenError(callback);
		}
		String json = getByInChI(inChI);
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	/**
	 * @param query
	 * @param max
	 * @return
	 */
	private String getByInChI(String inChI) {
		// init
		String dbName = Utils.getBundleConfElement("hibernate.connection.database.dbName");
		String login = Utils.getBundleConfElement("hibernate.connection.database.username");
		String password = Utils.getBundleConfElement("hibernate.connection.database.password");
		// run
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		try {
			// search
			StructureChemicalCompound compoundRef = StructuralCompoundManagementService.readByInChI(inChI,
					dbName, login, password);
			// prune
			compoundRef = (StructureChemicalCompound) Utils.prune(compoundRef);
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
