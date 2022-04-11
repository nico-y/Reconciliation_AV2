package Utils;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * Classe rapaz de fazer um wrapper para exibição de elementos gráficos 
 * adicionados na coleção.
 */
public class Canvas extends JPanel implements Runnable{
    
    // Constantes 
    private final int FPS = 60; 

    // Propriedades 
    public Image backgroundImage;
    public Image target;
    private int WindowH;
    private int WindowW;
    private ConcurrentLinkedQueue<SpritePoint> graphicElements = new ConcurrentLinkedQueue<SpritePoint>();
    private Graphics2D g2D;
    public  String GameTitle = "FollowTarget";
    private Semaphore removeElementSemaphore = new Semaphore(1, true);


    // Construtor 
    public Canvas(String bbgPath){
        
        // Carregando imagem de fundo 
        this.backgroundImage = new ImageIcon(bbgPath).getImage();

        // Definindo tamanho do painel 
        this.WindowH = backgroundImage.getHeight(null);
        this.WindowW = backgroundImage.getWidth(null);

        // Definindo tamanho do painel 
        this.setPreferredSize(new Dimension(WindowW, WindowH)); 

        // Inicializando Thread
        Thread t = new Thread(this);
        t.start();

    }

    //#region Métodos Publicos 

    /**
     * Executa processo de construção do canvas (tela).
     */
    public void paint(Graphics g) {

        // Convertendo formato 
        this.g2D = (Graphics2D)g;

        // Desenhando elementos
        this.drawBackground();
        this.drawElementsCollection();
        
    }

    /**
     * Define titulo do jogo
     */
    public void setGameTitle(String gameTitle) {
        GameTitle = gameTitle;
    }

    /**
     * Adiciona elemento gráfico do tipo SpritePoint
     * @param element elemento gráfico. 
     */
    public void addGraphicElement(SpritePoint element) {
        this.graphicElements.add(element);
    }

    /**
     * Remove elemento gráfico. 
     * @param element
     */
    public void removeGraphicElement(SpritePoint element) {
        try {
            this.removeElementSemaphore.acquire();
            this.graphicElements.remove(element);
            this.removeElementSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return Altura da janela a ser exibida.
     */
    public int getWindowH() {
        return WindowH;
    }

    /**
     * @return Largura da janela a ser exibida. 
     */
    public int getWindowW() {
        return WindowW;
    }

    /**
     * Atualiza elementos na tela. (Ação da Thread)
     */
    @Override
    public void run() {
        while (true) {
            Instant start = Instant.now();
            repaint();
            try {
                Thread.sleep(1000/this.FPS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Instant end = Instant.now();
            Debug.registerDrawingTime(start, end);
        }
    }

    //#endregion

    //#region Métodos Privados 

    /**
     * Desenha fundo da tela 
     */
    private void drawBackground(){
        // Desenhando fundo 
        this.g2D.drawImage(backgroundImage, 0, 0, null);
    }

    /**
     * Desenha elementos adicionados na coleção. 
     */
    private void drawElementsCollection(){

        // Desenhando elementos adicionados
        for (SpritePoint spritePoint : this.graphicElements) {
            // Desenhando elemento no buffer 
            this.g2D.drawImage(spritePoint.getImage(), spritePoint.getX(), spritePoint.getY(), 
                               spritePoint.getSizeX(), spritePoint.getSizeY(), null);
        }
    }

    //#endregion
}
