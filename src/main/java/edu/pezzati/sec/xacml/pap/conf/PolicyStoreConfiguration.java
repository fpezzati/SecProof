package edu.pezzati.sec.xacml.pap.conf;

import edu.pezzati.sec.xacml.exception.PolicyConfigurationException;
import edu.pezzati.sec.xacml.pap.FilesystemPolicyStore;

public interface PolicyStoreConfiguration {

    void handle(FilesystemPolicyStore filesystemPolicyStore) throws PolicyConfigurationException;
}
