package tud.tk3.ambientdisplay.app.display;

/**
 * Created by simon on 5/21/14.
 */
public class ImageSection {

    public int posX;
    public int posY;
    public int offsetX;
    public int offsetY;

    public ImageSection(int posX, int posY, int offsetX, int offsetY){
        this.posX = posX;
        this.posY = posY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public String toString(){
        return String.format("(%d/%d) + (%d/%d)", posX, posY, offsetX, offsetY);
    }

}
