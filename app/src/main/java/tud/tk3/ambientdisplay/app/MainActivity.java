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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);

        // Initialize the displayed section of an image as the whole image
        mySection = new ImageSection(0, 0, 1, 1);

        dispController = new DumbDisplayController(this);
        comm = new Communicator(this, dispController);
        comm.publishScreen();
    }

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
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        int resx = bitmap.getWidth();
        int resy = bitmap.getHeight();

        int posX = (int) (resx*mySection.posX);
        int posY = (int) (resy*mySection.posY);
        int offsetX= (int) (resx*mySection.offsetX);
        int offsetY= (int) ((resy*mySection.offsetY) * (((float)resx)/resy));
        //Log.e("Logging..",""+resx+":"+resy+" "+mySection.posX+":"+mySection.offsetX);
        //Log.e("Logging..",(resx/resy)+"|"+offsetX+":"+offsetY+" "+mySection.posY+":"+mySection.offsetY);
        if(posX+offsetX > resx)
        {
            offsetX = resx - posX;
        }
        if(posY+offsetY > resy)
        {
            offsetY = resy - posY;
        }

        this.image = Bitmap.createBitmap(bitmap, posX ,posY ,offsetX ,offsetY );

        runOnUiThread(new Thread() {
            public void run() {
                getActionBar().hide();
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
    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();

        switch (eventaction) {
              case MotionEvent.ACTION_UP:
                  if(getActionBar().isShowing())
                  {
                      getActionBar().hide();
                  }
                  else {
                      getActionBar().show();
                  }
                break;
        }

        // tell the system that we handled the event and no further processing is required
        return true;
    }

}
