package ThreadElements;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import Utils.Debug;

public class TargetSwitcher implements ShotsListener{

    // Atributos 
    private Launcher _launcher;
    /**
     * Coleção de alvos gerados 
     */
    private ConcurrentLinkedQueue<MovingTarget> targetsCollection = new ConcurrentLinkedQueue<MovingTarget>();

    //private Semaphore listSemaphore = new Semaphore(1, true); 

    // Construtor 
    public TargetSwitcher(Launcher launcher){

        // Armazenando atributos
        this._launcher = launcher;
        this._launcher.addShotListener(this);

    }

    // Evento a ser chamado
    @Override
    public void shotLaunched(Launcher launcer) {

        Debug.print("Switcher Chamado: " + this._launcher.getId());
        
        // Passando seguinte alvo para o launcher 
        //System.out.println(this.targetsCollection.size());
        MovingTarget aux = this.getNextTarget();
        
        // Chamando novamente caso nulo 
        while(aux == null || aux.getID().equals(this._launcher.getLastTargetID())){ 
            aux = this.getNextTarget(); 
        }
        
        // Definindo novo alvo
        this._launcher.setNowTarget(aux);
        
    }

    /**
     *Evento para verificação de colisão
     */
    @Override
    public void shotFinished(Shots finishedShot, MovingTarget aimedTarget) {
        finishedShot.setHit();
        aimedTarget.setHit();
    }

    /**
     * Adiciona alvo na coleção do Switcher
     * @param target alvo
     */
    public void addTarget(MovingTarget target){
        this.targetsCollection.add(target);
    }

    /**
     * Remove primeiro elemento da fila 
     * @return Alvo a ser atingido a seguir, primeiro da fila. 
     */
    public MovingTarget getNextTarget(){
        return  this.targetsCollection.poll();
    }

    /**
     * Launcer Relacionado ao Switcher
     * @return
     */
    public Launcher getLauncher() {
        return _launcher;
    }
}
