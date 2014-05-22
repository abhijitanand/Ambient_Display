package tud.tk3.ambientdisplay.app.display;

/**
 * Created by simon on 5/21/14.
 */
public class ImageSection {

    public double posX;
    public double posY;
    public double offsetX;
    public double offsetY;

    public ImageSection(double posX, double posY, double offsetX, double offsetY){
        this.posX = posX;
        this.posY = posY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public String toString(){
        return String.format("(%f/%f) + (%f/%f)", posX, posY, offsetX, offsetY);
    }

}
