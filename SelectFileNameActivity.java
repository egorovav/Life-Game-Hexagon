package com.gmail.egorovsonalexey.hexlifegame;

import android.content.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

//import com.google.android.gms.ads.*;

public class SelectFileNameActivity extends AppCompatActivity {

    static String FILE_NAME_KEY = "file_name";

    private EditText mFileNameEdit;
    //private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file_name);

        //mAdView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);

        SharedPreferences preferences = getSharedPreferences(SettingsActivity.SETTINGS_FILE_NAME, Context.MODE_PRIVATE);

        mFileNameEdit = (EditText)findViewById(R.id.file_name);
        mFileNameEdit.setText(preferences.getString(FILE_NAME_KEY, ""));

        Button cancelButton = (Button)findViewById(R.id.select_file_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        Button okButton = (Button)findViewById(R.id.select_file_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = mFileNameEdit.getText().toString();
                if (Helper.checkFileName(fileName) != null) {
                    SharedPreferences preferences = getSharedPreferences(SettingsActivity.SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(FILE_NAME_KEY, mFileNameEdit.getText().toString());
                    editor.commit();
                    setResult(RESULT_OK);
                    finish();
                    return;
                }
                Helper.showMessage(v.getContext(), v.getContext().getString(
                        R.string.supported_files, Helper.joinStrings(Helper.SUPPORTED_FILE_EXTENSIONS, ";")));
            }
        });
    }
}
