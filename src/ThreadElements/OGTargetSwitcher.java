package ThreadElements;

import java.time.Instant;
import java.util.concurrent.Semaphore;
import Utils.Debug;


public class OGTargetSwitcher implements ShotsListener{
    
    // Atributos
    private OGTargetGenerator targetGenerator;

    /**
     * Semaforo para acesso a seguinte elemento da lista
     */
    public Semaphore getNextSemaphore = new Semaphore(1, true);

    /**
     * Semaforo para definição de Hit atingido
     */
    public Semaphore setHitSemaphore = new Semaphore(1, true);

    // Construtor 
    public OGTargetSwitcher(OGTargetGenerator targetGenerator){

        // Armazenando atributos 
        this.targetGenerator = targetGenerator;
    }

    // Evento a ser chamado 
    @Override
    public void shotLaunched(Launcher launcher) {
        try {

            // Inicializando cronometro
            Instant start = Instant.now();

            this.getNextSemaphore.acquire();

            Debug.print("Switcher Chamado: " + launcher.getId());

            // Passando seguinte alvo para o launcher 
            MovingTarget aux = this.targetGenerator.getNextTarget();

            // Chamando novamente caso nulo 
            while(aux == null){ 
                aux = this.targetGenerator.getNextTarget(); 
            }

            // Definindo novo alvo
            launcher.setNowTarget(aux);

            // Liberando recursos
            this.getNextSemaphore.release();

            // Finalizando cronometro
            Instant end = Instant.now();

            // Registrando tempo 
            Debug.registerSwitchTime(start, end);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *Evento para verificação de colisão
     */
    @Override
    public void shotFinished(Shots finishedShot, MovingTarget aimedTarget) {
        
        try {

            this.setHitSemaphore.acquire();
            finishedShot.setHit();
            aimedTarget.setHit();
            this.setHitSemaphore.release();

        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }
}
