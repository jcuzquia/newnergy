package models;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class MeterForm {

	public String meterName;
	
	public String description;

	public String getTitle() {
		return meterName;
	}

	public void setTitle(String title) {
		this.meterName = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}