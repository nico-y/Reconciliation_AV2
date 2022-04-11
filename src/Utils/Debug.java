package Utils;

import java.time.*;

public class Debug implements Runnable{
    
    // Constantes 
    public final static boolean DEBUG_FLAG = false;
    public final static boolean TIME_MEASSURE_FLAG = true;
    private static long loadAverage = 0;
    private static long laodMax = 0; 
    private static long loadMin = 999999999; 

    private static long prepareAverage = 0;
    private static long prepareMax = 0; 
    private static long prepareMin = 999999999; 
    
    private static long shootAverage = 0;
    private static long shootMax = 0; 
    private static long shootMin = 9999999; 

    private static long switchTargetAverage = 0;
    private static long switchTargetMax = 0; 
    private static long switchTargetMin = 9999999; 

    private static long drawingAverage = 0;
    private static long drawingMax = 0; 
    private static long drawingMin = 9999999; 

    private static long targetGenerationAverage = 0;
    private static long targetGenerationMax = 0; 
    private static long targetGenerationMin = 9999999; 

    // Construtor 
    public Debug(){
        Thread t = new Thread(this);
        t.start();
    }

    // Métodos
    
    /**
     * Exibe texto no console apenas quando flag de debug é verdadeiro.
     * @param text Texto a ser Exibido.
     */
    public static void print(String text){
        if(Debug.DEBUG_FLAG) System.out.println(text);
    }

    /**
     * Exibe texto apenas quando Flag é verdadeira 
     * @param start Comeco da medicao 
     * @param end Fim da medicao 
     */
    public static void printInstant(Instant start, Instant end){

        if(TIME_MEASSURE_FLAG) System.out.println(Duration.between(start, end));
    }

    /**
     * Exibe texto apenas quando TIME_MEASSURE_FLAG = true. Exibe intervalos de tempo passados
     * @param start Instante Inicial
     * @param end Instante Final 
     * @param text Texto aleatorio
     */
    public static void printInstant(Instant start, Instant end, String text){
        if (TIME_MEASSURE_FLAG) {
            System.out.println(text);
            //System.out.println(Duration.between(start, end));
            long teste = Duration.between(start, end).toMillis();
            System.out.println(teste);
        }
    }

    public static void printInstant(long start, long end){
        if (TIME_MEASSURE_FLAG) {
            //System.out.println(text);
            System.out.println(end - start);
        }
    }

    public static void registerLoadTime(Instant start, Instant end){
        // Registrando na media
        long time = Duration.between(start, end).toMillis();
        loadAverage = (loadAverage + time)/2;

        // Registrando menor média 
        if(loadAverage < loadMin) loadMin = loadAverage;

        // Registrando maior média
        if (loadAverage > laodMax) laodMax = loadAverage;
    }

    public static void registerPrepareTime(Instant start, Instant end){
        // Registrando na media
        long time = Duration.between(start, end).toMillis();
        prepareAverage = (prepareAverage + time)/2;

        // Registrando menor média 
        if(prepareAverage < prepareMin) prepareMin = prepareAverage;

        // Registrando maior média
        if (prepareAverage > prepareMax) prepareMax = prepareAverage;
    }

    public static void registerShootTime(Instant start, Instant end){
        // Registrando na media
        long time = Duration.between(start, end).toMillis();
        shootAverage = (shootAverage + time)/2;

        // Registrando menor média 
        if(shootAverage < shootMin) shootMin = shootAverage;

        // Registrando maior média
        if (shootAverage > shootMax) shootMax = shootAverage;
    }

    public static void registerSwitchTime(Instant start, Instant end){
        // Registrando na media
        long time = Duration.between(start, end).toMillis();
        switchTargetAverage = (switchTargetAverage + time)/2;

        // Registrando menor média 
        if(switchTargetAverage < switchTargetMin) switchTargetMin = switchTargetAverage;

        // Registrando maior média
        if (switchTargetAverage > switchTargetMax) switchTargetMax = switchTargetAverage;
    }

    public static void registerDrawingTime(Instant start, Instant end){
        // Registrando na media
        long time = Duration.between(start, end).toMillis();
        drawingAverage = (drawingAverage + time)/2;

        // Registrando menor média 
        if(drawingAverage <drawingMin)drawingMin = drawingAverage;

        // Registrando maior média
        if (drawingAverage > drawingMax) drawingMax = drawingAverage;
    }

    public static void registerTargetGenerationTime(Instant start, Instant end){
        // Registrando na media
        long time = Duration.between(start, end).toMillis();
        targetGenerationAverage = (targetGenerationAverage + time)/2;

        // Registrando menor média 
        if(targetGenerationAverage < targetGenerationMin)targetGenerationMin = targetGenerationAverage;

        // Registrando maior média
        if (targetGenerationAverage > targetGenerationMax) targetGenerationMax = targetGenerationAverage;
    }

    @Override
    public void run() {
        // Exibir Tempo dos Métodos
        while (true) {
            
            if (TIME_MEASSURE_FLAG) {
                String text = "Load: " + loadAverage;
                text += "| Prepare: " + prepareAverage;
                text += "| Shoot: " + shootAverage;
                text += "| Switch: " + switchTargetAverage;
                text += "| Drawing: " + drawingAverage;
                text += "| Target Gen: " + targetGenerationAverage;
                text += "| ms";
                System.out.println(text);

                String maxText = "Load Max: " + laodMax;
                maxText += "| Prepare Max: " + prepareMax;
                maxText += "| Shoot Max: " + shootMax;
                maxText += "| Switch Max: " + switchTargetMax;
                maxText += "| Drawing Max: " + drawingMax;
                maxText += "| Target Gen Max: " + targetGenerationMax;
                maxText += "| ms";
                System.out.println(maxText);
                

                String minText = "Load Min: " + loadMin;
                minText += "| Prepare Min: " + prepareMin;
                minText += "| Shoot Min: " + shootMin;
                minText += "| Switch Min: " + switchTargetMin;
                minText += "| Drawing Min: " + drawingMin;
                minText += "| Target Gen Min: " + targetGenerationMin;
                minText += "| ms";
                System.out.println(minText);
                System.out.println("---------------------------------------------------------------------------------------------------------------------------------");
            }

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
