package edu.pezzati.sec.xacml;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinderModule;

import edu.pezzati.sec.xacml.balana.BalanaRequest;
import edu.pezzati.sec.xacml.balana.BalanaResponse;

public class BalanaAuthTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private Set<PolicyFinderModule> policyModules;
    private AuthorizationGateway authGateway;
    private List<AttributeFinderModule> attributeModules;

    @BeforeClass
    public static void initTests() {
	//	PDPConfig pdpConfig = new PDPConfig(getAttributeFinder(), getPolicyFinder(), getResourceFinder());
	//	pDP = new PDP(pdpConfig);
    }

    @Before
    public void initTest() {
	authGateway = new BalanaAuth();
    }

    /**
     * Configure it wrong. AuthorizationGateway rely on PolicyFinder and
     * AttributeFinder. PolicyFinder can't be null or without FinderModules.
     * AttributeFinder can't be null (Balana's bond) but can be empty.
     */
    @Test
    public void passANullPolicyFinderSet() {
	policyModules = null;
	((BalanaAuth) authGateway).setPolicyFinders(policyModules);
	expectedException.expect(IllegalArgumentException.class);
	authGateway.init();
	Assert.fail();
    }

    @Test
    public void passAnEmptyPolicyFinderSet() {
	policyModules = new HashSet<>();
	((BalanaAuth) authGateway).setPolicyFinders(policyModules);
	expectedException.expect(IllegalArgumentException.class);
	authGateway.init();
	Assert.fail();
    }

    @Test
    public void passANullAttributeFinderSet() {
	attributeModules = null;
	((BalanaAuth) authGateway).setAttributeFinders(attributeModules);
	expectedException.expect(IllegalArgumentException.class);
	authGateway.init();
	Assert.fail();
    }

    /**
     * Once AuthorizationGateway is configured it can handle requests. If
     * request is null gateway raise a NPE.
     */
    @Test
    public void passANullRequestToResourceGateway() {
	expectedException.expect(NullPointerException.class);
	authGateway.evaluate(null);
	Assert.fail();
    }

    @Test
    public void passRequestWhoWillEvaluatedAsIndeterminate() {
	Request notApplicableRequest = new BalanaRequest();
	authGateway.evaluate(notApplicableRequest);
	Response notApplicableResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_INDETERMINATE;
	int actualResult = ((BalanaResponse) notApplicableResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void passRequestWhoWillEvaluatedAsNotApplicable() {
	Request notApplicableRequest = new BalanaRequest();
	authGateway.evaluate(notApplicableRequest);
	Response notApplicableResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_NOT_APPLICABLE;
	int actualResult = ((BalanaResponse) notApplicableResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void passRequestWhoWillEvaluatedAsDeny() {
	Request notApplicableRequest = new BalanaRequest();
	authGateway.evaluate(notApplicableRequest);
	Response notApplicableResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_DENY;
	int actualResult = ((BalanaResponse) notApplicableResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void passRequestWhoWillEvaluatedAsPermit() {
	Request notApplicableRequest = new BalanaRequest();
	authGateway.evaluate(notApplicableRequest);
	Response notApplicableResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_PERMIT;
	int actualResult = ((BalanaResponse) notApplicableResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
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
