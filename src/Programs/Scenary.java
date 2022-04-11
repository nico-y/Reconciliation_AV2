package Programs;

import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;
import ThreadElements.Launcher;
import ThreadElements.LoadExecuter;
import ThreadElements.OGTargetGenerator;
import ThreadElements.OGTargetSwitcher;
import ThreadElements.TargetSwitcher;
import ThreadElements.TargetGenerator;
import Utils.Canvas;

/**
 * Esta classe carrega um painel com os gráficos a serem exibidos e analisa o estado dos elementos. 
 */
public class Scenary extends JFrame{
    
    // Constantes
    public static final String BACKGROUND_PATH = "src/Resources/Wallpaper.png";
    private final static int LAUNCHERS = 13;

    // Propriedades 
    public Canvas canvas;
    private ArrayList<TargetSwitcher> switcherCollection = new ArrayList<TargetSwitcher>();
    private ArrayList<LoadExecuter> executerCollection = new ArrayList<LoadExecuter>();
    private ArrayList<Launcher> launcherCollection = new ArrayList<Launcher>();
    private static int _shotCounter = 0;

    private OGTargetGenerator oTargetGenerator;
    private OGTargetSwitcher oTargetSwitcher;
    private TargetGenerator targetGenerator;

    // Construtor 
    public Scenary() throws IOException{

        // Criando Canvas
        this.canvas = new Canvas(BACKGROUND_PATH);

        // Criando Janela 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        this.add(canvas);  
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);

        //System.in.read();

        // Inicializando lancador
        //initializeQ2();

        initializeLauncherQ1();

    }

    //#region Métodos Publicos

    public Canvas getPanel() {
        return canvas;
    }

    public static int getShotCounter() {
        return _shotCounter;
    }

    //#endregion

    //#region Métodos Privados
    private void initializeLauncherQ1(){

        // Criando lançadores 
        for (int i = 0; i < LAUNCHERS; i++) {
            
            // Criando Launcher
            Launcher auxLauncher = new Launcher(this.canvas, false);

            // Adicionando ouvinte 
            //auxLauncher.addShotListener(this);

            // Adicionando na lista de Launchers
            launcherCollection.add(auxLauncher); 

            // Criar Executer
            LoadExecuter auxExecuter = new LoadExecuter(auxLauncher);
            this.executerCollection.add(auxExecuter);
            
        }

        // Criando gerador de alvos
        this.oTargetGenerator = new OGTargetGenerator(this.canvas, this.launcherCollection);

        // Criando switcher de alvos
        this.oTargetSwitcher = new OGTargetSwitcher(oTargetGenerator);

        // Adicinoando switcher como ouvinte de todos os lançadores 
        for (Launcher launcher : this.launcherCollection) {
            launcher.addShotListener(oTargetSwitcher);
        }

        // Inicializar executores
        for (LoadExecuter executer : this.executerCollection) {
            executer.start();
        }
        
        // Inicializando gerador de alvos 
        this.oTargetGenerator.start();
    }

    /**
     * Inicializando situação da questão 2.
     */
    private void initializeQ2(){
        for (int index = 0; index < LAUNCHERS; index++) {
            // Criando Launcher
            Launcher auxLauncher = new Launcher(this.canvas, false);

            // Adicionando ouvinte 
            //auxLauncher.addShotListener(this);

            // Adicionando na lista de Launchers
            launcherCollection.add(auxLauncher); 

            // Criar Executer
            LoadExecuter auxExecuter = new LoadExecuter(auxLauncher);
            this.executerCollection.add(auxExecuter);

            // Criar Swithcer e adicionando na coleção 
            this.switcherCollection.add(new TargetSwitcher(auxLauncher));
        }

        // Criando gerador de alvos
        this.targetGenerator = new TargetGenerator(this.canvas, this.switcherCollection);

        // Inicializando gerador de alvos 
        this.targetGenerator.start();

        // Inicializar executores
        for (LoadExecuter executer : this.executerCollection) {
            executer.start();
        }
    }

    //#endregion
}
