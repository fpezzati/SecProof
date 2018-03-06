package edu.pezzati.sec.util;

/**
 * Generic callback about FSystem events. This type is used by
 * {@link FSysPolicyRepo}.
 * 
 * @author pezzati
 */
public interface FSysCallback {

    void onSuccess();

    void onError(Throwable t);
}
