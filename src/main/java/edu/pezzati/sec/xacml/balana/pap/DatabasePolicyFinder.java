package edu.pezzati.sec.xacml.balana.pap;

import java.net.URI;

import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.VersionConstraints;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;

public class DatabasePolicyFinder extends PolicyFinderModule {

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
	//	List<AbstractPolicy> matchingPolicies = new ArrayList<>();
	//	for (AbstractPolicy policy : getPolicies()) {
	//	    MatchResult matchResult = policy.match(context);
	//	    if (matchResult.getResult() == MatchResult.MATCH) {
	//		matchingPolicies.add(policy);
	//	    }
	//	}
	//	return retreivePolicies(matchingPolicies);
	return null;
    }

    @Override
    public PolicyFinderResult findPolicy(URI idReference, int type, VersionConstraints constraints, PolicyMetaData parentMetaData) {
	//	List<AbstractPolicy> matchingPolicies = new ArrayList<>();
	//	for (AbstractPolicy policy : getPolicies()) {
	//	    if (policy.getId().compareTo(idReference) == 0) {
	//		matchingPolicies.add(policy);
	//	    }
	//	}
	//	return retreivePolicies(matchingPolicies);
	return null;
    }

    //    private PolicyFinderResult retreivePolicies(List<AbstractPolicy> policies) {
    //	if (policies.size() == 1) {
    //	    return new PolicyFinderResult(policies.get(0));
    //	} else {
    //	    return new PolicyFinderResult(new Status(Arrays.asList(new String[] { Status.STATUS_PROCESSING_ERROR })));
    //	}
    //    }
}
