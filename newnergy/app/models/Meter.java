package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.avaje.ebean.Model;

import constants.Const;
import play.data.format.Formats;
import play.data.validation.Constraints;

@Entity
@Table(name = "meter")
public class Meter extends Model {

	@Id
	public Long id;
	
	@ManyToOne
	public Project project;
	
	@Constraints.Required(message = "required.message")
	@Constraints.MaxLength(value = 50, message = "length.message")
	@Constraints.MinLength(value = 3, message = "length.message")
	public String meterName;
	
	@Constraints.Required(message = "required.message")
	@Constraints.MaxLength(value = 50, message = "length.message")
	@Constraints.MinLength(value = 3, message = "length.message")
	public String description;
	
	public Double maxKWh, minKWh, maxKW, minKW;
	
	public Integer startYear, endYear, startMonth, endMonth, startDay, endDay;
	

	@Temporal(TemporalType.DATE)
	@Formats.DateTime(pattern = "dd/MM/yyyy")
	public Date startDate;
	
	@Temporal(TemporalType.DATE)
	@Formats.DateTime(pattern = "dd/MM/yyyy")
	public Date endDate;
	
	public Long startDateValue, endDateValue;
	
	public String jsStartDate, jsEndDate;
	
	@OneToMany(mappedBy = "meter", cascade=CascadeType.ALL)
	public List<Data> dataList = new ArrayList<Data>();
	
	@OneToMany(mappedBy = "meter", cascade=CascadeType.ALL)
	public List<DailyData> dailyDataList = new ArrayList<DailyData>();
	
	public static final Finder<Long, Meter> find = new Model.Finder<Long, Meter>(Meter.class);

	/**
	 * You can only create a meter once a list of data points has been 
	 * inserted
	 * @param dataList, project
	 */
	public Meter(List<Data> dataList, Project project){
		dataList = deleteRepeatedDates(dataList);
		dataList = FillUpMissingData(dataList);
		dataList = trimToNearestDay(dataList);
		
		this.dataList = dataList;

		this.project = project;
		
	}
	
	/**
	 * Method that finds the Maximum kWh Value
	 * @param dataList
	 * @return
	 */
	private double findMaxKWh(List<Data> dataList) {
		double maxKWh = dataList.get(0).getKWh();
		for(int i = 1; i < dataList.size() -1; i++){
			if(dataList.get(i).getKWh() > maxKWh){
				maxKWh = dataList.get(i).getKWh();
			}
		}
		return maxKWh;
	}
	
	/**
	 * Method that finds the minimum kWh Value
	 * @param dataList
	 * @return
	 */
	private Double findMinKWh(List<Data> dataList) {
		double maxKWh = dataList.get(0).getKWh();
		for(int i = 1; i < dataList.size() -1; i++){
			if(dataList.get(i).getKWh() < maxKWh){
				maxKWh = dataList.get(i).getKWh();
			}
		}
		return maxKWh;
	}

	public static Meter findById(Long id){
		return find.byId(id);
	}
	
	/**
	 * Method that deletes repeated dates in the data
	 * @param dataList
	 * @return
	 */
	private List<Data> deleteRepeatedDates(List<Data> dataList) {
		//create 2 containers, one one to dump filtered data
		// the other to delete
		List<Data> deleteDataList = new ArrayList<Data>(dataList);
		List<Data> toAddList = new ArrayList<>();
		
		toAddList.add(deleteDataList.get(0)); // set up the lists
		deleteDataList.remove(0);
		
		while (deleteDataList.size()> 1){
			//Get the last one of the List
			Data data1 = toAddList.get(toAddList.size()-1);
			Data data2 = deleteDataList.get(0); //get the first of this list
			// if they are the same date
			if (data1.getDateValue() == data2.getDateValue()) {
				if (data1.getKWh() == data2.getKWh() || data1.getGenkWh() == data2.getGenkWh()){
					float con1 = data1.getKWh();
					float gen1 = data1.getGenkWh();
					
					float con2 = data2.getKWh();
					float gen2 = data2.getGenkWh();
					if(con1 != con2 || gen1 != gen2){
						data1.setGenkWh(gen1+gen2);
						data1.setKWh(con1+con2);
					}
					
				} 
				
				deleteDataList.remove(0);
			} else {
				toAddList.add(data2);
				deleteDataList.remove(0);
			}
		}
		
		dataList = toAddList;
		
		return dataList;
	}
	
	/**
	 * Fills up missing data with 0
	 * @param dataList
	 * @return
	 */
	private List<Data> FillUpMissingData (List<Data> dataList){
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

		for (int i = 0; i < dataList.size() - 1; i++) {
			Data data1 = dataList.get(i);
			Data data2 = dataList.get(i + 1);
			if (data2.getDateValue() - data1.getDateValue() > Const.MINUTE * 15) { //if it is greater, fill with empty interval

				long dateVal = data1.getDateValue() + Const.MINUTE * 15;

				cal.setTime(new Date(dateVal));
				Data fillData = new Data(cal.getTimeInMillis());//new interval with no energy
				fillData.setKWh(0);
				dataList.add(i + 1, fillData);
				data1 = dataList.get(i);

				data2 = dataList.get(i + 1);

			} 
			//if the interval data is 5 minutes
			else if (data2.getDateValue() - data1.getDateValue() < Const.MINUTE * 15){ //if they are
				dataList.get(i).setKWh(data1.getKWh() + data2.getKWh());
				
				dataList.remove(i+1);
				i--;
			}
		}

		return dataList;
	}
	
