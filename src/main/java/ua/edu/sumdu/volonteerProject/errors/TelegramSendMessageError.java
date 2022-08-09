package ua.edu.sumdu.volonteerProject.errors;

public class TelegramSendMessageError extends Throwable{
    public TelegramSendMessageError(String message) {
        super(message);
    }

    public TelegramSendMessageError(String message, Throwable cause) {
        super(message, cause);
    }
}
