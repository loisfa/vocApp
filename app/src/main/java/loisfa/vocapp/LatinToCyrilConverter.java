package loisfa.vocapp;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by lois on 05/06/17.
 */
public class LatinToCyrilConverter {

    private String rawTextLatinToCyrilSingleLetter;
    private String rawTextLatinToCyrilMultipleLetters;
    HashMap<String, String> latinToCyrilMapSingleLetter;
    HashMap<String, String> latinToCyrilMapMultipleLetters;

    private final String FILENAME_LATIN_TO_CYRIL_SINGLE = "latinToCyrilLetters/latinToCyrilSingleLetter.txt";
    private final String FILENAME_LATIN_TO_CYRIL_MULTIPLE = "latinToCyrilLetters/latinToCyrilMultipleLetters.txt";

    Context context;

    public LatinToCyrilConverter(Context context) throws Exception{
        this.context = context;
        initRawTextFromFile();
        initLatinToCyrilHashMaps();
    }

    private void initRawTextFromFile() throws Exception {
        TxtFileReader txtFileReaderSingleLetter = new TxtFileReader(FILENAME_LATIN_TO_CYRIL_SINGLE, this.context);
        this.rawTextLatinToCyrilSingleLetter = txtFileReaderSingleLetter.getRawText();
        TxtFileReader txtFileReaderMultipleLetters = new TxtFileReader(FILENAME_LATIN_TO_CYRIL_MULTIPLE, this.context);
        this.rawTextLatinToCyrilMultipleLetters = txtFileReaderMultipleLetters.getRawText();
    }

    private void initLatinToCyrilHashMaps() {
        initLatinToCyrilSingleLetterHashMap();
        initLatinToCyrilMultipleLettersHashMap();
    }

    private void initLatinToCyrilSingleLetterHashMap()  {
        latinToCyrilMapSingleLetter = new HashMap<String, String>();
        String mystring = "";
        String[] latinToCyrilCouples = this.rawTextLatinToCyrilSingleLetter.split(",");
        for (int i=0; i<latinToCyrilCouples.length; i++) {
            String[] latinToCyrilCouple = latinToCyrilCouples[i].split(">");
            this.latinToCyrilMapSingleLetter.put(latinToCyrilCouple[0], latinToCyrilCouple[1]);
        }
    }

    private void initLatinToCyrilMultipleLettersHashMap() {
        latinToCyrilMapMultipleLetters = new HashMap<String, String>();
        String mystring = "";
        String[] latinToCyrilCouples = this.rawTextLatinToCyrilMultipleLetters.split(",");
        for (int i=0; i<latinToCyrilCouples.length; i++) {
            String[] latinToCyrilCouple = latinToCyrilCouples[i].split(">");
            this.latinToCyrilMapMultipleLetters.put(latinToCyrilCouple[0], latinToCyrilCouple[1]);
        }
    }

    public CharSequence translateLatinToCyrilSequence(CharSequence latinCharSequence) {

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
}
