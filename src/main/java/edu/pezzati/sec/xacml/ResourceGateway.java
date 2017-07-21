package edu.pezzati.sec.xacml;

import java.util.Set;

import org.wso2.balana.PDPConfig;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.ResourceFinder;

public class ResourceGateway {

    private Set<PolicyFinderModule> policyModules;

    public void setPolicyFinders(Set<PolicyFinderModule> policyModules) {
	this.policyModules = policyModules;
    }

    public void init() {
	AttributeFinder attributeFinder = new AttributeFinder();
	PolicyFinder policyFinder = new PolicyFinder();
	policyFinder.getModules().addAll(policyModules);
	ResourceFinder resourceFinder = new ResourceFinder();
	PDPConfig pDPConfig = new PDPConfig(attributeFinder, policyFinder, resourceFinder);
    }
}
