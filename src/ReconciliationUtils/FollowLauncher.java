package ReconciliationUtils;

import ThreadElements.MovingTarget;
import Utils.Canvas;
import Utils.SpritePoint;

public class FollowLauncher implements TargetListener {
    
    //#region Constantes 
    private static final int SHOT_VELOCITY = 5;
    private final String ICON_PATH = "src/Resources/Launcher.png";
    private final int ICON_SIZE = 75;
    private final int LAUNCHER_VERTICAL_OFFSET = 40; 

    //#endregion

    //#region Atributos

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
     * Frequencua de atualizacao de informacao da Thread 
     */
    private int _UpdateFrequency;

    /**
     * Canvas onde o lançador será desenhado. 
     */
    private Canvas _Canvas; 

    //#endregion

    //#region Construtor 

    public FollowLauncher(Canvas canvas){
        
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

        // Definindo posicao do lancador
        this.setX(canvas.getWidth()/2 - this._AnchorPoint.getSizeX()/2);
        this.setY(canvas.getWidth() - this._AnchorPoint.getSizeY() + LAUNCHER_VERTICAL_OFFSET);

        // Adicionando elemento grafico
        canvas.addGraphicElement(this._AnchorPoint);

        // Definindo cenário atual
        this._Canvas = canvas;

    }

    //#endregion 

    //#region Métodos

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
     * @return SpritePoint do launcher
     */
    public SpritePoint getAnchorPoint(){
        return this._AnchorPoint;
    }

    /**
     * @return Cenário onde lançador está 
     */
    public Canvas getCanvas() {
        return _Canvas;
    }

    @Override
    public void targetDetected(MovingTarget target) {
        //System.out.println("Alvo detectado...");
        // Inicializa novo tiro
        FollowShot shot =  new FollowShot(this, target);
        shot.start(); 
    }

    //#endregion
}
