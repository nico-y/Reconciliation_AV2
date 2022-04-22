package Utils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe responsável por auxiliar na escrita de arquivos de texto. 
 */
public class TxtHelper {
    
    /**
     * Cria arquivo de texto tendo como nome data e hora atuais. 
     * @return nome do arquivo criado se criado, string vazia caso não criado.                           
     */
    public static String createTxt(){
        String fileName = "C:\\Users\\nicoh\\Desktop\\Java Test\\Reconciliation\\logs\\log-" + System.currentTimeMillis() + ".txt";
        try {
            File myObj = new File(fileName);
            if (myObj.createNewFile()) {
              System.out.println("File created: " + myObj.getName());
              return fileName;
            } else {
              System.out.println("File already exists.");
              return "";
            }
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        return "";
    }

    /**
     * adiciona texto em arquivo txt especificado 
     * @param filePath
     * @param text
     */
    public static void appendTxtLine(String filePath, String text){ 
        try (FileWriter f = new FileWriter(filePath, true); 
                    BufferedWriter b = new BufferedWriter(f); 
                    PrintWriter p = new PrintWriter(b);
                    ) { 
            p.println(text); 
        } catch (IOException i) { 
            i.printStackTrace(); 
        }
    }

    /**
     * Converte vetor em string 
     * @param array vetor de valores 
     * @return string com vetor de valores
     */
    public static String arr2str( double[] array){
        String str = "";
        for(int i = 0; i < array.length; i++){
            str = str + Double.toString(array[i]) + " ";
        }
        return str;
    }
}
