package ThreadElements;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

import ReconciliationUtils.Disturbance;
import static java.time.temporal.ChronoUnit.MILLIS;
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
    private float _Speed; 

    /**
     * Retangulo que vai definir margens do icone do alvo 
     */
    private Rectangle _IconBounds;

    /**
     * Objetivo de tempo de chegada a ser atingido pelo alvo. 
     */
    private long _TimeObjective;

    /**
     * Tamanho vertical do canvas. 
     */
    private int _CanvasHeight;

    /**
     * Indica se alvo deve sofrer disturbio ou não
     */
    private boolean _DisturbTarget = false;

    //#endregion

    //#region Construtor

    public MovingTarget(){}

    public MovingTarget(int canvasSizeX, int canvasSizeY, boolean left_right){
        
        // Valor inicial em X 
        int initialX; 

        // Frequencia de atualização do alvo 
        this._CanvasHeight = canvasSizeY;
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

        // Criando alvo com localização relacionada a icone 
        try {
            this._UpdatedLocation = new SpritePoint(this._OriginPoint, ICON_PATH);   
            
            // Criando margens do icone 
            this._IconBounds = new Rectangle();
            this._IconBounds.setBounds((int)(this._OriginPoint.getX()), (int)(this._OriginPoint.getY()), 
                                       this._UpdatedLocation.getSizeX(), this._UpdatedLocation.getSizeY());

        } catch (IOException e) {
            e.printStackTrace();
        }                              
    }

    public MovingTarget(int canvasSizeX, int canvasSizeY, boolean left_right, long timeObjective){
        this(canvasSizeX, canvasSizeY, left_right);

        // Calculando velocidade inicial
        this._TimeObjective = timeObjective;
        float velocity = estimateSpeed(timeObjective);
        this._Speed = velocity;
        //System.out.println(velocity);

        // Criando e Iniciando Thread 
        Thread t = new Thread(this); 
        t.start();
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
    public float getSpeed() {
        return _Speed;
    }

    /**
     * Altera velocidade de deslocamento do alvo. 
     * @param _Speed Nova velocidade do alvo 
     */
    public void setSpeed(float _Speed) {
        this._Speed = _Speed;
    }

    /**
     * Margem de contato do icone
     * @return Retangulo com margem de contato do icone.
     */
    public Rectangle getIconBounds() {
        return _IconBounds;
    }

    /**
     * Informa posição de objetivo do alvo. 
     */
    public SpritePoint getDestinyPoint() {
        return _DestinyPoint;
    }

    @Override
    public void run() {

        // Registrando momento de inicio do tiro
        this._TimeStamp = LocalTime.now();

        // Auxiliar para percorrer array de posições de perturbação
        int indexAux = 0;
        SpritePoint lastPos = new SpritePoint(0,0);

        // Enquanto a posição do alvo for diferente da posição de destino 
        while ((this._UpdatedLocation.getY() <= DestinyPoint().getY()) && !this._Hit) {

            // Armazenando posição anterior
            lastPos.setPositions(this._UpdatedLocation);

            // Incrementando posição pela velocidade definida 
            this._UpdatedLocation.setY(this._UpdatedLocation.getY() + _Speed);
            this._IconBounds.x = (int)(this._UpdatedLocation.getX());
            this._IconBounds.y = (int)(this._UpdatedLocation.getY());
            
            // Conferindo se passou das possições de disturbio definidas
            if ( indexAux < Disturbance.positions.length && lastPos.getY() < Disturbance.positions[indexAux] && this._UpdatedLocation.getY() >= Disturbance.positions[indexAux]) {
                
                // Auxiliar de velociadade
                float auxSpeed = this._Speed;

                if (this._DisturbTarget)
                {
                    // Alterando valor da velocidade e conferindo validade
                    do {
                    auxSpeed = this._Speed;
                    // Variando velocidade do alvo
                    auxSpeed += Disturbance.getRandomDisturb();
                    } while (auxSpeed <= 0);
                }

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

        // Exibindo tempo total de transcurso do alvo
        System.out.println(this.getElapsedTime());

        // Ao chegar no final o item deve ser removido do cenário
        this.removeFromScenary();

        // Dando hit ao Garbage Collector
        System.gc();
    }

    /**
     * Estima velocidade a partir de tempo Objetivo desejado
     * @param timeObjective tempo desejado
     * @return velociade necessária
     */
    private float estimateSpeed(long timeObjective){
        return ((float)(this._CanvasHeight))/(((float)(timeObjective))/((float)(this._UpdteFrequency + 0.95)));
    } 

    /**
     * Calcula tempo restante do ponto atual até o a posição objetivo do alvo. Considerando a 
     * velociadade instantanea atual. 
     * @return tempo restante até posição objetivo.
     */
    private long estimateTime(){
        float time =  ((this._DestinyPoint.getY() - this._UpdatedLocation.getY())* ((float)(this._UpdteFrequency + 0.95)))/this._Speed;
        if (time < 0) time = 0;
        return (long)(time);
    }

    private long getElapsedTime(){
        return this.getTimeStamp().until(LocalTime.now(), MILLIS);
    }

    //#endregion
}