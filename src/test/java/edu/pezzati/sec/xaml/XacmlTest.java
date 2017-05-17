package edu.pezzati.sec.xaml;

import java.io.File;

import org.wso2.balana.Balana;
import org.wso2.balana.PDP;
import org.wso2.balana.finder.impl.FileBasedPolicyFinderModule;

public class XacmlTest {

    public static final int PERMIT = 0;
    public static final int DENY = 1;
    public static final int INDETERMINATE = 2;
    public static final int NOT_APPLICABLE = 3;

    protected static PDP pDP;

    protected static PDP getPDP(File policy) {
	System.setProperty(FileBasedPolicyFinderModule.POLICY_DIR_PROPERTY, policy.getAbsolutePath());
	return new PDP(Balana.getInstance().getPdpConfig());
    }
}
