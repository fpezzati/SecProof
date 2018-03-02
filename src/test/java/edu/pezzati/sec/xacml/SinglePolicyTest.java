package edu.pezzati.sec.xacml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.RFC822NameAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.ResourceFinder;
import org.wso2.balana.xacml3.Attributes;

/**
 * Just everyone who have "med.example.com" as substring in their username will
 * get back a PERMIT as response.
 * 
 * @author fpezzati
 */
public class SinglePolicyTest extends XacmlTest {

    private PDP pDP;
    private final Logger log = Logger.getLogger(getClass());

    @Before
    public void setUp() {
	Set<String> policyLocations = new HashSet<>();
	policyLocations.add("src/test/resources/simplepolicy");
	PDPConfig PDPConfig = new PDPConfig(new AttributeFinder(), getPolicyFinder(policyLocations), new ResourceFinder());
	pDP = new PDP(PDPConfig);
    }

    @Test
    public void thisRequestWillBeEvaluatedAsNotApplicable() throws URISyntaxException {
	RequestCtx request = buildNotApplicableRequest();
	ResponseCtx response = pDP.evaluate(request);
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
    public void thisRequestWillBeEvaluatedAsPermit() throws URISyntaxException {
	RequestCtx request = buildPermitRequest();
	ResponseCtx response = pDP.evaluate(request);
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
	attributes.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id"), null, new DateTimeAttribute(),
		new StringAttribute("jim@med.example.com"), 1));
	Attributes attributeSet = new Attributes(category, attributes);
	setOfAttributesSet.add(attributeSet);
	Node documentRoot = null;
	RequestCtx request = new RequestCtx(setOfAttributesSet, documentRoot);
	return request;
    }
}