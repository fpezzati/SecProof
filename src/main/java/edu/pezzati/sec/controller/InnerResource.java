package edu.pezzati.sec.controller;

import edu.pezzati.sec.CanAccess;

public class InnerResource {

    @CanAccess
    public String getResourceName() {
	return "userA";
    }
}
