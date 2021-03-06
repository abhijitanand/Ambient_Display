package tud.tk3.ambientdisplay.app.communication;

import android.app.Activity;
import android.graphics.Bitmap;

import java.util.List;

import tud.tk3.ambientdisplay.app.display.ImageSection;

/**
 * Created by martin on 21.05.14.
 */
public interface AmbientDisplay {

    public void displayImage(Bitmap image);

    public void topologyChange(List<ImageSection> sections);

    public void imageSectionChange(ImageSection section);

}