	private List<Data> trimToNearestDay(List<Data> dataList){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dataList.get(0).getDateValue());
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);

		
		while (hour != 0 || min != 0) { // while the hour or minute are not 0
			dataList.remove(0);
			cal.setTimeInMillis(dataList.get(0).getDateValue());
			
			hour = cal.get(Calendar.HOUR_OF_DAY);
			min = cal.get(Calendar.MINUTE);
			
		}

		cal.setTimeInMillis(dataList.get(dataList.size()-1).getDateValue());
		int hour1 = cal.get(Calendar.HOUR_OF_DAY);
		
		while (hour1 != 23) { // while the hour and minute is not 23:45
			dataList.remove(dataList.size() - 1);
			cal.setTimeInMillis(dataList.get(dataList.size()-1).getDateValue());
			hour1 = cal.get(Calendar.HOUR_OF_DAY);
		}
		return dataList;
	}
	
	@Override
	public String toString() {
		return "Meter [id=" + id + ", meterName=" + meterName + ", description=" + description + "]";
	}
	
	public List<Data> getDataList() {
		return dataList;
	}
	
	/**
	 * Called from the setDataListMethod, since it is that method being called
	 * to create the meter object
	 * @param dataList
	 * @return
	 */
	private List<DailyData> createDailyMatrix(List<Data> dataList){
		List<DailyData> dailyDataList = new ArrayList<DailyData>();
		List<Data> deletionDataList = new ArrayList<Data>(dataList);
		
		//set up the first data
		Data firstData = deletionDataList.get(0);
		deletionDataList.remove(0);
		Data secondData = deletionDataList.get(0);
		
		/*
		 * Loop until deletion list has no items in it
		 * this means that the overall transfer is complete
		 */
		while (deletionDataList.size() > 0){
			//create a daily container
			List<Data> dayTimeData = new ArrayList<Data>();
			
			//while they are the same day, keep adding to this specific day container
			while(firstData.getDate().getDate() == secondData.getDate().getDate()){
				firstData.setkW(firstData.getKWh() * 4);
				firstData.setGenkW(firstData.getGenkWh() * 4);

				dayTimeData.add(firstData);
				firstData = deletionDataList.get(0); // we get the first data
				deletionDataList.remove(0);
				secondData = deletionDataList.get(0);
				if(deletionDataList.size() == 1){
					break;
				}
			}
			dayTimeData.add(firstData);
			firstData = deletionDataList.get(0);
			deletionDataList.remove(0);
			
			if(deletionDataList.size() == 0 ){
				break;
			}
			secondData = deletionDataList.get(0);
			if (dayTimeData.size() != 96) {
				dayTimeData = fixDayTimeData(dayTimeData);
			}
			dailyDataList.add(new DailyData(dayTimeData, dayTimeData.get(0).getDateValue()));
			
		}
		
		return dailyDataList;
	}
	
	@SuppressWarnings("deprecation")
	/**
	 * This method returns 96 items per list to build the matrix of the heat map. 
	 * It checks only for repeated hours and minutes for each specific day, if the daily list
	 * is not equal to 96 items. This is mostly done for daylight savings
	 * @param dayTimeData
	 * @return
	 */
	private List<Data> fixDayTimeData(List<Data> dayTimeData) {
		
		java.util.Collections.sort(dayTimeData);
		
		for (int i = 0; i < dayTimeData.size()-2; i++){
			Date date1 = new Date(dayTimeData.get(i).getDateValue());
			
			int h1 = date1.getHours();
			int m1 = date1.getMinutes();
			for(int j = i+1; j < dayTimeData.size()-1; j++){
				Date date2 = new Date(dayTimeData.get(j).getDateValue());
				int h2 = date2.getHours();
				int m2 = date2.getMinutes();
				
				if(h1 == h2 && m1 == m2){ //check if the minute and hour are equal for each item
					dayTimeData.get(i).setKWh(dayTimeData.get(j).getKWh());
					dayTimeData.get(i).setkW(dayTimeData.get(j).getkW());
					dayTimeData.get(i).setGenkWh(dayTimeData.get(j).getGenkWh());
					dayTimeData.get(i).setGenkW(dayTimeData.get(j).getGenkW());
					dayTimeData.remove(j);
				}
				
			}
		}
		
		return dayTimeData;
	}
	
	
	

	/**
	 * This is called from the project controller. It is actually the initializer of the
	 * meter object
	 * @param dataList
	 */
	public void setDataList(List<Data> dataList) {
		this.dataList = dataList;
		dailyDataList = createDailyMatrix(dataList);
		// Set start and end date of the meter
		startDate = this.dataList.get(0).getDate();
		endDate = this.dataList.get(this.dataList.size()-1).getDate();
		startDateValue = startDate.getTime();
		endDateValue = endDate.getTime();

		//get the string format for javascript template
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		startYear = cal.get(Calendar.YEAR);
		startMonth = cal.get(Calendar.MONTH);
		startDay = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(endDate);
		endYear = cal.get(Calendar.YEAR);
		endMonth = cal.get(Calendar.MONTH);
		endDay = cal.get(Calendar.DAY_OF_MONTH);
		
		maxKWh = findMaxKWh(dataList);
		minKWh = findMinKWh(dataList);
		
	}
	
}
