package edu.pezzati.sec.xacml.balana.pap;

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
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.VersionConstraints;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.PolicyFinderResult;

import edu.pezzati.sec.xacml.exception.PolicyConfigurationException;
import edu.pezzati.sec.xacml.exception.PolicyException;
import edu.pezzati.sec.xacml.pap.conf.FilesystemPolicyStoreConfiguration;
import edu.pezzati.sec.xacml.pap.conf.PolicyFinderModuleConfiguration;

public class FSystemPolicyFinderTest {

    @Rule
    public ExpectedException expex = ExpectedException.none();
    private FSystemPolicyFinder fSP;
    private PolicyFinderModuleConfiguration policyStoreConfiguration;
    private FilesystemPolicyStoreConfiguration filesystemPolicyStoreConfiguration;
    private FileSystem fileSystem;
    private WatchService watchService;

    @Before
    public void initForEachTest() throws IOException, Exception {
	fSP = new FSystemPolicyFinder();
	policyStoreConfiguration = new FilesystemPolicyStoreConfiguration();
	filesystemPolicyStoreConfiguration = new FilesystemPolicyStoreConfiguration();
	watchService = Mockito.mock(WatchService.class);
	fileSystem = Mockito.mock(FileSystem.class);
	Mockito.when(fileSystem.newWatchService()).thenReturn(watchService);
	filesystemPolicyStoreConfiguration
		.setPolicyStore(Paths.get(Thread.currentThread().getContextClassLoader().getResource("policypool").toURI()));
	fSP.configure(filesystemPolicyStoreConfiguration);
    }

    @After
    public void endForEachTest() {
	fSP.stop();
    }

    @Test
    public void fSPCantHandleANullPolicyStoreConfigurationObject() throws PolicyException {
	policyStoreConfiguration = null;
	expex.expect(PolicyConfigurationException.class);
	fSP.configure(policyStoreConfiguration);
	Assert.fail();
    }

    @Test
    public void fSPCantHandleANotFilesystemPolicyStoreConfigurationTypeObject() throws PolicyException {
	fSP = new FSystemPolicyFinder();
	policyStoreConfiguration = new PolicyFinderModuleConfiguration() {
	    @Override
	    public void handle(FSystemPolicyFinder filesystemPolicyFinder) throws PolicyConfigurationException {
		return;
	    }
	};
	expex.expect(PolicyConfigurationException.class);
	fSP.configure(policyStoreConfiguration);
	Assert.fail();
    }

    @Test
    public void fSPCantHaveAFilesystemPolicyStoreConfigurationObjectWithoutIndicatingAPolicyRepositoryDirectory() throws PolicyException {
	expex.expect(PolicyConfigurationException.class);
	fSP.configure(policyStoreConfiguration);
	Assert.fail();
    }

    @Test
    public void fSPCantHaveAFilesystemPolicyStoreConfigurationObjectIndicatingANonExistingPolicyRepositoryDirectory()
	    throws PolicyException, URISyntaxException {
	expex.expect(PolicyConfigurationException.class);
	((FilesystemPolicyStoreConfiguration) policyStoreConfiguration).setPolicyStore(Paths.get(new URI("file:///unexistingpath/")));
	fSP.configure(policyStoreConfiguration);
	Assert.fail();
    }

    @Test
    /**
     * FSP needs a directory where to check about policies. No matter if
     * directory is empty.
     */
    public void fSPCanRunWithAnEmptyPolicyRepository() throws Exception {
	filesystemPolicyStoreConfiguration
		.setPolicyStore(Paths.get(Thread.currentThread().getContextClassLoader().getResource("emptypool").toURI()));
	fSP.configure(filesystemPolicyStoreConfiguration);
	fSP.setRegexFilter("^\\w+\\.xml$");
	fSP.start();
	int expected = 0;
	int actual = fSP.getPolicies().size();
	Assert.assertEquals(expected, actual);
    }

