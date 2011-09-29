package vdi.commons.common;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;

/**
 * Provides a thread-safe ClientExecutor for RESTEasy ProxyFactorys.
 */
public final class RESTEasyClientExecutor {
	private static final int MAX_TOTAL_CONNECTIONS = 8;
	private static final int MAX_CONNECTIONS_PER_ROUTE = 4;
	private static final int HTTP_PORT = 80;
	private static final int HTTPS_PORT = 443;

	private static ClientExecutor clientExecutor;

	static {
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, MAX_TOTAL_CONNECTIONS);
		ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(MAX_CONNECTIONS_PER_ROUTE));

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), HTTP_PORT));
		registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), HTTPS_PORT));

		// Allow simultaneous connections
		ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(params, registry);

		HttpClient httpClient = new DefaultHttpClient(connectionManager, params);

		clientExecutor = new ApacheHttpClient4Executor(httpClient);
	}

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
