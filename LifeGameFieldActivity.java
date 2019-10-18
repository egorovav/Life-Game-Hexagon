package com.gmail.egorovsonalexey.hexlifegame;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.graphics.*;
import android.content.res.*;
//import com.google.android.gms.ads.*;

import java.util.*;
import java.io.*;

import com.gmail.egorovsonalexey.lifegame.core.*;

public class LifeGameFieldActivity extends AppCompatActivity {

    private final String GAME_POSITION_KEY = "game_position";
    private final String GAME_START_POSITION_KEY = "start_position";
    private final String STEP_COUNT_KEY = "step_count";
    private final String VIEWPORT_KEY = "viewport";
    private final String SCALE_KEY = "scale";
    private final String IS_RUNNING_KEY = "is_running";
    private final int LOAD_FILE_SELECT_CODE = 0;
    private final int REQUEST_PERMISSION_CODE = 1;
    private final int SAVE_FILE_NAME_SELECT_CODE = 2;
    private final int SETTINGS_CHANGED_CODE = 3;

    private static String[] STORAGE_PERMISSIONS = new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE };

    private LifeGame mGame;
    private LifeGameFieldView mFieldView;
    private StartTask mStartTask;
    private int mStepDuration = 300;
    private String mHeader;
    private TextView mHeaderView;
    private HashMap<Integer, Integer> mStartPosition;
    private int mFieldWidth = 100;
    private int mFieldHeight = 100;
    private boolean mIsRunning = false;
    private int mRandomElementsPercent;
    private LinearLayout mFieldButtons;
    //private AdView mAdView;
    //private AdRequest adRequest;

    private String mFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_life_game_field);

        //MobileAds.initialize(getApplicationContext(),
        //        getString(R.string.add_mob_app_code));
        //mAdView = (AdView) findViewById(R.id.adView);
        //adRequest = new AdRequest.Builder()
                // Remove this before realising
        //        .addTestDevice("DAAF5F440480126F344C27D93B27F336")
                //
        //        .build();

        //mAdView.loadAd(adRequest);

        mHeaderView = (TextView)findViewById(R.id.field_header_text);
        setPreferences();

        if(savedInstanceState != null) {
            Serializable positionObject = savedInstanceState.getSerializable(GAME_POSITION_KEY);
            if (positionObject != null) {
                HashMap<Integer, Integer> position = (HashMap<Integer, Integer>)positionObject;
                mGame.setPosition(position);
            }

            Serializable startPositionObject = savedInstanceState.getSerializable(GAME_START_POSITION_KEY);
            if(startPositionObject != null) {
                mStartPosition = (HashMap<Integer, Integer>)startPositionObject;
            }

            mGame.setStepCount(savedInstanceState.getInt(STEP_COUNT_KEY));
            mFieldView.setScale(savedInstanceState.getFloat(SCALE_KEY, 1));
            mFieldView.setViewport((RectF)savedInstanceState.getParcelable(VIEWPORT_KEY));
            mIsRunning = savedInstanceState.getBoolean(IS_RUNNING_KEY);
        }

        mFieldButtons = (LinearLayout)findViewById(R.id.field_buttons);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mFieldButtons.setOrientation(LinearLayout.HORIZONTAL);
        }

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mFieldButtons.setOrientation(LinearLayout.VERTICAL);
        }

        //mFieldView = (LifeGameFieldView)findViewById(R.id.game_field);

        Button stepButton = (Button)findViewById(R.id.step_button);
        stepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStartPosition == null) {
                    mStartPosition = mGame.getCurrentPosition();
                }
                mGame.step();
                setStep();
            }
        });

        Button clearButton = (Button)findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGame.getGameField().clearCells();
                mStartPosition = null;
                mGame.setStepCount(0);
                setHeader();
                mFieldView.postInvalidate();
            }
        });

        Button resetButton = (Button)findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
               //mGame.reset();
               if(mStartPosition != null) {
                   mGame.setPosition(mStartPosition);
               }
               mGame.setStepCount(0);
               setHeader();
               mFieldView.postInvalidate();
           }
        });

        Button startButton = (Button)findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStartPosition == null) {
                    mStartPosition = mGame.getCurrentPosition();
                }
                mStartTask = new StartTask();
                mStartTask.execute("");
                mIsRunning = true;
            }
        });

        Button stopButton = (Button)findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStartTask != null) {
                    mStartTask.cancel(true);
                    mIsRunning = false;
                }
            }
        });

        Button settingsButton = (Button)findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStartPosition = mGame.getCurrentPosition();
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_CHANGED_CODE);
            }
        });

        Button loadButton = (Button)findViewById(R.id.load_button);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        Button saveButton = (Button)findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectFileNameActivity.class);
                startActivityForResult(intent, SAVE_FILE_NAME_SELECT_CODE);
            }
        });

        Button randomButton = (Button)findViewById(R.id.random_button);
        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int elementCount = mRandomElementsPercent * mGame.getGameField().size() / 100;
                mGame.randomFillGameField(elementCount);
                mFieldView.postInvalidate();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFieldView.setGameField((RectangleGameField)mGame.getGameField());
        if(mIsRunning && mStartTask == null) {
            mStartTask = new StartTask();
            mStartTask.execute("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mStartTask != null) {
            mStartTask.cancel(true);
            mIsRunning = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        HashMap<Integer, Integer> position = mGame.getCurrentPosition();
        if(position != null && position.size() > 0) {
            outState.putSerializable(GAME_POSITION_KEY, mGame.getCurrentPosition());
        }
        if(mStartPosition != null) {
            outState.putSerializable(GAME_START_POSITION_KEY, mStartPosition);
        }
        outState.putInt(STEP_COUNT_KEY, mGame.getStepCount());
        outState.putFloat(SCALE_KEY, mFieldView.getScale());
        outState.putParcelable(VIEWPORT_KEY, mFieldView.getViewport());
        outState.putBoolean(IS_RUNNING_KEY, mIsRunning);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    saveData();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SAVE_FILE_NAME_SELECT_CODE: {
                SharedPreferences preferences = getSharedPreferences(SettingsActivity.SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
                mFileName = preferences.getString(SelectFileNameActivity.FILE_NAME_KEY, "");
                File file = getExportFile();
                if (resultCode == RESULT_OK) {
                    if (file.exists()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(String.format(getString(R.string.file_exists),
                                "DOWNLOADS/" + mFileName));
                        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                verifyPermission(STORAGE_PERMISSIONS);
                            }
                        });

                        builder.setNegativeButton(getString(R.string.no), null);
                        builder.create().show();
                    }
                    else
                        verifyPermission(STORAGE_PERMISSIONS);
                    break;
                }
            }
            case LOAD_FILE_SELECT_CODE: {
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String extension = Helper.checkFileName(uri.toString());
                    if(extension == null) {
                        Helper.showMessage(this, getString(
                                R.string.supported_files, Helper.joinStrings(Helper.SUPPORTED_FILE_EXTENSIONS, ";")));
                        break;
                    }

                    try {
                        InputStream stream = getContentResolver().openInputStream(uri);
                        InputStreamReader streamReader = new InputStreamReader(stream);
                        BufferedReader reader = new BufferedReader(streamReader);
                        LifeGame game = new LifeGame(new SquareCellTorGameField(mFieldWidth, mFieldHeight));
                        try {
                            if(extension.equals(Helper.SUPPORTED_FILE_EXTENSIONS[0])) {
                                game.loadFromRleFormat(reader);
                            }
                            else {
                                game.loadFromLife106Format(reader);
                            }
                        }
                        catch (IndexOutOfBoundsException ex) {
                            Helper.showMessage(this, getString(R.string.index_is_too_big, uri, mFieldWidth, mFieldHeight));
                        }
                        SharedPreferences preferences = getSharedPreferences(SettingsActivity.SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(SettingsActivity.SURVIVE_NEIGHBOURS_KEY, Helper.indicatorsToString(game.getSurviveNeighbours()));
                        editor.putString(SettingsActivity.BORN_NEIGHBOURS_KEY, Helper.indicatorsToString(game.getBornNeighbours()));
                        editor.putInt(SettingsActivity.MAX_AGE_KEY, game.getMaxAge());
                        if(game.getGameField() instanceof SquareCellTorGameField) {
                            editor.putInt(SettingsActivity.CELL_SHAPE_KEY, SettingsActivity.SQUARE_CELL);
                        }
                        if(game.getGameField() instanceof HexagonCellTorGameField) {
                            editor.putInt(SettingsActivity.CELL_SHAPE_KEY, SettingsActivity.HEXAGON_CELL);
                        }
                        editor.commit();
                        setPreferences();
                        mGame.setPosition(game.getCurrentPosition());
                        mFieldView.postInvalidate();
                    }
                    catch(IOException ex) {
                        Helper.showMessage(this, getString(R.string.load_file_error) + "\r\n" + ex.getMessage());
                    }
                    catch (Exception ex) {
                        Helper.showMessage(this, getString(R.string.load_file_error) + "\r\n" + ex.getMessage());
                    }
                }
                break;
            }
            case SETTINGS_CHANGED_CODE: {
                if(resultCode == RESULT_OK) {
                    setPreferences();
                    mFieldView.postInvalidate();
                }
                break;
            }
        }
    }

    private void setPreferences() {
        SharedPreferences preferences = getSharedPreferences(SettingsActivity.SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
        mFieldWidth = preferences.getInt(SettingsActivity.GAME_FIELD_WIDTH_KEY, 100);
        mFieldHeight = preferences.getInt(SettingsActivity.GAME_FIELD_HEIGHT_KEY, 100);
        int cellShape = preferences.getInt(SettingsActivity.CELL_SHAPE_KEY, SettingsActivity.SQUARE_CELL);
        ViewGroup parent = (ViewGroup)findViewById(R.id.activity_life_game_field);
        if(mFieldView != null) {
            parent.removeView(mFieldView);
        }
        switch(cellShape) {
            case SettingsActivity.HEXAGON_CELL: {
                mFieldView = new HexagonFieldView(this);
                mGame = GameCreator.HexagonalTorGame(mFieldWidth, mFieldHeight, 0);
                break;
            }
            case SettingsActivity.SQUARE_CELL: {
                mFieldView = new SquareFieldView(this);
                mGame = GameCreator.RectangleTorGame(mFieldWidth, mFieldHeight, 0);
                break;
            }
            default:{
                mFieldView = new SquareFieldView(this);
                mGame = GameCreator.RectangleTorGame(mFieldWidth, mFieldHeight, 0);
                break;
            }
        }
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.BELOW, R.id.field_header);
        p.addRule(RelativeLayout.ABOVE, R.id.field_buttons);
        mFieldView.setLayoutParams(p);

        parent.addView(mFieldView);

        mGame.setSurviveNeighbours(Helper.parseIndicators(preferences.getString(SettingsActivity.SURVIVE_NEIGHBOURS_KEY, "23")));
        mGame.setBornNeighbours(Helper.parseIndicators(preferences.getString(SettingsActivity.BORN_NEIGHBOURS_KEY, "3")));
        mGame.setMaxAge(preferences.getInt(SettingsActivity.MAX_AGE_KEY, 0));
        mRandomElementsPercent = preferences.getInt(SettingsActivity.RANDOM_CELLS_KEY, 0);

        setHeader();
        mFieldView.setGameField((RectangleGameField)mGame.getGameField());
    }

    private void setHeader() {
        mHeader = String.format("%s: %s; %s: b%s/s%s; %s: %d",
                getString(R.string.field_type),
                mGame.getGameField().getFieldKind(),
                getString(R.string.rule),
                Helper.indicatorsToString(mGame.getBornNeighbours()),
                Helper.indicatorsToString(mGame.getSurviveNeighbours()),
                getString(R.string.max_age),
                mGame.getMaxAge());
        mHeaderView.setText(mHeader);
    }

    private void setStep() {
        mFieldView.invalidate();
        mHeaderView.setText(String.format("%s; %s: %d: %s: %d",
                mHeader,
                getString(R.string.step),
                mGame.getStepCount(),
                getString(R.string.items),
                mGame.getItemCount()));
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.file_chooser_header)),
                    LOAD_FILE_SELECT_CODE);
        }
        catch (ActivityNotFoundException ex){
            Helper.showMessage(this, getString(R.string.install_file_manager));
        }
    }

    private File getExportFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(dir, mFileName);
        return file;
    }

    private void saveData() {
        FileWriter writer = null;
        try {
            File file = getExportFile();
            writer = new FileWriter(file);
            String extension = Helper.checkFileName(mFileName);
            if(extension == Helper.SUPPORTED_FILE_EXTENSIONS[0]) {
                mGame.saveToRleFormat(writer);
            }
            else {
                mGame.saveToLife106Format(writer);
            }
            Helper.showMessage(this, getString(R.string.file_saved, file.getPath()));
        }catch(IOException ex) {
            String message = getString(R.string.file_not_saved, mFileName) + "\n" + ex.getMessage();
            Helper.showMessage(this, message);
        }
        finally {
            if(writer != null) {
                try {
                    writer.flush();
                    writer.close();
                }
                catch(IOException ex) {
                }
            }
        }
    }

    private void verifyPermission(String[] permissions) {
        int checkResult = ActivityCompat.checkSelfPermission(this, permissions[0]);
        if(checkResult != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE);
        }
        else {
            saveData();
        }
    }

    class StartTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder sbResult = new StringBuilder();
            while(mGame.getItemCount() != 0 && !isCancelled()) {
                mGame.step();
                publishProgress("");
                synchronized(this) {
                    try {
                        wait(mStepDuration);
                    } catch (InterruptedException ex) {
                        sbResult.append(ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
            return sbResult.toString();
        }

        @Override
        protected void onProgressUpdate(String... params){
            setStep();
        }
    }
}
