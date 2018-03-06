package edu.pezzati.sec.xacml.pap.conf;

import edu.pezzati.sec.xacml.balana.pap.FSystemPolicyFinder;
import edu.pezzati.sec.xacml.exception.PolicyConfigurationException;

public interface PolicyFinderModuleConfiguration {

    void handle(FSystemPolicyFinder filesystemPolicyStore) throws PolicyConfigurationException;
}
