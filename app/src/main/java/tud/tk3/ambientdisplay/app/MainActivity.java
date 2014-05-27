package tud.tk3.ambientdisplay.app;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.List;

import tud.tk3.ambientdisplay.app.communication.AmbientDisplay;
import tud.tk3.ambientdisplay.app.communication.Communicator;
import tud.tk3.ambientdisplay.app.communication.DisplayController;
import tud.tk3.ambientdisplay.app.communication.DumbDisplayController;
import tud.tk3.ambientdisplay.app.display.ImageSection;


public class MainActivity extends ActionBarActivity implements AmbientDisplay{
    private ImageView imageView;
    private Communicator comm;
    private DisplayController displayController;
    private Bitmap image;
    private ImageSection mySection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);

        Bitmap bitmap = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);
        imageView.setImageBitmap(bitmap);

        // Initialize the displayed section of an image as the whole image
        mySection = new ImageSection(0, 0, 1, 1);

        displayController = new DumbDisplayController(this);
        comm = new Communicator(this, displayController);
        comm.publishScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        comm.disconnect();
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
                    if(selectedImage != null) {
                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        if(cursor != null) {
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
                }
        }

    }

    @Override
    public void displayImage(Bitmap bitmap) {
        //this.image= bitmap;
        //getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        int resX = bitmap.getWidth();
        int resY = bitmap.getHeight();

        int posX = (int) (resX*mySection.posX);
        int posY = (int) (resY*mySection.posY);
        int offsetX= (int) (resX*mySection.offsetX);
        int offsetY= (int) ((resY*mySection.offsetY) * (((double)resX)/((double)resY)));
        //Log.e("Logging..",""+resX+":"+resY+" "+mySection.posX+":"+mySection.offsetX);
        //Log.e("Logging..",(resX/resY)+"|"+offsetX+":"+offsetY+" "+mySection.posY+":"+mySection.offsetY);
        if(posX+offsetX > resX)
        {
            offsetX = resX - posX;
        }
        if(posY+offsetY > resY)
        {
            offsetY = resY - posY;
        }

        this.image = Bitmap.createBitmap(bitmap, posX ,posY ,offsetX ,offsetY );

        runOnUiThread(new Thread() {
            public void run() {
                ActionBar actionBar = getActionBar();
                if(actionBar != null) {
                    getActionBar().hide();
                }
                imageView.setImageBitmap(image);
            }

            ;
        });

    }

    @Override
    public void topologyChange(List<ImageSection> sections) {
        final Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawSections(canvas, sections);

        runOnUiThread(new Thread() {
            public void run() {
                ActionBar actionBar = getActionBar();
                if(actionBar != null) {
                    getActionBar().hide();
                }
                imageView.setImageBitmap(bitmap);
            }

            ;
        });
    }

    private void drawSections(Canvas canvas, List<ImageSection> sections){
        int width = canvas.getWidth();
        int height = canvas.getHeight();
//        Paint bluePaint = new Paint();
//        bluePaint.setARGB(255, 0, 0, 200);
//        canvas.drawPaint(bluePaint);
        Paint grayPaint = new Paint();
        grayPaint.setARGB(255, 128, 128, 128);
        for(ImageSection section : sections){
            float left = (float) (width * section.posX) + 2;
            float top = (float) (height * section.posY) + 2;
            float right = left + (float) (width * section.offsetX) -4;
            float bottom = top + (float) (height * section.offsetY) -4;
            canvas.save();
            canvas.clipRect(left, top, right, bottom);
            canvas.drawPaint(grayPaint);
            canvas.restore();
        }

        if(mySection != null){
            Paint orangePaint = new Paint();
            orangePaint.setARGB(255, 255, 140, 0);
            float left = (float) (width * mySection.posX) + 2;
            float top = (float) (height * mySection.posY) + 2;
            float right = left + (float) (width * mySection.offsetX) -4;
            float bottom = top + (float) (height * mySection.offsetY) -4;
            canvas.save();
            canvas.clipRect(left, top, right, bottom);
            canvas.drawPaint(orangePaint);
            canvas.restore();

        }


    }

    @Override
    public void imageSectionChange(ImageSection section) {
        mySection = section;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        switch (eventAction) {
              case MotionEvent.ACTION_UP:
                  ActionBar actionBar = getActionBar();
                  if(actionBar != null){
                      if(actionBar.isShowing())
                      {
                          actionBar.hide();
                      }
                      else {
                          actionBar.show();
                      }
                  }
                break;
        }
        // tell the system that we handled the event and no further processing is required
        return true;
    }

}
