package edu.pezzati.sec.xacml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.wso2.balana.Policy;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;
import org.wso2.balana.utils.Utils;

public class XacmlTest {

    public static final int PERMIT = 0;
    public static final int DENY = 1;
    public static final int INDETERMINATE = 2;
    public static final int NOT_APPLICABLE = 3;

    protected Policy getPolicy(File policyFile) throws Exception {
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

    protected PolicyFinder getPolicyFinder(Set<String> policyLocations) {
	PolicyFinder policyFinder = new PolicyFinder();
	Set<PolicyFinderModule> modules = new HashSet<>();
	PolicyFinderModule policyFinderModule = new FileBasedPolicyFinderModule(policyLocations);
	modules.add(policyFinderModule);
	policyFinder.setModules(modules);
	return policyFinder;
    }
}
