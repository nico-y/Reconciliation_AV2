package ThreadElements;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.awt.Rectangle;
import Utils.Canvas;
import Utils.Debug;
import Utils.SpritePoint;

/**
 * Classe para criação de Thread de tiros. 
 */
public class Shots extends Thread{


    //#region Constantes

    private static final String ICON_PATH = "src/Resources/Shot.png";
    private static final int ICON_SIZE = 20;

    //#endregion

    //#region Propriedades

    /**
     * Identificador do Objeto 
     */
    private UUID _ID;

    /**
     * Munição disponível ??
     */
    private Ammo _Ammo;

    /**
     * Ponto de origem do tiro. 
     */
    private SpritePoint _OriginPoint;

    /**
     * Posição de destino.
     */
    private SpritePoint _DestinyPoint; 

    /**
     * Posição Atualizada 
     */
    private SpritePoint _UpdatedLocation; 

    /**
     * Momento de lançamento. 
     */
    private LocalTime _TimeStamp; 

    /**
     * Frequência de atualização da posição 
     */
    private long _UpdteFrequency; 

    /**
     * Armazena se alvo foi atingido
     */
    private boolean _Hit; 

    /**
     * Armazena caminho a ser seguido pelo projetil
     */
    private ArrayList<SpritePoint> _TrajectoryPoints;

    /**
     * Cenario onde elemento será exibido
     */
    private Canvas _Scenary;

    /**
     * Define margens do icone do tiro. 
     */
    private Rectangle _IconBounds;

    /**
     * Alvo a ser eliminado pelo tiro
     */
    private MovingTarget _target; 

    /**
     * A receber notificação de tiro dado
     */
    private List<ShotsListener> _listeners = new ArrayList<ShotsListener>();

    /**
     * Launcher que lançou o tiro. 
     */
    private Launcher launcher;


    //#endregion

    //#region Construtor

    public Shots(){}

    public Shots(Launcher launcher, MovingTarget target, ArrayList<SpritePoint> trajectory){
        
        // Valores Iniciais
        this._UpdteFrequency = 30;
        this._Hit = false;
        this._TimeStamp = LocalTime.now();
        this._ID = UUID.randomUUID();
        //this._Ammo = launcher.popAmmo();
        this._TrajectoryPoints = trajectory;
        this._DestinyPoint = trajectory.get(trajectory.size()-1);
        this._target = target;
        this.launcher = launcher;

        // Inicializando ponto de origem 
        this._OriginPoint = new SpritePoint(launcher.getAnchorPoint());

        // Iniciando localização atualizada 
        try {
            // Inicializando icone a ser exibido 
            this._UpdatedLocation = new SpritePoint(this._OriginPoint, ICON_PATH);

            // Criando margens do icone 
            this._IconBounds = new Rectangle();
            this._IconBounds.setBounds((int)(this._OriginPoint.getX()), (int)(this._OriginPoint.getY()), 
                                       this._UpdatedLocation.getSizeX(), this._UpdatedLocation.getSizeY());

            // Definindo tamanho 
            this._UpdatedLocation.setSizeX(ICON_SIZE);
            this._UpdatedLocation.setSizeY(ICON_SIZE);
            
        } catch (IOException e) {
            e.printStackTrace();
        }    

        // Adicionando no cenario
        this.addToScennary(launcher.getScenary());

    }

    public Shots (Launcher launcher, MovingTarget target, List<ShotsListener> listeners, ArrayList<SpritePoint> trajectory){
        this(launcher, target, trajectory);
        this._listeners = listeners;
    }

    //#endregion

    //#region 

    // Métodos Públicos
    @Override
    public void run() {

        //notifyNewShot(this, this._target);

        // Exibindo tiro dado
        Debug.print("Tiro Dado...");

        // Seguir Trajetória Recebida
        for (SpritePoint spritePoint : _TrajectoryPoints) {
            if (!this._Hit) {
                this._UpdatedLocation.setPositions(spritePoint);
                this._IconBounds.x = (int)(this._UpdatedLocation.getX());
                this._IconBounds.y = (int)(this._UpdatedLocation.getY());
                try {
                    Thread.sleep(this._UpdteFrequency);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } 
            } else {
                break;
            }
        }

        // Notificando tiro finalizado
        notifyEndShot(this, this._target);

        // Removendo elemento do cenario
        this.removeFromScenary();

        // -->
        Debug.print("Fim do Tiro --> ");

        // Dando hint ao Garbage Collector 
        System.gc();
        
    }
    
    public static int getIconSize(){
        return ICON_SIZE;
    }

    public Rectangle getIconBounds() {
        return _IconBounds;
    }

    /**
     * Informa colisão do tiro.
     */
    public void setHit(){
        this._Hit = true; 
    }

    //#endregion 

    //#region Metodos Privados

    /**
     * Adiciona alvo movel ao cenario para exibição gráfica. 
     * @param scenary
     */
    private void addToScennary(Canvas canvas){
        // Adicionando elemento grafico
        canvas.addGraphicElement(this._UpdatedLocation);
        this._Scenary = canvas;
    }

    /**
     * Remove elemento do cenário que foi anteriormente adicionado. 
     */
    private void removeFromScenary(){
        this._Scenary.removeGraphicElement(this._UpdatedLocation);
        this._Scenary = null;
    }

    /**
     * Notifcando ouvintes evento de tiro finalizado. 
     */
    private void notifyEndShot(Shots shot, MovingTarget target){
        
        // Notificando os interessados
        for (ShotsListener listener : this._listeners) {
            listener.shotFinished(shot, target);
        }
    }

    //#endregion
}