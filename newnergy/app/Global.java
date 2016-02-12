import com.feth.play.module.pa.exceptions.AccessDeniedException;
import java.util.Arrays;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AuthException;

import controllers.routes;
import models.SecurityRole;
import play.Application;
import play.GlobalSettings;
import play.mvc.Call;

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		PlayAuthenticate.setResolver(new Resolver(){

			@Override
			public Call afterAuth() {
				// The user will be redirected to this page after authentication
				// if no original URL was saved
				return routes.Application.index();
			}

			@Override
			public Call afterLogout() {
				return routes.Application.index();
			}

			@Override
			public Call askLink() {
				return routes.Account.askLink();
			}

			@Override
			public Call askMerge() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Call auth(final String provider) {
				// You can provide your own authentication implementation,
				// however the default should be sufficient for most cases
				System.out.println("We are calling auth from Global settings with provider: " + provider);
				return com.feth.play.module.pa.controllers.routes.AuthenticateDI.authenticate(provider);
			}

			@Override
			public Call login() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Call onException(AuthException e) {
				if (e instanceof AccessDeniedException) {
					return routes.Signup
							.oAuthDenied(((AccessDeniedException) e)
									.getProviderKey());
				}
				return super.onException(e);
			}
			
			

			
		});

		initialData();

	}

	private void initialData() {
		if (SecurityRole.find.findRowCount() == 0) {
			for (final String roleName : Arrays
					.asList(controllers.Application.USER_ROLE)) {
				final SecurityRole role = new SecurityRole();
				role.roleName = roleName;
				role.save();
			}
		}
	}
	
	
}