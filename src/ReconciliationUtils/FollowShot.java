package ReconciliationUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import ThreadElements.MovingTarget;
import ThreadElements.ShotsListener;
import Utils.Canvas;
import Utils.SpritePoint;
import java.awt.Rectangle;
import java.io.IOException;
import java.lang.Math;

public class FollowShot extends Thread{
    
    //#region Constantes

    private static final String ICON_PATH = "src/Resources/Shot.png";
    private static final int ICON_SIZE = 20;

    //#endregion

    //#region Atributos

    /**
     * Ponto de origem do tiro. 
     */
    private SpritePoint originPoint;

    /**
     * A receber notificação de tiro dado
     */
    private List<ShotsListener> listeners = new ArrayList<ShotsListener>();
    
    /**
     * Armazena se alvo foi atingido
     */
    private boolean hit = false; 

    /**
     * Cenario onde elemento será exibido
     */
    private Canvas canvas;

    /**
     * Alvo a ser eliminado pelo tiro, representa o ponto de destino. 
     */
    private MovingTarget target; 

    /**
     * Launcher que lançou o tiro. 
     */
    private FollowLauncher followLauncher;

    /**
     * Momento de lançamento. 
     */
    private LocalTime timeStamp;

    /**
     * Frequência de atualização da posição 
     */
    private long updteFrequency; 

    /**
     * Posição Atualizada 
     */
    private SpritePoint _UpdatedLocation;

    /**
     * Define margens do icone do tiro. 
     */
    private Rectangle _IconBounds;

    /**
     * Define distância limite de verificações de distância. 
     */
    private int distanceThreshold = 15;

    //#endregion

    //#region Coonstrutores

    public FollowShot(FollowLauncher followLauncher, MovingTarget target){

        // Valores Iniciais 
        this.updteFrequency = 30;
        this.hit = false;
        this.target = target;
        this.followLauncher = followLauncher;

        // Inicializando ponto de origem 
        this.originPoint = new SpritePoint(followLauncher.getAnchorPoint());

        // Iniciando localização atualizada 
        try {
            // Inicializando icone a ser exibido 
            this._UpdatedLocation = new SpritePoint(this.originPoint, ICON_PATH);

            // Criando margens do icone 
            this._IconBounds = new Rectangle();
            this._IconBounds.setBounds((int)(this.originPoint.getX()), (int)(this.originPoint.getY()), 
                                       this._UpdatedLocation.getSizeX(), this._UpdatedLocation.getSizeY());

            // Definindo tamanho 
            this._UpdatedLocation.setSizeX(ICON_SIZE);
            this._UpdatedLocation.setSizeY(ICON_SIZE);
            
        } catch (IOException e) {
            e.printStackTrace();
        }   

        // Adicionando no cenario
        this.addToScennary(this.followLauncher.getCanvas());

    }

    //#endregion

    //#region Métodos

    @Override
    public void run(){

        //this._UpdatedLocation.printPosition();
        //double distance = euclidianDistance(this._UpdatedLocation, this.target.getActualPosition());

        // Definindo tempo de inicio do tiro
        this.timeStamp = LocalTime.now();

        while ( target != null && !this.hit && !this.target.hasArrived()) {
            
            // Conferindo distância do alvo
            if(euclidianDistance(this._UpdatedLocation, this.target.getActualPosition()) > distanceThreshold)
            {

                // Calcular trajetória até o alvo
                ArrayList<SpritePoint> trajectory =  getDDALineMap(this._UpdatedLocation, target.getActualPosition(), this.target.getSpeed()*2f);

                // Percorrer pontos por 30 ms
                Instant startTime = Instant.now();
                Instant endTime = startTime; 
                    
                for (SpritePoint point : trajectory) {
                    // Atualizando Posição 
                    this._UpdatedLocation.setPositions(point);
                    this._IconBounds.x = (int)(this._UpdatedLocation.getX());
                    this._IconBounds.y = (int)(this._UpdatedLocation.getY());

                    // Marcando fim de operação
                    endTime = Instant.now();

                    // Conferindo se tempo máximo de operação já transcorreu
                    if (Duration.between(startTime, endTime).toMillis() >= 90) {
                        // Finalizar laço
                        break;
                    }

                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else{
                this.hit = true;
                this.target.setHit();
            }
        }

        //System.out.println("Tiro finalizado");

        // Removendo elemento do cenario
        this.removeFromScenary();

        // Dando hint ao Garbage Collector 
        System.gc();

    }

    /**
     * Adiciona alvo movel ao cenario para exibição gráfica. 
     * @param canvas
     */
    private void addToScennary(Canvas canvas){
        // Adicionando elemento grafico
        canvas.addGraphicElement(this._UpdatedLocation);
        this.canvas = canvas;
    }

    /**
     * Remove elemento do cenário que foi anteriormente adicionado. 
     */
    private void removeFromScenary(){
        this.canvas.removeGraphicElement(this._UpdatedLocation);
        this.canvas = null;
    }

    /**
     * @param start Posição inicial 
     * @param end Posição final 
     * @return Distância euclidiana entre pontos
     */
    private static double euclidianDistance(SpritePoint start, SpritePoint end){ 
        
        // Definindo variáveis
        double x1 = start.getX();
        double y1 = start.getY();
        double x2 = end.getX();
        double y2 = end.getY();
        double res = Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
        
        return res;
    }

    private ArrayList<SpritePoint> getDDALineMap(SpritePoint start, SpritePoint end, float velVector){

        // Declarando variáveis
        double Vx, Vy, dx, dy;
        double theta; 
        double x, y;

        // Criando lista de coordenadas
        ArrayList<SpritePoint> trajectory = new ArrayList<SpritePoint>();

        // Definindo pontos iniciais 
        x = start.getX();
        y = start.getY();

        // Variação no deslocamento
        dx = (float)(end.getX() - start.getX());
        dy = (float)(end.getY() - start.getY());

        // Calculando ângulo de inclinação
        theta = Math.atan2(dy, dx);

        // Decompondo velocidades
        Vx = Math.cos(theta)*velVector;
        Vy = Math.sin(theta)*velVector;

        // Adicionando Ponto inicial 
        trajectory.add(new SpritePoint((float)x, (float)y));

        // Tiro Simulado 
        SpritePoint auxShot = new SpritePoint(start);

        // Adicionando pontos enquanto for necessário 
        while (euclidianDistance(auxShot, end) > distanceThreshold) {
            x += Vx; 
            y += Vy;
            auxShot.setX((float)(x));
            auxShot.setY((float)(y));
            // Adicionando ponto ao mapa 
            trajectory.add(new SpritePoint(auxShot));
        }

        // Caso elemento muito próximo ao alvo
        if (trajectory.isEmpty() && (euclidianDistance(auxShot, end) < distanceThreshold)) {
            trajectory.add(end);
        }

        return trajectory;
    }

    //#endregion

}
