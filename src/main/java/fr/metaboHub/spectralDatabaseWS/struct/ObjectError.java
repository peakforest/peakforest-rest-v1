package fr.metaboHub.spectralDatabaseWS.struct;

/**
 * Base use to create a object formated response (json or xml)
 * 
 * @author Nils Paulhe
 * 
 */
public class ObjectError {
	/* ********************************************************************* */
	/* Class parameters */
	/* ********************************************************************* */
	boolean success = false;
	String error = null;

	/* ********************************************************************* */
	/* Class constructors */
	/* ********************************************************************* */
	/**
	 * basic empty constructor
	 */
	public ObjectError() {
		super();
		this.error = "";
	}

	/**
	 * create an Error response an set the message
	 * 
	 * @param error
	 */
	public ObjectError(String error) {
		super();
		this.error = error;
	}

	/* ********************************************************************* */
	/* Class getters / setters */
	/* ********************************************************************* */
	/**
	 * @return the success
	 */
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
