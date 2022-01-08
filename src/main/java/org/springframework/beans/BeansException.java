package org.springframework.beans;

/**
 * unchecked exception
 */
public class BeansException extends RuntimeException {

    public BeansException() {
        super ();
    }

    public BeansException(String msg) {
        super (msg);
    }

    public BeansException(String msg, Throwable cause) {
        super (msg, cause);
    }
}
