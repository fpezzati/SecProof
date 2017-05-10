package edu.pezzati.sec.token;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.apache.log4j.Logger;

import edu.pezzati.sec.model.Token;

@Singleton
public class TokenBlacklist {

    private Logger log = Logger.getLogger(getClass());
    private static List<Token> blacklist = null;
    private long period = Long.parseLong(System.getProperty("jwt.token.lifetime"));

    public TokenBlacklist() {
	blacklist = new ArrayList<>();
	ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(1);
	scheduledService.scheduleAtFixedRate(new Runnable() {
	    @Override
	    public void run() {
		cleanBlacklist();
	    }
	}, 0, period, TimeUnit.SECONDS);
    }

    public void put(Token token) {
	blacklist.add(token);
    }

    public void remove(Token token) {
	blacklist.remove(token);
    }

    public boolean isInBlacklist(Token token) {
	return blacklist.contains(token);
    }

    private void cleanBlacklist() {
	List<Token> toRemove = new ArrayList<>();
	blacklist.stream().filter(token -> token.getExpires().before(new Date())).forEach(token -> {
	    log.info("Token " + token.getJwtToken() + " will be removed.");
	    toRemove.add(token);
	});
	blacklist.removeAll(toRemove);
    }
}
