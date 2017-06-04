package loisfa.vocapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

import loisfa.vocapp.R;

public class MenuActivity extends AppCompatActivity {

   // ImageButton fraLangImageButton;
    //ImageButton rusLangImageButton;
    LinearLayout fraToRusLayout;
    LinearLayout rusToFraLayout;


    public static final String EXTRA_LANGUAGE= "loisfa.vocapp.LANGUAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        //fraLangImageButton = (ImageButton) findViewById(R.id.button_fra);
        //rusLangImageButton = (ImageButton) findViewById(R.id.button_rus);
        fraToRusLayout = (LinearLayout) findViewById(R.id.linearLayout_fraToRus);
        rusToFraLayout = (LinearLayout) findViewById(R.id.linearLayout_rusToFra);

        fraToRusLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                launchMainActivity("fra", "rus");
            }
        });

        rusToFraLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                launchMainActivity("rus", "fra");
            }
        });

    }

    private void launchMainActivity(String langBeg, String langEnd) {
        Log.d("mytag", "in launchMainActivity()");
        Intent intent = new Intent(this, MainActivity.class);
        String languages = langBeg + "->" + langEnd;
        intent.putExtra(EXTRA_LANGUAGE, languages);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
