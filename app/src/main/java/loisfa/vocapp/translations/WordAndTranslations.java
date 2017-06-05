package loisfa.vocapp.translations;


import java.util.ArrayList;

/**
 * Created by tom on 21/05/17.
 */
public class WordAndTranslations {

    private String word;
    private ArrayList<String> translations;

    public WordAndTranslations(String word) {
        translations = new ArrayList<>();
        this.word = word;
    }

    public WordAndTranslations(String word, String translation) {
        this.word = word;
        this.translations = new ArrayList<>();
        this.addTranslation(translation);
    }

    public WordAndTranslations(String word, ArrayList<String> translations) {
        this.word = word;
        this.translations = translations;
    }

    public void addTranslation(String translation) {
        this.translations.add(translation);
    }

    public void addTranslations(ArrayList<String> translations) {
        for(String translation: translations) {
            this.translations.add(translation);
        }
    }

    public ArrayList<String> getTranslations() {
        return this.translations;
    }

    public String getWord() {
        return this.word;
    }
}
