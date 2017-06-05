package loisfa.vocapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import loisfa.vocapp.translations.DicoWordsAndTranslations;
import loisfa.vocapp.translations.WordAndTranslations;

public class MainActivity extends AppCompatActivity {

    private Button nextWordButton;
    private EditText editText;
    private TextView resultWordTextView;
    private TextView toGuessWordTextView;


    private Context context;

    private String FILENAME_DICO_FRA_TO_RUSSIAN = "vocFraToRus.txt";
    private String dicoRawText = null;
    DicoWordsAndTranslations dico;
    WordAndTranslations word;

    String languages;
    private boolean cyrilToLatinTextEntry;

    LatinToCyrilConverter converter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nextWordButton = (Button) findViewById(R.id.button_next_word);
        editText = (EditText)findViewById(R.id.edit_message);
        resultWordTextView = (TextView)findViewById(R.id.your_text_view);
        toGuessWordTextView = (TextView)findViewById(R.id.guess_text_view);
        context = getApplicationContext();


        Intent intent = getIntent();
        this.languages = intent.getStringExtra(MenuActivity.EXTRA_LANGUAGE);
        initLanguageTextEntry(this.languages);

        try {
            TxtFileReader txtFileReader = new TxtFileReader(FILENAME_DICO_FRA_TO_RUSSIAN, context);
            dicoRawText = txtFileReader.getRawText();
        } catch (Exception e) {
            editText.setText("did not load the file properly not work");
        }

        initGame();
        Log.d("mytag", "trying to log");

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start,
                                      int before, int count) {

                if (charSequence.length()!=0) {
                    CharSequence text;
                    if (cyrilToLatinTextEntry) {
                        Log.d("mytag", "charSequence: " + charSequence);
                        Log.d("mytag", "converter: " + converter);
                        text = converter.translateLatinToCyrilSequence(charSequence);
                    } else {
                        text = charSequence;
                    }
                    resultWordTextView.setText(text);
                    resultWordTextView.setTextColor(Color.BLACK);
                    checksEqualsWordTranslation(text);
                }

            }
        });

        nextWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameNextWord(false);
            }
        });
    }

    private void initLanguageTextEntry(String languages) {
        Log.d("mytag", "languages: "+languages);

        if ((languages.split("->")[1]).equals("rus") ) {
            Log.d("mytag", "inif");

            cyrilToLatinTextEntry = true;
            try {
                converter = new LatinToCyrilConverter(context);
            } catch (Exception e) {
                Log.d("mytag", e.toString());
            }
        } else {
            Log.d("mytag", "inelse");
            cyrilToLatinTextEntry = false;
        }
    }

    private void initGame() {
        dico = new DicoWordsAndTranslations(dicoRawText);
        gameNextWord(false);
    }

    private void gameNextWord(boolean hasWon) {
        if (hasWon) {
            resultWordTextView.setTextColor(Color.rgb(0, 255, 0));
            resultWordTextView.setText(word.getWord() + " -> " + word.getTranslations());
        } else {
            resultWordTextView.setTextColor(Color.rgb(255, 0, 0));
        }
        word = dico.getAleaWordFromTo(this.languages);
        toGuessWordTextView.setText(word.getWord() + " -> " + word.getTranslations());
        editText.setText("");
    }

    private void checksEqualsWordTranslation(CharSequence typedWord) {
        String stringTypedWord = typedWord.toString();

        if (word.getTranslations().contains(stringTypedWord) ||
                word.getTranslations().contains(stringTypedWord+" ") ) {
            gameNextWord(true);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
