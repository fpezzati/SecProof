package edu.pezzati.sec.xacml.pap;

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
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.Policy;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.RFC822NameAttribute;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.ResourceFinder;
import org.wso2.balana.utils.Utils;
import org.wso2.balana.xacml3.Attributes;

import edu.pezzati.sec.xacml.balana.pap.PAPInMemoryModule;

/**
 * Test class to show how to load policies by PAP module and change policies on
 * the fly to have a different behavior.
 * 
 * @author pezzati
 */
public class PAPusageTest {

    private static File policy1;
    private List<AbstractPolicy> policies;

    @BeforeClass
    public static void init() {
	policy1 = new File("src/test/resources/policypool/policy1.xml");
    }

    @Before
    public void setUp() {
	policies = new ArrayList<>();
    }

    @Test
    public void loadPolicies() throws Exception {
	AttributeFinder attributeFinder = new AttributeFinder();
	PAPInMemoryModule papInMemoryModule = new PAPInMemoryModule();
	policies = getPolicies();
	papInMemoryModule.setPolicies(policies);
	PolicyFinder policyFinder = new PolicyFinder();
	Set<PolicyFinderModule> modules = new HashSet<>();
	modules.add(papInMemoryModule);
	policyFinder.setModules(modules);
	ResourceFinder resourceFinder = new ResourceFinder();
	PDPConfig pdpConfig = new PDPConfig(attributeFinder, policyFinder, resourceFinder);
	PDP pdp = new PDP(pdpConfig);
	RequestCtx request = buildRequest();
	ResponseCtx response = pdp.evaluate(request);
	int expectedDecision = 0;
	int actualDecision = response.getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedDecision, actualDecision);
	policies.clear();
	response = pdp.evaluate(request);
	expectedDecision = 2;
	actualDecision = response.getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedDecision, actualDecision);
    }

    private RequestCtx buildRequest() throws URISyntaxException {
	Set<Attributes> attributesSet = new HashSet<>();
	Set<Attribute> attributeSet = new HashSet<>();
	attributeSet.add(new Attribute(new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id"), null, new DateTimeAttribute(),
		new RFC822NameAttribute("joe@med.example.com"), 1));
	URI category = new URI("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
	Attributes attributes = new Attributes(category, attributeSet);
	attributesSet.add(attributes);
	Node documentRoot = null;
	RequestCtx request = new RequestCtx(attributesSet, documentRoot);
	return request;
    }

    private List<AbstractPolicy> getPolicies() throws Exception {
	List<AbstractPolicy> policies = new ArrayList<>();
	policies.add(getPolicy(policy1));
	return policies;
    }

    private Policy getPolicy(File policyFile) throws Exception {
	DocumentBuilderFactory factory = Utils.getSecuredDocumentBuilderFactory();
	factory.setIgnoringComments(true);
	factory.setNamespaceAware(true);
	factory.setValidating(false);
	DocumentBuilder db = factory.newDocumentBuilder();
	InputStream stream = new FileInputStream(policyFile);
	Document doc = db.parse(stream);
	Policy policy = Policy.getInstance(doc.getDocumentElement());
	return policy;
    }
}
