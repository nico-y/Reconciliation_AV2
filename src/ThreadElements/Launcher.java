package ThreadElements;

import java.security.InvalidParameterException;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Semaphore;
import static java.time.temporal.ChronoUnit.MILLIS;
import Utils.Canvas;
import Utils.Debug;
import Utils.SpritePoint;

/**
 * Classe do lannçador, gerenia munição, lança tiros e faz cálculos para estimação de posição 
 * do alvo e cálculo da trajetória do projétil. 
 * @author Nicolás Henríquez 
 */
public class Launcher extends Thread {
    
    //#region Constantes

    private static final int SHOT_VELOCITY = 5;
    private final String ICON_PATH = "src/Resources/Launcher.png";
    private final int ICON_SIZE = 75;
    private final int LAUNCHER_VERTICAL_OFFSET = 40; 

    //#endregion
    
    //#region Propriedades

    /**
     * Carregador com munições. 
     */
    private Stack<Ammo> _Charger = new Stack<Ammo>();

    /**
     * Representa posição do lançador em X 
     */
    private int _X; 

    /**
     * Representa posicao do lancador em Y 
     */
    private int _Y; 

    /**
     * Define o ponto onde o lancador sera posicionado e exibido
     */
    private SpritePoint _AnchorPoint;

    /**
     * Armazena o cenario onde o lancador deve ser exibido
     */
    private Canvas _Scenary;

    /**
     * Frequencua de atualizacao de informacao da Thread 
     */
    private int _UpdateFrequency;

    /**
     * Semaforo para processo de tiro
     */
    private Semaphore shootSemaphore = new Semaphore(1, true);

    /**
     * Armazena alvo a ser calculado para disparo no momenot de chamada 
     * do método de carregamento. 
     */
    private MovingTarget selectedTarget = null;

    /**
     * Alvo mais atual na lista de alvos. 
     */
    private MovingTarget nowTarget = null;

    /**
     * Armazena último alvo processado
     */
    private String lastTargetID = "";

    /**
     * Ouvintes das notificações de novos tiros.
     */
    private List<ShotsListener> _listeners = new ArrayList<ShotsListener>();

    /**
     * Conta tiros dados pelo lançador 
     */
    public int ShotsCounter = 0;
      
    //#endregion

    //#region Construtores

    public Launcher(){

        // Inicializando posicao do launcher em 0 
        this._X = 0; 
        this._Y = 0; 

        // Inicializando Ponto onde lancador sera exibido 
        this._AnchorPoint = new SpritePoint(this._X, this._Y, ICON_PATH);

        // Definindo frequencia de  atualizacao 
        this._UpdateFrequency = 30; 

        // Redefinindo tamanho do lancador na tela
        this._AnchorPoint.setSizeX(ICON_SIZE);
        this._AnchorPoint.setSizeY(ICON_SIZE);

        // Inicializando munição
        this.initializeCharger(500);

    }

    public Launcher(Canvas canvas, boolean addToScennary){
        this();
        this.addToScennary(canvas, addToScennary);

    }


    //#endregion

    //#region Métodos Públicos

    public void Load() throws InterruptedException{
        // - Deve verificar se existe munição disponivel no carregador, 
        // deve ser verificado até o carregamento acontecer.
        // - Deve se retirar munição do carregador

        // Inicializando timer do Load
        
        

        // Esperando que recursos do método sejam liberados
        this.shootSemaphore.acquire();
        Debug.print("Load chamado... " + this.getId());
        

        // Processamento de região critica:
        // Confere se carregamento deve ocorrer 
        do {
            // Inicializando cronometro 
            Instant loadStart = Instant.now();
            
            Debug.print("Try to Load...");
            // Região de processamento não critica
            // Recuperando alvo atual 
            this.selectedTarget = this.nowTarget;

            
            // Confere se existe munição se destino não é nulo e se é diferente do anterior
            if ((!this._Charger.isEmpty()) && (this.selectedTarget != null) && !this.selectedTarget.getID().equals(this.lastTargetID)){

                // Simulando Carregamento
                Thread.sleep(30);

                // Atualizando último alvo processado
                this.lastTargetID = this.selectedTarget.getID();

                // Finalizando timer do Load
                Instant loadEnd = Instant.now();

                // Registrando tempo
                Debug.registerLoadTime(loadStart, loadEnd);

                // Removendo munição e preparando trajetória 
                this.Prepare(this._Charger.pop());

            }
        } while (selectedTarget == null || this.selectedTarget.getID().equals(this.lastTargetID));
    }

