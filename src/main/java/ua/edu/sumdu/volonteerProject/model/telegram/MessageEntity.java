package ua.edu.sumdu.volonteerProject.model.telegram;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.sumdu.volonteerProject.model.telegram.CallbackTelegramButtonEntity;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
//conventions omitted because of json serialization
public class MessageEntity implements Serializable {
    private String text;
    private long chat_id;
    private TelegramReplyMarkup reply_markup;
}
