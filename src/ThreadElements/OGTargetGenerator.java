 package ThreadElements;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import Utils.Canvas;
import Utils.Debug;

public class OGTargetGenerator extends Thread {
    
    /**
     * Tempo entre a geração de cada Alvo 
     */
    public int sleepTime = 500;

    /**
     * Quantidade de alvos a ser gerados a cada ciclo
     */
    public int targets = 2;

    /**
     * Canvas onde o alvo será exibido 
     */
    private Canvas canvas; 

    /**
     * Coleção de alvos gerados 
     */
    private ConcurrentLinkedDeque<MovingTarget> _targetsCollection = new ConcurrentLinkedDeque<MovingTarget>();

    /**
     * Coleção de Launchers
     */
    private ArrayList<Launcher> launcherCollection = new ArrayList<Launcher>();
    
    /**
     * Ouvintes das notificações
     */
    public List<ShotsListener> _listeners = new ArrayList<ShotsListener>();

    //#region Construtor
    public OGTargetGenerator(Canvas canvas, ArrayList<Launcher> launcherCollect){
        this.canvas = canvas; 
        this.launcherCollection = launcherCollect;
    }

    //#endregion

    //#region Métodos Públicos 
    @Override
    public void run(){
        
        // Criando novos alvos 
        boolean sideAux = true;
        for (int i = 0; i < launcherCollection.size(); i++) {
            // Criando alvo
            MovingTarget target = new MovingTarget(this.canvas.getWindowW(), this.canvas.getWindowH(), sideAux);
            this._targetsCollection.add(target);
            target.addToScennary(this.canvas);
            sideAux = !sideAux;
        }

        // Passando alvos para os lançadores
        for (Launcher launcher : launcherCollection) {
            launcher.setNowTarget(getNextTarget());
        }
        
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        while (true) {
            Instant start = Instant.now();

            try {

                for (int i = 0; i < this.targets; i++) {
                    // Criando alvo
                    MovingTarget target = new MovingTarget(this.canvas.getWindowW(), this.canvas.getWindowH(), sideAux);
                    this._targetsCollection.add(target);
                    target.addToScennary(this.canvas);
                    sideAux = !sideAux;
                }
                
                // Pausa  
                Thread.sleep(sleepTime);
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }  
            
            Instant end = Instant.now();

            Debug.registerTargetGenerationTime(start, end);
        }
    }

    /**
     * Remove primeiro elemento da fila 
     * @return Alvo a ser atingido a seguir, primeiro da fila. 
     */
    public MovingTarget getNextTarget(){
        return  this._targetsCollection.poll();
    }

    /**
     * Adiciona launcher a coleção 
     */
    public void addLauncher(Launcher launcher){
        this.launcherCollection.add(launcher);        
    }

    /**
     * Retorna coleção de launchers
     * @return
     */
    public ArrayList<Launcher> getLauncherCollection() {
        return launcherCollection;
    }

    /**
     * Adiciona ouvintes a serem notifiacdos.
     */
    public void addShotListener(ShotsListener listener){
        this._listeners.add(listener);
    }

    //#endregion
}