    /**
     * Prepara trajetória do projetil
     * @param ammo projetil a ser lançado
     * @throws InterruptedException
     */
    public void Prepare(Ammo ammo) throws InterruptedException{

        // Realizar a mira, representado por um tempo de espera de 30ms 

        // Realizar Calculos para atingir alvo movel. Gerar trajetória, conjunto 
        // de coordenadas x, y que o Tiro deverá seguir 

        // Inicializando cronometro
        Instant prepareStart = Instant.now();

        // Simulando realização da mira 
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Conferindo que alvo selecionado continue não nulo 
        if (this.selectedTarget != null){

            // Estimando posição atual do alvo
            float deltaY = this.selectedTarget.getSpeed() * ((int)(this.selectedTarget.getTimeStamp().until(
                LocalTime.now(), MILLIS))/this._UpdateFrequency);

            // Instanciando posição atual do alvo
            SpritePoint targetPosition = new SpritePoint(this.selectedTarget.OriginPoint().getX(), deltaY);

            // Calculando trajetória do alvo e estimando futura posição
            SpritePoint estimatedPosition = this.estimateTargetPosition(targetPosition, this.selectedTarget.getSpeed());

            // Declarando mapa de coordenadas do tiro 
            ArrayList<SpritePoint> shotMap;

            // Conferindo se houve posição estimada possível
            if (estimatedPosition != null) {
                // Calculando trajetória a ser seguida pelo tiro 
                shotMap = getDDALineMap(estimatedPosition, SHOT_VELOCITY); 
            } else {
                shotMap = null;
            }

            // Terminando cronometro 
            Instant prepareEnd = Instant.now();

            // Registrando
            Debug.registerPrepareTime(prepareStart, prepareEnd);

            // Chamando método para atirar, passando trajetoria como paramtro 
            this.Shoot(shotMap);

        } else {
            // Liberando recursos
            Debug.print("Recursos Liberados " + this.getId());
            this.shootSemaphore.release();
        }
    }

    public void Shoot(ArrayList<SpritePoint> trajectory) throws InterruptedException{

        // Inicializando cronometro
        Instant shootStart = Instant.now();
        
        // Inicializando instancia de tiro
        Shots shot = new Shots(this, this.selectedTarget, this._listeners, trajectory);

        if (trajectory != null) {
        
            // Iniciando Thread do tiro
            shot.start();
            Debug.print("Atirando: " + this.getId());
            //shot.join();

            // Notificando tiro lançado 
            notifyNewShot(shot, this.selectedTarget);

            // Finalizando cronometro
            Instant shootEnd = Instant.now();

            // Registrando
            Debug.registerShootTime(shootStart, shootEnd);
        }

        Debug.print("Recursos Liberados " + this.getId());

        
        
        

        // Sinalizando liberação de recursos 
        this.shootSemaphore.release();

        
        
    }

    /**
     * Adiciona alvo movel ao cenario para exibição gráfica. 
     * @param scenary
     */
    public void addToScennary(Canvas canvas, boolean draw){
        
        // Definindo posicao do lancador
        this.setX(canvas.getWidth()/2 - this._AnchorPoint.getSizeX()/2);
        this.setY(canvas.getWidth() - this._AnchorPoint.getSizeY() + LAUNCHER_VERTICAL_OFFSET);

        // Adicionando elemento grafico
        if (draw) {
            canvas.addGraphicElement(this._AnchorPoint);
        }

        // Definindo cenário atual
        this._Scenary = canvas;

    }

    /**
     * Remove elemento do cenário que foi anteriormente adicionado. 
     */
    public void removeFromScenary(){
        this._Scenary.removeGraphicElement(this._AnchorPoint);
        this._Scenary = null;
    }

    /**
     * Adiciona ouvinte na coleção para notificar ação do tiro.
     * @param listener ouvinte a ser adicionado.
     */
    public void addShotListener(ShotsListener listener){
        this._listeners.add(listener);
    }

    public void notifyNewShot(Shots shot, MovingTarget target){

        // Notificando os interessados
        for (ShotsListener listener : this._listeners) {
            listener.shotLaunched(this);
        }
    }

    public void setX(int X) {
        // Armazenando Posicao 
        this._X = X;

        // Atualizando posicao no icone 
        this._AnchorPoint.setX(X);
    }

    public void setY(int Y) {
        // Armazenando Posicao 
        this._Y = Y;

        // Atualizando posicao no icone 
        this._AnchorPoint.setY(Y);
    }

    /**
     * @return Posição em X
     */
    public int getX() {
        return _X;
    }

    /**
     * @return Posição em Y
     */
    public int getY() {
        return _Y;
    }

    /**
     * Retira munição do carregador. 
     */
    public Ammo popAmmo(){
        return this._Charger.pop();
    }

    public String getLastTargetID() {
        return lastTargetID;
    }

    /**
     * @return SpritePoint do launcher
     */
    public SpritePoint getAnchorPoint(){
        return this._AnchorPoint;
    }

    /**
     * @return Cenário onde lançador está 
     */
    public Canvas getScenary() {
        return _Scenary;
    }

    /**
     * Define alvo atual a ser atingido pelo 
     * @param target
     */
    public void setNowTarget(MovingTarget target) {
        this.nowTarget = target;
    }

    public boolean hasSelectedTarget(){
        return this.selectedTarget != null;
    }


