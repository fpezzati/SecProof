package edu.pezzati.sec.xacml;

public interface AuthorizationGateway {

    void init();

    void evaluate(Request request);

    Response getResponse();
}
