package ua.edu.sumdu.volonteerProject.model.telegram;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CallbackTelegramButtonEntity implements Serializable {
    private String text;
    private String callback_data;
}
