package sys;

import java.util.ArrayList;
import java.util.List;

public class AppError {

	public Integer code = null;
	public List<String> descriptions = new ArrayList<>();

	public AppError() {
	}

	public AppError(Integer code, String description) {
		this.code = code;
		this.descriptions.add(description);
	}

	public AppError(Integer code, List<String> descriptions) {
		this.code = code;
		this.descriptions.addAll(descriptions);
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public List<String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}

	

}
