/**
 * 
 */
package vdi.commons.common;

import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

/**
 * Contains HTTP status codes not in {@link javax.ws.rs.core.Response.Status}.
 */
public enum HttpStatus implements StatusType {
	/**
	 * HTTP status code for insufficient storage.
	 */
	INSUFFICIENT_STORAGE(507, "Insufficient storage.");

	private final int code;
	private final String reason;
	private Family family;

	/**
	 * Implements StatusType.
	 * 
	 * @param statusCode
	 *            new HTTP status code
	 * @param reasonPhrase
	 *            reason phrase for new HTTP status code
	 */
	HttpStatus(final int statusCode, final String reasonPhrase) {
		this.code = statusCode;
		this.reason = reasonPhrase;
		switch (code / 100) {
		case 1:
			this.family = Family.INFORMATIONAL;
			break;
		case 2:
			this.family = Family.SUCCESSFUL;
			break;
		case 3:
			this.family = Family.REDIRECTION;
			break;
		case 4:
			this.family = Family.CLIENT_ERROR;
			break;
		case 5:
			this.family = Family.SERVER_ERROR;
			break;
		default:
			this.family = Family.OTHER;
			break;
		}
	}

	@Override
	public int getStatusCode() {
		return code;
	}

	@Override
	public Family getFamily() {
		return family;
	}

	@Override
	public String getReasonPhrase() {
		return reason;
	}
}
