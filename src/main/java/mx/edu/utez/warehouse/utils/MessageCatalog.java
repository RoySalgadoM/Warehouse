package mx.edu.utez.warehouse.utils;

public enum MessageCatalog {
    SUCCESS_REGISTER("Se ha registrado correctamente", "info"),
    SUCCESS_UPDATE("Se ha actualizado correctamente", "info"),
    SUCCESS_DISABLE("Se ha deshabilitado correctamente", "info"),
    SUCCESS_ENABLE("Se ha habilitado correctamente", "info"),
    SUCCESS_CANCEL("Se ha cancelado correctamente", "info"),
    SUCCESS_DELIVERED("Se ha recibido correctamente", "info"),
    SUCCESS_SENT("Se ha enviado correctamente", "info"),
    RECORDS_FOUND("Registros listados correctamente", "info"),
    NO_RECORDS_FOUND("No hay registros para mostrar", "warning"),
    UNK_ERROR_FOUND("Ha ocurrido un error inesperado, contacte a soporte. Quedate tranquilo ningún dato fue modificado :D. Código de operación: ", "error"),
    VALIDATION_ERROR("Uno o más valores de los campos es incorrecto", "error"),
    ERROR_CANCEL("No se pudo cancelar la entrada debido a que ya fue recibida", "error"),
    ERROR_DELIVERED("No se pudo marcar la entrada como recibida", "error"),
    ERROR_SENT("No se pudo marcar la salida como enviada", "error");

    private final String message;
    private final String typeOfMessage;
    MessageCatalog(String message, String typeOfMessage){
        this.message = message;
        this.typeOfMessage = typeOfMessage;
    }
    public String getMessage(){
        return message;
    }
    public String getTypeOfMessage(){
        return typeOfMessage;
    }
}
