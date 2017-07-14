package edu.pezzati.sec.xaml;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Node;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.RFC822NameAttribute;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.xacml3.Attributes;

/**
 * Let's see if I got something about XACML. Here there are two tests who run
 * two requests against a policy (policywithdenyrule/policy.xml). Because of the
 * policy I expect that one test will produce a PERMIT response and the other
 * test will produce a DENY one. Only user jim@med.example.com will grant a
 * PERMIT.
 * 
 * @author fpezzati
 */
public class PolicyWithDenyRuleTest extends XacmlTest {

    private final Logger log = Logger.getLogger(getClass());

    @BeforeClass
    public static void init() {
	pDP = getPDP(new File("src/test/resources/policywithdenyrule"));
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

    @Test
    public void thisRequestWillBeEvaluatedAsDeny() throws URISyntaxException {
	RequestCtx request = buildDenyRequest();
	ResponseCtx response = pDP.evaluate(request);
	Set<AbstractResult> results = response.getResults();
	int expectedSize = 1;
	int actualSize = results.size();
	Assert.assertEquals(expectedSize, actualSize);
	AbstractResult result = results.iterator().next();
	log.info("DENY result: " + "\n" + result.encode());
	int expectedDecision = DENY;
	int actualDecision = result.getDecision();
	Assert.assertEquals(expectedDecision, actualDecision);
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
