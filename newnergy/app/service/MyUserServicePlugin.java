package service;

import com.feth.play.module.pa.service.UserServicePlugin;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.google.inject.Inject;

import models.User;
import play.Application;

public class MyUserServicePlugin extends UserServicePlugin{

	@Inject
	public MyUserServicePlugin(final Application app) {
		super(app);
	}

	/**
	 * Called when the user clicks the link from the email
	 */
	@Override
	public Object getLocalIdentity(final AuthUserIdentity identity) {
		// For production: Catching might be a good idea here...
		// ...and don't forget to sync the cache when users get deactivated/deleted
		final User u = User.findByAuthUserIdentity(identity);
		if(u != null) {
			return u.id;
		} else {
			return null;
		}
	}

	@Override
	public AuthUser link(AuthUser arg0, AuthUser arg1) {
		System.out.println("calling LInk");
		
		return null;
	}

	@Override
	public AuthUser merge(AuthUser arg0, AuthUser arg1) {
		System.out.println("Calling Merge");
		return null;
	}

	@Override
	public Object save(AuthUser authUser) {
		System.out.println("calling save");
		return null;
	}
	
	
}
