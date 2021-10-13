package fr.metaboHub.peakforest.ws;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import fr.metaboHub.peakforest.ws.struct.ObjectError;
import fr.metabohub.peakforest.utils.Utils;

/**
 * 
 * @author Nils Paulhe
 * 
 */
@Path("/")
public class IndexImpl extends SpectralDatabaseImpl {

	public static final String docURL = Utils.getBundleInfoElement("ws_doc_url");

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public String index(@QueryParam("callback") String callback) {
		// based on POST method
		String json = index();
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(".war")
	public String war(@QueryParam("callback") String callback) {
		// based on POST method
		String json = index();
		// returning JSON or JSONP
		if (callback != null && callback.trim().length() > 0)
			return callback + "(" + json + ")";
		return json;
	}

	// @GET
	// @Produces(MediaType.APPLICATION_JSON)
	public String index() {
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		// Error
		try {
			ret = mapper.writeValueAsString(new ObjectError(
					"path '/' is not valide, please see webservice documentation at: '" + docURL + "'"));
		} catch (IOException e1) {
			ret = "{ success: false, error: \"" + e1.getMessage() + "\"}";
		}
		return ret;
	}

}
