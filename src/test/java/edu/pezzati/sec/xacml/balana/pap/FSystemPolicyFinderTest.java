package edu.pezzati.sec.xacml.balana.pap;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.google.common.io.Files;

import edu.pezzati.sec.xacml.exception.PolicyConfigurationException;
import edu.pezzati.sec.xacml.exception.PolicyException;
import edu.pezzati.sec.xacml.pap.conf.FilesystemPolicyStoreConfiguration;
import edu.pezzati.sec.xacml.pap.conf.PolicyFinderModuleConfiguration;

public class FSystemPolicyFinderTest {

    @Rule
    public ExpectedException expex = ExpectedException.none();
    private FSystemPolicyFinder fsysPolicyFinder;
    private PolicyFinderModuleConfiguration policyStoreConfiguration;
    private File temporaryPolicyStore;
    private FilesystemPolicyStoreConfiguration filesystemPolicyStoreConfiguration;
    private FileSystem fileSystem;
    private WatchService watchService;

    @Before
    public void initForEachTest() throws IOException {
	fsysPolicyFinder = new FSystemPolicyFinder();
	policyStoreConfiguration = new FilesystemPolicyStoreConfiguration();
	filesystemPolicyStoreConfiguration = Mockito.spy(new FilesystemPolicyStoreConfiguration());
	temporaryPolicyStore = new File(System.getProperty("java.io.tmpdir"), "tmpPolicyStore");
	if (!temporaryPolicyStore.exists()) {
	    temporaryPolicyStore.mkdirs();
	}
	watchService = Mockito.mock(WatchService.class);
	fileSystem = Mockito.mock(FileSystem.class);
	Mockito.when(fileSystem.newWatchService()).thenReturn(watchService);
    }

    @After
    public void endForEachTest() {
	temporaryPolicyStore.delete();
    }

    @Test
    public void fPSCantHandleANullPolicyStoreConfigurationObject() throws PolicyException {
	policyStoreConfiguration = null;
	expex.expect(PolicyConfigurationException.class);
	fsysPolicyFinder.configure(policyStoreConfiguration);
	Assert.fail();
    }

    @Test
    public void fPSCantHandleANotFilesystemPolicyStoreConfigurationTypeObject() throws PolicyException {
	policyStoreConfiguration = new PolicyFinderModuleConfiguration() {
	    @Override
	    public void handle(FSystemPolicyFinder filesystemPolicyFinder) throws PolicyConfigurationException {
		return;
	    }
	};
	expex.expect(PolicyConfigurationException.class);
	fsysPolicyFinder.configure(policyStoreConfiguration);
	Assert.fail();
    }

    @Test
    public void fPSCantHaveAFilesystemPolicyStoreConfigurationObjectWithoutIndicatingAPolicyRepositoryDirectory() throws PolicyException {
	expex.expect(PolicyConfigurationException.class);
	fsysPolicyFinder.configure(policyStoreConfiguration);
	Assert.fail();
    }

    @Test
    public void fPSCantHaveAFilesystemPolicyStoreConfigurationObjectIndicatingANonExistingPolicyRepositoryDirectory()
	    throws PolicyException, URISyntaxException {
	expex.expect(PolicyConfigurationException.class);
	((FilesystemPolicyStoreConfiguration) policyStoreConfiguration).setPolicyStore(Paths.get(new URI("file:///unexistingpath/")));
	fsysPolicyFinder.configure(policyStoreConfiguration);
	Assert.fail();
    }

    @Test
    /**
     * FSP needs a directory where to check about policies. No matter if
     * directory is empty.
     */
    public void fPSCanRunWithAnEmptyPolicyRepository() throws PolicyException {
	filesystemPolicyStoreConfiguration.setPolicyStore(temporaryPolicyStore.toPath());
	fsysPolicyFinder.configure(filesystemPolicyStoreConfiguration);
	int expected = 0;
	int actual = fsysPolicyFinder.getPolicies().size();
	Assert.assertEquals(expected, actual);
    }

    @Test
    public void asPolicyIsAddedToPolicyRepositoryFPSMustLoadItAsSoonAsPossible() throws Exception {
	filesystemPolicyStoreConfiguration.setPolicyStore(temporaryPolicyStore.toPath());
	fsysPolicyFinder.configure(filesystemPolicyStoreConfiguration);
	//	File from = new File(Thread.currentThread().getContextClassLoader().getResource("policypool/policy1.xml").toURI());
	//	File to = new File(temporaryPolicyStore, from.getName());
	//	Files.copy(from, to);
	//	Thread.sleep(2000);
	String expectedPath = "policyfile1.xml";
	Path path = Mockito.mock(Path.class);
	Mockito.when(path.toString()).thenReturn(expectedPath);
	WatchEvent<Path> event = Mockito.mock(WatchEvent.class);
	Mockito.when(event.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
	Mockito.when(event.context()).thenReturn(path);
	List<WatchEvent<?>> events = new ArrayList<>();
	events.add(event);
	WatchKey key = Mockito.mock(WatchKey.class);
	Mockito.when(key.pollEvents()).thenReturn(events);
	Mockito.when(watchService.take()).thenReturn(key);
	int expected = 1;
	int actual = fsysPolicyFinder.getPolicies().size();
	Assert.assertEquals(expected, actual);
    }

    @Ignore
    @Test
    public void asPolicyIsRemovedFromPolicyRepositoryFPSMustUnloadItAsSoonAsPossible() throws Exception {
	filesystemPolicyStoreConfiguration.setPolicyStore(temporaryPolicyStore.toPath());
	fsysPolicyFinder.configure(filesystemPolicyStoreConfiguration);
	File from = new File(Thread.currentThread().getContextClassLoader().getResource("policypool/policy1.xml").toURI());
	File to = new File(temporaryPolicyStore, from.getName());
	Files.copy(from, to);
	Thread.sleep(2000);
	to.delete();
	Thread.sleep(2000);
	int expected = 0;
	int actual = fsysPolicyFinder.getPolicies().size();
	Assert.assertEquals(expected, actual);
    }

    @Ignore
    @Test
    public void asPolicyIsAddedToPolicyRepositoryFSPMustReplaceTheExistingOldOneWithTheNewOneAsSoonAsPossible() throws Exception {
	filesystemPolicyStoreConfiguration.setPolicyStore(temporaryPolicyStore.toPath());
	fsysPolicyFinder.configure(filesystemPolicyStoreConfiguration);
	File from1 = new File(Thread.currentThread().getContextClassLoader().getResource("policypool/policy1.xml").toURI());
	File from2 = new File(Thread.currentThread().getContextClassLoader().getResource("policypool/policy3.xml").toURI());
	File to = new File(temporaryPolicyStore, from1.getName());
	Files.copy(from1, to);
	Thread.sleep(2000);
	Files.copy(from2, to);
	Thread.sleep(2000);
	int expected = 1;
	int actual = fsysPolicyFinder.getPolicies().size();
	Assert.assertEquals(expected, actual);
    }

    @Ignore
    @Test
    public void fPSMustRejectNonPolicyFilesAndCarryOn() {
	Assert.fail();
    }
}
