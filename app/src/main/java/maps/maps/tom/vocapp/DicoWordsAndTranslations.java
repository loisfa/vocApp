package maps.maps.tom.vocapp;


import android.os.Debug;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tom on 21/05/17.
 */

// no abstraction about languages : only "Fra to Rus"
// should be changed to "lang1 to lang2"
public class DicoWordsAndTranslations {

    private ListWordsAndTranslations listWordsFraToRus;
    private ListWordsAndTranslations listWordsRusToFra;


    public DicoWordsAndTranslations(String rawStringFraToRus) {
        listWordsFraToRus = new ListWordsAndTranslations();
        listWordsRusToFra = new ListWordsAndTranslations();
        buildArrayLists(rawStringFraToRus);
    }

    private void buildArrayLists(String rawStringFraToRus) {
        String[] lines = rawStringFraToRus.split("\n");
        String[] chunck;
        for (String line : lines) {
            chunck = line.split(";");
            Log.d("mylog", "chunk: " + chunck[0]);
            for (int i = 1; i < chunck.length; i++) {
                addTwoWaysDico(chunck[0].toLowerCase(), chunck[i].toLowerCase());
            }
        }
    }

    private void addTwoWaysDico(String word, String translation) {

        WordAndTranslations wordAndTrans = new WordAndTranslations(word, translation);
        listWordsFraToRus.addWord(wordAndTrans);

        WordAndTranslations transAndWord = new WordAndTranslations(translation, word);
        listWordsRusToFra.addWord(transAndWord);
    }

    public WordAndTranslations getAleaWordFraToRus() {
        double alea = Math.random();
        WordAndTranslations word = listWordsFraToRus.getWord((int) Math.floor(alea * listWordsFraToRus.size()));
        return word;
    }


    public WordAndTranslations getAleaWordRusToFra() {
        double alea = Math.random();
        WordAndTranslations word = listWordsRusToFra.getWord((int) Math.floor(alea * listWordsRusToFra.size()));
        return word;
    }
}
