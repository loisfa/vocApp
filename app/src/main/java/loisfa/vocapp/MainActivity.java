package loisfa.vocapp;

import android.content.Context;
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

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private Button buttonNextWord;
    private EditText editText;
    private TextView yourTextView;
    private TextView toGuessView;
    private String rawTextLatinToCyrilSingleLetter;
    private String rawTextLatinToCyrilMultipleLetters;

    private Context context;
    private String FILENAME = "textFile.txt";
    private String wholeTextFile = "";
    WordAndTranslations word;
    DicoWordsAndTranslations dico;

    //private TextView debugView;
    //private String debugString;

    HashMap<String, String> latinToCyrilMapSingleLetter = new HashMap<String, String>();
    HashMap<String, String> latinToCyrilMapMultipleLetters = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initLatinToCyrilMaps();

        buttonNextWord = (Button) findViewById(R.id.button_next_word);
        editText = (EditText)findViewById(R.id.edit_message);
        //debugView = (TextView)findViewById(R.id.your_debug_view);
        //debugString = "";
        yourTextView = (TextView)findViewById(R.id.your_text_view);
        toGuessView = (TextView)findViewById(R.id.guess_text_view);
        context = getApplicationContext();
        try {
            TxtFileReader txtFileReader = new TxtFileReader(FILENAME, context);
            wholeTextFile = txtFileReader.getRawText();
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
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if (s.length()!=0) {
                    yourTextView.setText(translateLatinToCyrilSequence(s));
                    yourTextView.setTextColor(Color.BLACK);
                    checksEqualsWordTranslation(translateLatinToCyrilSequence(s));
                }

            }
        });

        buttonNextWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameNextWord(false);
            }
        });
    }

    private void initGame() {
        dico = new DicoWordsAndTranslations(wholeTextFile);
        gameNextWord(false);
    }

    private void gameNextWord(boolean hasWon) {
        if (hasWon) {
            yourTextView.setTextColor(Color.rgb(0, 255, 0));
        } else {
            yourTextView.setTextColor(Color.rgb(255, 0, 0));
        }
        word = dico.getAleaWordFraToRus();
        toGuessView.setText(word.getWord() + " -> " + word.getTranslations());
        editText.setText("");
    }


    private void checksEqualsWordTranslation(CharSequence typedWord) {
        String stringTypedWord = typedWord.toString();

        if (word.getTranslations().contains(stringTypedWord) ||
                word.getTranslations().contains(stringTypedWord+" ") ) {
            gameNextWord(true);
        }
    }

    private void initLatinToCyrilMaps() {
        initLatinToCyrilSingleLetterMap();
        initLatinToCyrilMultipleLettersMap();
    }

    private void initLatinToCyrilSingleLetterMap() {
        this.rawTextLatinToCyrilSingleLetter = getString(R.string.latinToCyrilSingleLetter);
        String mystring = "";
        String[] latinToCyrilCouples = this.rawTextLatinToCyrilSingleLetter.split(",");
        for (int i=0; i<latinToCyrilCouples.length; i++) {
            String[] latinToCyrilCouple = latinToCyrilCouples[i].split(">");
            this.latinToCyrilMapSingleLetter.put(latinToCyrilCouple[0], latinToCyrilCouple[1]);
        }
    }


    private void initLatinToCyrilMultipleLettersMap() {
        this.rawTextLatinToCyrilMultipleLetters = getString(R.string.latinToCyrilMutlipleLetters);
        String mystring = "";
        String[] latinToCyrilCouples = this.rawTextLatinToCyrilMultipleLetters.split(",");
        for (int i=0; i<latinToCyrilCouples.length; i++) {
            String[] latinToCyrilCouple = latinToCyrilCouples[i].split(">");
            this.latinToCyrilMapMultipleLetters.put(latinToCyrilCouple[0], latinToCyrilCouple[1]);
        }
    }


    private CharSequence translateLatinToCyrilSequence(CharSequence latinCharSequence) {

        CharSequence newLatinCharSequence = translateLatinToCyrilMultipleLetters(latinCharSequence);
        CharSequence finalLatinCharSequence = translateLatinToCyrilSingleLetter(newLatinCharSequence);


        return finalLatinCharSequence;
    }

    private CharSequence translateLatinToCyrilMultipleLetters(CharSequence latinCharSequence) {

        String latinString = (String) latinCharSequence.toString();
        String newLatinString = latinString;

        int maxLengthLatinMultipleLetters = 4; // could be fixed automatically
        int actualMaxLength;

        for (int i=0; i<latinString.length(); i++) {
            actualMaxLength = Math.min(maxLengthLatinMultipleLetters, latinString.length()-i);
            String maxLengthString = latinString.substring(i,i+actualMaxLength);

            for(int chunckSize=1; chunckSize<=actualMaxLength; chunckSize++) {
                String chunck = maxLengthString.substring(0, chunckSize);

                if (latinToCyrilMapMultipleLetters.containsKey(chunck)) {

                    newLatinString = newLatinString.replace(chunck, latinToCyrilMapMultipleLetters.get(chunck));
                }
            }
        }

        return (CharSequence) newLatinString;
    }


    private CharSequence translateLatinToCyrilSingleLetter(CharSequence latinCharSequence) {
        String cyrilCharSequence = "";
        char cyrilChar;

        for (char latinChar: (latinCharSequence.toString()).toCharArray()) {
            cyrilChar = latinToCyrilChar(latinChar);
            cyrilCharSequence += String.valueOf(cyrilChar);
        }

        return (CharSequence) cyrilCharSequence;
    }

    private char latinToCyrilChar(char latinChar) {
        char cyrilChar;
        if (latinToCyrilMapSingleLetter.get(String.valueOf(latinChar)) != null) {
            cyrilChar = latinToCyrilMapSingleLetter.get(String.valueOf(latinChar)).charAt(0);
        } else {
            cyrilChar = latinChar;
        }

        return cyrilChar;
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
