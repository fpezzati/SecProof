package edu.pezzati.sec.xacml.pap;

import java.util.HashSet;

import edu.pezzati.sec.xacml.exception.PolicyConfigurationException;
import edu.pezzati.sec.xacml.pap.conf.PolicyStoreConfiguration;

public interface PolicyStore {

    void configure(PolicyStoreConfiguration policyStoreConfiguration) throws PolicyConfigurationException;

    HashSet<String, > getPolicies();
}
