package edu.pezzati.sec.xacml.balana;

import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.ResourceFinder;

import edu.pezzati.sec.xacml.AuthorizationGateway;
import edu.pezzati.sec.xacml.Request;
import edu.pezzati.sec.xacml.Response;

public class BalanaAuth implements AuthorizationGateway {

    private PolicyFinder policyFinder;
    private AttributeFinder attributeFinder;
    private BalanaResponse response;
    private PDPConfig pDPConfig;
    private PDP pDP;

    public void setPolicyFinder(PolicyFinder policyFinder) {
	this.policyFinder = policyFinder;
    }

    @Override
    public void init() {
	checkPreconditions();
	ResourceFinder resourceFinder = new ResourceFinder();
	pDPConfig = new PDPConfig(attributeFinder, policyFinder, resourceFinder);
	pDP = new PDP(pDPConfig);
    }

    /**
     * Policy and attributes finder modules can not be null. This is a Balana's
     * request. Policy finder set can not be empty or no policy will be load by
     * the gateway.
     */
    private void checkPreconditions() {
	if (policyFinder == null || policyFinder.getModules() == null || policyFinder.getModules().isEmpty())
	    throw new IllegalArgumentException();
	if (attributeFinder == null || attributeFinder.getModules() == null)
	    throw new IllegalArgumentException();
    }

    public void setAttributeFinder(AttributeFinder attributeFinder) {
	this.attributeFinder = attributeFinder;
    }

    @Override
    public void evaluate(Request request) {
	request.accept(this);
    }

    @Override
    public Response getResponse() {
	return response;
    }

    public void evaluate(RequestCtx request) {
	ResponseCtx responseCtx = pDP.evaluate(request);
	response = new BalanaResponse();
	response.setResponse(responseCtx);
    }

    public PDPConfig getConfig() {
	return pDPConfig;
    }
}
