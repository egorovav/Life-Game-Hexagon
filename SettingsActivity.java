package com.gmail.egorovsonalexey.hexlifegame;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.content.*;
import android.util.*;

//import com.google.android.gms.ads.*;

import java.util.*;

public class SettingsActivity extends AppCompatActivity {

    static final String SETTINGS_FILE_NAME = "life_game_settings";
    static final int HEXAGON_CELL = 0;
    static final int SQUARE_CELL = 1;
    private int MAX_HEXAGON_CELL_NEIGHBOURS = 6;
    private int MAX_SQUARE_CELL_NEIGHBOURS = 8;

    static String CELL_SHAPE_KEY = "cell_shape";
    static String SURVIVE_NEIGHBOURS_KEY = "survive_neighbour";
    static String BORN_NEIGHBOURS_KEY = "born_neighbours";
    static String MAX_AGE_KEY = "max_age";
    static String DELAY_KEY = "delay";
    static String RANDOM_CELLS_KEY = "random_cells";
    static String GAME_FIELD_WIDTH_KEY = "game_field_width";
    static String GAME_FIELD_HEIGHT_KEY = "game_field_height";

    private RadioButton mHexagonRadio;
    private RadioButton mSquareRadio;
    private EditText mMaxAgeEdit;
    private EditText mDelayEdit;
    private EditText mRandomCellsEdit;
    private ArrayList<CheckBox> mSurviveIndicators = new ArrayList<>();
    private ArrayList<CheckBox> mBornIndicators = new ArrayList<>();
    private LinearLayout mSurviveLayout;
    private LinearLayout mBornLayout;
    private EditText mWidthEdit;
    private EditText mHeightEdit;
    private TextView mRandomCellsError;
    //private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //mAdView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);

        mHexagonRadio = (RadioButton)findViewById(R.id.radio_hexagon_cell);
        mSquareRadio = (RadioButton)findViewById(R.id.radio_square_cell);
        mMaxAgeEdit = (EditText)findViewById(R.id.max_age);
        mDelayEdit = (EditText)findViewById(R.id.delay);
        mRandomCellsEdit = (EditText)findViewById(R.id.random_cells);
        mSurviveLayout = (LinearLayout)findViewById(R.id.survive_neighbours);
        mBornLayout = (LinearLayout)findViewById(R.id.born_neighbours);
        mWidthEdit = (EditText)findViewById(R.id.field_width);
        mHeightEdit = (EditText)findViewById(R.id.field_height);
        mRandomCellsError = (TextView)findViewById(R.id.random_cells_err);

