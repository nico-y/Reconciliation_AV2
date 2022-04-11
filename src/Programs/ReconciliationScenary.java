package Programs;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

import ReconciliationUtils.FollowLauncher;
import ReconciliationUtils.TargetGenerator;
import ReconciliationUtils.TargetListener;
import Utils.Canvas;

public class ReconciliationScenary extends JFrame {
    
    // Constantes
    public static final String BACKGROUND_PATH = "src/Resources/Wallpaper.png";

    // Propriedades 
    public Canvas canvas;
    private TargetGenerator targetGenerator;
    private List<TargetListener> listeners = new ArrayList<TargetListener>();
    private FollowLauncher launcher;
    private int targetTimeObjective = 3000;

    // Construtor
    public ReconciliationScenary(){
        
        // Criando canvas
        this.canvas = new Canvas(BACKGROUND_PATH);
        
        // Criando Janela 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        this.add(canvas);  
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);

        // Inicializando Cenario
        initialize1();

    }

    //#region Métodos de Inicialização

    private void initialize1(){

        // Instanciando lançador
        this.launcher = new FollowLauncher(this.canvas);

        // Instanciando gerador de alvos 
        targetGenerator = new TargetGenerator(canvas, targetTimeObjective);

        // Adicionando ouvinte
        targetGenerator.addTargetListener(this.launcher);

        // Inicializando thread do gerador
        targetGenerator.start();
    }

    //#endregion

}
