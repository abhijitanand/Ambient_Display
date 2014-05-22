package tud.tk3.ambientdisplay.app.display;

/**
 * Created by martin on 21.05.14.
 */
public class Display implements Comparable<Display>{

    public String uid;

    public int width;
    public int height;
    public int dpi;


    public Display(String id, int width, int height, int dpi) {
        this.uid = id;
        this.width = width;
        this.height = height;
        this.dpi = dpi;
    }

    public int getSize() {
        return width * height;
    }

    @Override
    public int compareTo(Display display) {
        int cmp = getSize() - display.getSize();
        if(cmp == 0){
            return uid.compareTo(display.uid);
        }
        return cmp;
    }
}
