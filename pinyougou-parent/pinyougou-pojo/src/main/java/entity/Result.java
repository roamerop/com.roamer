package entity;

import java.io.Serializable;

/**
 * 增删改结果封装
 * 
 * @author Think
 *
 */
public class Result implements Serializable {

	private boolean success;

	private String message;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Result(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}

}
