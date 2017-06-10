package loisfa.vocapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import loisfa.vocapp.translations.DicoWordsAndTranslations;
import loisfa.vocapp.translations.WordAndTranslations;

public class MainActivity extends AppCompatActivity {

    private Button nextWordButton;
    private EditText editText;
    private TextView resultWordTextView;
    private TextView toGuessWordTextView;
    private ListView listViewPreviousWords;
    private Context context;

    private String FILENAME_DICO_FRA_TO_RUSSIAN = "vocFraToRus.txt";
    private String dicoRawText = null;
    private DicoWordsAndTranslations dico;
    private WordAndTranslations word;
    private LatinToCyrilConverter converter;

    private String languages;
    private boolean cyrilToLatinTextEntry;

    private ArrayList<String> historyWords;
    private ArrayAdapter<String> listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nextWordButton = (Button) findViewById(R.id.button_next_word);
        editText = (EditText)findViewById(R.id.edit_message);
        resultWordTextView = (TextView)findViewById(R.id.result_text_view);
        toGuessWordTextView = (TextView)findViewById(R.id.guess_text_view);
        listViewPreviousWords = (ListView)findViewById(R.id.list_view_previous_words);
        context = getApplicationContext();
        historyWords = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, historyWords) {
            @Override
            public View getView ( int position, View convertView, ViewGroup parent){

                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextSize(14f);
                textView.setPadding(0, 0, 0, 0);
                textView.setGravity(Gravity.CENTER);
                String rawText = (String) textView.getText();
                String result = rawText.split(":")[0];
                String text = rawText.split(":")[1];

                if (result.equals("WON")) {
                    textView.setTextColor(Color.GREEN);
                    textView.setText(text);
                } else if (result.equals("ERR"))  {
                    textView.setTextColor(Color.RED);
                    textView.setText(text);
                }
                if (position==historyWords.size()-1) {
                    textView.setAlpha(1f);
                } else {
                    textView.setAlpha(0.3f);
                }

                return view;
            }
        };
        historyWords.add("WON: essai0");
        listAdapter.notifyDataSetChanged();
        listViewPreviousWords.setAdapter(listAdapter);
        historyWords.add("ERR: essai1");
        listAdapter.notifyDataSetChanged();

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

                Log.d("mytag", "charSequence: " + charSequence);
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
                    checksEqualsWordTranslation(text);
                }

            }
        });

        nextWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameNextWord(false);
                resultWordTextView.setText("");
            }
        });
    }

    private void initLanguageTextEntry(String languages) {

        if ((languages.split("->")[1]).equals("rus") ) {

            cyrilToLatinTextEntry = true;
            try {
                converter = new LatinToCyrilConverter(context);
            } catch (Exception e) {
                Log.d("mytag", e.toString());
            }
        } else {
            cyrilToLatinTextEntry = false;
        }
    }

    private void initGame() {
        dico = new DicoWordsAndTranslations(dicoRawText);
        generateWord();
    }

    private void gameNextWord(boolean hasWon) {
        if (hasWon) {
            //resultWordTextView.setTextColor(Color.rgb(0, 255, 0));
            //resultWordTextView.setText(word.getWord() + " -> " + word.getTranslations());
            historyWords.add("WON: " + word.getWord() + " -> " + word.getStringTranslations());
            listAdapter.notifyDataSetChanged();

        } else {
            //resultWordTextView.setTextColor(Color.rgb(255, 0, 0));
            historyWords.add("ERR: " + word.getWord() + " -> " + word.getStringTranslations());
            listAdapter.notifyDataSetChanged();
        }
        generateWord();
    }

    private void generateWord() {
        word = dico.getAleaWordFromTo(this.languages);
        toGuessWordTextView.setText(word.getWord());
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
