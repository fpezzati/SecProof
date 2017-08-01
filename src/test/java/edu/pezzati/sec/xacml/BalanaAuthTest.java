package edu.pezzati.sec.xacml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.Policy;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.RFC822NameAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;
import org.wso2.balana.utils.Utils;
import org.wso2.balana.xacml3.Attributes;

import edu.pezzati.sec.xacml.balana.BalanaRequest;
import edu.pezzati.sec.xacml.balana.BalanaResponse;
import edu.pezzati.sec.xacml.balana.pap.DatabasePolicyFinder;
import edu.pezzati.sec.xacml.balana.pip.DatabaseRoleFinder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ DatabaseRoleFinder.class })
public class BalanaAuthTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private Set<PolicyFinderModule> policyModules;
    private AuthorizationGateway authGateway;
    private List<AttributeFinderModule> attributeModules;
    private DatabaseRoleFinder databaseRoleFinder;
    private DatabasePolicyFinder databasePolicyFinder;
    private static File policy;

    @BeforeClass
    public static void initTests() {
	policy = new File("src/test/resources/policypool/policy.balana.auth.1.xml");
    }

    @Before
    public void initTest() {
	authGateway = new BalanaAuth();
	databaseRoleFinder = PowerMockito.mock(DatabaseRoleFinder.class);
	PowerMockito.when(databaseRoleFinder.isDesignatorSupported()).thenReturn(true);
	PowerMockito.when(databaseRoleFinder.isSelectorSupported()).thenReturn(false);
	databasePolicyFinder = Mockito.mock(DatabasePolicyFinder.class);
	Mockito.when(databasePolicyFinder.isRequestSupported()).thenReturn(true);
	Mockito.when(databasePolicyFinder.isIdReferenceSupported()).thenReturn(true);
	attributeModules = getAttributeFinderModules();
	policyModules = getPolicyFinderModules();
	((BalanaAuth) authGateway).setAttributeFinders(attributeModules);
	((BalanaAuth) authGateway).setPolicyFinders(policyModules);
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
	authGateway.init();
	expectedException.expect(NullPointerException.class);
	authGateway.evaluate(null);
	Assert.fail();
    }

    //    @Test
    public void passRequestWhoWillBeEvaluatedAsIndeterminate() throws URISyntaxException {
	Request notApplicableRequest = getIndeterminateRequest();
	authGateway.init();
	authGateway.evaluate(notApplicableRequest);
	Response notApplicableResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_INDETERMINATE;
	int actualResult = ((BalanaResponse) notApplicableResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    //    @Test
    public void passRequestWhoWillBeEvaluatedAsNotApplicable() {
	Request notApplicableRequest = new BalanaRequest();
	authGateway.init();
	authGateway.evaluate(notApplicableRequest);
	Response notApplicableResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_NOT_APPLICABLE;
	int actualResult = ((BalanaResponse) notApplicableResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    //    @Test
    public void passRequestWhoWillBeEvaluatedAsDeny() {
	Request notApplicableRequest = new BalanaRequest();
	authGateway.init();
	authGateway.evaluate(notApplicableRequest);
	Response notApplicableResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_DENY;
	int actualResult = ((BalanaResponse) notApplicableResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void passRequestWhoWillBeEvaluatedAsPermit() throws Exception {
	List<AttributeValue> retreivedPermissions = new ArrayList<>();
	retreivedPermissions.add(new StringAttribute("READA"));
	retreivedPermissions.add(new StringAttribute("READB"));

	AbstractPolicy policyfound = getPolicyByFile(policy);
	PolicyFinderResult policyFinderResult = new PolicyFinderResult(policyfound);
	Mockito.when(databasePolicyFinder.findPolicy(Mockito.any())).thenReturn(policyFinderResult);

	PowerMockito.doReturn(retreivedPermissions).when(databaseRoleFinder, "retreiveUserPermissions", "alice");
	Request notApplicableRequest = getPermitRequest();
	authGateway.init();
	authGateway.evaluate(notApplicableRequest);
	Response permitResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_PERMIT;
	int actualResult = ((BalanaResponse) permitResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    private AbstractPolicy getPolicyByFile(File policyFile) throws Exception {
	DocumentBuilderFactory factory = Utils.getSecuredDocumentBuilderFactory();
	factory.setIgnoringComments(true);
	factory.setNamespaceAware(true);
	factory.setValidating(false);
	DocumentBuilder db = factory.newDocumentBuilder();
	InputStream stream = new FileInputStream(policyFile);
	Document doc = db.parse(stream);
	AbstractPolicy policy = Policy.getInstance(doc.getDocumentElement());
	return policy;
    }

    private Request getIndeterminateRequest() throws URISyntaxException {
	BalanaRequest request = new BalanaRequest();
	Set<Attributes> attributesSet = new HashSet<>();
	Set<Attribute> attributeSet = new HashSet<>();
	attributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id"), null, new DateTimeAttribute(),
		new RFC822NameAttribute("alice"), 1));
	URI category = new URI("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
	Attributes attributes = new Attributes(category, attributeSet);
	attributesSet.add(attributes);
	Node documentRoot = null;
	RequestCtx requestCtx = new RequestCtx(attributesSet, documentRoot);
	request.setRequest(requestCtx);
	return request;
    }

    private Request getPermitRequest() throws URISyntaxException {
	BalanaRequest request = new BalanaRequest();
	Set<Attributes> attributesSet = new HashSet<>();
	Set<Attribute> userAttributeSet = new HashSet<>();
	Set<Attribute> actionAttributeSet = new HashSet<>();
	Set<Attribute> resourceAttributeSet = new HashSet<>();
	/**
	 * User Alice wants to do action READ about resource RESOURCEA.
	 */
	userAttributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id"), null, new DateTimeAttribute(),
		new StringAttribute("alice"), 1));
	actionAttributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:action:action-id"), null, new DateTimeAttribute(),
		new StringAttribute("READ"), 1));
	resourceAttributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:resource:resource-id"), null, new DateTimeAttribute(),
		new StringAttribute("RESOURCEA"), 1));
	URI userCategory = new URI("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
	URI actionCategory = new URI("urn:oasis:names:tc:xacml:3.0:attribute-category:action");
	URI resourceCategory = new URI("urn:oasis:names:tc:xacml:3.0:attribute-category:resource");
	Attributes userAttributes = new Attributes(userCategory, userAttributeSet);
	Attributes actionAttributes = new Attributes(actionCategory, actionAttributeSet);
	Attributes resourceAttributes = new Attributes(resourceCategory, resourceAttributeSet);
	attributesSet.add(userAttributes);
	attributesSet.add(actionAttributes);
	attributesSet.add(resourceAttributes);
	Node documentRoot = null;
	RequestCtx requestCtx = new RequestCtx(attributesSet, documentRoot);
	request.setRequest(requestCtx);
	return request;
    }

    private List<AttributeFinderModule> getAttributeFinderModules() {
	List<AttributeFinderModule> modules = new ArrayList<>();
	modules.add(databaseRoleFinder);
	return modules;
    }

    private Set<PolicyFinderModule> getPolicyFinderModules() {
	Set<PolicyFinderModule> modules = new HashSet<>();
	modules.add(databasePolicyFinder);
	return modules;
    }
    //
    //    private static ResourceFinder getResourceFinder() {
    //	// TODO Auto-generated method stub
    //	return null;
    //    }
}
