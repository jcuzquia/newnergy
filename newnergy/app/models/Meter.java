package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.avaje.ebean.Model;

import play.data.validation.Constraints;

@Entity
@Table(name = "meter")
public class Meter extends Model{

	@Id
	public Long id;
	
	@ManyToMany(cascade = CascadeType.REMOVE)
	public List<User> users = new ArrayList<User>();
	
	@Constraints.Required(message = "required.message")
	@Constraints.MaxLength(value = 50, message = "length.message")
	@Constraints.MinLength(value = 3, message = "length.message")
	public String description;
	
	public static final Finder<Long, Meter> find = new Model.Finder<Long, Meter>(Meter.class);

	public Meter(){}
	
	
	public static Meter create(String name){
		return null;
	}
	
}
