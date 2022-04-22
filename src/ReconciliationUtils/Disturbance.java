package ReconciliationUtils;

import java.util.Random;

/**
 * Classe responsável por definir parâmetros dos disturbios 
 * da simulação. 
 */
public class Disturbance {

    //#region Variáveis

    // +- Variação de velocidade (em pixels) que o sistema pode apresentar. 
    public static double pseudoDeviation = 2.5; 

    /**
     * Lista de posições em Y onde a velocidade deve ser alterada. 
     */
    public static int[] positions = new int[]{70, 125, 250, 315, 400};

    //#endregion

    //#region Métodos 

    /**
     * @return Variação em intervalo definido na classe ( variável pseudoDeviation). 
     */
    public static float getRandomDisturb() {
        double min = -Disturbance.pseudoDeviation;
        double max = Disturbance.pseudoDeviation;
        Random random = new Random();
        return (float)(random.nextDouble(max - min) + min);
    }

    /**
     * Número de posições de simulação de disturbio 
     * @return número de elementos na array 
     */
    public static int getDistubanceNumber(){
        return positions.length;
    }
    
    //#endregion 
    
}
