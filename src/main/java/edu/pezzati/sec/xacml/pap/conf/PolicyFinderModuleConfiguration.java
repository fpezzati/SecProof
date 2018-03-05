package edu.pezzati.sec.xacml.pap.conf;

import edu.pezzati.sec.xacml.exception.PolicyConfigurationException;
import edu.pezzati.sec.xacml.pap.FSysPolicyFinder;

public interface PolicyFinderModuleConfiguration {

    void handle(FSysPolicyFinder filesystemPolicyStore) throws PolicyConfigurationException;
}
