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

import com.google.common.io.Files;

import edu.pezzati.sec.xacml.exception.PolicyConfigurationException;
import edu.pezzati.sec.xacml.exception.PolicyException;
import edu.pezzati.sec.xacml.pap.conf.FilesystemPolicyStoreConfiguration;
import edu.pezzati.sec.xacml.pap.conf.PolicyFinderModuleConfiguration;

public class FSysPolicyFinderTest {

    @Rule
    public ExpectedException expex = ExpectedException.none();
    private FSysPolicyFinder fsysPolicyStore;
    private PolicyFinderModuleConfiguration policyStoreConfiguration;
    private File temporaryPolicyStore;
    private FilesystemPolicyStoreConfiguration filesystemPolicyStoreConfiguration;

    @Before
    public void initForEachTest() {
	fsysPolicyStore = new FSysPolicyFinder();
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
	policyStoreConfiguration = new PolicyFinderModuleConfiguration() {
	    @Override
	    public void handle(FSysPolicyFinder filesystemPolicyStore) throws PolicyConfigurationException {
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
    /**
     * FSP needs a directory where to check about policies. No matter if
     * directory is empty.
     */
    public void fPSCanRunWithAnEmptyPolicyRepository() throws PolicyException {
	filesystemPolicyStoreConfiguration.setPolicyStore(temporaryPolicyStore.toPath());
	fsysPolicyStore.configure(filesystemPolicyStoreConfiguration);
	int expected = 0;
	int actual = fsysPolicyStore.getPolicies().size();
	Assert.assertEquals(expected, actual);
    }

    @Test
    public void asPolicyIsAddedToPolicyRepositoryFPSMustLoadItAsSoonAsPossible() throws Exception {
	filesystemPolicyStoreConfiguration.setPolicyStore(temporaryPolicyStore.toPath());
	fsysPolicyStore.configure(filesystemPolicyStoreConfiguration);
	File from = new File(Thread.currentThread().getContextClassLoader().getResource("policypool/policy1.xml").toURI());
	File to = new File(temporaryPolicyStore, from.getName());
	Files.copy(from, to);
	Thread.sleep(2000);
	int expected = 1;
	int actual = fsysPolicyStore.getPolicies().size();
	Assert.assertEquals(expected, actual);
    }

    @Test
    public void asPolicyIsRemovedFromPolicyRepositoryFPSMustUnloadItAsSoonAsPossible() throws Exception {
	filesystemPolicyStoreConfiguration.setPolicyStore(temporaryPolicyStore.toPath());
	fsysPolicyStore.configure(filesystemPolicyStoreConfiguration);
	File from = new File(Thread.currentThread().getContextClassLoader().getResource("policypool/policy1.xml").toURI());
	File to = new File(temporaryPolicyStore, from.getName());
	Files.copy(from, to);
	Thread.sleep(2000);
	to.delete();
	Thread.sleep(2000);
	int expected = 0;
	int actual = fsysPolicyStore.getPolicies().size();
	Assert.assertEquals(expected, actual);
    }

    @Test
    public void asPolicyIsAddedToPolicyRepositoryFSPMustReplaceTheExistingOldOneWithTheNewOneAsSoonAsPossible() throws Exception {
	filesystemPolicyStoreConfiguration.setPolicyStore(temporaryPolicyStore.toPath());
	fsysPolicyStore.configure(filesystemPolicyStoreConfiguration);
	File from1 = new File(Thread.currentThread().getContextClassLoader().getResource("policypool/policy1.xml").toURI());
	File from2 = new File(Thread.currentThread().getContextClassLoader().getResource("policypool/policy3.xml").toURI());
	File to = new File(temporaryPolicyStore, from1.getName());
	Files.copy(from1, to);
	Thread.sleep(2000);
	Files.copy(from2, to);
	Thread.sleep(2000);
	int expected = 1;
	int actual = fsysPolicyStore.getPolicies().size();
	Assert.assertEquals(expected, actual);
    }

    @Test
    public void fPSMustRejectNonPolicyFilesAndCarryOn() {
	Assert.fail();
    }
}
