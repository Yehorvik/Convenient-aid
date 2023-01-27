package ua.edu.sumdu.volonteerProject.errors;

public class CityNotFoundException extends NullPointerException {
    public CityNotFoundException(String s) {
        super(s);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return super.fillInStackTrace();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
