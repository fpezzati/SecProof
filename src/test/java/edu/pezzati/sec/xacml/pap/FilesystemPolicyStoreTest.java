package edu.pezzati.sec.xacml.pap;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.pezzati.sec.xacml.exception.PolicyConfigurationException;
import edu.pezzati.sec.xacml.exception.PolicyException;
import edu.pezzati.sec.xacml.pap.conf.FilesystemPolicyStoreConfiguration;
import edu.pezzati.sec.xacml.pap.conf.PolicyStoreConfiguration;

public class FilesystemPolicyStoreTest {

    @Rule
    public ExpectedException expex = ExpectedException.none();
    private PolicyStore fsysPolicyStore;
    private PolicyStoreConfiguration policyStoreConfiguration;
    private File temporaryPolicyStore;
    private FilesystemPolicyStoreConfiguration filesystemPolicyStoreConfiguration;

    @Before
    public void initForEachTest() {
	fsysPolicyStore = new FilesystemPolicyStore();
	policyStoreConfiguration = new FilesystemPolicyStoreConfiguration();
	filesystemPolicyStoreConfiguration = new FilesystemPolicyStoreConfiguration();
	temporaryPolicyStore = new File(System.getProperty("java.io.tmpdir"), "tmpPolicyStore");
	if (!temporaryPolicyStore.exists()) {
	    temporaryPolicyStore.mkdirs();
	}
    }

    @After
    public void endForEachTest() {
	temporaryPolicyStore.delete();
    }

    @Test
    public void fPSCantHandleANullPolicyStoreConfigurationObject() throws PolicyException {
	policyStoreConfiguration = null;
	expex.expect(PolicyConfigurationException.class);
	fsysPolicyStore.configure(policyStoreConfiguration);
	Assert.fail();
    }

    @Test
    public void fPSCantHandleANotFilesystemPolicyStoreConfigurationTypeObject() throws PolicyException {
	policyStoreConfiguration = new PolicyStoreConfiguration() {
	    @Override
	    public void handle(FilesystemPolicyStore filesystemPolicyStore) throws PolicyConfigurationException {
		return;
	    }
	};
	expex.expect(PolicyConfigurationException.class);
	fsysPolicyStore.configure(policyStoreConfiguration);
	Assert.fail();
    }

    @Test
    public void fPSCantHaveAFilesystemPolicyStoreConfigurationObjectWithoutIndicatingAPolicyRepositoryDirectory() throws PolicyException {
	expex.expect(PolicyConfigurationException.class);
	fsysPolicyStore.configure(policyStoreConfiguration);
	Assert.fail();
    }

    @Test
    public void fPSCantHaveAFilesystemPolicyStoreConfigurationObjectIndicatingANonExistingPolicyRepositoryDirectory()
	    throws PolicyException, URISyntaxException {
	expex.expect(PolicyConfigurationException.class);
	((FilesystemPolicyStoreConfiguration) policyStoreConfiguration).setPolicyStore(Paths.get(new URI("file:///unexistingpath/")));
	fsysPolicyStore.configure(policyStoreConfiguration);
	Assert.fail();
    }

    @Test
    public void fPSCanRunWithAnEmptyPolicyRepository() throws PolicyException {
	filesystemPolicyStoreConfiguration.setPolicyStore(temporaryPolicyStore.toPath());
	fsysPolicyStore.configure(filesystemPolicyStoreConfiguration);
	Assert.assertTrue(fsysPolicyStore.getPolicies().isEmpty());
    }

    @Test
    public void asPolicyIsAddedToPolicyRepositoryFPSMustLoadItImmediately() {

	Assert.fail();
    }

    @Test
    public void asPolicyIsRemovedFromPolicyRepositoryFPSMustUnloadItImmediately() {
	Assert.fail();
    }

    @Test
    public void fPSMustRejectNonPolicyFilesAndCarryOn() {
	Assert.fail();
    }
}
