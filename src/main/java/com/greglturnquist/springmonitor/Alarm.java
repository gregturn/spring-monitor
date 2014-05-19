package com.greglturnquist.springmonitor;

public class Alarm {

	private String severity;
	private String description;

	public Alarm(String severity, String description) {
		this.severity = severity;
		this.description = description;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}