    /**
     * Acao a ser executada ao criar uma instancia de lancador 
     */
    @Override
    public void run() {
        
        try {
            while (true) {
                Thread.sleep(this._UpdateFrequency);
                
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //#endregion

    //#region Metodos Privados

    /**
     * Utiliza algoritmo de Bresenham para calcular pontos por onde o tiro deve passar. 
     * @param start Ponto inicial do tiro 
     * @param end Ponto final do tiro
     * @return Mapa com pontos a serem segudios pelo tiro 
     */
    private static ArrayList<SpritePoint> getBresenhamMap(SpritePoint start, SpritePoint end){
        
        // Declarando variáveis 
        float x, y; // Posição atual 
        float xf; // Representa posição final em x 
        ArrayList<SpritePoint> trajectory = new ArrayList<SpritePoint>();

        // Variação de posições 
        float dx = Math.abs(end.getX() - start.getX());
        float dy = Math.abs((end.getY()) - (start.getY()));

        // Definindo variáveis de decisão
        float p = 2*dy - dx; 
        float p2 = 2*dy;
        float xy2 = 2*(dy-dx); 

        if (start.getX() > end.getX()) {
            x = end.getX();
            y = end.getY();
            xf = start.getX();
        } else {
            x = start.getX();
            y = start.getY();
            xf = end.getX();
        }

        // Adicionando primeira coordenada na tela 
        trajectory.add(new SpritePoint(x, y));

        // Analisando variáveis de decisão até chegar no ponto final 
        while (x < xf) {
            x = x + 1; 
            if (p < 0) {
                p += p2;
            } else {
                y = y + 1;
                p += xy2;
            }
            // Adicionando Pixel no Mapa
            trajectory.add(new SpritePoint(x, y));
        }

        // Retornando mapa com trajetoria a ser segudia
        return trajectory;
    }

    private ArrayList<SpritePoint> getDDALineMap(SpritePoint end, int velVector){

        // Conferindo que angulação não passe de 180 graus 
        if (end.getY() > this._AnchorPoint.getY()) {
            throw new InvalidParameterException("O lançador não pode girar em +180 graus...");
        }

        // Definindo start como posição do launcher 
        SpritePoint start = this._AnchorPoint;

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
        trajectory.add(new SpritePoint((int)(x), (int)(y)));

        // No caso de variação de espaço negativa
        if (dx < 0) {
            while ((x >= end.getX() && y >= end.getY())) {
                // Somando componentes a posição inicial
                x += Vx; 
                y += Vy;
                // Adicionando ponto ao mapa 
                trajectory.add(new SpritePoint((int)(x), (int)(y)));
            }
        } else {
            while (x <= end.getX() && y >= end.getY()) {
                // Somando componentes a posição inicial
                x += Vx; 
                y += Vy;
                // Adicionando ponto ao mapa 
                trajectory.add(new SpritePoint((int)(x), (int)(y)));
            }
        }

        return trajectory;
    }

    private void initializeCharger(int ammoAmnt){
        
        // Adicionando munição ao carregador repetidamente 
        for (int i = 0; i < ammoAmnt; i++) {
            // Adicionando munição 
            this._Charger.add(new Ammo());
        }
    }

    /**
     * Estima melhor posição de interseição a partir da posição atual estimada do mesmo
     * @param targetPosition posição atual do alvo
     * @param targetSpeed velocidade do alvo
     * @return Melhor posição de tiro estimada
     */
    private SpritePoint estimateTargetPosition(SpritePoint targetPosition, float targetSpeed){
        
        // Inicializando Variáveis
        int interactions = 0;
        int angle = 0; // Começa teste em 0 graus
        int angleVariation = 2; // Razão da angulação de pontos 
        SpritePoint estimatedPoint = new SpritePoint(targetPosition.getX(), 0);
        double targetTime; // Tempo estimado de chegada do alvo 
        double shotTime; // Tempo estimado de chegada do tiro 

        do {

            // Calculando posição Y de interseição 
            int Y; 
            Y = (int)(Math.ceil((Math.tan(Math.toRadians(angle)) * Math.abs(targetPosition.getX() - this._X))));
            estimatedPoint.setY(this._Scenary.getWindowH() - LAUNCHER_VERTICAL_OFFSET - Y);

            // Tempo de chegada do alvo 
            targetTime = Math.abs(estimatedPoint.getY() - targetPosition.getY())/((double)((1000/(double)(this._UpdateFrequency)) * (double)(targetSpeed)));

            // Tempo de chegada do tiro
            shotTime = Math.abs(euclidianDistance(this._AnchorPoint, estimatedPoint))/((double)((1000/(double)(this._UpdateFrequency)) * (double)(SHOT_VELOCITY)));

            // Incrementando angulo para seguinte iteração 
            angle += angleVariation;

            // Incrementando número de iterações contadas
            interactions++;
            
        // Repetindo laço enquanto o tempo do tiro for menor ao tempo do alvo chegar e o ponto estimado estiver após a posição original do alvo
        } while (shotTime <= targetTime && (estimatedPoint.getY() > targetPosition.getY()));

        // Conferindo validade da solução
        if (interactions == 1 && shotTime > targetTime) {
            estimatedPoint = null;
        }

        estimatedPoint.setY(estimatedPoint.getY() + 10);

        return estimatedPoint;
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

    //#endregion

}
