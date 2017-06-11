package loisfa.vocapp;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import loisfa.vocapp.translations.DicoWordsAndTranslations;
import loisfa.vocapp.translations.WordAndTranslations;

/**
 * Created by lois on 11/06/17.
 */
import java.util.ArrayList;
import java.util.HashMap;


public class GameDico {
    private String languages;
    private HashMap<String, DicoWordsAndTranslations> listDicos;
    private ArrayList<Proba_Theme> probabilitySegmentsActiveThemes;
    private ArrayList<String> activeThemes;

    public GameDico(String languages, ArrayList<DicoWordsAndTranslations> dicos) {
        this.languages = languages;
        this.listDicos = new HashMap<>();
        this.activeThemes = new ArrayList<>();
        this.probabilitySegmentsActiveThemes = new ArrayList<>();
        for (DicoWordsAndTranslations dico: dicos) {
            this.listDicos.put(dico.getTheme(), dico);
            this.activeThemes.add(dico.getTheme());
        }
        computeProbabilitySegments();
    }
/*
    public void setActiveThemes(ArrayList<String> activeThemes) {
        this.activeThemes = activeThemes;
        computeProbabilitySegments();
    }
*/
    public void setActiveTheme(String theme, boolean isActive) {
        if (isActive == true) {
            this.activeThemes.add(theme);
        } else {
            this.activeThemes.remove(theme);
        }
        Log.d("tag-inSetActiveTheme", "");
        for (String str:activeThemes) {
            Log.d("mytag-activetheme", str);
        }
        computeProbabilitySegments();
    }


    private void computeProbabilitySegments() {
        int globalSize = 0;
        int[] probabilitySegments = new int[activeThemes.size()];
        probabilitySegmentsActiveThemes = new ArrayList<>();
        int i=0;

        for (String activeTheme: this.activeThemes) {
            Log.d("mytag-activeTheme", activeTheme);
            globalSize += listDicos.get(activeTheme).getSize(this.languages);
            this.probabilitySegmentsActiveThemes.add(new Proba_Theme((float) globalSize, activeTheme));
            i++;
        }
        for(Proba_Theme proba_theme:this.probabilitySegmentsActiveThemes) {
            proba_theme.proba = (float) proba_theme.proba / globalSize;
            Log.d("mytag-theme.proba", Float.toString(proba_theme.proba) );

        }
    }

    public WordAndTranslations getAleaWord() {
        if (activeThemes.size() == 0) {
            return (new WordAndTranslations("NO THEME", "NO THEME"));
        }

        WordAndTranslations aleaWord = null;
        float rand = (float) Math.random();
        int size = this.probabilitySegmentsActiveThemes.size();
        int i=0;
        Proba_Theme proba_theme;
        System.out.println("this.probabilitySegmentsActiveThemes");
        System.out.println(this.probabilitySegmentsActiveThemes);
        while(i<size) {
            proba_theme = this.probabilitySegmentsActiveThemes.get(i);

            if(rand<proba_theme.proba) {
                System.out.println("2 i:"+i);
                System.out.println("2 rand:"+rand);
                System.out.println("2 proba:"+proba_theme.proba);
                System.out.println("2 theme:"+proba_theme.theme);

                DicoWordsAndTranslations dico = listDicos.get(proba_theme.theme);
                System.out.println("dico theme:"+dico.getTheme());


                aleaWord = dico.getAleaWordFromTo(languages);
                System.out.println("2 aleaWord:"+aleaWord.getWord());

                break;
            }
            System.out.println("should not pass here afetr if");
            i++;
        }
        return aleaWord;
    }

    public void upateDico(DicoWordsAndTranslations dico) {
        listDicos.put(dico.getTheme(), dico);
    }
/*
    public void updateDicos(ArrayList<DicoWordsAndTranslations> dicos) {
        this.listDicos = new HashMap<>();
        for (DicoWordsAndTranslations dico: dicos) {
            listDicos.put(dico.getTheme(), dico);
        }
    }
*/
    private class Proba_Theme {
        public float proba;
        public String theme;
        public Proba_Theme(Float flt, String str) {
            this.proba = flt;
            this.theme = str;
        }
    }

}