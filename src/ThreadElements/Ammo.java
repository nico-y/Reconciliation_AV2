package ThreadElements;

import java.util.UUID;

/**
 * Classe de munição 
 *  @author Nicolás Henríquez 
 */
public class Ammo {
    
    // Propriedades
    private UUID _ID;
    private boolean _Used;

    // Constutor
    public Ammo(){

        // Definindo atributos 
        this._ID = UUID.randomUUID();
        this._Used = false; 
    }

    // Métodos Públicos 
    
    /**
     * @return Identificador da munição
     */
    public String getID(){
        return this._ID.toString();
    }

    /**
     * @return Indicador de utilização da munição
     */
    public boolean IsUsed(){
        return this._Used;
    }

}
