package edu.pezzati.sec.xacml.pap;

import java.nio.file.Files;
import java.nio.file.Path;

import edu.pezzati.sec.xacml.exception.PolicyConfigurationException;
import edu.pezzati.sec.xacml.pap.conf.PolicyStoreConfiguration;

public class FilesystemPolicyStore implements PolicyStore {

    private Path policyStore;

    @Override
    public void configure(PolicyStoreConfiguration policyStoreConfiguration) throws PolicyConfigurationException {
	if (policyStoreConfiguration == null)
	    throw new PolicyConfigurationException();
	policyStoreConfiguration.handle(this);
	if (this.policyStore == null || Files.notExists(policyStore))
	    throw new PolicyConfigurationException();
    }

    public void setPolicyStore(Path policyStore) {
	this.policyStore = policyStore;
    }
}
