package security;

import java.util.Optional;

import com.feth.play.module.pa.PlayAuthenticate;

import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Http.Context;
import play.mvc.Result;

public class MyDeadboltHandler extends AbstractDeadboltHandler {

	@Override
	public Promise<Optional<Result>> beforeAuthCheck(final Context context) {
		
		//check if it is logged in
		if(PlayAuthenticate.isLoggedIn(context.session())){
			return F.Promise.pure(Optional.empty());
		} else {
			// user is not logged in

			// call this if you want to redirect your visitor to the page that
			// was requested before sending him to the login page
			// if you don't call this, the user will get redirected to the page
			// defined by your resolver
			final String originalUrl = PlayAuthenticate
					.storeOriginalUrl(context);
			context.flash().put("error", "You Need to log in first, to view '" + originalUrl + "'");
			
			return F.Promise.promise(new F.Function0<Optional<Result>>() {

				@Override
				public Optional<Result> apply() throws Throwable {
					return Optional.ofNullable(redirect(PlayAuthenticate.getResolver().login()));
				}
			});
		}
		
	}


}
