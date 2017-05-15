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
import org.wso2.balana.ConfigurationStore;
import org.wso2.balana.PDP;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.Attribute;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;
import org.wso2.balana.xacml3.Attributes;

public class SimplePDPTest {

    private final Logger log = Logger.getLogger(getClass());
    private static final int NOT_APPLICABLE = 3;
    private PDPConfig pdpConfig;
    private PDP simplePDP;
    private File singlePdpConf;
    private File singlePolicyMultipleRules;
    private File singlePolicySingleRule;

    @Before
    public void setUp() {
	System.clearProperty(ConfigurationStore.PDP_CONFIG_PROPERTY);
	singlePdpConf = new File("src/test/resources/conf/simple.pdp.conf.xml");
	System.clearProperty(FileBasedPolicyFinderModule.POLICY_DIR_PROPERTY);
	singlePolicyMultipleRules = new File("src/test/resources/policies");
	singlePolicySingleRule = new File("src/test/resources/policies");
    }

    public void generatePDPWithNullConfiguration() throws URISyntaxException {
	System.setProperty(ConfigurationStore.PDP_CONFIG_PROPERTY, singlePdpConf.getAbsolutePath());
	Balana balana = Balana.getInstance();
	pdpConfig = balana.getPdpConfig();
	simplePDP = new PDP(pdpConfig);
	RequestCtx xacmlRequest = buildRequest();
	simplePDP.evaluate(xacmlRequest.toString());
    }

    @Test
    public void generatePDPwithSinglePolicy() {
	System.setProperty(FileBasedPolicyFinderModule.POLICY_DIR_PROPERTY, singlePolicyMultipleRules.getAbsolutePath());
	Balana balana = Balana.getInstance();
	pdpConfig = balana.getPdpConfig();
	simplePDP = new PDP(pdpConfig);
	Assert.assertTrue(true);
    }

    @Test
    public void thisRequestWillBeEvaluatedAsNotApplicable() throws URISyntaxException {
	pdpConfig = Balana.getInstance().getPdpConfig();
	simplePDP = new PDP(pdpConfig);
	RequestCtx request = buildRequest();
	ResponseCtx response = simplePDP.evaluate(request);
	Set<AbstractResult> results = response.getResults();
	int expectedSize = 1;
	int actualSize = results.size();
	Assert.assertEquals(expectedSize, actualSize);
	AbstractResult result = results.iterator().next();
	log.info("Not applicable request: " + "\n" + result.encode());
	int expectedDecision = NOT_APPLICABLE;
	int actualDecision = result.getDecision();
	Assert.assertEquals(expectedDecision, actualDecision);
    }

    @Test
    public void thisRequestWillBeEvaluatedAsPermit() {
	pdpConfig = Balana.getInstance().getPdpConfig();
	simplePDP = new PDP(pdpConfig);
    }

    @Test
    public void thisRequestWillBeEvaluatedAsDeny() {

    }

    @Test
    public void thisRequestWillBeEvaluatedAsIndeterminate() {

    }

    private RequestCtx buildRequest() throws URISyntaxException {
	Set<Attributes> setOfAttributesSet = new HashSet<>();
	URI category = new URI("urn:oasis:names:tc:xacml:3.0:attribute-category:resource");
	Set<Attribute> attributes = new HashSet<>();
	attributes.add(new Attribute(new URI("attribute.id.1"), "the issuer", new DateTimeAttribute(), new StringAttribute("friends"), 1));
	Attributes attributeSet = new Attributes(category, attributes);
	setOfAttributesSet.add(attributeSet);
	Node documentRoot = null; /** ??? */
	RequestCtx request = new RequestCtx(setOfAttributesSet, documentRoot);
	return request;
    }
}