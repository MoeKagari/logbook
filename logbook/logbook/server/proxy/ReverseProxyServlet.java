package logbook.server.proxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;

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

	/** ライブラリバグ対応 (HttpRequest#queryを上書きする) */
	private static final Field QUERY_FIELD = getDeclaredField(HttpRequest.class, "query");

	/*
	 * リモートホストがローカルループバックアドレス以外の場合400を返し通信しない
	 */
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

	/*
	 * Hop-by-Hop ヘッダーを除去します
	 */
	@Override
	protected void customizeProxyRequest(Request proxyRequest, HttpServletRequest request) {
		proxyRequest.onRequestContent(new RequestContentListener(request));

		if (!AppConfig.get().isUseProxy()) { // アップストリームプロキシがある場合は除外

			// HTTP/1.1 ならkeep-aliveを追加します
			if (proxyRequest.getVersion() == HttpVersion.HTTP_1_1) {
				proxyRequest.header(HttpHeader.CONNECTION, "keep-alive");
			}

			// Pragma: no-cache はプロキシ用なので Cache-Control: no-cache に変換します
			String pragma = proxyRequest.getHeaders().get(HttpHeader.PRAGMA);
			if ((pragma != null) && pragma.equals("no-cache")) {
				proxyRequest.header(HttpHeader.PRAGMA, null);
				if (!proxyRequest.getHeaders().containsKey(HttpHeader.CACHE_CONTROL.asString())) {
					proxyRequest.header(HttpHeader.CACHE_CONTROL, "no-cache");
				}
			}
		}

		String queryString = ((org.eclipse.jetty.server.Request) request).getQueryString();
		fixQueryString(proxyRequest, queryString);

		super.customizeProxyRequest(proxyRequest, request);
	}

	@Override
	protected String filterResponseHeader(HttpServletRequest request, String headerName, String headerValue) {
		// Content Encoding を取得する
		if (headerName.compareToIgnoreCase("Content-Encoding") == 0) {
			request.setAttribute(Filter.CONTENT_ENCODING, headerValue);
		}
		return super.filterResponseHeader(request, headerName, headerValue);
	}

	/*
	 * レスポンスが帰ってきた
	 */
	@Override
	protected void onResponseContent(HttpServletRequest request, HttpServletResponse response, Response proxyResponse, byte[] buffer, int offset, int length) throws IOException {

		// 注意: 1回のリクエストで複数回の応答が帰ってくるので全ての応答をキャプチャする必要がある
		ByteArrayOutputStream stream = (ByteArrayOutputStream) request.getAttribute(Filter.RESPONSE_BODY);
		if (stream == null) {
			stream = new ByteArrayOutputStream();
			request.setAttribute(Filter.RESPONSE_BODY, stream);
		}
		// ストリームに書き込む
		stream.write(buffer, offset, length);

		super.onResponseContent(request, response, proxyResponse, buffer, offset, length);
	}

	/*
	 * レスポンスが完了した
	 */
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

	/*
	 * HttpClientを作成する
	 */
	@Override
	protected HttpClient newHttpClient() {
		HttpClient client = super.newHttpClient();
		// プロキシを設定する
		if (AppConfig.get().isUseProxy()) {
			// ポート
			int port = AppConfig.get().getProxyPort();
			// ホスト
			String host = AppConfig.get().getProxyHost();
			// 設定する
			client.setProxyConfiguration(new ProxyConfiguration(host, port));
		}
		return client;
	}

	/**
	 * private フィールドを取得する
	 * @param clazz クラス
	 * @param string フィールド名
	 * @return フィールドオブジェクト
	 */
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
		if (!StringUtils.isEmpty(queryString)) {
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
