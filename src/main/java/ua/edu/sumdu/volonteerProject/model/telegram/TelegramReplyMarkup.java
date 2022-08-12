package ua.edu.sumdu.volonteerProject.model.telegram;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Data
public class TelegramReplyMarkup implements Serializable {
    private List<List<CallbackTelegramButtonEntity>> inline_keyboard;
}
