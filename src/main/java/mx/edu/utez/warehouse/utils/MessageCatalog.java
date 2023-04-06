package mx.edu.utez.warehouse.utils;

public enum MessageCatalog {
    SUCCESS_REGISTER("Se ha registrado correctamente", "info"),
    SUCCESS_UPDATE("Se ha actualizado correctamente", "info"),
    SUCCESS_DISABLE("Se ha deshabilitado correctamente", "info"),
    RECORDS_FOUND("Registros listados correctamente", "info"),
    NO_RECORDS_FOUND("No hay registros para mostrar", "warning"),
    UNK_ERROR_FOUND("Ha ocurrido un error inesperado, contacte a soporte", "error"),
    VALIDATION_ERROR("Uno o más valores de los campos es incorrecto", "error");

    private String message;
    private String typeOfMessage;
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
