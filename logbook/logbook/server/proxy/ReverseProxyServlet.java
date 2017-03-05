package logbook.server.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.api.ProxyConfiguration;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.swt.widgets.Display;

import logbook.config.AppConfig;
import logbook.context.update.GlobalContextUpdater;
import logbook.context.update.data.UndefinedData;

/**
 * リバースプロキシ
 *
 */
@SuppressWarnings("serial")
public final class ReverseProxyServlet extends ProxyServlet {

	private static final Field QUERY_FIELD = getDeclaredField(HttpRequest.class, "query");

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (AppConfig.get().isAllowOnlyFromLocalhost() && !AppConfig.get().isCloseOutsidePort()) {
			if (!InetAddress.getByName(request.getRemoteAddr()).isLoopbackAddress()) {
				response.setStatus(400);
				return;
			}
		}
		super.service(request, response);
	}

	@Override
	protected void customizeProxyRequest(Request proxyRequest, HttpServletRequest httpRequest) {
		proxyRequest.onRequestContent((request, buffer) -> {
			if (((buffer.limit() > 0) && (buffer.limit() <= Filter.MAX_POST_FIELD_SIZE))) {
				httpRequest.setAttribute(Filter.REQUEST_BODY, Arrays.copyOf(buffer.array(), buffer.limit()));
			}
		});

		if (!AppConfig.get().isUseProxy()) {
			if (proxyRequest.getVersion() == HttpVersion.HTTP_1_1) {
				proxyRequest.header(HttpHeader.CONNECTION, "keep-alive");
			}

			String pragma = proxyRequest.getHeaders().get(HttpHeader.PRAGMA);
			if ((pragma != null) && pragma.equals("no-cache")) {
				proxyRequest.header(HttpHeader.PRAGMA, null);
				if (!proxyRequest.getHeaders().containsKey(HttpHeader.CACHE_CONTROL.asString())) {
					proxyRequest.header(HttpHeader.CACHE_CONTROL, "no-cache");
				}
			}
		}

		String queryString = ((org.eclipse.jetty.server.Request) httpRequest).getQueryString();
		fixQueryString(proxyRequest, queryString);

		super.customizeProxyRequest(proxyRequest, httpRequest);
	}

	@Override
	protected String filterResponseHeader(HttpServletRequest request, String headerName, String headerValue) {
		if (headerName.compareToIgnoreCase("Content-Encoding") == 0) {
			request.setAttribute(Filter.CONTENT_ENCODING, headerValue);
		}
		return super.filterResponseHeader(request, headerName, headerValue);
	}

	@Override
	protected void onResponseContent(HttpServletRequest request, HttpServletResponse response, Response proxyResponse, byte[] buffer, int offset, int length) throws IOException {
		ByteArrayOutputStream stream = (ByteArrayOutputStream) request.getAttribute(Filter.RESPONSE_BODY);
		if (stream == null) {
			stream = new ByteArrayOutputStream();
			request.setAttribute(Filter.RESPONSE_BODY, stream);
		}
		stream.write(buffer, offset, length);

		super.onResponseContent(request, response, proxyResponse, buffer, offset, length);
	}

	@Override
	protected void onResponseSuccess(HttpServletRequest request, HttpServletResponse response, Response proxyResponse) {
		byte[] postField = (byte[]) request.getAttribute(Filter.REQUEST_BODY);
		ByteArrayOutputStream stream = (ByteArrayOutputStream) request.getAttribute(Filter.RESPONSE_BODY);
		String contentEncoding = (String) request.getAttribute(Filter.CONTENT_ENCODING);
		String contentType = response.getContentType();
		String serverName = request.getServerName();
		String fullUrl = request.getRequestURL().toString();
		String url = request.getRequestURI();
		if (stream != null) {
			UndefinedData rawData = new UndefinedData(fullUrl, url, postField, stream.toByteArray());
			UndefinedData undefinedData = rawData.decode(contentEncoding);
			Display.getDefault().asyncExec(() -> GlobalContextUpdater.update(undefinedData, serverName, contentType));
		} else {
			System.out.println("stream == null : " + fullUrl);
		}

		super.onResponseSuccess(request, response, proxyResponse);
	}

	@Override
	protected HttpClient newHttpClient() {
		HttpClient client = super.newHttpClient();
		if (AppConfig.get().isUseProxy()) {
			int port = AppConfig.get().getProxyPort();
			String host = AppConfig.get().getProxyHost();
			client.setProxyConfiguration(new ProxyConfiguration(host, port));
		}
		return client;
	}

	private static <T> Field getDeclaredField(Class<T> clazz, String string) {
		try {
			Field field = clazz.getDeclaredField(string);
			field.setAccessible(true);
			return field;
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * <p>
	 * ライブラリのバグを修正します<br>
	 * URLにマルチバイト文字が含まれている場合にURLが正しく組み立てられないバグを修正します
	 * </p>
	 */
	private static void fixQueryString(Request proxyRequest, String queryString) {
		if (StringUtils.isNotEmpty(queryString)) {
			if (proxyRequest instanceof HttpRequest) {
				try {
					QUERY_FIELD.set(proxyRequest, queryString);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

}
