package logbook.context.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.commons.io.IOUtils;

public class UndefinedData implements Data {

	private final String fullUrl;// "http://...."
	private final String url; // "/ksapi/..."
	private final byte[] request;
	private final byte[] response;
	private final Date date;

	public UndefinedData(String fullUrl, String url, byte[] request, byte[] response) {
		this(fullUrl, url, request, response, Calendar.getInstance().getTime());
	}

	public UndefinedData(String fullUrl, String url, byte[] request, byte[] response, Date date) {
		this.fullUrl = fullUrl;
		this.url = url;
		this.request = request;
		this.response = response;
		this.date = date;
	}

	@Override
	public final DataType getDataType() {
		return null;
	}

	@Override
	public final Date getCreateDate() {
		return this.date;
	}

	@Override
	public final JsonObject getJsonObject() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final String getField(String key) {
		throw new UnsupportedOperationException();
	}

	/*------------------------------------------------------------------------------------------------------------------------------*/

	public String getFullUrl() {
		return this.fullUrl;
	}

	public String getUrl() {
		return this.url;
	}

	public byte[] getRequest() {
		return this.request;
	}

	public byte[] getResponse() {
		return this.response;
	}

	/*------------------------------------------------------------------------------------------------------------------------------*/

	public UndefinedData decode(String contentEncoding) {
		try {
			if ((contentEncoding != null) && contentEncoding.equals("gzip")) {
				byte[] responseDecoded = IOUtils.toByteArray(new GZIPInputStream(new ByteArrayInputStream(this.response)));
				return new UndefinedData(this.fullUrl, this.url, this.request, responseDecoded, this.date);
			}
		} catch (IOException e) {

		}
		return this;
	}

	public final Data toDefinedData() {
		if (this.response.length == 0) return this;

		DataType type = DataType.TYPEMAP.get(this.url);
		if (type == null) return this;

		try {
			// リクエストのフィールドを復号します
			Map<String, String> field = null;
			if (this.request != null) {
				field = getQueryMap(URLDecoder.decode(new String(this.request).trim(), "UTF-8"));
			}

			JsonObject json = responseToJsonObiect(this.response);
			if (json == null) return this;

			return new ActionData(type, this.date, json, field);
		} catch (Exception e) {
			return this;
		}
	}

	public static Map<String, String> getQueryMap(String query) {
		String[] params = query.trim().split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String param : params) {
			String[] splited = param.split("=");
			String name = splited[0];
			String value = null;
			if (splited.length == 2) value = splited[1];
			map.put(name, value);
		}
		return map;
	}

	public static JsonObject responseToJsonObiect(byte[] response) {
		try {
			InputStream stream = new ByteArrayInputStream(response);
			if ((response[0] == (byte) 0x1f) && (response[1] == (byte) 0x8b)) {
				stream = new GZIPInputStream(stream);
			}

			int read;
			while (((read = stream.read()) != -1) && (read != '=')) {}

			return Json.createReader(stream).readObject();
		} catch (IOException e) {
			return null;
		}
	}

}
