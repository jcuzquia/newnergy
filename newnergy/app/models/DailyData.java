package models;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import constants.Const.DayType;

@Entity
public class DailyData extends Model {

	@Id
	public Long id;
	private double temperature;
	private List<Data> dailyIntervalList;
	private Date date;
	private long dateValue;
	private int dayOfWeek;
	private DayType dayType;
	private float totalDailykWh;
	
	@ManyToOne
	public Meter meter;
	
	public DailyData (List<Data> dailyIntervalList, Long dateValue){
		this.dailyIntervalList = dailyIntervalList;
		this.dateValue = dateValue;
		date = new Date(dateValue);
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		
		dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		dayType = DayType.BUSSINESS_DAY;
		
		if(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY){
			dayType = DayType.WEEKEND;
		} 
		
		
		totalDailykWh = getTotalDailyConsumption(this.dailyIntervalList);
	}
	
	/**
	 * Simple method that adds up the total kWh for each day
	 * @param dailyIntervalList
	 * @return totalDailyConsumption
	 */
	private float getTotalDailyConsumption(List<Data> dailyIntervalList) {
		float totalDailyConsumption = 0;
		for(Data data : dailyIntervalList){
			totalDailyConsumption = totalDailyConsumption + data.getKWh() - data.getGenkWh();
		}
		return totalDailyConsumption;
	}
	
	public List<Data> getDailyIntervalList() {
		return dailyIntervalList;
	}
	public void setDailyIntervalList(List<Data> dailyIntervalList) {
		this.dailyIntervalList = dailyIntervalList;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public long getDateValue() {
		return dateValue;
	}
	public void setDateValue(long dateValue) {
		this.dateValue = dateValue;
	}
	public int getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public DayType getDayType() {
		return dayType;
	}
	public void setDayType(DayType dayType) {
		this.dayType = dayType;
	}
	public float getTotalDailykWh() {
		return totalDailykWh;
	}
	
}
