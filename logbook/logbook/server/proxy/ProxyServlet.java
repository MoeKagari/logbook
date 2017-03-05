//
//  ========================================================================
//  Copyright (c) 1995-2013 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package logbook.server.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.InputStreamContentProvider;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.proxy.ConnectHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.HttpCookieStore;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 * Asynchronous ProxyServlet.
 * <p/>
 * Forwards requests to another server either as a standard web reverse proxy
 * (as defined by RFC2616) or as a transparent reverse proxy.
 * <p/>
 * To facilitate JMX monitoring, the {@link HttpClient} instance is set as
 * context attribute, prefixed with the servlet's name and exposed by the
 * mechanism provided by {@link ContextHandler#MANAGED_ATTRIBUTES}.
 * <p/>
 * The following init parameters may be used to configure the servlet:
 * <ul>
 * <li>hostHeader - forces the host header to a particular value</li>
 * <li>viaHost - the name to use in the Via header: Via: http/1.1
 * &lt;viaHost&gt;</li>
 * </ul>
 * <p/>
 * In addition, see {@link #createHttpClient()} for init parameters used to
 * configure the {@link HttpClient} instance.
 *
 * @see ConnectHandler
 */
@SuppressWarnings("serial")
public class ProxyServlet extends HttpServlet {
	protected static final String ASYNC_CONTEXT = ProxyServlet.class.getName() + ".asyncContext";
	private static final Set<String> HOP_HEADERS = new HashSet<>();
	static {
		HOP_HEADERS.add("proxy-connection");
		HOP_HEADERS.add("connection");
		HOP_HEADERS.add("keep-alive");
		HOP_HEADERS.add("transfer-encoding");
		HOP_HEADERS.add("te");
		HOP_HEADERS.add("trailer");
		HOP_HEADERS.add("proxy-authorization");
		HOP_HEADERS.add("proxy-authenticate");
		HOP_HEADERS.add("upgrade");
	}

	private String _hostHeader;
	private HttpClient _client;
	private long _timeout;

	@Override
	public void init() throws ServletException {
		ServletConfig config = this.getServletConfig();

		this._hostHeader = config.getInitParameter("hostHeader");

		try {
			this._client = this.createHttpClient();
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	public long getTimeout() {
		return this._timeout;
	}

	@Override
	public void destroy() {
		try {
			this._client.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a {@link HttpClient} instance, configured with init parameters of
	 * this servlet.
	 * <p/>
	 * The init parameters used to configure the {@link HttpClient} instance
	 * are:
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>init-param</th>
	 * <th>default</th>
	 * <th>description</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>maxThreads</td>
	 * <td>256</td>
	 * <td>The max number of threads of HttpClient's Executor</td>
	 * </tr>
	 * <tr>
	 * <td>maxConnections</td>
	 * <td>32768</td>
	 * <td>The max number of connections per destination, see
	 * {@link HttpClient#setMaxConnectionsPerDestination(int)}</td>
	 * </tr>
	 * <tr>
	 * <td>idleTimeout</td>
	 * <td>30000</td>
	 * <td>The idle timeout in milliseconds, see
	 * {@link HttpClient#setIdleTimeout(long)}</td>
	 * </tr>
	 * <tr>
	 * <td>timeout</td>
	 * <td>60000</td>
	 * <td>The total timeout in milliseconds, see
	 * {@link Request#timeout(long, TimeUnit)}</td>
	 * </tr>
	 * <tr>
	 * <td>requestBufferSize</td>
	 * <td>HttpClient's default</td>
	 * <td>The request buffer size, see
	 * {@link HttpClient#setRequestBufferSize(int)}</td>
	 * </tr>
	 * <tr>
	 * <td>responseBufferSize</td>
	 * <td>HttpClient's default</td>
	 * <td>The response buffer size, see
	 * {@link HttpClient#setResponseBufferSize(int)}</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 *
	 * @return a {@link HttpClient} configured from the
	 *         {@link #getServletConfig() servlet configuration}
	 * @throws ServletException
	 *             if the {@link HttpClient} cannot be created
	 */
	protected HttpClient createHttpClient() throws ServletException {
		ServletConfig config = this.getServletConfig();

		HttpClient client = this.newHttpClient();
		// Redirects must be proxied as is, not followed
		client.setFollowRedirects(false);

		// Must not store cookies, otherwise cookies of different clients will
		// mix
		client.setCookieStore(new HttpCookieStore.Empty());

		String value = config.getInitParameter("maxThreads");
		if (value == null) value = "256";
		QueuedThreadPool executor = new QueuedThreadPool(Integer.parseInt(value));
		String servletName = config.getServletName();
		int dot = servletName.lastIndexOf('.');
		if (dot >= 0) servletName = servletName.substring(dot + 1);
		executor.setName(servletName);
		client.setExecutor(executor);

		value = config.getInitParameter("maxConnections");
		if (value == null) value = "32768";
		client.setMaxConnectionsPerDestination(Integer.parseInt(value));

		value = config.getInitParameter("idleTimeout");
		if (value == null) value = "30000";
		client.setIdleTimeout(Long.parseLong(value));

		value = config.getInitParameter("timeout");
		if (value == null) value = "60000";
		this._timeout = Long.parseLong(value);

		value = config.getInitParameter("requestBufferSize");
		if (value != null) client.setRequestBufferSize(Integer.parseInt(value));

		value = config.getInitParameter("responseBufferSize");
		if (value != null) client.setResponseBufferSize(Integer.parseInt(value));

		try {
			client.start();

			// Content must not be decoded, otherwise the client gets confused
			client.getContentDecoderFactories().clear();

			return client;
		} catch (Exception x) {
			throw new ServletException(x);
		}
	}

	/**
	 * @return a new HttpClient instance
	 */
	protected HttpClient newHttpClient() {
		return new HttpClient();
	}

	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		URI rewrittenURI = this.rewriteURI(request);

		if (rewrittenURI == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		AsyncContext asyncContext = request.startAsync();
		// We do not timeout the continuation, but the proxy request
		asyncContext.setTimeout(0);
		request.setAttribute(ASYNC_CONTEXT, asyncContext);

		ProxyRequestHandler proxyRequestHandler = new ProxyRequestHandler(request, response, rewrittenURI);
		proxyRequestHandler.send();
	}

	private Request createProxyRequest(HttpServletRequest request, HttpServletResponse response, URI targetUri, ContentProvider contentProvider) {
		final Request proxyRequest = this._client.newRequest(targetUri).method(HttpMethod.fromString(request.getMethod())).version(HttpVersion.fromString(request.getProtocol()));

		// Copy headers
		for (Enumeration<String> headerNames = request.getHeaderNames(); headerNames.hasMoreElements();) {
			String headerName = headerNames.nextElement();
			String lowerHeaderName = headerName.toLowerCase(Locale.ENGLISH);

			// Remove hop-by-hop headers
			if (HOP_HEADERS.contains(lowerHeaderName)) continue;
			if ((this._hostHeader != null) && lowerHeaderName.equals("host")) continue;

			for (Enumeration<String> headerValues = request.getHeaders(headerName); headerValues.hasMoreElements();) {
				String headerValue = headerValues.nextElement();
				if (headerValue != null) proxyRequest.header(headerName, headerValue);
			}
		}

		// Force the Host header if configured
		if (this._hostHeader != null) proxyRequest.header(HttpHeader.HOST, this._hostHeader);

		proxyRequest.content(contentProvider);
		this.customizeProxyRequest(proxyRequest, request);
		proxyRequest.timeout(this.getTimeout(), TimeUnit.MILLISECONDS);
		return proxyRequest;
	}

	protected void onResponseHeaders(HttpServletRequest request, HttpServletResponse response, Response proxyResponse) {
		for (HttpField field : proxyResponse.getHeaders()) {
			String headerName = field.getName();
			String lowerHeaderName = headerName.toLowerCase(Locale.ENGLISH);
			if (HOP_HEADERS.contains(lowerHeaderName)) continue;

			String newHeaderValue = this.filterResponseHeader(request, headerName, field.getValue());
			if ((newHeaderValue == null) || (newHeaderValue.trim().length() == 0)) continue;

			response.addHeader(headerName, newHeaderValue);
		}
	}

	protected void onResponseContent(HttpServletRequest request, HttpServletResponse response, Response proxyResponse, byte[] buffer, int offset, int length) throws IOException {
		response.getOutputStream().write(buffer, offset, length);
	}

	protected void onResponseSuccess(HttpServletRequest request, HttpServletResponse response, Response proxyResponse) {
		AsyncContext asyncContext = (AsyncContext) request.getAttribute(ASYNC_CONTEXT);
		asyncContext.complete();
	}

	protected void onResponseFailure(HttpServletRequest request, HttpServletResponse response, Response proxyResponse, Throwable failure) {
		if (!response.isCommitted()) {
			if (failure instanceof TimeoutException) response.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
			else response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
		}
		AsyncContext asyncContext = (AsyncContext) request.getAttribute(ASYNC_CONTEXT);
		asyncContext.complete();
	}

	protected URI rewriteURI(HttpServletRequest request) {
		StringBuffer uri = request.getRequestURL();
		String query = request.getQueryString();
		if (query != null) uri.append("?").append(query);
		return URI.create(uri.toString());
	}

	/**
	 * Extension point for subclasses to customize the proxy request. The
	 * default implementation does nothing.
	 *
	 * @param proxyRequest
	 *            the proxy request to customize
	 * @param request
	 *            the request to be proxied
	 */
	protected void customizeProxyRequest(Request proxyRequest, HttpServletRequest request) {}

	/**
	 * Extension point for remote server response header filtering. The default
	 * implementation returns the header value as is. If null is returned, this
	 * header won't be forwarded back to the client.
	 *
	 * @param headerName
	 *            the header name
	 * @param headerValue
	 *            the header value
	 * @param request
	 *            the request to proxy
	 * @return filteredHeaderValue the new header value
	 */
	protected String filterResponseHeader(HttpServletRequest request, String headerName, String headerValue) {
		return headerValue;
	}

	private class ProxyRequestHandler extends Response.Listener.Empty {
		private final HttpServletRequest request;
		private final HttpServletResponse response;
		private final URI targetUri;
		private final InputStream contentInputStream;

		public ProxyRequestHandler(HttpServletRequest request, HttpServletResponse response, URI targetUri) throws IOException {
			this.request = request;
			this.response = response;
			this.targetUri = targetUri;
			this.contentInputStream = request.getInputStream();
		}

		public void send() {
			final HttpServletRequest request = this.request;
			Request proxyRequest = ProxyServlet.this.createProxyRequest(request, this.response, this.targetUri, new InputStreamContentProvider(this.contentInputStream) {
				@Override
				public long getLength() {
					return request.getContentLength();
				}
			});
			proxyRequest.send(this);
		}

		@Override
		public void onBegin(Response proxyResponse) {
			this.response.setStatus(proxyResponse.getStatus());
		}

		@Override
		public void onHeaders(Response proxyResponse) {
			ProxyServlet.this.onResponseHeaders(this.request, this.response, proxyResponse);
		}

		@Override
		public void onContent(Response proxyResponse, ByteBuffer content) {
			byte[] buffer;
			int offset;
			int length = content.remaining();
			if (content.hasArray()) {
				buffer = content.array();
				offset = content.arrayOffset();
			} else {
				buffer = new byte[length];
				content.get(buffer);
				offset = 0;
			}

			try {
				ProxyServlet.this.onResponseContent(this.request, this.response, proxyResponse, buffer, offset, length);
			} catch (IOException x) {
				proxyResponse.abort(x);
			}
		}

		@Override
		public void onSuccess(Response proxyResponse) {
			ProxyServlet.this.onResponseSuccess(this.request, this.response, proxyResponse);
		}

		@Override
		public void onFailure(Response proxyResponse, Throwable failure) {
			ProxyServlet.this.onResponseFailure(this.request, this.response, proxyResponse, failure);
		}

		@Override
		public void onComplete(Result result) {}
	}

}
