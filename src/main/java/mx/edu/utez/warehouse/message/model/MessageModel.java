package mx.edu.utez.warehouse.message.model;

public class MessageModel {
    private String message;
    private String typeOfMessage;
    private Object data;
    private boolean isError;
    private String error;

    private String uuid;


    public MessageModel(String message, String typeOfMessage, Object data, boolean isError, String error) {
        this.message = message;
        this.typeOfMessage = typeOfMessage;
        this.data = data;
        this.isError = isError;
        this.error = error;
    }

    public MessageModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTypeOfMessage() {
        return typeOfMessage;
    }

    public void setTypeOfMessage(String typeOfMessage) {
        this.typeOfMessage = typeOfMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        this.isError = error;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "message='" + message + '\'' +
                ", typeOfMessage='" + typeOfMessage + '\'' +
                ", data=" + data +
                ", error=" + isError +
                '}';
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
