package logbook.context.data;

import java.util.Date;

import javax.json.JsonObject;

public interface Data {

	DataType getDataType();

	Date getCreateDate();

	JsonObject getJsonObject();

	String getField(String key);

}