    @Test
    public void whenFSPRunOnADirectoryItLoadAllThePoliciesStoredInside() throws Exception {
	fSP.configure(filesystemPolicyStoreConfiguration);
	fSP.setRegexFilter("^\\w+\\d[\\.\\w+]*\\.xml$");
	fSP.start();
	int expected = 3;
	int actual = fSP.getPolicies().size();
	Assert.assertEquals(expected, actual);
    }

    @Test
    public void asPolicyIsAddedToPolicyRepositoryFSPMustLoadItAsSoonAsPossible() throws Exception {
	String expectedPath = "policy1.xml";
	Path path = Mockito.mock(Path.class);
	Mockito.when(path.toString()).thenReturn(expectedPath);
	WatchEvent<Path> addFileEvent = Mockito.mock(WatchEvent.class);
	Mockito.when(addFileEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
	Mockito.when(addFileEvent.context()).thenReturn(path);
	List<WatchEvent<?>> events = new ArrayList<>();
	events.add(addFileEvent);
	WatchKey addFileKey = Mockito.mock(WatchKey.class);
	Mockito.when(addFileKey.pollEvents()).thenReturn(events);
	fSP.handleEvents(addFileKey);
	int expected = 1;
	int actual = fSP.getPolicies().size();
	Assert.assertEquals(expected, actual);
    }

    @Test
    public void asPolicyIsRemovedFromPolicyRepositoryFSPMustUnloadItAsSoonAsPossible() throws Exception {
	String expectedPath = "policy1.xml";
	Path path = Mockito.mock(Path.class);
	Mockito.when(path.toString()).thenReturn(expectedPath);
	WatchEvent<Path> addFileEvent = Mockito.mock(WatchEvent.class);
	Mockito.when(addFileEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
	Mockito.when(addFileEvent.context()).thenReturn(path);
	List<WatchEvent<?>> addFileEvents = new ArrayList<>();
	addFileEvents.add(addFileEvent);
	WatchKey addFileKey = Mockito.mock(WatchKey.class);
	Mockito.when(addFileKey.pollEvents()).thenReturn(addFileEvents);
	fSP.handleEvents(addFileKey);
	int expected = 1;
	int actual = fSP.getPolicies().size();
	Assert.assertEquals(expected, actual);

	WatchEvent<Path> removeFileEvent = Mockito.mock(WatchEvent.class);
	Mockito.when(removeFileEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_DELETE);
	Mockito.when(removeFileEvent.context()).thenReturn(path);
	List<WatchEvent<?>> removeFileEvents = new ArrayList<>();
	removeFileEvents.add(removeFileEvent);
	WatchKey removeFileKey = Mockito.mock(WatchKey.class);
	Mockito.when(removeFileKey.pollEvents()).thenReturn(removeFileEvents);
	fSP.handleEvents(removeFileKey);
	expected = 0;
	actual = fSP.getPolicies().size();
	Assert.assertEquals(expected, actual);
    }

    @Test
    public void asPolicyIsAddedToPolicyRepositoryFSPMustReplaceTheExistingOldOneWithTheNewOneAsSoonAsPossible() throws Exception {
	String expectedPath = "policy1.xml";
	Path path = Mockito.mock(Path.class);
	Mockito.when(path.toString()).thenReturn(expectedPath);
	WatchEvent<Path> addFileEvent = Mockito.mock(WatchEvent.class);
	Mockito.when(addFileEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
	Mockito.when(addFileEvent.context()).thenReturn(path);
	List<WatchEvent<?>> addFileEvents = new ArrayList<>();
	addFileEvents.add(addFileEvent);
	WatchKey addFileKey = Mockito.mock(WatchKey.class);
	Mockito.when(addFileKey.pollEvents()).thenReturn(addFileEvents);
	fSP.handleEvents(addFileKey);
	int expected = 1;
	int actual = fSP.getPolicies().size();
	Assert.assertEquals(expected, actual);

	WatchEvent<Path> modifyFileEvent = Mockito.mock(WatchEvent.class);
	Mockito.when(modifyFileEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_MODIFY);
	Mockito.when(modifyFileEvent.context()).thenReturn(path);
	List<WatchEvent<?>> removeFileEvents = new ArrayList<>();
	removeFileEvents.add(modifyFileEvent);
	WatchKey removeFileKey = Mockito.mock(WatchKey.class);
	Mockito.when(removeFileKey.pollEvents()).thenReturn(removeFileEvents);
	fSP.handleEvents(removeFileKey);
	expected = 1;
	actual = fSP.getPolicies().size();
	Assert.assertEquals(expected, actual);
    }

    @Test
    public void fSPMustRejectNonPolicyFilesAndCarryOn() throws Exception {
	String expectedPath = "policy.wrong.xml";
	Path path = Mockito.mock(Path.class);
	Mockito.when(path.toString()).thenReturn(expectedPath);
	WatchEvent<Path> addFileEvent = Mockito.mock(WatchEvent.class);
	Mockito.when(addFileEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
	Mockito.when(addFileEvent.context()).thenReturn(path);
	List<WatchEvent<?>> addFileEvents = new ArrayList<>();
	addFileEvents.add(addFileEvent);
	WatchKey addFileKey = Mockito.mock(WatchKey.class);
	Mockito.when(addFileKey.pollEvents()).thenReturn(addFileEvents);
	fSP.handleEvents(addFileKey);
	int expected = 0;
	int actual = fSP.getPolicies().size();
	Assert.assertEquals(expected, actual);
    }

    @Test
    public void fSPCanWorkCorrectlyWithoutSpecifyingAFilter() throws Exception {
	String regex = null;
	fSP.setRegexFilter(regex);
	Assert.assertNull(fSP.getRegexFilter());
    }

    @Test
    public void fSPIgnoresEventsAboutFilesWhoDoesNotMatchFilter() throws Exception {
	String regex = "^\\w+\\.balana\\.\\w+\\.xml$";
	fSP.setRegexFilter(regex);
	String expectedPath = "policy1.xml";
	Path path = Mockito.mock(Path.class);
	Mockito.when(path.toString()).thenReturn(expectedPath);
	WatchEvent<Path> addFileEvent = Mockito.mock(WatchEvent.class);
	Mockito.when(addFileEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_CREATE);
	Mockito.when(addFileEvent.context()).thenReturn(path);
	List<WatchEvent<?>> addFileEvents = new ArrayList<>();
	addFileEvents.add(addFileEvent);
	WatchKey addFileKey = Mockito.mock(WatchKey.class);
	Mockito.when(addFileKey.pollEvents()).thenReturn(addFileEvents);
	fSP.handleEvents(addFileKey);
	int expected = 0;
	int actual = fSP.getPolicies().size();
	Assert.assertEquals(expected, actual);

	WatchEvent<Path> modifyFileEvent = Mockito.mock(WatchEvent.class);
	Mockito.when(modifyFileEvent.kind()).thenReturn(StandardWatchEventKinds.ENTRY_MODIFY);
	Mockito.when(modifyFileEvent.context()).thenReturn(path);
	List<WatchEvent<?>> removeFileEvents = new ArrayList<>();
	removeFileEvents.add(modifyFileEvent);
	WatchKey removeFileKey = Mockito.mock(WatchKey.class);
	Mockito.when(removeFileKey.pollEvents()).thenReturn(removeFileEvents);
	fSP.handleEvents(removeFileKey);
	expected = 0;
	actual = fSP.getPolicies().size();
	Assert.assertEquals(expected, actual);
    }

    @Test
    public void fSPReturnsASTATUS_PROCESSING_ERRORValueWhenIsAskedToFindPolicyByNullIdReference() {
	URI idReference = null;
	int type = 0;
	VersionConstraints constraints = null;
	PolicyMetaData parentMetaData = null;
	PolicyFinderResult policiesFound = fSP.findPolicy(idReference, type, constraints, parentMetaData);
	Assert.assertNull(policiesFound.getPolicy());
	Status policyRequestStatus = policiesFound.getStatus();
	Assert.assertEquals(1, policyRequestStatus.getCode().size());
	Assert.assertEquals(Status.STATUS_PROCESSING_ERROR, policyRequestStatus.getCode().get(0));
    }

    @Test
    public void fSPReturnsASTATUS_PROCESSING_ERRORValueWhenIsAskedToFindPolicyByNonExistingIdReference() throws Exception {
	URI idReference = new URI("this:is:a:valid:uri");
	int type = 0;
	VersionConstraints constraints = null;
	PolicyMetaData parentMetaData = null;
	fSP = new FSystemPolicyFinder();
	filesystemPolicyStoreConfiguration
		.setPolicyStore(Paths.get(Thread.currentThread().getContextClassLoader().getResource("simplepolicy").toURI()));
	fSP.configure(filesystemPolicyStoreConfiguration);
	fSP.start();
	PolicyFinderResult policiesFound = fSP.findPolicy(idReference, type, constraints, parentMetaData);
	Assert.assertNull(policiesFound.getPolicy());
	Status policyRequestStatus = policiesFound.getStatus();
	Assert.assertEquals(1, policyRequestStatus.getCode().size());
	Assert.assertEquals(Status.STATUS_PROCESSING_ERROR, policyRequestStatus.getCode().get(0));
    }

    @Test
    public void fSPReturnsOnlyAPolicyWhenIsAskedToFindPolicyByExistingIdReference() throws Exception {
	URI expected = new URI("urn:oasis:names:tc:xacml:3.0:example:SimplePolicy1");
	int type = 0;
	VersionConstraints constraints = null;
	PolicyMetaData parentMetaData = null;
	fSP = new FSystemPolicyFinder();
	filesystemPolicyStoreConfiguration
		.setPolicyStore(Paths.get(Thread.currentThread().getContextClassLoader().getResource("simplepolicy").toURI()));
	fSP.configure(filesystemPolicyStoreConfiguration);
	fSP.start();
	PolicyFinderResult policiesFound = fSP.findPolicy(expected, type, constraints, parentMetaData);
	Assert.assertNotNull(policiesFound.getPolicy());
	Assert.assertEquals(expected, policiesFound.getPolicy().getId());
	Assert.assertNull(policiesFound.getStatus());
    }

    @Test
    public void fSPRaisesANullPontierExceptionWhenIsAskedToFindPoliciesByNullContext() {
	EvaluationCtx context = null;
	PolicyFinderResult policiesFound = fSP.findPolicy(context);
	Assert.assertNull(policiesFound.getPolicy());
	Status policyRequestStatus = policiesFound.getStatus();
	Assert.assertEquals(1, policyRequestStatus.getCode().size());
	Assert.assertEquals(Status.STATUS_PROCESSING_ERROR, policyRequestStatus.getCode().get(0));
    }

    @Test
    public void fSPReturnsAnEmptyResultSetWhenIsAskedToFindPolicyByEmptyContext() throws Exception {
	EvaluationCtx context = Mockito.mock(EvaluationCtx.class);
	EvaluationResult evaluationResult = new EvaluationResult(
		new Status(Arrays.asList(new String[] { Status.STATUS_MISSING_ATTRIBUTE })));
	Mockito.when(context.getAttribute(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(evaluationResult);

	fSP = new FSystemPolicyFinder();
	filesystemPolicyStoreConfiguration
		.setPolicyStore(Paths.get(Thread.currentThread().getContextClassLoader().getResource("simplepolicy").toURI()));
	fSP.configure(filesystemPolicyStoreConfiguration);
	fSP.start();
	PolicyFinderResult policiesFound = fSP.findPolicy(context);
	Assert.assertNull(policiesFound.getPolicy());
	Status policyRequestStatus = policiesFound.getStatus();
	Assert.assertEquals(1, policyRequestStatus.getCode().size());
	Assert.assertEquals(Status.STATUS_PROCESSING_ERROR, policyRequestStatus.getCode().get(0));
    }

    @Test
    public void fSPReturnsAnEmptyResultSetWhenIsAskedToFindPoliciesWhoDontMatchTheGivenContext() {
	Assert.fail();
    }

    @Test
    public void fSPReturnsPoliciesWhenTheyMatchGivenContext() {
	Assert.fail();
    }
}
