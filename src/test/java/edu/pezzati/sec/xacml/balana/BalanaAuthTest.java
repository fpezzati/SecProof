package edu.pezzati.sec.xacml.balana;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.Policy;
import org.wso2.balana.PolicySet;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;
import org.wso2.balana.utils.Utils;
import org.wso2.balana.xacml3.Attributes;

import edu.pezzati.sec.xacml.AuthorizationGateway;
import edu.pezzati.sec.xacml.Request;
import edu.pezzati.sec.xacml.Response;
import edu.pezzati.sec.xacml.balana.pap.DatabasePolicyFinder;
import edu.pezzati.sec.xacml.balana.pip.DatabasePermissionFinder;

public class BalanaAuthTest {

    private static final String policyCreateResource1Id = "addResource1";
    private static final String policyRemoveResource1Id = "removeResource1";
    private static final String policyReadResource1Id = "readResource1";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private Set<PolicyFinderModule> policyModules;
    private AuthorizationGateway authGateway;
    private PolicyFinder policyFinder;
    private AttributeFinder attributeFinder;
    private List<AttributeFinderModule> attributeModules;
    private DatabasePermissionFinder databaseRoleFinder;
    private DatabasePolicyFinder databasePolicyFinder;
    private static File policy;
    private static File policySetCreateDeleteReadResource1;
    private static File policySetCreateDeleteReadResource1MadeByReferences;
    private static File policyCreateResource1;
    private static File policyRemoveResource1;
    private static File policyReadResource1;

    @BeforeClass
    public static void initTests() {
	policy = new File("src/test/resources/policypool/policy.balana.auth.1.xml");
	policySetCreateDeleteReadResource1 = new File("src/test/resources/policypool/policy.balana.auth.2.xml");
	policySetCreateDeleteReadResource1MadeByReferences = new File("src/test/resources/policypool/policy.balana.auth.3.xml");
	policyCreateResource1 = new File("src/test/resources/policypool/policy.create.resource1.xml");
	policyRemoveResource1 = new File("src/test/resources/policypool/policy.remove.resource1.xml");
	policyReadResource1 = new File("src/test/resources/policypool/policy.read.resource1.xml");
    }

    @Before
    public void initTest() {
	authGateway = new BalanaAuth();
	setUpDatabaseRoleFinder();
	databasePolicyFinder = Mockito.mock(DatabasePolicyFinder.class);
	Mockito.when(databasePolicyFinder.isRequestSupported()).thenReturn(true);
	Mockito.when(databasePolicyFinder.isIdReferenceSupported()).thenReturn(true);
	attributeModules = getAttributeFinderModules();
	policyModules = getPolicyFinderModules();
	policyFinder = Mockito.spy(new PolicyFinder());
	policyFinder.setModules(policyModules);
	attributeFinder = Mockito.spy(new AttributeFinder());
	attributeFinder.setModules(attributeModules);
	((BalanaAuth) authGateway).setAttributeFinder(attributeFinder);
	((BalanaAuth) authGateway).setPolicyFinder(policyFinder);
    }

    private void setUpDatabaseRoleFinder() {
	databaseRoleFinder = Mockito.mock(DatabasePermissionFinder.class);
	Mockito.when(databaseRoleFinder.isDesignatorSupported()).thenReturn(true);
	Mockito.when(databaseRoleFinder.isSelectorSupported()).thenReturn(false);
	Set<String> supportedIds = new HashSet<>(Arrays.asList(new String[] { "urn:oasis:names:tc:xacml:1.0:subject:subject-permission" }));
	Set<String> supportedCategories = new HashSet<>(
		Arrays.asList(new String[] { "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" }));
	Mockito.when(databaseRoleFinder.getSupportedCategories()).thenReturn(supportedCategories);
	Mockito.when(databaseRoleFinder.getSupportedIds()).thenReturn(supportedIds);
    }

    /**
     * Configure it wrong. AuthorizationGateway rely on PolicyFinder and
     * AttributeFinder. PolicyFinder can't be null or without FinderModules.
     * AttributeFinder can't be null (Balana's bond) but can be empty.
     */
    @Test
    public void passANullPolicyFinderSet() {
	policyFinder = null;
	((BalanaAuth) authGateway).setPolicyFinder(policyFinder);
	expectedException.expect(IllegalArgumentException.class);
	authGateway.init();
	Assert.fail();
    }

