package ThreadElements;

/**
 * Interface para criar eventos no momento de lançamento de um tiro.
 */
public interface ShotsListener {

    /**
     * Ação a ser realizada ao notificar tiro lançado. 
     */
    public void shotLaunched(Launcher launcer);

    /**
     * Ação a ser executada ao notificar tiro finalizado.
     */
    public void shotFinished(Shots finishedShot, MovingTarget aimedTarget);

    
}
