package edu.pezzati.sec.xacml.pap.conf;

import edu.pezzati.sec.xacml.balana.pap.FileSystemPolicyFinder;
import edu.pezzati.sec.xacml.exception.PolicyConfigurationException;

public interface PolicyFinderModuleConfiguration {

    void handle(FileSystemPolicyFinder filesystemPolicyStore) throws PolicyConfigurationException;
}
