package Utils;
import java.awt.*;

import javax.swing.ImageIcon;
import java.io.IOException;

public class SpritePoint {
    
    // Constantes

    //#region Propriedades
    
    private float _X = 0; // Posição em X
    private float _Y = 0; // Posição em Y 
    private int _SizeX = 26;
    private int _SizeY = 26;
    private Image _Icon;

    //#endregion

    //#region Construtores

    public SpritePoint(int X, int Y, String iconPath){
        this._X = X;
        this._Y = Y;
        this._Icon = new ImageIcon(iconPath).getImage();
    }

    public SpritePoint (float f, float y){
        this._X = f; 
        this._Y = y;
        this._Icon = null;
    }

    public SpritePoint (SpritePoint point, String iconPath) throws IOException{
        this.setPositions(point);
        this._Icon = new ImageIcon(iconPath).getImage(); 
    }

    public SpritePoint (SpritePoint point){
        this.setPositions(point);
    }

    public SpritePoint (SpritePoint point, String iconPath, int sizeX, int sizeY) throws IOException{
        this(point, iconPath);
        this._SizeX = sizeX;
        this._SizeY = sizeY;
    }


    //#endregion

    //#region Métodos Públicos

    /**
     * @return Posição X da origem.
     */
    public float getX(){
        return this._X;
    }

    /**
     * @return Posição Y da origem. 
     */
    public float getY(){
        return this._Y;
    }

    public Image getImage(){
        if (this._Icon == null) {
            return null;
        }
        return this._Icon;
    }

    public void setPositions(SpritePoint point){
        this._X = point.getX();
        this._Y = point.getY();
    }

    public void setPositions(int X, int Y){
        this._X = X; 
        this._Y = Y;
    }

    public void setX(int X) {
        this._X = X;
    }

    public void setY(float f) {
        this._Y = f;
    }

    public int getSizeX() {
        return _SizeX;
    }

    public int getSizeY() {
        return _SizeY;
    }
    
    public void setSizeX(int sizeX) {
        this._SizeX = sizeX;
    }

    public void setSizeY(int sizeY) {
        this._SizeY = sizeY;
    }

    public void printPosition(){
        System.out.println("X = " + this.getX() + "|| Y = " + this.getY());
    }

    public void linearMoveTest(int startX, int startY, int endX, int endY, int sleepTimeMs, boolean verbose){

        // Colocando em posição inicial
        this._X = startX;
        this._Y = startY;

        // Deslocamento em X
        while (this._X != endX ) {
            this._X++;
            try {
                Thread.sleep(sleepTimeMs);
                if (verbose) System.out.println(System.currentTimeMillis());
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Deslocamento em Y 
        while (this._Y != endY) {
            this._Y++;
            try {
                Thread.sleep(sleepTimeMs);
                if (verbose) System.out.println(System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //#endregion
}
