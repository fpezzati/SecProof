package edu.pezzati.sec.xaml;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.RFC822NameAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;
import org.wso2.balana.xacml3.Attributes;

public class SinglePolicyTest {

    private final Logger log = Logger.getLogger(getClass());
    private static final int PERMIT = 0;
    private static final int DENY = 1;
    //    private static final int INDETERMINATE = 2;
    private static final int NOT_APPLICABLE = 3;
    private PDPConfig pdpConfig;
    private PDP simplePDP;
    private File singlePolicySingleRule;

    @Before
    public void setUp() {
	System.clearProperty(FileBasedPolicyFinderModule.POLICY_DIR_PROPERTY);
	singlePolicySingleRule = new File("src/test/resources/singlepolicy");
	simplePDP = getPDP(singlePolicySingleRule);
    }

    @Test
    public void generatePDPwithSinglePolicy() {
	Assert.assertTrue(true);
    }

    @Test
    public void thisRequestWillBeEvaluatedAsNotApplicable() throws URISyntaxException {
	RequestCtx request = buildNotApplicableRequest();
	log.info("NOT_APPLICABLE request: " + "\n" + request.toString());
	ResponseCtx response = simplePDP.evaluate(request);
	Set<AbstractResult> results = response.getResults();
	int expectedSize = 1;
	int actualSize = results.size();
	Assert.assertEquals(expectedSize, actualSize);
	AbstractResult result = results.iterator().next();
	log.info("NOT_APPLICABLE result: " + "\n" + result.encode());
	int expectedDecision = NOT_APPLICABLE;
	int actualDecision = result.getDecision();
	Assert.assertEquals(expectedDecision, actualDecision);
    }

    @Test
    public void thisRequestWillBeEvaluatedAsPermitAgainstSinglePolicySingleRule() throws URISyntaxException {
	RequestCtx request = buildPermitRequest();
	log.info("PERMIT request: " + "\n" + request.toString());
	ResponseCtx response = simplePDP.evaluate(request);
	Set<AbstractResult> results = response.getResults();
	int expectedSize = 1;
	int actualSize = results.size();
	Assert.assertEquals(expectedSize, actualSize);
	AbstractResult result = results.iterator().next();
	log.info("PERMIT result: " + "\n" + result.encode());
	int expectedDecision = PERMIT;
	int actualDecision = result.getDecision();
	Assert.assertEquals(expectedDecision, actualDecision);
    }

    @Test
    public void thisRequestWillBeEvaluatedAsDeny() throws URISyntaxException {
	RequestCtx request = buildDenyRequest();
	log.info("DENY request: " + "\n" + request.toString());
	ResponseCtx response = simplePDP.evaluate(request);
	Set<AbstractResult> results = response.getResults();
	AbstractResult result = results.iterator().next();
	log.info("DENY result: " + "\n" + result.encode());
	int expectedDecision = DENY;
	int actualDecision = result.getDecision();
	Assert.assertEquals(expectedDecision, actualDecision);
    }

    @Test
    public void thisRequestWillBeEvaluatedAsIndeterminate() {

    }

    private PDP getPDP(File policy) {
	System.setProperty(FileBasedPolicyFinderModule.POLICY_DIR_PROPERTY, policy.getAbsolutePath());
	Balana balana = Balana.getInstance();
	pdpConfig = balana.getPdpConfig();
	return new PDP(pdpConfig);
    }

    private RequestCtx buildNotApplicableRequest() throws URISyntaxException {
	Set<Attributes> setOfAttributesSet = new HashSet<>();
	URI category = new URI("urn:oasis:names:tc:xacml:3.0:attribute-category:resource");
	Set<Attribute> attributes = new HashSet<>();
	attributes.add(new Attribute(new URI("attribute.id.1"), "the issuer", new DateTimeAttribute(), new StringAttribute("friends"), 1));
	Attributes attributeSet = new Attributes(category, attributes);
	setOfAttributesSet.add(attributeSet);
	Node documentRoot = null;
	RequestCtx request = new RequestCtx(setOfAttributesSet, documentRoot);
	return request;
    }

    private RequestCtx buildPermitRequest() throws URISyntaxException {
	Set<Attributes> setOfAttributesSet = new HashSet<>();
	URI category = new URI("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
	Set<Attribute> attributes = new HashSet<>();
	attributes.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id"), null, new DateTimeAttribute(),
		new RFC822NameAttribute("jim@med.example.com"), 1));
	Attributes attributeSet = new Attributes(category, attributes);
	setOfAttributesSet.add(attributeSet);
	Node documentRoot = null;
	RequestCtx request = new RequestCtx(setOfAttributesSet, documentRoot);
	return request;
    }

    private RequestCtx buildDenyRequest() throws URISyntaxException {
	Set<Attributes> setOfAttributesSet = new HashSet<>();
	URI category = new URI("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
	Set<Attribute> attributes = new HashSet<>();
	attributes.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id"), null, new DateTimeAttribute(),
		new RFC822NameAttribute("joe@med.example.com"), 1));
	Attributes attributeSet = new Attributes(category, attributes);
	setOfAttributesSet.add(attributeSet);
	Node documentRoot = null;
	RequestCtx request = new RequestCtx(setOfAttributesSet, documentRoot);
	return request;
    }
}