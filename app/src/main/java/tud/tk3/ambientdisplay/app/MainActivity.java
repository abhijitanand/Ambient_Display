package tud.tk3.ambientdisplay.app;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.util.List;
import java.util.logging.LogRecord;

import tud.tk3.ambientdisplay.app.communication.AmbientDisplay;
import tud.tk3.ambientdisplay.app.communication.Communicator;
import tud.tk3.ambientdisplay.app.communication.DisplayController;
import tud.tk3.ambientdisplay.app.communication.DumbDisplayController;
import tud.tk3.ambientdisplay.app.display.ImageSection;


public class MainActivity extends ActionBarActivity implements AmbientDisplay{
    private ImageView imageView;
    private Communicator comm;
    private DisplayController dispController;
    private Bitmap image;
    ImageSection mySection;
   // private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher);
        drawable.setBounds(5, 5, 10, 10);
//        iv.setImageDrawable(drawable);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//        iv.setImageBitmap(bitmap);
        Bitmap bitmapCropped = Bitmap.createBitmap(bitmap, 0, 0, 50, 50);
        System.out.println("Cropped: " + bitmapCropped.getHeight() + " " + bitmapCropped.getWidth());
        imageView.setImageBitmap(bitmapCropped);
        dispController = new DumbDisplayController(this);
        comm = new Communicator(this, dispController);
        comm.publishScreen();
       /* handler = new Handler(){
            public void displayOnMainThread(Bitmap image)
            {
                imageView.setImageBitmap(image);
            }
        };*/
    }


   /*     @Override
              public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_gallery) {
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            final int ACTIVITY_SELECT_IMAGE = 1234;
            startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 1234:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    System.out.println("Image Path : " + filePath);
                    cursor.close();

                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    comm.sendImage(yourSelectedImage);
                    displayImage(yourSelectedImage);
                }
        }

    };

    @Override
    public void displayImage(Bitmap bitmap) {
        //this.image= bitmap;

        int resx = bitmap.getWidth();
        int resy = bitmap.getHeight();
        int posX = (int) (resx*mySection.posX);
        int posY = (int) (resy*mySection.posY);
        int offsetX= (int) (resx*mySection.offsetX);
        int offsetY= (int) (resy*mySection.offsetY);
        Log.e("Logging..",""+resx+":"+resy+" "+mySection.posX+":"+mySection.offsetX);
        if(posX+offsetX >resx)
        {
            offsetX = resx - posX;
        }
        if(posY+offsetY >resy)
        {
            offsetY = resy - posY;
        }

        this.image = Bitmap.createBitmap(bitmap,posX ,posY ,offsetX ,offsetY );

        runOnUiThread(new Thread() {
            public void run() {

                imageView.setImageBitmap(image);
            }

            ;
        });

    }

    @Override
    public void topologyChange(List<ImageSection> sections) {

    }

    @Override
    public void imageSectionChange(ImageSection section) {
        mySection = section;
    }

}
