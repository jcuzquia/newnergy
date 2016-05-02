package models;

import java.util.Date;

import com.avaje.ebean.Model;

public class MeterInfo extends Model {

	public Long id;
	
	public Date starDate, endDate;
	
	public Long startDateValue, endDateValue;
	
	public Double maxKWh, minkWh;

	public float maxKW, minKW;
	
	public MeterInfo(Meter meter) {
		this.id = meter.id;
		this.starDate = meter.startDate;
		this.endDate = meter.endDate;
		this.startDateValue = meter.startDateValue;
		this.endDateValue = meter.endDateValue;
		this.maxKWh = meter.maxKWh;
		this.minkWh = meter.minKWh;
	}

}
