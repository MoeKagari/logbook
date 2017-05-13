package logbook.server.proxy;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import logbook.update.GlobalContextUpdater;

/**
 * 为了使用缓存
 * @author MoeKagari
 */
@SuppressWarnings("serial")
public class CacheReverseProxyServlet extends ReverseProxyServlet {
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String serverName = request.getServerName();
		String url = request.getRequestURI();
		if (GlobalContextUpdater.SERVER_LIST.contains(serverName)) {
			if (url != null && url.startsWith("/kcs/")) {
				File cacheFile = new File("D:\\kancolle\\ShimakazeGo\\cache" + url.replace('/', '\\'));
				if (cacheFile.exists() && cacheFile.isFile()) {
					byte[] bytes = FileUtils.readFileToByteArray(cacheFile);
					response.getOutputStream().write(bytes, 0, bytes.length);
					return;
				}
			}
		}

		super.service(request, response);
	}
}
