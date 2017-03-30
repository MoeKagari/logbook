package logbook.server.proxy;

/**
 * 動作に必要なデータのみ取得するためのフィルターです。
 *
 */
public class Filter {

	/** フィルターするContent-Type */
	public static final String CONTENT_TYPE_FILTER = "text/plain";

	/** キャプチャーするリクエストのバイトサイズ上限 */
	public static final int MAX_POST_FIELD_SIZE = 1024 * 1024;

	/** setAttribute用のキー(Response) */
	public static final String RESPONSE_BODY = "res-body";

	/** setAttribute用のキー(Request) */
	public static final String REQUEST_BODY = "req-body";

	/** setAttribute用のキー(Content-Encoding) */
	public static final String CONTENT_ENCODING = "logbook.content-encoding";

}
