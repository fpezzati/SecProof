package edu.pezzati.sec.xacml.balana;

import org.wso2.balana.ctx.xacml3.RequestCtx;

import edu.pezzati.sec.xacml.AuthorizationGateway;
import edu.pezzati.sec.xacml.BalanaAuth;
import edu.pezzati.sec.xacml.Request;

public class BalanaRequest implements Request {

    private RequestCtx request;

    @Override
    public void accept(AuthorizationGateway authGateway) {
	((BalanaAuth) authGateway).evaluate(request);
    }

    public void setRequest(RequestCtx requestCtx) {
	this.request = requestCtx;
    }
}
