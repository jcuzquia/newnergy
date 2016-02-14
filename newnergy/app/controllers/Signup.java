package controllers;
import models.TokenAction;
import models.User;
import models.TokenAction.Type;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.account.unverified;
import views.html.account.signup.exists;

public class Signup extends Controller {

	
	/**
	 * This is called after the user has clicked the link on his email
	 * @param token
	 * @return
	 */
	public Result verify(final String token){
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final TokenAction ta = tokenIsValid(token, Type.EMAIL_VERIFICATION);
		
		if (ta == null) {
//			return badRequest(no_token_or_invalid.render());
			return badRequest();
		}
		
		final String email = ta.targetUser.email;
		User.verify(ta.targetUser);
		flash(Application.FLASH_MESSAGE_KEY, 
				Messages.get("playauthenticate.verify_email.success", email));
		
		if (Application.getLocalUser(session()) != null) {
			return redirect(routes.Application.index());
		} else {
			return redirect(routes.Application.login());
		}
		
	}
	
	/**
	 * Return a token object if valid, null if not
	 * @param token
	 * @param emailVerification
	 * @return
	 */
	private TokenAction tokenIsValid(String token, final Type type) {
		TokenAction ret = null;
		if (token != null && !token.trim().isEmpty()) {
			final TokenAction ta = TokenAction.findByToken(token, type);
			if (ta != null && ta.isValid()) {
				ret = ta;
			}
		}
		return ret;
	}

	public Result oAuthDenied(String provider){
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return TODO;
	}
	
	public Result unverified() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return ok(unverified.render());
	}
	
	public Result exists(){
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		return ok(exists.render());
	}
}
