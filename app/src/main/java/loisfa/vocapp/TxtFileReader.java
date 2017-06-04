package loisfa.vocapp;


import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

/**
 * Created by tom on 13/05/17.
 */
public class TxtFileReader {

    private FileReader fileReader;
    private BufferedReader bufferedReader;
    private String rawText = "";
    private Context context;

    public TxtFileReader(String fileName, Context context) throws Exception {
        initPrivateLocals(fileName, context);
        readTextFile();
    }

    private void initPrivateLocals(String fileName, Context context)  {
        this.context = context;

        try {
            //this.fileReader = new FileReader(fileName);
            this.bufferedReader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(fileName)));
            Log.d("mytag", "construction ok");
        } catch (Exception e) {

            Log.d("mytag", "construction not ok");
        }
    }

    private void readTextFile() throws  Exception {
        String line = "";
        line = bufferedReader.readLine();
        while (line != null) {
            rawText += line + '\n';
            line = bufferedReader.readLine();
        }

        //fileReader.close();
    }

    public String getRawText() {
        return this.rawText;//return rawText;
    }
}

