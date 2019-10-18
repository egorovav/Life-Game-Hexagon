package com.gmail.egorovsonalexey.hexlifegame;

import android.app.AlertDialog;
import android.content.Context;

import java.util.*;


public class Helper {

    public static final String[] SUPPORTED_FILE_EXTENSIONS = new String[] { ".rle", ".lif", ".life" };

    static void showMessage(Context context, String message) {
        if (message == null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setPositiveButton("OK", null);

        builder.setMessage(message);
        builder.create().show();
    }

    static int[] parseIndicators(String indicators) {
        int[] result = new int[indicators.length()];
        for(int i = 0; i < indicators.length(); i++) {
            int x = Integer.parseInt(indicators.substring(i, i + 1));
            result[i] = x;
        }
        return result;
    }

    static String indicatorsToString(ArrayList<Integer> indicators) {
        String str = "";
        for(Integer i : indicators) {
            str += i.toString();
        }
        return str;
    }

    static String checkFileName(String fileName) {
        for(String extension : SUPPORTED_FILE_EXTENSIONS) {
            if(fileName.endsWith(extension)) {
                return extension;
            }
        }
        return null;
    }

    static String joinStrings(String[] strings, String separator) {
        String result = "";
        for(int i = 0; i < strings.length; i++) {
            result += strings[i];
            if(i != strings.length - 1) {
                result += separator;
            }
        }
        return result;
    }
}
