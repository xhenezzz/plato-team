package aidyn.kelbetov.exception;

public class EmailNotConfirmedException extends RuntimeException{
    public EmailNotConfirmedException(String msg){
        super(msg);
    }
}
