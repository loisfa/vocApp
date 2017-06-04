package loisfa.vocapp;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

import loisfa.vocapp.R;

public class MenuActivity extends AppCompatActivity {

    ImageButton fraLangImageButton;
    ImageButton rusLangImageButton;
    ArrayList<ImageButton> listImageButtons;
    ImageButton buttonPressed;
    ImageButton buttonReleased;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        fraLangImageButton = (ImageButton) findViewById(R.id.button_fra);
        listImageButtons.add(fraLangImageButton);
        rusLangImageButton = (ImageButton) findViewById(R.id.button_rus);
        listImageButtons.add(rusLangImageButton);

        for (final ImageButton langButton:listImageButtons) {
            langButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        // Pressed
                        buttonPressed = langButton;
                        langButton.setImageDrawable();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        // Released
                        buttonReleased = langButton;
                        checkLangForActivity();
                    }
                    return true;
                }
            });
        }
    }

    private void checkLangForActivity() {
        if (buttonPressed == fraLangImageButton && buttonReleased == rusLangImageButton) {

        } else if (buttonPressed == rusLangImageButton && buttonReleased == fraLangImageButton) {

        }

        buttonPressed = null;
        buttonReleased = null;
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
