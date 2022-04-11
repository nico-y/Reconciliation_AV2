package ThreadElements;

/**
 * Thread Simples para chamada constante do método Load do Launcer
 */
public class LoadExecuter extends Thread {
    
    /**
     * Launcher referenciado 
     */
    private Launcher launcher;

    /**
     * Construtor 
     * @param launch Launcher a ser chamado pela Thread
     */
    public LoadExecuter(Launcher launch){

        this.launcher = launch;

    }

    @Override
    public void run() {
        
        while (true) {
            // Chamando método load
            try {
                this.launcher.Load();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