    @Test
    public void passAnEmptyPolicyFinder() {
	policyFinder = null;
	((BalanaAuth) authGateway).setPolicyFinder(policyFinder);
	expectedException.expect(IllegalArgumentException.class);
	authGateway.init();
	Assert.fail();
    }

    @Test
    public void passAnEmptyPolicyFinderModuleSet() {
	policyModules = new HashSet<>();
	policyFinder = new PolicyFinder();
	policyFinder.setModules(policyModules);
	((BalanaAuth) authGateway).setPolicyFinder(policyFinder);
	expectedException.expect(IllegalArgumentException.class);
	authGateway.init();
	Assert.fail();
    }

    @Test
    public void passANullAttributeFinder() {
	attributeFinder = null;
	((BalanaAuth) authGateway).setAttributeFinder(attributeFinder);
	expectedException.expect(IllegalArgumentException.class);
	authGateway.init();
	Assert.fail();
    }

    @Test
    public void passANullAttributeFinderModuleSet() {
	attributeModules = null;
	attributeFinder = new AttributeFinder();
	expectedException.expect(NullPointerException.class);
	attributeFinder.setModules(attributeModules);
	((BalanaAuth) authGateway).setAttributeFinder(attributeFinder);
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

    @Test
    public void passRequestWhoWillBeEvaluatedAsNotApplicable() throws Exception {
	loadNoPolicyDueToUnmatchingRequest();
	setUpAliceDatabaseRoleFinderResult();
	Request notApplicableRequest = getNotApplicableRequest();
	authGateway.init();
	authGateway.evaluate(notApplicableRequest);
	Response notApplicableResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_NOT_APPLICABLE;
	int actualResult = ((BalanaResponse) notApplicableResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void passRequestWhoWillBeEvaluatedAsDeny() throws Exception {
	loadSinglePolicyFile();
	setUpBobDatabaseRoleFinderResult();
	Request denyRequest = getDenyRequest();
	authGateway.init();
	authGateway.evaluate(denyRequest);
	Response notApplicableResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_DENY;
	int actualResult = ((BalanaResponse) notApplicableResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void passRequestWhoWillBeEvaluatedAsPermit() throws Exception {
	loadSinglePolicyFile();
	setUpAliceDatabaseRoleFinderResult();
	Request permitRequest = getPermitRequest();
	authGateway.init();
	authGateway.evaluate(permitRequest);
	Response permitResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_PERMIT;
	int actualResult = ((BalanaResponse) permitResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void passRequestWhoWillBeEvaluatedAsNotApplicableAgainstAPolicySet() throws Exception {
	loadNoPolicyDueToUnmatchingRequest();
	setUpAliceDatabaseRoleFinderResult();
	Request permitRequest = getRequestWhoWillBeEvaluatedAsNotApplicableAgainstPolicySet();
	authGateway.init();
	authGateway.evaluate(permitRequest);
	Response notApplicableResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_NOT_APPLICABLE;
	int actualResult = ((BalanaResponse) notApplicableResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void passRequestWhoWillBeEvaluatedAsDenyAgainstAPolicySet() throws Exception {
	loadPolicySetAboutCreateDeleteReadResource1();
	setUpBobDatabaseRoleFinderResult();
	Request permitRequest = getRequestWhoWillBeEvaluatedAsDenyAgainstPolicySet();
	authGateway.init();
	authGateway.evaluate(permitRequest);
	Response notApplicableResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_DENY;
	int actualResult = ((BalanaResponse) notApplicableResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void passRequestWhoWillBeEvaluatedAsPermitAgainstAPolicySet() throws Exception {
	loadPolicySetAboutCreateDeleteReadResource1();
	setUpAliceDatabaseRoleFinderResult();
	Request permitRequest = getRequestWhoWillBeEvaluatedAsPermitAgainstPolicySet();
	authGateway.init();
	authGateway.evaluate(permitRequest);
	Response notApplicableResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_PERMIT;
	int actualResult = ((BalanaResponse) notApplicableResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void passRequestWhoWillBeEvaluatedAsNotApplicableAgainstAPolicySetBuildWithPolicyReferences() throws Exception {
	loadNoPolicyDueToUnmatchingRequest();
	loadPoliciesByUriId();
	setUpAliceDatabaseRoleFinderResult();
	Request permitRequest = getRequestWhoWillBeEvaluatedAsNotApplicableAgainstPolicySet();
	authGateway.init();
	authGateway.evaluate(permitRequest);
	Response notApplicableResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_NOT_APPLICABLE;
	int actualResult = ((BalanaResponse) notApplicableResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void passRequestWhoWillBeEvaluatedAsDenyAgainstAPolicySetBuildWithPolicyReferences() throws Exception {
	loadPolicySetAboutCreateDeleteReadResource1MadeByReferences(policyFinder);
	loadPoliciesByUriId();
	setUpBobDatabaseRoleFinderResult();
	Request permitRequest = getRequestWhoWillBeEvaluatedAsDenyAgainstPolicySet();
	authGateway.init();
	authGateway.evaluate(permitRequest);
	Response notApplicableResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_DENY;
	int actualResult = ((BalanaResponse) notApplicableResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    @Test
    public void passRequestWhoWillBeEvaluatedAsPermitAgainstAPolicySetBuildWithPolicyReferences() throws Exception {
	loadPolicySetAboutCreateDeleteReadResource1MadeByReferences(policyFinder);
	loadPoliciesByUriId();
	setUpAliceDatabaseRoleFinderResult();
	Request permitRequest = getRequestWhoWillBeEvaluatedAsPermitAgainstPolicySet();
	authGateway.init();
	authGateway.evaluate(permitRequest);
	Response notApplicableResponse = authGateway.getResponse();
	int expectedResult = AbstractResult.DECISION_PERMIT;
	int actualResult = ((BalanaResponse) notApplicableResponse).getResponse().getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
    }

    /**
     * Here we mock {@link DatabasePermissionFinder} behavior to retreive
     * Alice's permissions.
     * 
     * @throws URISyntaxException
     */
    private void setUpAliceDatabaseRoleFinderResult() throws URISyntaxException {
	List<AttributeValue> retreivedPermissions = new ArrayList<>();
	retreivedPermissions.add(new StringAttribute("READA"));
	retreivedPermissions.add(new StringAttribute("READB"));
	retreivedPermissions.add(new StringAttribute("CREATE.RESOURCE1"));
	retreivedPermissions.add(new StringAttribute("GUEST"));
	URI attributeType = new URI("http://www.w3.org/2001/XMLSchema#string");
	URI attributeId = new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-permission");
	String issuer = null;
	URI category = new URI("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
	BagAttribute bagPermission = new BagAttribute(new URI("http://www.w3.org/2001/XMLSchema#string"), retreivedPermissions);
	EvaluationResult evaluationResult = new EvaluationResult(bagPermission);
	Mockito.when(databaseRoleFinder.findAttribute(Mockito.eq(attributeType), Mockito.eq(attributeId), Mockito.eq(issuer),
		Mockito.eq(category), Mockito.any())).thenReturn(evaluationResult);
    }

    /**
     * Here we mock {@link DatabasePermissionFinder} behavior to retreive Bob's
     * permissions.
     * 
     * @throws URISyntaxException
     */
    private void setUpBobDatabaseRoleFinderResult() throws URISyntaxException {
	List<AttributeValue> retreivedPermissions = new ArrayList<>();
	retreivedPermissions.add(new StringAttribute("READB"));
	retreivedPermissions.add(new StringAttribute("WRITEB"));
	URI attributeType = new URI("http://www.w3.org/2001/XMLSchema#string");
	URI attributeId = new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-permission");
	String issuer = null;
	URI category = new URI("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
	BagAttribute bagPermission = new BagAttribute(new URI("http://www.w3.org/2001/XMLSchema#string"), retreivedPermissions);
	EvaluationResult evaluationResult = new EvaluationResult(bagPermission);
	Mockito.when(databaseRoleFinder.findAttribute(Mockito.eq(attributeType), Mockito.eq(attributeId), Mockito.eq(issuer),
		Mockito.eq(category), Mockito.any())).thenReturn(evaluationResult);
    }

    private void loadSinglePolicyFile() throws Exception {
	AbstractPolicy policyfound = getPolicyByFile(policy);
	PolicyFinderResult policyFinderResult = new PolicyFinderResult(policyfound);
	Mockito.when(databasePolicyFinder.findPolicy(Mockito.any())).thenReturn(policyFinderResult);
    }

    /**
     * When you put Target at Policy or PolicySet level then PolicyFinder will
     * evaluate their target to see if they worth to be used in evaluating
     * requests. So to emulate a scenario where top level Target does not match
     * with request I have to mock the PolicyFinder to return an empty set of
     * policies.
     */
    private void loadNoPolicyDueToUnmatchingRequest() throws Exception {
	PolicyFinderResult policyFinderResult = new PolicyFinderResult();
	Mockito.when(databasePolicyFinder.findPolicy(Mockito.any())).thenReturn(policyFinderResult);
    }

    private void loadPolicySetAboutCreateDeleteReadResource1() throws Exception {
	AbstractPolicy policyfound = getPolicySetByFile(policySetCreateDeleteReadResource1);
	PolicyFinderResult policyFinderResult = new PolicyFinderResult(policyfound);
	Mockito.when(databasePolicyFinder.findPolicy(Mockito.any())).thenReturn(policyFinderResult);
    }

    private void loadPolicySetAboutCreateDeleteReadResource1MadeByReferences(PolicyFinder policyFinder) throws Exception {
	AbstractPolicy policyfound = getPolicySetByFile(policySetCreateDeleteReadResource1MadeByReferences, policyFinder);
	PolicyFinderResult policyFinderResult = new PolicyFinderResult(policyfound);
	Mockito.when(databasePolicyFinder.findPolicy(Mockito.any())).thenReturn(policyFinderResult);
    }

    private void loadCreateResource1Policy() throws Exception {
	AbstractPolicy policyCreateResource1found = getPolicyByFile(policyCreateResource1);
	PolicyFinderResult policyCreateResource1Finder = new PolicyFinderResult(policyCreateResource1found);
	Mockito.when(
		databasePolicyFinder.findPolicy(Mockito.eq(new URI(policyCreateResource1Id)), Mockito.eq(0), Mockito.any(), Mockito.any()))
		.thenReturn(policyCreateResource1Finder);
    }

    private void loadRemoveResource1Policy() throws Exception {
	AbstractPolicy policyRemoveResource1found = getPolicyByFile(policyRemoveResource1);
	PolicyFinderResult policyRemoveResource1Finder = new PolicyFinderResult(policyRemoveResource1found);
	Mockito.when(
		databasePolicyFinder.findPolicy(Mockito.eq(new URI(policyRemoveResource1Id)), Mockito.eq(0), Mockito.any(), Mockito.any()))
		.thenReturn(policyRemoveResource1Finder);
    }

    private void loadReadResource1Policy() throws Exception {
	AbstractPolicy policyReadResource1found = getPolicyByFile(policyReadResource1);
	PolicyFinderResult policyReadResource1Finder = new PolicyFinderResult(policyReadResource1found);
	Mockito.when(
		databasePolicyFinder.findPolicy(Mockito.eq(new URI(policyReadResource1Id)), Mockito.eq(0), Mockito.any(), Mockito.any()))
		.thenReturn(policyReadResource1Finder);
    }

    private void loadPoliciesByUriId() throws Exception {
	loadCreateResource1Policy();
	loadRemoveResource1Policy();
	loadReadResource1Policy();
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

    private AbstractPolicy getPolicySetByFile(File policyFile) throws Exception {
	return getPolicySetByFile(policyFile, null);
    }

    private AbstractPolicy getPolicySetByFile(File policyFile, PolicyFinder finder) throws Exception {
	DocumentBuilderFactory factory = Utils.getSecuredDocumentBuilderFactory();
	factory.setIgnoringComments(true);
	factory.setNamespaceAware(true);
	factory.setValidating(false);
	DocumentBuilder db = factory.newDocumentBuilder();
	InputStream stream = new FileInputStream(policyFile);
	Document doc = db.parse(stream);
	AbstractPolicy policy = PolicySet.getInstance(doc.getDocumentElement(), finder);
	return policy;
    }

    private Request getNotApplicableRequest() throws URISyntaxException {
	BalanaRequest request = new BalanaRequest();
	Set<Attributes> attributesSet = new HashSet<>();
	Set<Attribute> userAttributeSet = new HashSet<>();
	Set<Attribute> actionAttributeSet = new HashSet<>();
	Set<Attribute> resourceAttributeSet = new HashSet<>();
	/**
	 * User Alice wants to do action WRITE about resource RESOURCEA. There
	 * is not any policy who handle action WRITE.
	 */
	userAttributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id"), null, new DateTimeAttribute(),
		new StringAttribute("alice"), 1));
	actionAttributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:action:action-id"), null, new DateTimeAttribute(),
		new StringAttribute("WRITE"), 1));
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

    private Request getDenyRequest() throws URISyntaxException {
	BalanaRequest request = new BalanaRequest();
	Set<Attributes> attributesSet = new HashSet<>();
	Set<Attribute> userAttributeSet = new HashSet<>();
	Set<Attribute> actionAttributeSet = new HashSet<>();
	Set<Attribute> resourceAttributeSet = new HashSet<>();
	/**
	 * User Bob wants to do action READ about resource RESOURCEA. Bob won't
	 * be allowed to do so.
	 */
	userAttributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id"), null, new DateTimeAttribute(),
		new StringAttribute("bob"), 1));
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

    private Request getPermitRequest() throws URISyntaxException {
	BalanaRequest request = new BalanaRequest();
	Set<Attributes> attributesSet = new HashSet<>();
	Set<Attribute> userAttributeSet = new HashSet<>();
	Set<Attribute> actionAttributeSet = new HashSet<>();
	Set<Attribute> resourceAttributeSet = new HashSet<>();
	/**
	 * User Alice wants to do action READ about resource RESOURCEA. Alice
	 * will be allowed to do so.
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

    private Request getRequestWhoWillBeEvaluatedAsNotApplicableAgainstPolicySet() throws URISyntaxException {
	BalanaRequest request = new BalanaRequest();
	Set<Attributes> attributesSet = new HashSet<>();
	Set<Attribute> userAttributeSet = new HashSet<>();
	Set<Attribute> actionAttributeSet = new HashSet<>();
	Set<Attribute> resourceAttributeSet = new HashSet<>();
	/**
	 * User Alice wants to do action CREATE about resource RESOURCE2.
	 * RESOURCE2 is an unknown resource.
	 */
	userAttributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id"), null, new DateTimeAttribute(),
		new StringAttribute("Alice"), 1));
	actionAttributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:action:required.action"), null, new DateTimeAttribute(),
		new StringAttribute("READ"), 1));
	resourceAttributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:resource:required.resource"), null,
		new DateTimeAttribute(), new StringAttribute("RESOURCE2"), 1));
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

    private Request getRequestWhoWillBeEvaluatedAsDenyAgainstPolicySet() throws URISyntaxException {
	BalanaRequest request = new BalanaRequest();
	Set<Attributes> attributesSet = new HashSet<>();
	Set<Attribute> userAttributeSet = new HashSet<>();
	Set<Attribute> actionAttributeSet = new HashSet<>();
	Set<Attribute> resourceAttributeSet = new HashSet<>();
	/**
	 * User Bob wants to do action CREATE about resource RESOURCE1. Bob
	 * won't be allowed to do so.
	 */
	userAttributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id"), null, new DateTimeAttribute(),
		new StringAttribute("bob"), 1));
	actionAttributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:action:required.action"), null, new DateTimeAttribute(),
		new StringAttribute("DELETE"), 1));
	resourceAttributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:resource:required.resource"), null,
		new DateTimeAttribute(), new StringAttribute("RESOURCE1"), 1));
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

    private Request getRequestWhoWillBeEvaluatedAsPermitAgainstPolicySet() throws URISyntaxException {
	BalanaRequest request = new BalanaRequest();
	Set<Attributes> attributesSet = new HashSet<>();
	Set<Attribute> userAttributeSet = new HashSet<>();
	Set<Attribute> actionAttributeSet = new HashSet<>();
	Set<Attribute> resourceAttributeSet = new HashSet<>();
	/**
	 * User Alice wants to do action CREATE about resource RESOURCE1. Alice
	 * will be allowed to do so.
	 */
	userAttributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id"), null, new DateTimeAttribute(),
		new StringAttribute("alice"), 1));
	actionAttributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:action:required.action"), null, new DateTimeAttribute(),
		new StringAttribute("CREATE"), 1));
	resourceAttributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:resource:required.resource"), null,
		new DateTimeAttribute(), new StringAttribute("RESOURCE1"), 1));
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
}
