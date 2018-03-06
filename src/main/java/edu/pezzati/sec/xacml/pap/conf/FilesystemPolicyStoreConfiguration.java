package edu.pezzati.sec.xacml.pap.conf;

import java.nio.file.Path;

import edu.pezzati.sec.xacml.balana.pap.FSystemPolicyFinder;
import edu.pezzati.sec.xacml.exception.PolicyConfigurationException;

public class FilesystemPolicyStoreConfiguration implements PolicyFinderModuleConfiguration {

    private Path policyStore;

    public Path getPolicyStore() {
	return policyStore;
    }

    public void setPolicyStore(Path policyStore) {
	this.policyStore = policyStore;
    }

    @Override
    public void handle(FSystemPolicyFinder filesystemPolicyFinder) throws PolicyConfigurationException {
	filesystemPolicyFinder.setPolicyStore(getPolicyStore());
    }
}
