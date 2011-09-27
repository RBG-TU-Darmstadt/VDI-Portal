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
 * Provides a thread-safe ClientExecutor for RESTEasy ProxyFactorys
 */
public class RESTEasyClientExecutor {

	private static ClientExecutor clientExecutor;

	static {
		HttpParams params = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(params, 8);
		ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(4));

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		// Allow simultaneous connections
		ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(params, registry);

		HttpClient httpClient = new DefaultHttpClient(connectionManager, params);

		clientExecutor = new ApacheHttpClient4Executor(httpClient);
	}

	public static ClientExecutor get() {
		return clientExecutor;
	}

}
