package maps.maps.tom.vocapp;

import java.util.ArrayList;

/**
 * Created by tom on 21/05/17.
 */
public class ListWordsAndTranslations {

    private ArrayList<WordAndTranslations> list;
    private ArrayList<String> wordsStringList;

    public ListWordsAndTranslations(ArrayList list) {
        this.list = list;
        updateWordsStringList();
    }

    public ListWordsAndTranslations() {
        this.list = new ArrayList<WordAndTranslations>();
        this.wordsStringList = new ArrayList<String>();
    }

    private void updateWordsStringList() {
        for(WordAndTranslations word:list) {
            wordsStringList.add(word.getWord());
        }
    }


    public void addWord(WordAndTranslations word) {
        // handles cases with already existing word
        if (wordsStringList.contains(word.getWord()) == false) {
            list.add(word);
            wordsStringList.add(word.getWord());
        } else {
            int indexExistingWord = wordsStringList.indexOf(word.getWord());
            WordAndTranslations existingWord  = list.get(indexExistingWord);
            existingWord.addTranslations(word.getTranslations());
        }
    }

    public void addWords(ArrayList<WordAndTranslations> words) {
        for(WordAndTranslations word:words) {
            this.addWord(word);
        }
    }

    public boolean containsWord(WordAndTranslations word) {
        if (wordsStringList.contains(word.getWord())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean containsWord(String word) {
        if (wordsStringList.contains(word)) {
            return true;
        } else {
            return false;
        }
    }

    public WordAndTranslations getWord(int x) {
        return list.get(x);
    }

    public int size() {
        return list.size();
    }
}
