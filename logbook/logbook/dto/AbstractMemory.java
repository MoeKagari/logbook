package logbook.dto;

import java.io.Serializable;

public abstract class AbstractMemory extends AbstractData implements Serializable {
	private static final long serialVersionUID = 1L;

	public abstract long getTime();

	public boolean isBattle() {
		return false;
	}
}
