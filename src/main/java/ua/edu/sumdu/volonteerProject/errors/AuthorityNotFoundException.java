package ua.edu.sumdu.volonteerProject.errors;

public class AuthorityNotFoundException extends Throwable {
    public AuthorityNotFoundException() {
        super();
    }

    public AuthorityNotFoundException(String message) {
        super(message);
    }
}
