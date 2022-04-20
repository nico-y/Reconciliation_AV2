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
    private final int SENSOR_AMOUNT = 19;

    //#endregion

    //#region Atributos 

    private double sensorValues[];
    
    /**
     * Alvo a ser observado.
     */
    private MovingTarget target; 

    /**
     * Posicao anterior do alvo. 
     */
    private float lastTargetPosition;

    /**
     * Posições onde o alvo deve ser sensoreado. 
     */
    public int[] reconciliationPositions;

    /**
     * Vetor de variancias
     */
    public double[] varianceVector; 

    /**
     * Matrizes de restrições 
     */
    public double[][] restrictionVector; 

    /**
     * Indice com posição do seguinte sensor
     */
    private int nextSensor = 0;

    /**
     * Valores corrigidos a cada passagem do sensor. 
     */
    public double[] correctedValues;
    
    /**
     * Elemento de Thread a ser inicializado
     */
    public Thread t; 

    //#endregion

    //#region Construtores

    public TargetReconcilator(MovingTarget target){
        
        // Atribuindo propriedades
        this.target = target;
        this.reconciliationPositions = new int[SENSOR_AMOUNT];
        this.sensorValues = new double[SENSOR_AMOUNT];
        this.varianceVector = new double[SENSOR_AMOUNT];
        this.restrictionVector = new double[SENSOR_AMOUNT-1][SENSOR_AMOUNT];
        t = new Thread(this);

        // Definindo intervalo entre posições 
        int posInterval = (int)(this.target.getDestinyPoint().getY()/(SENSOR_AMOUNT + 1));
        int auxPos = posInterval;
        int j = 0;
        
        // Definindo posições de leitura e preenchendo vetor de variancia  
        for (int i = 0; i < SENSOR_AMOUNT; i++) {

            // Preenchendo vetor de variancia 
            this.varianceVector[i] = (float)Disturbance.pseudoDeviation;
            
            // Definindo posições de sensoreamento
            this.reconciliationPositions[i] = auxPos;

            // Incrementando intervalo de posição
            auxPos += posInterval;

            // Definindo valores iniciais dos sensores
            this.sensorValues[i] = (float)this.target.getTimeObjective();

            // Definindo matriz de restrições 
            if (i < SENSOR_AMOUNT - 1) {
                restrictionVector[i][j] = 1;
                restrictionVector[i][j+1] = -1;
                j++;
            }
        }    
        
        // Definindo posição anterior como posição atual
        this.lastTargetPosition = 0;

    }

    //#endregion

    //#region Métodos

    @Override
    public void run() {

        
        
        while (!this.target.hasArrived()  && nextSensor < SENSOR_AMOUNT) {

            float sensorPos = (float)this.reconciliationPositions[nextSensor];
            float actPos = this.target.getYPos();
            System.out.println(actPos);
            
            // Conferindo se alvo passou por ponto de sensoreamento
            if (lastTargetPosition < sensorPos && actPos >= sensorPos) 
            {
                // Obtendo estimação de tempo a partir de sensor
                this.sensorValues[nextSensor] = this.target.getElapsedTime() + this.target.estimateTime();

                // Calculando reconciliação
                Reconciliation auxRec =  new Reconciliation(sensorValues, varianceVector, restrictionVector);
                this.correctedValues = auxRec.getReconciledFlow();

                // Passando nova velocidade 
                this.target.setSpeed(((long)this.correctedValues[nextSensor]) - this.target.getElapsedTime());

                // Exibindo reconciliação
                auxRec.printMatrix(this.correctedValues);

                // Incrementando posições
                nextSensor++; 

                // Atualizando posição anterior 
                lastTargetPosition = actPos;
            }

            
        }
    }

    //#endregion
}
