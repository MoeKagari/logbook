package logbook.server.proxy;

import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Request.ContentListener;

/**
 * リクエストをキャプチャします
 *
 */
public final class RequestContentListener implements ContentListener {

	private final HttpServletRequest httpRequest;

	public RequestContentListener(HttpServletRequest request) {
		this.httpRequest = request;
	}

	@Override
	public void onContent(Request request, ByteBuffer buffer) {
		if (((buffer.limit() > 0) && (buffer.limit() <= Filter.MAX_POST_FIELD_SIZE))) {
			this.httpRequest.setAttribute(Filter.REQUEST_BODY, Arrays.copyOf(buffer.array(), buffer.limit()));
		}
	}

}
