package ua.edu.sumdu.volonteerProject.errors;

public class WrongAmountException extends IllegalArgumentException{
    public WrongAmountException(String s) {
        super(s);
    }

    public WrongAmountException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongAmountException(Throwable cause) {
        super(cause);
    }
}
