package tud.tk3.ambientdisplay.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_launcher);
        drawable.setBounds(5, 5, 10, 10);
//        iv.setImageDrawable(drawable);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//        iv.setImageBitmap(bitmap);
        Bitmap bitmapCropped = Bitmap.createBitmap(bitmap, 0, 0, 50, 50);
        System.out.println("Cropped: " + bitmapCropped.getHeight() + " " + bitmapCropped.getWidth());
        iv.setImageBitmap(bitmapCropped);
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
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_gallery) openOptionsMenu();
        return super.onOptionsItemSelected(item);
    }
}
