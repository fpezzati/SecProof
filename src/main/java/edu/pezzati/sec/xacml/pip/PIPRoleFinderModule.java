package edu.pezzati.sec.xacml.pip;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.AttributeFinderModule;

public class PIPRoleFinderModule extends AttributeFinderModule {

    private Set<String> supportedCategories;
    private Set<String> supportedIds;

    public PIPRoleFinderModule() {
	supportedCategories = new HashSet<>();
	supportedCategories.add("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
	supportedIds = new HashSet<>();
	supportedIds.add("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
    }

    @Override
    public Set<String> getSupportedCategories() {
	return supportedCategories;
    }

    @Override
    public Set<String> getSupportedIds() {
	return supportedIds;
    }

    @Override
    public EvaluationResult findAttribute(URI attributeType, URI attributeId, String issuer, URI category, EvaluationCtx context) {
	try {
	    if (supportedIds.contains(attributeId.toString()) && supportedCategories.contains(category.toString())) {
		EvaluationResult retreivedAttirbute = context.getAttribute(new URI("urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name"),
			new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id"), null,
			new URI("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"));
		AttributeValue retreivedAttributeValue = (AttributeValue) ((BagAttribute) retreivedAttirbute.getAttributeValue()).iterator()
			.next();
		BagAttribute bagPermission = new BagAttribute(new URI("http://www.w3.org/2001/XMLSchema#string"),
			retreiveUserPermissions(retreivedAttributeValue.encode()));
		return new EvaluationResult(bagPermission);
	    }
	    return new EvaluationResult(new Status(Arrays.asList(new String[] { Status.STATUS_MISSING_ATTRIBUTE })));
	} catch (Exception e) {
	    return new EvaluationResult(new Status(Arrays.asList(new String[] { Status.STATUS_PROCESSING_ERROR }), e.getMessage()));
	}
    }

    private List<AttributeValue> retreiveUserPermissions(String username) {
	List<AttributeValue> permission = new ArrayList<>();
	/**
	 * Roles are embedded in this case. It is only a proof of concept.
	 */
	switch (username) {
	case "joe@med.example.com":
	    permission.add(new StringAttribute("create"));
	    permission.add(new StringAttribute("read"));
	    permission.add(new StringAttribute("update"));
	    permission.add(new StringAttribute("delete"));
	    break;
	default:
	    permission.add(new StringAttribute("read"));
	    break;
	}
	return permission;
    }

    @Override
    public EvaluationResult findAttribute(String contextPath, URI attributeType, String contextSelector, Node root, EvaluationCtx context,
	    String xpathVersion) {
	// TODO Auto-generated method stub
	return super.findAttribute(contextPath, attributeType, contextSelector, root, context, xpathVersion);
    }

    @Override
    public boolean isSelectorSupported() {
	return false;
    }

    @Override
    public boolean isDesignatorSupported() {
	return true;
    }
}
