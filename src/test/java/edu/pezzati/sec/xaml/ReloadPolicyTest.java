package edu.pezzati.sec.xaml;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;
import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.RFC822NameAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;
import org.wso2.balana.xacml3.Attributes;

public class ReloadPolicyTest extends XacmlTest {

    private File firstPolicy;
    private File secondPolicy;

    /**
     * Balana's PDP can't reload a statically provided policy file.
     * 
     * @throws Exception
     */
    @Test
    public void reloadPolicyCheck() throws Exception {
	firstPolicy = new File("src/test/resources/policywithdenyrule");
	secondPolicy = new File("src/test/resources/policytoreload");
	System.setProperty(FileBasedPolicyFinderModule.POLICY_DIR_PROPERTY, firstPolicy.getAbsolutePath());
	PDP pDP = new PDP(Balana.getInstance().getPdpConfig());
	RequestCtx request = buildPermitRequest();
	ResponseCtx response1 = pDP.evaluate(request);
	int expectedResult = XacmlTest.PERMIT;
	int actualResult = response1.getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedResult, actualResult);
	System.setProperty(FileBasedPolicyFinderModule.POLICY_DIR_PROPERTY, secondPolicy.getAbsolutePath());
	pDP = new PDP(Balana.getInstance().getPdpConfig());
	ResponseCtx response2 = pDP.evaluate(request);
	expectedResult = XacmlTest.DENY;
	actualResult = response2.getResults().iterator().next().getDecision();
	Assert.assertNotEquals(expectedResult, actualResult);
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