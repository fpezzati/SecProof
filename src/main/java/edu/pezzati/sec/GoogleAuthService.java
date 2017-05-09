package edu.pezzati.sec;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("logout")
public class GoogleAuthService {

    @Path("now")
    @GET
    public void logoutByGoogle() {
	//	Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
	//	            new ResultCallback<Status>() {
	//	                @Override
	//	                public void onResult(Status status) {
	//	                    // ...
	//	                }
	//	            });
    }
}
