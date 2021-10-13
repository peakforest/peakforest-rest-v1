package fr.metaboHub.peakforest.ws.struct;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Create an object response use to for XML / Json response
 * 
 * @author Nils Paulhe
 * 
 */
public class ObjectResponse {

	/* ********************************************************************* */
	/* Class parameters */
	/* ********************************************************************* */
	boolean success = false;
	String error = "";

	/* ********************************************************************* */
	/* Class constructors */
	/* ********************************************************************* */
	/**
	 * basic empty constructor
	 */
	public ObjectResponse() {
		super();
	}

	/* ********************************************************************* */
	/* Class getters / setters */
	/* ********************************************************************* */
	/**
	 * @return the success
	 */
	@JsonProperty("success")
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @param success
	 *            the success to set
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * @return the error
	 */
	@JsonProperty("error")
	public String getError() {
		return error;
	}

	/**
	 * @param error
	 *            the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

}
