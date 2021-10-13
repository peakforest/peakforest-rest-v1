package fr.metabohub.peakforest.ws;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;

import fr.metabohub.peakforest.utils.PeakForestUtils;
import fr.metabohub.peakforest.ws.struct.ObjectError;
import fr.metabohub.peakforest.ws.utils.JsonDumperTools;

/**
 * 
 * @author Nils Paulhe
 * 
 */
@Path("/")
public class IndexImpl extends SpectralDatabaseImpl {

	/**
	 * Webservices' documentation URL
	 */
	public static final String docURL = PeakForestUtils.getBundleString(ResourceBundle.getBundle("info"), "ws_doc_url");

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public String index(@QueryParam("callback") String callback) {
		// based on POST method
		String json = index();
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(".war")
	public String war(@QueryParam("callback") String callback) {
		// based on POST method
		String json = index();
		// returning JSON or JSONP
		return JsonDumperTools.returnCallback(callback, json);
	}

	private String index() {
		ObjectMapper mapper = new ObjectMapper();
		String ret;
		// Error
		try {
			ret = mapper.writeValueAsString(new ObjectError(
					"path '/' is not valide, please see webservice documentation at: '" + docURL + "'"));
		} catch (final IOException e1) {
			ret = "{\"success\":false,\"error\":\"" + e1.getMessage() + "\"}";
		}
		return ret;
	}

}
