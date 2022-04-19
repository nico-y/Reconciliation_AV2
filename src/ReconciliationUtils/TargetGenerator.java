package ReconciliationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import ThreadElements.MovingTarget;
import Utils.Canvas;

public class TargetGenerator extends Thread{
    
    //#region Propriedades 

    /**
     * Tempo entre a geração de cada Alvo 
     */
    public int sleepTime = 6500;

    /**
     * Quantidade de alvos a ser gerados a cada ciclo
     */
    public int targets = 2;

    /**
     * Tempo o qual o alvo deve demorar para chegar no objetivo. (Final da tela)
     */
    private int timeObjective;

    /**
     * Canvas onde o alvo será exibido 
     */
    private Canvas canvas; 

    /**
     * Coleção de alvos gerados 
     */
    private ConcurrentLinkedDeque<MovingTarget> _targetsCollection = new ConcurrentLinkedDeque<MovingTarget>();

    /**
     * Ouvintes das notificações de novos tiros.
     */
    private List<TargetListener> listeners = new ArrayList<TargetListener>();

    //#endregion

    //#region Construtores

    //#region Construtor
    public TargetGenerator(Canvas canvas, int time){
        this.canvas = canvas; 
        this.timeObjective = time;
    }

    //#endregion

    //#region Métodos 

    @Override
    public void run(){
        // Criando novos alvos
        targetGenerationTest();
    }

    private void targetGenerationTest(){

        // Criando novos alvos
        boolean sideAux = true; 

        while (true) {

            for (int i = 0; i < this.targets; i++) {
                // Criando alvo
                MovingTarget target = new MovingTarget(this.canvas.getWindowW(), this.canvas.getWindowH(), sideAux, this.timeObjective);
                this._targetsCollection.add(target);
                target.addToScennary(this.canvas);
                //Notificando alvo criado 
                notifyNewTarget(target);
                sideAux = !sideAux;
            }
            
            // Pausa  
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyNewTarget(MovingTarget target){
        // Notificando os interessados
        for (TargetListener listener : this.listeners) {
            listener.targetDetected(target);
        }
    }

    public void addTargetListener(TargetListener listener){
        this.listeners.add(listener);
    }

    //#endregion

}
