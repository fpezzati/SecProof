package edu.pezzati.sec.xacml.pip;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Node;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.RFC822NameAttribute;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.finder.AttributeFinder;
import org.wso2.balana.finder.AttributeFinderModule;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.ResourceFinder;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;
import org.wso2.balana.xacml3.Attributes;

import edu.pezzati.sec.xacml.XacmlTest;
import edu.pezzati.sec.xacml.pip.PIPRoleFinderModule;

/**
 * @author pezzati
 */
public class PIPusageTest extends XacmlTest {

    /**
     * This test wants to provide an example about using a PIP to retreive
     * user's roles by his username. Test is build on top of Balana.
     */
    @BeforeClass
    public static void initTest() {
	File policies = new File("src/test/resources/policypool/policy1.with.obligations.xml");
	System.setProperty(FileBasedPolicyFinderModule.POLICY_DIR_PROPERTY, policies.getAbsolutePath());
    }

    @Test
    public void getRolesByUsernameWithPIP() throws URISyntaxException {
	AttributeFinder attributeFinder = getAttributeFinder();
	PolicyFinder policyFinder = getPolicyFinder();
	ResourceFinder resourceFinder = new ResourceFinder();
	PDPConfig pdpConfig = new PDPConfig(attributeFinder, policyFinder, resourceFinder);
	PDP pdp = new PDP(pdpConfig);
	RequestCtx request = buildRequest();
	ResponseCtx response = pdp.evaluate(request);
	int expectedResultsSize = 1;
	int actualResultsSize = response.getResults().size();
	Assert.assertEquals(expectedResultsSize, actualResultsSize);
	int expectedDecision = PERMIT;
	int actualDecision = response.getResults().iterator().next().getDecision();
	Assert.assertEquals(expectedDecision, actualDecision);
    }

    private AttributeFinder getAttributeFinder() throws URISyntaxException {
	AttributeFinder attributeFinder = new AttributeFinder();
	List<AttributeFinderModule> modules = new ArrayList<>();
	modules.add(new PIPRoleFinderModule());
	attributeFinder.setModules(modules);
	return attributeFinder;
    }

    private PolicyFinder getPolicyFinder() {
	PolicyFinder policyFinder = new PolicyFinder();
	Set<PolicyFinderModule> modules = new HashSet<>();
	PolicyFinderModule policyFinderModule = new FileBasedPolicyFinderModule();
	modules.add(policyFinderModule);
	policyFinder.setModules(modules);
	return policyFinder;
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
}
