package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

import be.objectify.deadbolt.core.models.Role;

@Entity
public class SecurityRole extends Model implements Role{

	@Id
	public Long id;
	
	public String roleName;
	
	public static final Finder<Long, SecurityRole> find = new Model.Finder<Long, SecurityRole>(SecurityRole.class);
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return roleName;
	}
	
	public static SecurityRole findByRoleName(String rolename) {
		return find.where().eq("roleName", rolename).findUnique();
	}
	
}
