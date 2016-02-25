package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.Model;

import play.data.validation.Constraints;

@Entity
@Table(name = "project")
public class Project extends Model {

	@Id
	public String id;
	
	@Constraints.Required(message = "required.message")
	@ManyToOne
	public User owner;
	
	//@OneToMany(mappedBy="project", cascade=CascadeType.ALL)
	//public List<User> participants;

	@Constraints.Required(message = "required.message")
	@Constraints.MaxLength(value = 50, message = "length.message")
	@Constraints.MinLength(value = 3, message = "length.message")
	public String title;
	
	@Constraints.Required(message = "required.message")
	@Constraints.MaxLength(value = 50, message = "length.message")
	@Constraints.MinLength(value = 3, message = "length.message")
	public String description;
	
	
	private static Model.Finder<String, Project> find = new Model.Finder<String, Project>(Project.class);
	
	/**
	 * Method that finds all the projects globally
	 * @return
	 */
	public static List<Project> findAll(){
		return Project.find.orderBy("id").findList();
	}
	
	public static List<Project> findAllByUser(String email){
		return find.fetch("owner").where().eq("owner.email", email)
				.orderBy("id").findList();
	}
	
}
