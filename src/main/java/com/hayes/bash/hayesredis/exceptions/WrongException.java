package com.hayes.bash.hayesredis.exceptions;

public class WrongException extends Exception {

    private static final long serialVersionUID = 1L;

    public WrongException() {
        super();
    }

    public WrongException(String message) {
        super(message);
    }

    public WrongException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongException(Throwable cause) {
        super(cause);
    }
}
