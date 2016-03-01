package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Model;

import constants.Const;
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
	
	private Date startDate, endDate;
	
	private List<Data> dataList = new ArrayList<Data>();
	
	public static final Finder<Long, Meter> find = new Model.Finder<Long, Meter>(Meter.class);

	/**
	 * You can only create a meter oncea list of data points has been 
	 * inserted
	 * @param dataList, project
	 */
	public Meter(List<Data> dataList, Project project){
		dataList = deleteRepeatedDates(dataList);
		dataList = FillUpMissingData(dataList);
		dataList = trimToNearestDay(dataList);
		this.dataList = dataList;
		// Set start and 
		setStartDate(this.dataList.get(0).getDate());
		setEndDate(this.dataList.get(this.dataList.size()-1).getDate());
		this.project = project;
		
	}
	
	public static Meter findById(Long id){
		return find.byId(id);
	}
	
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
	
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<Data> getDataList() {
		return dataList;
	}

	public void setDataList(List<Data> dataList) {
		this.dataList = dataList;
	}

}
