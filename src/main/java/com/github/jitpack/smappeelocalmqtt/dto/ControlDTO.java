package com.github.jitpack.smappeelocalmqtt.dto;

public class ControlDTO {
	private String id;
	private boolean status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ControlDTO{" +
				"id='" + id + '\'' +
				", status=" + status +
				'}';
	}
}
