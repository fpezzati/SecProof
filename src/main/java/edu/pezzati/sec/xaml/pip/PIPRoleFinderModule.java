package edu.pezzati.sec.xaml.pip;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Node;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.AttributeFinderModule;

public class PIPRoleFinderModule extends AttributeFinderModule {

    private Set<String> supportedCategories;
    private Set<URI> supportedIds;

    public PIPRoleFinderModule() throws URISyntaxException {
	supportedCategories = new HashSet<>();
	supportedCategories.add("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
	supportedIds = new HashSet<>();
	supportedIds.add(new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id"));
    }

    @Override
    public Set<String> getSupportedCategories() {
	return supportedCategories;
    }

    @Override
    public Set<URI> getSupportedIds() {
	return supportedIds;
    }

    @Override
    public EvaluationResult findAttribute(URI attributeType, URI attributeId, String issuer, URI category, EvaluationCtx context) {
	try {
	    if (supportedIds.contains(attributeId) && supportedCategories.contains(category)) {
		EvaluationResult evaluationResult = new EvaluationResult(new Status(Arrays.asList(new String[] { Status.STATUS_OK })));
		EvaluationResult evl = context.getAttribute(attributeType, attributeId, issuer, category);
		return evaluationResult;
	    }
	    return new EvaluationResult(new Status(Arrays.asList(new String[] { Status.STATUS_MISSING_ATTRIBUTE })));
	} catch (Exception e) {
	    return new EvaluationResult(new Status(Arrays.asList(new String[] { Status.STATUS_PROCESSING_ERROR }), e.getMessage()));
	}
    }

    @Override
    public EvaluationResult findAttribute(String contextPath, URI attributeType, String contextSelector, Node root, EvaluationCtx context,
	    String xpathVersion) {
	// TODO Auto-generated method stub
	return super.findAttribute(contextPath, attributeType, contextSelector, root, context, xpathVersion);
    }

}
