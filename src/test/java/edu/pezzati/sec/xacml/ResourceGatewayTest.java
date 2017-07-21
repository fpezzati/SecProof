package edu.pezzati.sec.xacml;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ResourceGatewayTest {

    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void init() {
	//	PDPConfig pdpConfig = new PDPConfig(getAttributeFinder(), getPolicyFinder(), getResourceFinder());
	//	pDP = new PDP(pdpConfig);
    }

    @Test
    public void passANullPolicyFinderModuleToResourceGateway() {
	ResourceGateway resourceGateway = new ResourceGateway();
	resourceGateway.setPolicyFinders(null);
	exception.expect(NullPointerException.class);
	resourceGateway.init();
	Assert.fail();
    }

    @Test
    public void passANullAttributeFinderModuleToResourceGateway() {
	Assert.fail();
    }

    @Test
    public void passANullRequestToResourceGateway() {
	Assert.fail();
    }

    //    private static AttributeFinder getAttributeFinder() {
    //	AttributeFinder attributeFinder = new AttributeFinder();
    //	List<AttributeFinderModule> modules = new ArrayList<>();
    //	modules.add(new PIPRoleFinderModule());
    //	attributeFinder.setModules(modules);
    //	return attributeFinder;
    //    }
    //
    //    private static PolicyFinder getPolicyFinder() {
    //	PolicyFinder policyFinder = new PolicyFinder();
    //	Set<PolicyFinderModule> modules = new HashSet<>();
    //	PolicyFinderModule policyFinderModule = new FileBasedPolicyFinderModule();
    //	modules.add(policyFinderModule);
    //	policyFinder.setModules(modules);
    //	return policyFinder;
    //    }
    //
    //    private static ResourceFinder getResourceFinder() {
    //	// TODO Auto-generated method stub
    //	return null;
    //    }
}
