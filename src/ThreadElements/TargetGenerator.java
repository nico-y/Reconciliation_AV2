package ThreadElements;

import java.util.ArrayList;
import Utils.Canvas;

public class TargetGenerator extends Thread {
    
    /**
     * Tempo entre a geração de cada Alvo 
     */
    private int sleepTime = 100;

    /**
     * Quantidade de alvos a ser gerados a cada ciclo
     */
    public int targets = 2;

    /**
     * Canvas onde o alvo será exibido 
     */
    private Canvas canvas; 

    /**
     * Launcher responsável de atirar nos alvos
     */
    private ArrayList<TargetSwitcher> switcherCollection = new ArrayList<TargetSwitcher>();

    private int switcherIndex = 0;    
    /**
     * Construtor
     */
    public TargetGenerator(Canvas canvas, ArrayList<TargetSwitcher> switcherCollection){
        this.canvas = canvas; 
        this.switcherCollection = switcherCollection;
    }

    //#region Métodos Públicos 
    @Override
    public void run(){

        boolean sideAux = true;
        for (TargetSwitcher switcher : this.switcherCollection) {
            
            // Criando alvo
            MovingTarget target = new MovingTarget(this.canvas.getWindowW(), this.canvas.getWindowH(), sideAux);
            switcher.addTarget(target);
            target.addToScennary(this.canvas);
            sideAux = !sideAux;

            //  Passando alvos para o lançador 
            switcher.getLauncher().setNowTarget(target);
        } 

        // Pausa
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        while (true) {
            try {

                for (int i = 0; i < this.targets; i++) {
                    // Criando alvo
                    MovingTarget target = new MovingTarget(this.canvas.getWindowW(), this.canvas.getWindowH(), sideAux);
                    getNextSwitcher().addTarget(target);
                    target.addToScennary(this.canvas);
                    sideAux = !sideAux;
                } 

                // Pausa
                Thread.sleep(sleepTime);
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }    
        }
    }

    /**
     * Define intervalo de criação dos alvos
     * @param sleepTime Tempo em ms 
     */
    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    //#endregion

    //#region   Métodos Privados 

    private TargetSwitcher getNextSwitcher(){
        TargetSwitcher auxSwitcher = this.switcherCollection.get(this.switcherIndex);
        if (switcherIndex > (switcherCollection.size() - 1)) switcherIndex = 0;
        return auxSwitcher;
    }

    //#endregion
}