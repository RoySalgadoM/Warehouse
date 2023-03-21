package mx.edu.utez.warehouse.message.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.warehouse.utils.MessageCatalog;

@Getter
@Setter
@NoArgsConstructor
public class MessageModel {
    private String uuid;
    private MessageCatalog message;
    private Object data;
    private Boolean isError;

    public MessageModel(MessageCatalog message, Object data, Boolean isError) {
        this.message = message;
        this.data = data;
        this.isError = isError;
    }

}