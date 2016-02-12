package providers;

import com.feth.play.module.pa.providers.wwwauth.basic.BasicAuthProvider;
import com.feth.play.module.pa.user.AuthUser;
import com.google.inject.Inject;

import play.Application;
import play.mvc.Http.Context;
import play.twirl.api.Content;

/**
 * A really simple basic auth provider that accepts one hard coded user
 */
public class MyStupidBasicAuthProvider extends BasicAuthProvider {
	
	@Inject
	public MyStupidBasicAuthProvider(Application app){
		super(app);
	}

	@Override
	protected AuthUser authenticateUser(String username, String password) {
		if(username.equals("example") && password.equals("secret")){
			return new AuthUser() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public String getProvider() {
					return "basic";
				}
				
				@Override
				public String getId() {
					return "example";
				}
			};
		}
		return null;
	}

	@Override
	public String getKey() {
		return "basic";
	}

	@Override
	protected Content unauthorized(Context context) {
		return null;
	}

	
	
	
}
