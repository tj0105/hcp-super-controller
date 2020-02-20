package org.onosproject.hcp.exceptions;

public class HCPParseError extends Exception {
    private static final long serialVersionUID=1L;

    public HCPParseError(){
        super();
    }
    public HCPParseError(final String message,final Throwable cause){
        super(message,cause);
    }

    public HCPParseError(final String message){
        super(message);
    }
    public HCPParseError(final Throwable cause){
        super(cause);
    }
}