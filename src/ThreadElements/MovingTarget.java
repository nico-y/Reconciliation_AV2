package ThreadElements;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

import ReconciliationUtils.Disturbance;
import Utils.Canvas;
import Utils.SpritePoint;
import java.awt.Rectangle;

/**
 * Classe de alvo movel, representa uma thread com as propriedades
   especificas do alvo.
   @author Nicolás Henríquez 
 */
public class MovingTarget implements Runnable {
    
    //#region Constantes

    /**
     * Velocidade máxima do alvo 
     */
    private static final int MAX_SPEED = 4;

    /**
     * Velocidade mínima do alvo 
     */
    private static final int MIN_SPEED = 1;

    /**
     * Caminho da imagem a ser utilizada 
     */
    private static final String ICON_PATH = "src/Resources/Target.png";
    

    //#endregion

    //#region Propriedades

    /**
     * Identificador do Objeto 
     */
    private UUID _ID = UUID.randomUUID();

    /**
     * Ponto de origem do alvo. 
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
     * Armazena se alvo chegou no destino. 
     */
    private boolean _DestinyArrival; 

    /**
     * Armazena se alvo foi atingido
     */
    private boolean _Hit; 

    /**
     * Cenário onde alvo será desenhado 
     */
    private Canvas _Scenary; 

    /**
     * Velocidade do alvo 
     */
    private int _Speed; 

    /**
     * Retangulo que vai definir margens do icone do alvo 
     */
    private Rectangle _IconBounds;

    //#endregion

    //#region Construtor

    public MovingTarget(){}

    public MovingTarget(int canvasSizeX, int canvasSizeY, boolean left_right){
        
        // Valor inicial em X 
        int initialX; 

        // Frequencia de atualização do alvo 
        this._UpdteFrequency = 30;
        this._OriginPoint = new SpritePoint(0, 0);
        this._DestinyPoint = new SpritePoint(0, canvasSizeY);
        
        // Caso alvo lado esquerdo 
        if (left_right) {
            initialX = (int)(canvasSizeX*0.1);
        } else {
            // Caso seja lado direito 
            initialX = (int)(canvasSizeX*0.9)-(this._OriginPoint.getSizeX());
        }

        // Atualizando posicao inicial X 
        this._OriginPoint.setX(initialX);
        this._DestinyPoint.setX(initialX);
        this._DestinyArrival = false;
        this._Hit = false;

        // Obtendo velocidade aleatoria entre 1 e 5, corresponde a quantidade de pixeis por ciclo 
        //this._Speed = ThreadLocalRandom.current().nextInt(MIN_SPEED, MAX_SPEED);
        this._Speed = 4;

        // Criando alvo com localização relacionada a icone 
        try {
            this._UpdatedLocation = new SpritePoint(this._OriginPoint, ICON_PATH);   
            
            // Criando margens do icone 
            this._IconBounds = new Rectangle();
            this._IconBounds.setBounds(this._OriginPoint.getX(), this._OriginPoint.getY(), 
                                       this._UpdatedLocation.getSizeX(), this._UpdatedLocation.getSizeY());

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Criando Thread e inicializando 
        Thread t = new Thread(this);
        t.start();
        this._TimeStamp = LocalTime.now();
                                       
    }

    //#endregion

    //#region Métodos Publicos 

    /**
     * @return Ponto de Origem do Alvo
     */
    public SpritePoint OriginPoint() {
        return this._OriginPoint;
    }

    /**
     * @return Ponto com posição atual do alvo. 
     */
    public SpritePoint getActualPosition(){
        return this._UpdatedLocation;
    }

    /**
     * @return Ponto de Destino do Alvo
     */
    public SpritePoint DestinyPoint() {
        return this._DestinyPoint;
    }

    /**
     * @return String com identifição do objeto. 
     */
    public String getID(){
        return this._ID.toString();
    }

    /**
     * Informa colisão com o alvo. 
     */
    public void setHit(){
        this._Hit = true;
    }

    /**
     * @return Momento de criação do alvo. 
     */
    public LocalTime getTimeStamp(){
        return this._TimeStamp;
    }

    /**
     * @return Frequencia de atualização da posição.
     */
    public long getUpdteFrequency(){
        return this._UpdteFrequency;
    }

    /**
     * Define valor de frequencia de atualização 
     * @param frequency valor desejado em ms
     */
    public void setUpdteFrequency(long frequency){
        this._UpdteFrequency = frequency;
    }

    /**
     * Adiciona alvo movel ao cenario para exibição gráfica. 
     * @param scenary
     */
    public void addToScennary(Canvas canvas){
        canvas.addGraphicElement(this._UpdatedLocation);
        this._Scenary = canvas;
    }

    public void testTargetMove(){
        this._UpdatedLocation.linearMoveTest(0, 0, 300, 250, 5, false);
    }

    /**
     * Remove elemento do cenário que foi anteriormente adicionado. 
     */
    public void removeFromScenary(){
        this._Scenary.removeGraphicElement(this._UpdatedLocation);
        this._Scenary = null;
    }

    /**
     * @return Velocidade do alvo em px/_UpdateFrequency
     */
    public int getSpeed() {
        return _Speed;
    }

    /**
     * Altera velocidade de deslocamento do alvo. 
     * @param _Speed Nova velocidade do alvo 
     */
    public void setSpeed(int _Speed) {
        this._Speed = _Speed;
    }

    /**
     * Margem de contato do icone
     * @return Retangulo com margem de contato do icone.
     */
    public Rectangle getIconBounds() {
        return _IconBounds;
    }

    @Override
    public void run() {

        // Auxiliar para percorrer array de posições de perturbação
        int indexAux = 0;
        SpritePoint lastPos = new SpritePoint(0,0);

        // Enquanto a posição do alvo for diferente da posição de destino 
        while ((this._UpdatedLocation.getY() <= DestinyPoint().getY()) && !this._Hit) {

            // Armazenando posição anterior
            lastPos.setPositions(this._UpdatedLocation);

            // Incrementando posição pela velocidade definida 
            this._UpdatedLocation.setY(this._UpdatedLocation.getY() + _Speed);
            this._IconBounds.x = this._UpdatedLocation.getX();
            this._IconBounds.y = this._UpdatedLocation.getY();
            
            // Conferindo se passou das possições de disturbio definidas
            if ( indexAux < Disturbance.positions.length && lastPos.getY() < Disturbance.positions[indexAux] && this._UpdatedLocation.getY() >= Disturbance.positions[indexAux]) {
                
                // Auxiliar de velociadade
                int auxSpeed;

                // Alterando valor da velocidade e conferindo validade
                do {
                    auxSpeed = this._Speed;
                    auxSpeed += Disturbance.getRandomDisturb();
                } while (auxSpeed <= 0);

                // Definindo nova velocidade do alvo
                this._Speed = auxSpeed;

                // Incrementando posição
                indexAux++;
            }

            try {
                Thread.sleep(this._UpdteFrequency);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Ao chegar no final o item deve ser removido do cenário
        this.removeFromScenary();

        // Dando hit ao Garbage Collector
        System.gc();
    }

    //#endregion

}
