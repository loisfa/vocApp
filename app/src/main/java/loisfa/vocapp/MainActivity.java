package loisfa.vocapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.CheckedInputStream;

import loisfa.vocapp.translations.DicoWordsAndTranslations;
import loisfa.vocapp.translations.WordAndTranslations;


public class MainActivity extends AppCompatActivity {

    private Button nextWordButton;
    private EditText editText;
    private TextView resultWordTextView;
    private TextView toGuessWordTextView;
    private ListView listViewPreviousWords;
    private GridView gridViewVocThemes;
    private Context context;
    private Button expandingButton;

    private String FILENAME_DICO_FRA_TO_RUSSIAN = "vocFraToRus.txt";
    private String PATH_TO_VOC_FOLDER = "vocFraToRus";
    private ArrayList<String> FILENAMES_VOC_FRA_TO_RUSSIAN;
    private String rawTextForDico;
    //private DicoWordsAndTranslations dico;
    private WordAndTranslations word;
    private LatinToCyrilConverter converter;
    private GameDico gameDico;
    private boolean isDisplayThemes;

    private String languages;
    private boolean cyrilToLatinTextEntry;

    private ArrayList<String> historyWords;
    private ArrayAdapter<String> listAdapter;

    private List<HashMap<String, String>> vocThemes;
    private HashMap<String, Boolean> vocThemesIsActive;
    private SimpleAdapter gridAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nextWordButton = (Button) findViewById(R.id.button_next_word);
        editText = (EditText)findViewById(R.id.edit_message);
        resultWordTextView = (TextView)findViewById(R.id.result_text_view);
        toGuessWordTextView = (TextView)findViewById(R.id.guess_text_view);
        listViewPreviousWords = (ListView)findViewById(R.id.list_view_previous_words);
        expandingButton = (Button)findViewById(R.id.expanding_button);
        context = getApplicationContext();

        isDisplayThemes = true;
        expandingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button b = (Button) view.findViewById(R.id.expanding_button);
                b.setText(isDisplayThemes?"HIDE THEMES":"SHOW THEMES");
                isDisplayThemes = !isDisplayThemes;
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_grid_view);
                if (isDisplayThemes == true) {
                    layout.setVisibility(View.GONE);
                } else {
                    layout.setVisibility(View.VISIBLE);
                }
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        gridViewVocThemes = (GridView)findViewById(R.id.grid_view);
        Log.d("mytag-grid", gridViewVocThemes.toString());

        try {
            final String[] listAssets = getResources().getAssets().list(PATH_TO_VOC_FOLDER);
            FILENAMES_VOC_FRA_TO_RUSSIAN = new ArrayList<String>();
            vocThemes = new ArrayList<HashMap<String, String>>();
            vocThemesIsActive = new HashMap<String, Boolean>();
            String vocThemeName;
            for (String asset:listAssets) {
                FILENAMES_VOC_FRA_TO_RUSSIAN.add(asset);
                HashMap item = new HashMap();
                vocThemeName = asset.split(".csv")[0];
                item.put("name", vocThemeName);
                vocThemes.add(item);


                HashMap itemIsActive = new HashMap();
                vocThemesIsActive.put(vocThemeName, false);
            }
            String[] from = new String[]{"name", "name1", "isActive"};
            int[] id_numbers = new int[]{R.id.checkbox_voc_theme};
            gridAdapter = new SimpleAdapter(this,
                    vocThemes, R.layout.item_voc_theme, from, id_numbers) {
                @Override
                public View getView (int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox_voc_theme);
                    String theme = (String) cb.getText();
                    cb.setChecked(vocThemesIsActive.get(theme));
                    cb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckBox checkBox = (CheckBox) v;
                            boolean isActive = checkBox.isChecked();
                            String theme = (String) checkBox.getText();
                            Log.d("mytag-theme-isactive", "theme: " + theme + "; isactive: " + isActive);
                            gameDico.setActiveTheme(theme, isActive);
                            vocThemesIsActive.put(theme, isActive);
                        }
                    });
                    return view;
                }
            };
            //Log.d("mytag-grid", gridAdapter.toString());
            gridViewVocThemes.setAdapter(gridAdapter);

        } catch (IOException io) {
            Log.d("mytag", io.toString());
        }


        historyWords = new ArrayList<String>();
        listAdapter = new ArrayAdapter<String>(this,
                R.layout.listview_previous_words, historyWords) {
            @Override
            public View getView ( int position, View convertView, ViewGroup parent){

                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(R.id.textview_voc_theme);
                String rawText = (String) textView.getText();
                String result = rawText.split(":")[0];
                String text = rawText.split(":")[1];

                if (result.equals("WON")) {
                    textView.setTextColor(Color.rgb(0,200,50));
                    textView.setText(text);
                } else if (result.equals("ERR"))  {
                    textView.setTextColor(Color.RED);
                    textView.setText(text);
                }
                if (position==historyWords.size()-1) {
                    textView.setAlpha(1f);
                } else {
                    textView.setAlpha(0.4f);
                }

                return view;
            }
        };
        listAdapter.notifyDataSetChanged();
        listViewPreviousWords.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

        Intent intent = getIntent();
        this.languages = intent.getStringExtra(MenuActivity.EXTRA_LANGUAGE);
        initLanguageTextEntry(this.languages);
