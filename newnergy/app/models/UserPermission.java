package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

import be.objectify.deadbolt.core.models.Permission;

@Entity
public class UserPermission extends AppModel implements Permission {
	
	@Id
	public Long id;
	
	public String value;
	
	public static final Finder<Long, UserPermission> find = new Model.Finder<Long, UserPermission>(UserPermission.class);
	
	public String getValue(){
		return value;
	}
	
	public static UserPermission findByValue(String value){
		return find.where().eq("value", value).findUnique();
	}
	
	
}