        SharedPreferences preferences = getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
        if(preferences != null) {
            int cellShape = preferences.getInt(CELL_SHAPE_KEY, SQUARE_CELL);
                switch(cellShape) {
                    case HEXAGON_CELL: {
                        mHexagonRadio.setChecked(true);
                        setIndicators(MAX_HEXAGON_CELL_NEIGHBOURS + 1);
                        break;
                    }
                    case SQUARE_CELL: {
                        mSquareRadio.setChecked(true);
                        setIndicators(MAX_SQUARE_CELL_NEIGHBOURS + 1);
                        break;
                    }
                    default: {
                        mSquareRadio.setChecked(true);
                        setIndicators(MAX_SQUARE_CELL_NEIGHBOURS + 1);
                        break;
                    }
            }
            String surviveNeighbours = preferences.getString(SURVIVE_NEIGHBOURS_KEY, "");
            for(int i = 0; i < surviveNeighbours.length(); i++) {
                int x = Integer.parseInt(surviveNeighbours.substring(i, i + 1));
                if(x >= 0 && x < mSurviveIndicators.size()) {
                    mSurviveIndicators.get(x).setChecked(true);
                }
            }

            String bornNeighbours = preferences.getString(BORN_NEIGHBOURS_KEY, "");
            for(int i = 0; i < bornNeighbours.length(); i++) {
                int x = Integer.parseInt(bornNeighbours.substring(i, i + 1));
                if(i >= 0 && i < mBornIndicators.size()) {
                    mBornIndicators.get(x).setChecked(true);
                }
            }

            int max_age = preferences.getInt(MAX_AGE_KEY, 0);
            int delay = preferences.getInt(DELAY_KEY, 200);
            int random_cells = preferences.getInt(RANDOM_CELLS_KEY, 0);
            int width = preferences.getInt(GAME_FIELD_WIDTH_KEY, 100);
            int height = preferences.getInt(GAME_FIELD_HEIGHT_KEY, 100);

            mMaxAgeEdit.setText(Integer.toString(max_age));
            mDelayEdit.setText(Integer.toString(delay));
            mRandomCellsEdit.setText(Integer.toString(random_cells));
            mWidthEdit.setText(Integer.toString(width));
            mHeightEdit.setText(Integer.toString(height));

            mRandomCellsEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                 if(!hasFocus) {
                        try {
                            int random_cells = Integer.parseInt(mRandomCellsEdit.getText().toString());
                            if(random_cells < 0 || random_cells > 100) {
                                throw new NumberFormatException();
                            }
                        }
                        catch(NumberFormatException ex) {
                            String message = String.format("%s. %s",
                                    getString(R.string.incorrect_input),
                                    getString(R.string.number_required, 0, 100));
                            mRandomCellsError.setText(message);
                            return;
                        }
                    }
                    mRandomCellsError.setText("");
              }
            });
        }

        Button cancelButton = (Button)findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        Button okButton = (Button)findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                if(mHexagonRadio.isChecked()) {
                    editor.putInt(CELL_SHAPE_KEY, HEXAGON_CELL);
                }

                if(mSquareRadio.isChecked()) {
                    editor.putInt(CELL_SHAPE_KEY, SQUARE_CELL);
                }

                String surviveIndicators = "";
                for(int i = 0; i < mSurviveIndicators.size(); i++) {
                    if(mSurviveIndicators.get(i).isChecked()) {
                        surviveIndicators += Integer.toString(i);
                    }
                }
                editor.putString(SURVIVE_NEIGHBOURS_KEY, surviveIndicators);

                String bornIndicators = "";
                for(int i = 0; i < mBornIndicators.size(); i++) {
                    if(mBornIndicators.get(i).isChecked()) {
                        bornIndicators += Integer.toString(i);
                    }
                }
                editor.putString(BORN_NEIGHBOURS_KEY, bornIndicators);

                try {
                    int maxAge = Integer.parseInt(mMaxAgeEdit.getText().toString());
                    editor.putInt(MAX_AGE_KEY, maxAge);
                }
                catch(NumberFormatException ex) {
                    String message = String.format("%s '%s'. %s %s",
                            getString(R.string.incorrect_input),
                            getString(R.string.max_age),
                            getString(R.string.number_required, 0, 1000),
                            getString(R.string.zero_means_infinity));
                    Helper.showMessage(SettingsActivity.this, message);
                    return;
                }

                try {
                    int delay = Integer.parseInt(mDelayEdit.getText().toString());
                    editor.putInt(DELAY_KEY, delay);
                }
                catch(NumberFormatException ex) {
                    String message = String.format("%s '%s'. %s",
                            getString(R.string.incorrect_input),
                            getString(R.string.delay),
                            getString(R.string.number_required, 0, 1000));
                    Helper.showMessage(SettingsActivity.this, message);
                    return;
                }

                try {
                    int random_cells = Integer.parseInt(mRandomCellsEdit.getText().toString());
                    if(random_cells < 0 || random_cells > 100) {
                        throw new NumberFormatException();
                    }
                    editor.putInt(RANDOM_CELLS_KEY, random_cells);
                }
                catch(NumberFormatException ex) {
                    String message = String.format("%s '%s'. %s",
                            getString(R.string.incorrect_input),
                            getString(R.string.random_cells),
                            getString(R.string.number_required, 0, 100));
                    Helper.showMessage(SettingsActivity.this, message);
                    return;
                }

                try {
                    int width = Integer.parseInt(mWidthEdit.getText().toString());
                    if(width < 1 || width > 200) {
                        throw new NumberFormatException();
                    }
                    editor.putInt(GAME_FIELD_WIDTH_KEY, width);
                }
                catch (NumberFormatException ex) {
                    String message = String.format("%s '%s'. %s",
                            getString(R.string.incorrect_input),
                            getString(R.string.width),
                            getString(R.string.number_required, 1, 1000));
                }

                try {
                    int height = Integer.parseInt(mHeightEdit.getText().toString());
                    if(height < 1 || height > 200) {
                        throw new NumberFormatException();
                    }
                    editor.putInt(GAME_FIELD_HEIGHT_KEY, height);
                }
                catch (NumberFormatException ex) {
                    String message = String.format("%s '%s'. %s",
                            getString(R.string.incorrect_input),
                            getString(R.string.height),
                            getString(R.string.number_required, 1, 1000));
                }

                editor.commit();
                setResult(RESULT_OK);
                finish();
            }
        });

        Button conways = (Button)findViewById(R.id.conways_button);
        conways.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSquareRadio.setChecked(true);
                setIndicators(MAX_SQUARE_CELL_NEIGHBOURS + 1);
                mSurviveIndicators.get(2).setChecked(true);
                mSurviveIndicators.get(3).setChecked(true);
                mBornIndicators.get(3).setChecked(true);
                mMaxAgeEdit.setText("0");
            }
        });

        RadioButton hexRadio = (RadioButton)findViewById(R.id.radio_hexagon_cell);
        hexRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    setIndicators(MAX_HEXAGON_CELL_NEIGHBOURS + 1);
                }
            }
        });

        RadioButton squareRadio = (RadioButton)findViewById(R.id.radio_square_cell);
        squareRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                        setIndicators(MAX_SQUARE_CELL_NEIGHBOURS + 1);
                }
            }
        });
    }

    private void setIndicators(int count) {
        mSurviveLayout.removeAllViews();
        mBornLayout.removeAllViews();
        mSurviveIndicators.clear();
        mBornIndicators.clear();
        for(int i = 0; i < count; i++) {
            CheckBox ch = new CheckBox(this);
            ch.setText(Integer.toString(i));
            ch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            mSurviveLayout.addView(ch);
            mSurviveIndicators.add(ch);

            ch = new CheckBox(this);
            ch.setText(Integer.toString(i));
            ch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            mBornLayout.addView(ch);
            mBornIndicators.add(ch);
        }
    }
}