/*
        try {
            TxtFileReader txtFileReader = new TxtFileReader(FILENAME_DICO_FRA_TO_RUSSIAN, context);
            rawTextForDico = txtFileReader.getRawText();
        } catch (Exception e) {
            editText.setText("did not load the file properly not work");
        }
*/


        initGame();
        //Log.d("mytag", "trying to log");

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

                //Log.d("mytag", "charSequence: " + charSequence);
                if (charSequence.toString().indexOf('\n') != -1) {
                    gameNextWord(false);
                    return;
                }

                CharSequence text;
                if (cyrilToLatinTextEntry) {
                    //Log.d("mytag", "charSequence: " + charSequence);
                    //Log.d("mytag", "converter: " + converter);
                    text = converter.translateLatinToCyrilSequence(charSequence);
                } else {
                    text = charSequence;
                }
                resultWordTextView.setText(text);
                checksEqualsWordTranslation(text);
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

        if ((languages.split("->")[1]).equals("rus") ) {

            cyrilToLatinTextEntry = true;
            try {
                converter = new LatinToCyrilConverter(context);
            } catch (Exception e) {
                Log.d("mytag-Exception", e.toString());
            }
        } else {
            cyrilToLatinTextEntry = false;
        }
    }

    private void initGame() {
        //dico = new DicoWordsAndTranslations(rawTextForDico);
        //GameDico gameDico = new GameDico();
        //dico = (new DicoBuilder(FILENAME_DICO_FRA_TO_RUSSIAN, this.context)).getDico();

        ArrayList<DicoWordsAndTranslations> listDicos = new ArrayList<>();
        DicoWordsAndTranslations dico;
        //Log.d("mytag-FILENAMES_VOC", FILENAMES_VOC_FRA_TO_RUSSIAN.toString());
        for (String filename:FILENAMES_VOC_FRA_TO_RUSSIAN) {
            dico = (new DicoBuilder(PATH_TO_VOC_FOLDER, filename, this.context)).getDico();
            //Log.d("tag-aleaWord", dico.getAleaWordFromTo("fra->rus").getWord());
            listDicos.add(dico);
        }

        gameDico = new GameDico(this.languages, listDicos);
        generateWord();
    }


    private class DicoBuilder {
        private String theme;
        private String path_to_folder;
        private String filename;
        private String rawText;
        private DicoWordsAndTranslations dico;
        private Context context;

        private DicoBuilder(String path_to_folder, String filename, Context context) {
            this.path_to_folder = path_to_folder;
            this.filename = filename;
            this.context = context;
            setTheme();
            try {
                setRawText();
                setDico();
            } catch (Exception e) {
                Log.d("mytag-Exception", e.toString());
            }
        }

        private DicoWordsAndTranslations getDico() {
            return this.dico;
        }

        private void setTheme() {
            this.theme = this.filename.split(".csv")[0];
            //Log.d("mytag-theme", this.theme);

        }

        private void setRawText() throws Exception {
            TxtFileReader txtFileReader = new TxtFileReader(this.path_to_folder+"/"+this.filename, this.context);
            //Log.d("mytag-txtFileReader", "here");
            this.rawText = txtFileReader.getRawText();
            //Log.d("mytag-rawText", this.rawText);

        }

        private void setDico() {
            this.dico = new DicoWordsAndTranslations(this.theme, this.rawText);
            //Log.d("mytag-dico", this.dico.toString());
        }
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
        word = gameDico.getAleaWord();
        toGuessWordTextView.setText(word.getWord());
        editText.setText("");
    }

    private boolean checksEqualsWordTranslation(CharSequence typedWord) {
        String stringTypedWord = typedWord.toString();

        if (word.getTranslations().contains(stringTypedWord) ||
                word.getTranslations().contains(stringTypedWord+" ") ) {
            gameNextWord(true);
            return true;
        } else {
            return false;
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
        //return super.onSupportNavigateUp();
    }
}
