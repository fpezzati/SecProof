package edu.pezzati.sec.xacml.balana;

import org.wso2.balana.ctx.ResponseCtx;

import edu.pezzati.sec.xacml.Response;

public class BalanaResponse implements Response {

    private ResponseCtx response;

    public ResponseCtx getResponse() {
	return response;
    }

    public void setResponse(ResponseCtx response) {
	this.response = response;
    }
}
