package edu.pezzati.sec.xacml.balana.pap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.MatchResult;
import org.wso2.balana.Policy;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.VersionConstraints;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;
import org.wso2.balana.utils.Utils;

import edu.pezzati.sec.xacml.exception.PolicyConfigurationException;
import edu.pezzati.sec.xacml.pap.conf.PolicyFinderModuleConfiguration;

public class FSystemPolicyFinder extends PolicyFinderModule {

    private Path policyStore;
    private HashMap<URI, AbstractPolicy> policies;

    public FSystemPolicyFinder() {
	policies = new HashMap<>();
    }

    public void configure(PolicyFinderModuleConfiguration policyStoreConfiguration) throws PolicyConfigurationException {
	if (policyStoreConfiguration == null)
	    throw new PolicyConfigurationException();
	policyStoreConfiguration.handle(this);
	if (this.policyStore == null || Files.notExists(policyStore))
	    throw new PolicyConfigurationException();

	Thread t = new Thread() {
	    @Override
	    public void run() {
		try (WatchService watcher = policyStore.getFileSystem().newWatchService();) {
		    policyStore.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
			    StandardWatchEventKinds.ENTRY_DELETE);
		    while (true) {
			WatchKey key = null;
			try {
			    key = watcher.take();
			} catch (InterruptedException e) {
			    return;
			}
			for (WatchEvent<?> event : key.pollEvents()) {
			    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
				AbstractPolicy policy = getPolicy(new File(policyStore.toFile(), ((Path) event.context()).toString()));
				getPolicies().put(((Path) event.context()).toUri(), policy);
			    } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
				getPolicies().remove(((Path) event.context()).toString());
			    } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
				AbstractPolicy policy = getPolicy(new File(policyStore.toFile(), ((Path) event.context()).toString()));
				getPolicies().put(((Path) event.context()).toUri(), policy);
			    }
			    if (!key.reset()) {
				break;
			    }
			}
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	};
	t.start();
    }

    public void setPolicyStore(Path policyStore) {
	this.policyStore = policyStore;
    }

    @Override
    public void init(PolicyFinder finder) {
	return;
    }

    @Override
    public boolean isRequestSupported() {
	return true;
    }

    @Override
    public boolean isIdReferenceSupported() {
	return true;
    }

    @Override
    public PolicyFinderResult findPolicy(EvaluationCtx context) {
	List<AbstractPolicy> matchingPolicies = new ArrayList<>();
	for (AbstractPolicy policy : getPolicies().values()) {
	    MatchResult matchResult = policy.match(context);
	    if (matchResult.getResult() == MatchResult.MATCH) {
		matchingPolicies.add(policy);
	    }
	}
	return getFinderResult(matchingPolicies);
    }

    @Override
    public PolicyFinderResult findPolicy(URI idReference, int type, VersionConstraints constraints, PolicyMetaData parentMetaData) {
	List<AbstractPolicy> matchingPolicies = new ArrayList<>();
	for (AbstractPolicy policy : getPolicies().values()) {
	    if (policy.getId().equals(idReference)) {
		matchingPolicies.add(getPolicies().get(idReference));
		break;
	    }
	}
	return getFinderResult(matchingPolicies);
    }

    private PolicyFinderResult getFinderResult(List<AbstractPolicy> policies) {
	if (policies.size() == 1) {
	    return new PolicyFinderResult(policies.get(0));
	} else {
	    return new PolicyFinderResult(new Status(Arrays.asList(new String[] { Status.STATUS_PROCESSING_ERROR })));
	}
    }

    public HashMap<URI, AbstractPolicy> getPolicies() {
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
