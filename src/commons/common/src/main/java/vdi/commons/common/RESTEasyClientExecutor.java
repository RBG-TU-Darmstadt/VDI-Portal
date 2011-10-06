package vdi.commons.common;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;

/**
 * Provides a thread-safe ClientExecutor for RESTEasy ProxyFactorys.
 */
public final class RESTEasyClientExecutor {

	private static ClientExecutor clientExecutor = new ApacheHttpClientExecutor(
			new HttpClient(new MultiThreadedHttpConnectionManager())
	);

	/**
	 * Provides a thread-safe ClientExecutor for RESTEasy ProxyFactorys.
	 * 
	 * @return a thread-safe ClientExecutor
	 */
	public static ClientExecutor get() {
		return clientExecutor;
	}

	/**
	 * Remove ability to be instantiated.
	 */
	private RESTEasyClientExecutor() { }
}
