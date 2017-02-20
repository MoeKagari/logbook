package logbook.context.update.data;

import java.util.Date;
import java.util.Map;

import javax.json.JsonObject;

public final class ActionData implements Data {

	private final DataType type;
	private final Date date;
	private final JsonObject json;
	private final Map<String, String> postField;

	public ActionData(DataType type, Date createDate, JsonObject json, Map<String, String> postField) {
		this.type = type;
		this.date = createDate;
		this.json = json;
		this.postField = postField;
	}

	@Override
	public DataType getDataType() {
		return this.type;
	}

	@Override
	public Date getCreateDate() {
		return this.date;
	}

	@Override
	public JsonObject getJsonObject() {
		return this.json;
	}

	@Override
	public String getField(String key) {
		return this.postField == null ? null : this.postField.get(key);
	}

	public Map<String, String> getFields() {
		return this.postField;
	}

	@Override
	public String toString() {
		if (this.json == null) return null;
		return this.type.toString() + ":" + this.json.toString();
	}

}
