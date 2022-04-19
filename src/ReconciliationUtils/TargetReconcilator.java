package ReconciliationUtils;

import ThreadElements.MovingTarget;
import Utils.SpritePoint;

/**
 * Esta classe é responsável por fazer a reconcialicao de dados para compensar 
 * a velocidade entre os trechos do sistema. Consiste em uma thread com a capacidade de 
 * analisar a posicao atual do alvo e calcular a velocidade necessária em cada trajeto 
 * para cumprir o tempo objetivo desejado. 
 */
public class TargetReconcilator implements Runnable{
    
    //#region Constantes 

    /**
     * Quantidade de pontos onde a velocidade será medida. 
     */
    private final int SENSOR_AMOUNT = 20;

    //#endregion

    //#region Atributos 
    
    /**
     * Alvo a ser observado.
     */
    private MovingTarget target; 

    /**
     * Posicao anterior do alvo. 
     */
    private SpritePoint lastTargetPosition;

    /**
     * Posições onde o alvo deve ser sensoreado. 
     */
    public int[] reconciliationPositions;

    public float[] varianceVector; 

    //#endregion

    //#region Construtores

    public TargetReconcilator(MovingTarget target){
        // Atribuindo propriedades
        this.target = target;
        this.reconciliationPositions = new int[SENSOR_AMOUNT];
        this.varianceVector = new float[SENSOR_AMOUNT];

        // Definindo intervalo entre posições 
        int posInterval = (int)(this.target.getDestinyPoint().getY()/SENSOR_AMOUNT);
        int auxPos = 0;
        
        // Definindo posições de leitura 
        for (int i = 0; i < SENSOR_AMOUNT; i++) {
            this.varianceVector[i] = (float)Disturbance.pseudoDeviation;
            this.reconciliationPositions[i] = auxPos;
            auxPos += posInterval;
        }

        // Declarando vetor de variância
        
    }

    //#endregion

    //#region Métodos

    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }

    //#endregion
}
