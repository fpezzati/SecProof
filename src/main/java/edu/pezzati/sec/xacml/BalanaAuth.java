package edu.pezzati.sec.xacml;

import java.util.List;
import java.util.Set;

import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.ResourceFinder;

import edu.pezzati.sec.xacml.balana.BalanaResponse;

public class BalanaAuth implements AuthorizationGateway {

    private Set<PolicyFinderModule> policyModules;
    private List<AttributeFinderModule> attributeModules;
    private BalanaResponse response;
    private PDPConfig pDPConfig;
    private PDP pDP;

    public void setPolicyFinders(Set<PolicyFinderModule> policyModules) {
	this.policyModules = policyModules;
    }

    @Override
    public void init() {
	checkPreconditions();
	AttributeFinder attributeFinder = new AttributeFinder();
	attributeFinder.setModules(attributeModules);
	PolicyFinder policyFinder = new PolicyFinder();
	policyFinder.setModules(policyModules);
	ResourceFinder resourceFinder = new ResourceFinder();
	pDPConfig = new PDPConfig(attributeFinder, policyFinder, resourceFinder);
	pDP = new PDP(pDPConfig);
    }

    /**
     * Policy and attributes finder modules can not be null. This is a Balana's
     * request. Policy finder set can not be empty or no policy will ever be
     * load by the gateway.
     */
    private void checkPreconditions() {
	if (policyModules == null || policyModules.isEmpty())
	    throw new IllegalArgumentException();
	if (attributeModules == null)
	    throw new IllegalArgumentException();
    }

    public void setAttributeFinders(List<AttributeFinderModule> attributeModules) {
	this.attributeModules = attributeModules;
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
}
