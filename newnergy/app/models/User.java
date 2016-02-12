package models;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Model;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.FirstLastNameIdentity;
import com.feth.play.module.pa.user.NameIdentity;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;
import models.TokenAction.Type;
import play.data.format.Formats;
import play.data.validation.Constraints;
import providers.MyUsernamePasswordAuthUser;

/**
 * Initial version based on work by Steve Chaloner (steve@objectify.be) for
 * Deadbolt2
 */
@Entity
@Table(name = "users")
public class User extends AppModel implements Subject {

	@Id
	public Long id;

	@Constraints.Email
	// if you make this unique, keep in mind that users *must* merge/link their
	// accounts then on signup with additional providers
	// @Column(unique = true)
	public String email;

	public String name;

	public String firstName;

	public String lastName;

	@Formats.DateTime(pattern = "yyy-MM-dd HH:mm:ss")
	public Date lastLogin;

	public boolean active;

	public boolean emailValidated;

	@ManyToMany
	public List<SecurityRole> roles;

	@OneToMany(cascade = CascadeType.ALL)
	public List<LinkedAccount> linkedAccounts;

	@ManyToMany
	public List<UserPermission> permissions;

	public static final Finder<Long, User> find = new Model.Finder<Long, User>(User.class);

	public User(){}
	
	@Override
	public String getIdentifier() {
		return Long.toString(this.id);
	}

	@Override
	public List<? extends Permission> getPermissions() {
		return this.permissions;
	}

	@Override
	public List<? extends Role> getRoles() {
		return this.roles;
	}

	public static void verify(final User unverified) {
		// You might want to wrap this into a transaction
		unverified.emailValidated = true;
		unverified.save();
		TokenAction.deleteByUser(unverified, Type.EMAIL_VERIFICATION);

	}

	public static User findByAuthUserIdentity(final AuthUserIdentity identity) {
		if (identity == null) {
			return null;
		}
		if (identity instanceof UsernamePasswordAuthUser) {
			return findByUsernamePasswordIdentity((UsernamePasswordAuthUser) identity);
		}

		return null;
	}

	public static User findByUsernamePasswordIdentity(final UsernamePasswordAuthUser identity) {
		return getUsernamePasswordAuthUserFind(identity).findUnique();
	}

	private static ExpressionList<User> getUsernamePasswordAuthUserFind(
			final UsernamePasswordAuthUser identity) {
		
		return getEmailUserFind(identity.getEmail()).eq(
				"linkedAccounts.providerKey", identity.getProvider());
	}

	public Set<String> getProviders() {
		final Set<String> providerKeys = new HashSet<String>(this.linkedAccounts.size());
		for (final LinkedAccount acc : this.linkedAccounts) {
			providerKeys.add(acc.providerKey);
		}
		return providerKeys;
	}

	public void changePassword(MyUsernamePasswordAuthUser myUsernamePasswordAuthUser, boolean b) {
		// TODO Auto-generated method stub

	}

	public static User create(final AuthUser authUser) {
		System.out.println("We are Creating the User. User.create");
		
		final User user = new User();
		user.roles = Collections.singletonList(SecurityRole.findByRoleName(controllers.Application.USER_ROLE));
		user.active = true;
		user.lastLogin = new Date();
		user.linkedAccounts = Collections.singletonList(LinkedAccount.create(authUser));

		if (authUser instanceof EmailIdentity) {
			final EmailIdentity identity = (EmailIdentity) authUser;
			// Remember, even when getting them from FB & Co., emails should be
			// verified within the application as a security breach there might
			// break your security as well!
			user.email = identity.getEmail();
			user.emailValidated = false;
		}

		if (authUser instanceof NameIdentity) {
			final NameIdentity identity = (NameIdentity) authUser;
			final String name = identity.getName();
			if (name != null) {
				user.name = name;
			}
		}
		if (authUser instanceof FirstLastNameIdentity) {
			final FirstLastNameIdentity identity = (FirstLastNameIdentity) authUser;
			final String firstName = identity.getFirstName();
			final String lastName = identity.getLastName();
			if (firstName != null) {
				user.firstName = firstName;
			}
			if (lastName != null) {
				user.lastName = lastName;
			}
		}

		System.out.println("############################");
		System.out.println("Saving USER!!!!");
		user.save();
		// Ebean.saveManyToManyAssociations(user, "roles");
		// Ebean.saveManyToManyAssociations(user, "permissions");
		return user;

	}
	
	private static ExpressionList<User> getEmailUserFind(final String email) {
		return find.where().eq("active", true).eq("email", email);
	}

}