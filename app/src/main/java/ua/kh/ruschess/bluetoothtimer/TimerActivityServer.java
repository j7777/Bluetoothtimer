package ua.kh.ruschess.bluetoothtimer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;

public class TimerActivityServer extends AppCompatActivity {
    private String device_name1 = null;
    private String device_name2 = null;
    private String device_name3 = null;
    private String device_addr1 = null;
    private String device_addr2 = null;
    private String device_addr3 = null;
    private String device_color1 = null;
    private String device_color2 = null;
    private String device_color3 = null;

    private Boolean reloadGame1 = false;
    private Boolean reloadGame2 = false;

    private Boolean enable_bt_adapter_flag;

    private Boolean activeTimer = true;
    private Boolean gameActive = false;

    private GifImageView gifMyTime;
    private ImageView pngMyTime;
    private TextView my_time_text;

    private CoordinatorLayout rootLayout;

    private BluetoothAdapter mBluetoothAdapter = null;

    private TimerService mTimerService = null;
    private TimerService2 mTimerService2 = null;

    private StringBuffer mOutStringBuffer;
    private StringBuffer mOutStringBuffer2;

    private String mConnectedDeviceName = null;

    private Integer setColor = null;
    private Integer activeColor = 0;
    private Boolean gameStart = false;

    private LinearLayout LayoutAllTime;
    private LinearLayout LayoutWhiteTime;
    private LinearLayout LayoutBrownTime;
    private LinearLayout LayoutBlackTime;

    private TextView textViewAllTime;
    private TextView textViewWhiteTime;
    private TextView textViewBrownTime;
    private TextView textViewBlackTime;

    private ImageView iconMyTime;

    private LinearLayout layoutPause;

    private Timer myTimer;
    private int counters[] = new int[4];

    private Boolean disableColor[] = {false, false, false};

    private SharedPreferences setting;

    private Vibrator vibr;
    private long[] pattern = {0, 100, 100};
    private Boolean vibroCheck = true;

    private Ringtone ringtone;
    private Uri ringtoneUri;
    private Integer time_signal;
    private String timer_sound;

    private Integer time_game = 0;
    private Integer time_game_user = 0;

    private Boolean start_device1 = false;
    private Boolean start_device2 = false;

    private Boolean status_connect_device1 = false;
    private Boolean status_connect_device2 = false;

    private LinearLayout layoutLoading;
    private LinearLayout connect_run_device1;
    private LinearLayout connect_success_device1;
    private LinearLayout connect_error_device1;
    private LinearLayout connect_run_device2;
    private LinearLayout connect_success_device2;
    private LinearLayout connect_error_device2;
    private TextView name_run_device1;
    private TextView name_run_device2;
    private TextView name_success_device1;
    private TextView name_success_device2;
    private TextView name_error_device1;
    private TextView name_error_device2;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private com.github.clans.fab.FloatingActionButton fab1;
    private com.github.clans.fab.FloatingActionButton fab2;
    private com.github.clans.fab.FloatingActionButton fab3;
    private com.github.clans.fab.FloatingActionButton fab4;
    private com.github.clans.fab.FloatingActionButton fab5;
    private com.github.clans.fab.FloatingActionButton fab6;
    private com.github.clans.fab.FloatingActionButton fab7;

    private FloatingActionMenu menuLabels;

    Snackbar snackBarPause;
    Snackbar snackBarBack;

    private TextView total_time_pause;
    private TextView black_time_pause;
    private TextView white_time_pause;
    private TextView brown_time_pause;
    private TextView my_time_end_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_server);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        rootLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        layoutPause = (LinearLayout) findViewById(R.id.LayoutPause);

        layoutLoading = (LinearLayout) findViewById(R.id.connectingLayout);
        connect_run_device1 = (LinearLayout) findViewById(R.id.connect_run_device1);
        connect_success_device1 = (LinearLayout) findViewById(R.id.connect_success_device1);
        connect_error_device1 = (LinearLayout) findViewById(R.id.connect_error_device1);
        connect_run_device2 = (LinearLayout) findViewById(R.id.connect_run_device2);
        connect_success_device2 = (LinearLayout) findViewById(R.id.connect_success_device2);
        connect_error_device2 = (LinearLayout) findViewById(R.id.connect_error_device2);
        name_run_device1 = (TextView) findViewById(R.id.name_run_device1);
        name_run_device2 = (TextView) findViewById(R.id.name_run_device2);
        name_success_device1 = (TextView) findViewById(R.id.name_success_device1);
        name_success_device2 = (TextView) findViewById(R.id.name_success_device2);
        name_error_device1 = (TextView) findViewById(R.id.name_error_device1);
        name_error_device2 = (TextView) findViewById(R.id.name_error_device2);

        LayoutAllTime = (LinearLayout) findViewById(R.id.block_total_time);
        LayoutWhiteTime = (LinearLayout) findViewById(R.id.block_white_time);
        LayoutBrownTime = (LinearLayout) findViewById(R.id.block_brown_time);
        LayoutBlackTime = (LinearLayout) findViewById(R.id.block_black_time);

        textViewAllTime = (TextView) findViewById(R.id.total_time);
        textViewWhiteTime = (TextView) findViewById(R.id.white_time);
        textViewBrownTime = (TextView) findViewById(R.id.brown_time);
        textViewBlackTime = (TextView) findViewById(R.id.black_time);

        iconMyTime = (ImageView) findViewById(R.id.iconMyTime);

        gifMyTime = (GifImageView) findViewById(R.id.gifMyTime);
        pngMyTime = (ImageView) findViewById(R.id.pngMyTime);
        my_time_text = (TextView) findViewById(R.id.my_time_text);

        total_time_pause = (TextView) findViewById(R.id.total_time_pause);
        black_time_pause = (TextView) findViewById(R.id.black_time_pause);
        white_time_pause = (TextView) findViewById(R.id.white_time_pause);
        brown_time_pause = (TextView) findViewById(R.id.brown_time_pause);
        my_time_end_text = (TextView) findViewById(R.id.my_time_end_text);

        try {
            setting = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            vibr = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            vibroCheck = setting.getBoolean("timer_vibro_sel", true);

            timer_sound = setting.getString("timer_sound_sel", "");
            ringtoneUri = Uri.parse(timer_sound);
            ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
            time_signal = Integer.parseInt(setting.getString("time_signal", "1"));

            time_game = Integer.parseInt(setting.getString("time_game", "0")) * 60;
            time_game_user = Integer.parseInt(setting.getString("time_game_user", "0")) * 60;
        } catch (Exception e) {
            Toast.makeText(TimerActivityServer.this, "Ошибка установки настроек!", Toast.LENGTH_LONG).show();
        }

        try {
            enable_bt_adapter_flag = getIntent().getExtras().getBoolean("enable_bt_adapter_flag");

            device_name1 = getIntent().getStringExtra("device_name1");
            device_name2 = getIntent().getStringExtra("device_name2");
            device_name3 = getIntent().getStringExtra("device_name3");
            device_addr1 = getIntent().getStringExtra("device_addr1");
            device_addr2 = getIntent().getStringExtra("device_addr2");
            device_addr3 = getIntent().getStringExtra("device_addr3");
            device_color2 = getIntent().getStringExtra("device_color2");
            device_color3 = getIntent().getStringExtra("device_color3");

            if (!device_color2.equals("white") && !device_color3.equals("white")) {
                device_color1 = "white";
            } else if (!device_color2.equals("brown") && !device_color3.equals("brown")) {
                device_color1 = "brown";
            } else {
                device_color1 = "black";
            }
        } catch (Exception e) {
            Toast.makeText(TimerActivityServer.this, "Ошибка данных!", Toast.LENGTH_LONG).show();
        }

        Log.d("mylog", "device_name2: "+device_name2);
        Log.d("mylog", "device_name3: "+device_name3);
        Log.d("mylog", "device_addr2: "+device_addr2);
        Log.d("mylog", "device_addr3: "+device_addr3);

        if(time_game > 0){
            my_time_end_text.setVisibility(View.VISIBLE);
            my_time_end_text.setText(getCountToElem(time_game));
        }

        if(time_game_user > 0){
            my_time_end_text.setVisibility(View.VISIBLE);
            my_time_end_text.setText(getCountToElem(time_game_user));
        }

        Log.d("mylog", String.valueOf(enable_bt_adapter_flag));

        menuLabels = (FloatingActionMenu) findViewById(R.id.menu_labels);

        layoutLoading.setVisibility(View.VISIBLE);

        fab1 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab4);
        fab5 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab5);
        fab6 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab6);
        fab7 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab7);

        final Snackbar snackNewMove = Snackbar.make(findViewById(android.R.id.content), R.string.dialog_start_move, Snackbar.LENGTH_LONG);
        View view = snackNewMove.getView();
        TextView textView = (TextView)view.findViewById(android.support.design.R.id.snackbar_text);

        snackNewMove.setAction(R.string.dialog_confirm_close_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackNewMove.dismiss();
                menuLabels.toggle(true);

                if(checkColorDisabled()) {
                    if (ringtone.isPlaying()) {
                        ringtone.stop();
                    }

                    vibr.cancel();

                    JSONObject jsonObject = new JSONObject();

                    try {
                        jsonObject.put("setColorActive", setColor);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject.put("setEnableColor", setColor);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject.put("setCounters0", counters[0]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject.put("setCounters1", counters[1]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject.put("setCounters2", counters[2]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject.put("setCounters3", counters[3]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(sendCommandAll(String.valueOf(jsonObject), String.valueOf(jsonObject))){
                        activeColor = setColor;

                        gifMyTime.setVisibility(View.VISIBLE);
                        pngMyTime.setVisibility(View.GONE);

                        activeTimer = true;

                        if (vibroCheck) {
                            vibr.vibrate(pattern, -1);
                        }

                        if (!timer_sound.equals("")) {
                            mySound();
                        }

                        onStartTimer();
                        my_time_end_text.setTextColor(ContextCompat.getColor(TimerActivityServer.this, R.color.my_time_start_color));
                    }
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackNewMove.show();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLabels.toggle(true);

                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("setCounters0", counters[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    jsonObject.put("setCounters1", counters[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    jsonObject.put("setCounters2", counters[2]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    jsonObject.put("setCounters3", counters[3]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    jsonObject.put("pauseStart", "true");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(sendCommandAll(String.valueOf(jsonObject), String.valueOf(jsonObject))){
                    gifMyTime.setVisibility(View.GONE);
                    pngMyTime.setVisibility(View.VISIBLE);

                    layoutPause.setVisibility(View.VISIBLE);

                    if(ringtone.isPlaying()){
                        ringtone.stop();
                    }

                    vibr.cancel();

                    activeTimer = false;

                    if (vibroCheck) {
                        vibr.vibrate(pattern, -1);
                    }

                    if (myTimer != null)
                        myTimer.cancel();
                    myTimer = null;

                    total_time_pause.setText(getCountToElem(counters[0]));
                    white_time_pause.setText(getCountToElem(counters[1]));
                    brown_time_pause.setText(getCountToElem(counters[2]));
                    black_time_pause.setText(getCountToElem(counters[3]));
                }
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutLoading.setVisibility(View.VISIBLE);
                menuLabels.toggle(true);
            }
        });

        final Snackbar snackBarGame = Snackbar.make(findViewById(android.R.id.content), R.string.dialog_confirm_close4, Snackbar.LENGTH_LONG);
        view = snackBarGame.getView();
        textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);

        snackBarGame.setAction(R.string.dialog_confirm_close_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBarGame.dismiss();
                menuLabels.toggle(true);

                JSONObject jsonObject = new JSONObject();
                int oldActiveColor = 0;

                try {
                    jsonObject.put("setDisableColor", setColor);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(activeTimer) {
                    oldActiveColor = activeColor;

                    activeColor++;

                    if (activeColor > 2) {
                        activeColor = 0;
                    }

                    if (disableColor[activeColor]) {
                        activeColor++;
                    }

                    if (activeColor > 2) {
                        activeColor = 0;
                    }

                    disableColor[setColor] = true;

                    try {
                        jsonObject.put("setColorActive", activeColor);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject.put("setCounters0", counters[0]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject.put("setCounters1", counters[1]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject.put("setCounters2", counters[2]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject.put("setCounters3", counters[3]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(sendCommandAll(String.valueOf(jsonObject), String.valueOf(jsonObject))){
                    if(ringtone.isPlaying()){
                        ringtone.stop();
                    }

                    vibr.cancel();

                    if(activeTimer) {
                        gifMyTime.setVisibility(View.GONE);
                        pngMyTime.setVisibility(View.VISIBLE);

                        activeTimer = false;

                        if (vibroCheck) {
                            vibr.vibrate(pattern, -1);
                        }

                        if (myTimer != null)
                            myTimer.cancel();
                        myTimer = null;
                    }
                }
                else{
                    activeColor = oldActiveColor;
                    disableColor[setColor] = false;
                }
            }
        });

        final Snackbar snackBarReload = Snackbar.make(findViewById(android.R.id.content), R.string.dialog_confirm_close7, Snackbar.LENGTH_LONG);
        view = snackBarReload.getView();
        textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);

        fab6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBarReload.show();
            }
        });

        fab6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLabels.toggle(true);
                gameActive = true;
                gameStart = false;
                activeColor = 0;
                activeTimer = false;

                gifMyTime.setVisibility(View.GONE);
                pngMyTime.setVisibility(View.VISIBLE);

                disableColor[0] = false;
                disableColor[1] = false;
                disableColor[2] = false;

                if(ringtone.isPlaying()){
                    ringtone.stop();
                }

                vibr.cancel();

                if (myTimer != null)
                    myTimer.cancel();
                myTimer = null;

                if (mTimerService != null) {
                    mTimerService.stop();
                }

                if (mTimerService2 != null) {
                    mTimerService2.stop();
                }

                mTimerService = null;
                mTimerService2 = null;

                setupTimer();

                reloadGame1 = true;
                reloadGame2 = true;
            }
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBarGame.show();
            }
        });

        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLabels.toggle(true);
                Intent intent = new Intent(TimerActivityServer.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        final Snackbar snackBarExit = Snackbar.make(findViewById(android.R.id.content), R.string.dialog_confirm_close2, Snackbar.LENGTH_LONG);

        snackBarExit.setAction(R.string.dialog_confirm_close_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBarExit.dismiss();

                if (myTimer != null)
                    myTimer.cancel();
                myTimer = null;

                if(enable_bt_adapter_flag){
                    mBluetoothAdapter.disable();
                }

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        fab7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBarExit.show();
            }
        });

        snackBarPause = Snackbar.make(findViewById(android.R.id.content), R.string.dialog_confirm_close5, Snackbar.LENGTH_LONG);
        snackBarPause.setAction(R.string.dialog_confirm_close_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBarExit.dismiss();
                layoutPause.setVisibility(View.GONE);

                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("setStartGame", "true");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(sendCommandAll(String.valueOf(jsonObject), String.valueOf(jsonObject))){
                    if(activeColor == setColor){
                        gifMyTime.setVisibility(View.VISIBLE);
                        pngMyTime.setVisibility(View.GONE);

                        activeTimer = true;

                        if(vibroCheck) {
                            vibr.vibrate(pattern, -1);
                        }

                        if(!timer_sound.equals("")) {
                            mySound();
                        }
                    }

                    onStartTimer();
                }
            }
        });

        snackBarBack = Snackbar.make(findViewById(android.R.id.content), R.string.dialog_confirm_close6, Snackbar.LENGTH_LONG);
        snackBarBack.setAction(R.string.dialog_confirm_close_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBarBack.dismiss();

                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("backToHome", "true");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                sendCommandAll(String.valueOf(jsonObject), String.valueOf(jsonObject));

                if(ringtone.isPlaying()){
                    ringtone.stop();
                }

                vibr.cancel();

                if (myTimer != null)
                    myTimer.cancel();
                myTimer = null;

                Intent intent = new Intent(TimerActivityServer.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        snackBarBack.show();
    }

    //Пауза
    public void clickPause(View view) {
        snackBarPause.show();
    }

    public void closeLoading(View view) {
        layoutLoading.setVisibility(View.GONE);
    }

    public void startStopTimerBut(View view) {
        Log.d("mylog", "gameActive - "+gameActive);
        Log.d("mylog", "start_device1 - "+start_device1);
        Log.d("mylog", "start_device2 - "+start_device2);
        Log.d("mylog", "setColor - "+setColor);
        Log.d("mylog", "gameStart - "+gameStart);

        if(gameActive){
            if(setColor != null){
                if(!gameStart){
                    if(setColor == 0) {
                        Log.d("mylog", "mTimerService.getState() - "+mTimerService.getState());
                        Log.d("mylog", "mTimerService2.getState() - "+mTimerService2.getState());

                        gameStart = true;

                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("setColorActive", activeColor);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            jsonObject.put("setGameStart", gameStart);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            jsonObject.put("setCounters0", counters[0]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            jsonObject.put("setCounters1", counters[1]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            jsonObject.put("setCounters2", counters[2]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            jsonObject.put("setCounters3", counters[3]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(sendCommandAll(String.valueOf(jsonObject), String.valueOf(jsonObject))){
                            if(vibroCheck) {
                                vibr.vibrate(pattern, -1);
                            }

                            gifMyTime.setVisibility(View.VISIBLE);
                            pngMyTime.setVisibility(View.GONE);

                            activeTimer = true;
                            onStartTimer();
                        }
                        else{
                            gameStart = false;
                        }
                    }
                }
                else{
                    if(activeTimer){
                        Log.d("mylog", "mTimerService.getState() - "+mTimerService.getState());
                        Log.d("mylog", "mTimerService2.getState() - "+mTimerService2.getState());

                        if(!checkColorDisabled()){
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("setStopGame", "true");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(sendCommandAll(String.valueOf(jsonObject), String.valueOf(jsonObject))){
                                if (myTimer != null)
                                    myTimer.cancel();
                                myTimer = null;
                            }

                            return;
                        }

                        int oldActiveColor = activeColor;
                        activeColor++;

                        if(activeColor > 2){
                            activeColor = 0;
                        }

                        if(disableColor[activeColor]){
                            activeColor++;
                        }

                        if(activeColor > 2){
                            activeColor = 0;
                        }

                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("setColorActive", activeColor);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            jsonObject.put("setCounters0", counters[0]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            jsonObject.put("setCounters1", counters[1]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            jsonObject.put("setCounters2", counters[2]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            jsonObject.put("setCounters3", counters[3]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(sendCommandAll(String.valueOf(jsonObject), String.valueOf(jsonObject))){
                            gifMyTime.setVisibility(View.GONE);
                            pngMyTime.setVisibility(View.VISIBLE);

                            activeTimer = false;

                            if(vibroCheck) {
                                vibr.vibrate(pattern, -1);
                            }
                        }
                        else{
                            activeColor = oldActiveColor;
                        }
                    }
                }
            }
        }
    }

    public Boolean checkColorDisabled(){
        Integer counter = 0;

        for(Integer i = 0; i < disableColor.length; i++){
            if(disableColor[i]){
                counter++;
            }
        }

        if(counter >= 2){
            return false;
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("mylog", "onStart");

        if(mTimerService == null || mTimerService2 == null){
            setupTimer();
        }
        else{
            if (mTimerService.getState() == TimerService.STATE_NONE) {
                mTimerService.start();
            }

            if (mTimerService2.getState() == TimerService2.STATE_NONE) {
                mTimerService2.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("mylog", "onDestroy");

        if (mTimerService != null) {
            mTimerService.stop();
        }

        if (mTimerService2 != null) {
            mTimerService2.stop();
        }

        if (myTimer != null)
            myTimer.cancel();
        myTimer = null;
    }

    @Override
    protected  void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        Log.d("mylog", "onSaveInstanceState");

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("setCounters0", counters[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonObject.put("setCounters1", counters[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonObject.put("setCounters2", counters[2]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonObject.put("setCounters3", counters[3]);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonObject.put("pauseStart", "true");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(sendCommandAll(String.valueOf(jsonObject), String.valueOf(jsonObject))){
            gifMyTime.setVisibility(View.GONE);
            pngMyTime.setVisibility(View.VISIBLE);

            layoutPause.setVisibility(View.VISIBLE);

            if(ringtone.isPlaying()){
                ringtone.stop();
            }

            vibr.cancel();

            activeTimer = false;

            if (vibroCheck) {
                vibr.vibrate(pattern, -1);
            }

            if (myTimer != null)
                myTimer.cancel();
            myTimer = null;

            total_time_pause.setText(getCountToElem(counters[0]));
            white_time_pause.setText(getCountToElem(counters[1]));
            brown_time_pause.setText(getCountToElem(counters[2]));
            black_time_pause.setText(getCountToElem(counters[3]));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("mylog", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("mylog", "onStop");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("mylog", "onResume");

        if(mTimerService == null || mTimerService2 == null){
            setupTimer();
        }
        else{
            if (mTimerService.getState() == TimerService.STATE_NONE) {
                mTimerService.start();
            }

            if (mTimerService2.getState() == TimerService2.STATE_NONE) {
                mTimerService2.start();
            }
        }

        try {
            setting = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            vibr = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            vibroCheck = setting.getBoolean("timer_vibro_sel", true);

            timer_sound = setting.getString("timer_sound_sel", "");
            ringtoneUri = Uri.parse(timer_sound);
            ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
            time_signal = Integer.parseInt(setting.getString("time_signal", "1"));
        } catch (Exception e) {
            Log.d("mylog", String.valueOf(e));
        }
    }

    private void setupTimer() {
        Log.d("mylog", "setupTimer");

        mTimerService = new TimerService(TimerActivityServer.this, mHandler);
        mTimerService2 = new TimerService2(TimerActivityServer.this, mHandler2);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
        mOutStringBuffer2 = new StringBuffer("");

        connectDevice(device_addr2, device_addr3);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case TimerService.STATE_CONNECTED:
                            Log.d("mylog", "STATE_CONNECTED - "+mConnectedDeviceName);

                            if(!start_device1){
                                JSONObject jsonObject = new JSONObject();
                                JSONObject jsonObject2 = new JSONObject();

                                switch (device_color1){
                                    case "white":
                                        setColor = 0;
                                        LayoutWhiteTime.setVisibility(View.GONE);
                                        iconMyTime.setImageResource(R.drawable.icon_my_color_white);
                                        iconMyTime.setVisibility(View.VISIBLE);
                                        break;
                                    case "brown":
                                        setColor = 1;
                                        LayoutBrownTime.setVisibility(View.GONE);
                                        iconMyTime.setImageResource(R.drawable.icon_my_color_brown);
                                        iconMyTime.setVisibility(View.VISIBLE);
                                        break;
                                    case "black":
                                        setColor = 2;
                                        LayoutBlackTime.setVisibility(View.GONE);
                                        iconMyTime.setImageResource(R.drawable.icon_my_color_black);
                                        iconMyTime.setVisibility(View.VISIBLE);
                                        break;
                                }

                                start_device1 = true;

                                try {
                                    jsonObject.put("setColor", device_color2);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject2.put("setColor", device_color3);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("gameActive", "true");
                                    jsonObject2.put("gameActive", "true");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setTimeGame", time_game);
                                    jsonObject2.put("setTimeGame", time_game);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setTimeGameUser", time_game_user);
                                    jsonObject2.put("setTimeGameUser", time_game_user);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if(sendCommandAll(String.valueOf(jsonObject), String.valueOf(jsonObject2))){
                                    gameActive = true;
                                }
                            }

                            status_connect_device1 = true;

                            name_success_device1.setText(device_name2);
                            connect_run_device1.setVisibility(View.GONE);
                            connect_success_device1.setVisibility(View.VISIBLE);
                            connect_error_device1.setVisibility(View.GONE);

                            if (status_connect_device1 && status_connect_device2) {
                                layoutLoading.setVisibility(View.GONE);
                            }

                            if(reloadGame1){
                                JSONObject jsonObject = new JSONObject();

                                try {
                                    jsonObject.put("setTimeGame", time_game);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setTimeGameUser", time_game_user);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("reloadTimer", "true");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setCounters0", counters[0]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setCounters1", counters[1]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setCounters2", counters[2]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setCounters3", counters[3]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setColor", device_color2);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("gameActive", "true");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if(sendCommandAll(String.valueOf(jsonObject), "")){
                                    reloadGame1 = false;
                                }
                            }

                            break;
                        case TimerService.STATE_CONNECTING:
                            name_run_device1.setText(device_name2);
                            connect_run_device1.setVisibility(View.VISIBLE);
                            connect_success_device1.setVisibility(View.GONE);
                            connect_error_device1.setVisibility(View.GONE);

                            status_connect_device1 = false;

                            Log.d("mylog", "STATE_CONNECTING - "+mConnectedDeviceName);
                            break;
                        case TimerService.STATE_LISTEN:
                        case TimerService.STATE_NONE:
                            name_error_device1.setText(device_name2);
                            connect_run_device1.setVisibility(View.GONE);
                            connect_success_device1.setVisibility(View.GONE);
                            connect_error_device1.setVisibility(View.VISIBLE);
                            layoutLoading.setVisibility(View.VISIBLE);

                            status_connect_device1 = false;

                            Log.d("mylog", "STATE_NONE - "+mConnectedDeviceName);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    Log.d("mylog", "MESSAGE_WRITE - "+writeMessage);

                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    Log.d("mylog", "MESSAGE_READ - "+readMessage);

                    JSONObject jsonObject = null;
                    Boolean getGameActive = false;
                    String getSetColor = "";
                    Integer setColorActive = null;
                    Boolean setGameStart = false;
                    Boolean setStopGame = false;
                    Boolean pauseStart = false;
                    Boolean setStartGame = false;
                    Boolean backToHome = false;
                    Boolean reloadTimer = false;

                    Integer setCounters0 = 0;
                    Integer setCounters1 = 0;
                    Integer setCounters2 = 0;
                    Integer setCounters3 = 0;

                    Integer setDisableColor = null;
                    Integer setEnableColor = null;

                    Boolean userTimeEnd = false;
                    Boolean timeEnd = false;

                    JSONObject jsonObject2 = new JSONObject();

                    try {
                        jsonObject = new JSONObject(readMessage);
                        userTimeEnd = Boolean.valueOf(jsonObject.getString("userTimeEnd"));
                        jsonObject2.put("userTimeEnd", jsonObject.getString("userTimeEnd"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        timeEnd = Boolean.valueOf(jsonObject.getString("timeEnd"));
                        jsonObject2.put("timeEnd", jsonObject.getString("timeEnd"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setEnableColor = Integer.valueOf(jsonObject.getString("setEnableColor"));
                        jsonObject2.put("setEnableColor", jsonObject.getString("setEnableColor"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setStopGame = Boolean.valueOf(jsonObject.getString("setStopGame"));
                        jsonObject2.put("setStopGame", jsonObject.getString("setStopGame"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        pauseStart = Boolean.valueOf(jsonObject.getString("pauseStart"));
                        jsonObject2.put("pauseStart", jsonObject.getString("pauseStart"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        reloadTimer = Boolean.valueOf(jsonObject.getString("reloadTimer"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        backToHome = Boolean.valueOf(jsonObject.getString("backToHome"));
                        jsonObject2.put("backToHome", jsonObject.getString("backToHome"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setStartGame = Boolean.valueOf(jsonObject.getString("setStartGame"));
                        jsonObject2.put("setStartGame", jsonObject.getString("setStartGame"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        getGameActive = Boolean.valueOf(jsonObject.getString("gameActive"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        getSetColor = jsonObject.getString("setColor");
                        jsonObject2.put("setColor", jsonObject.getString("setColor"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setColorActive = Integer.valueOf(jsonObject.getString("setColorActive"));
                        jsonObject2.put("setColorActive", jsonObject.getString("setColorActive"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setGameStart = Boolean.valueOf(jsonObject.getString("setGameStart"));
                        jsonObject2.put("setGameStart", jsonObject.getString("setGameStart"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setCounters0 = Integer.valueOf(jsonObject.getString("setCounters0"));
                        jsonObject2.put("setCounters0", jsonObject.getString("setCounters0"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setCounters1 = Integer.valueOf(jsonObject.getString("setCounters1"));
                        jsonObject2.put("setCounters1", jsonObject.getString("setCounters1"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setCounters2 = Integer.valueOf(jsonObject.getString("setCounters2"));
                        jsonObject2.put("setCounters2", jsonObject.getString("setCounters2"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setCounters3 = Integer.valueOf(jsonObject.getString("setCounters3"));
                        jsonObject2.put("setCounters3", jsonObject.getString("setCounters3"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setDisableColor = Integer.valueOf(jsonObject.getString("setDisableColor"));
                        jsonObject2.put("setDisableColor", jsonObject.getString("setDisableColor"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(!jsonObject2.equals("")) {
                        sendCommandAll("", String.valueOf(jsonObject2));
                    }

                    if(userTimeEnd){
                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.txt_end_time_user, Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);
                        snack.show();
                        my_time_end_text.setTextColor(ContextCompat.getColor(TimerActivityServer.this, R.color.my_time_end_color));
                    }

                    if(timeEnd){
                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.txt_end_time_total, Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);
                        snack.show();
                        my_time_end_text.setTextColor(ContextCompat.getColor(TimerActivityServer.this, R.color.my_time_end_color));
                    }

                    if(getGameActive){
                        gameActive = true;

                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.start_game_true, Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);
                        snack.show();
                    }

                    if(!getSetColor.equals("")){
                        switch (getSetColor){
                            case "white":
                                setColor = 0;
                                LayoutWhiteTime.setVisibility(View.GONE);
                                iconMyTime.setImageResource(R.drawable.icon_my_color_white);
                                iconMyTime.setVisibility(View.VISIBLE);
                                break;
                            case "brown":
                                setColor = 1;
                                LayoutBrownTime.setVisibility(View.GONE);
                                iconMyTime.setImageResource(R.drawable.icon_my_color_brown);
                                iconMyTime.setVisibility(View.VISIBLE);
                                break;
                            case "black":
                                setColor = 2;
                                LayoutBlackTime.setVisibility(View.GONE);
                                iconMyTime.setImageResource(R.drawable.icon_my_color_black);
                                iconMyTime.setVisibility(View.VISIBLE);
                                break;
                        }
                    }

                    if(setGameStart){
                        gameStart = true;
                        onStartTimer();
                    }

                    if(setEnableColor != null){
                        disableColor[setEnableColor] = false;
                    }

                    if(setColorActive != null){
                        activeColor = setColorActive;

                        if(setColorActive == setColor){
                            gifMyTime.setVisibility(View.VISIBLE);
                            pngMyTime.setVisibility(View.GONE);

                            activeTimer = true;

                            if(vibroCheck) {
                                vibr.vibrate(pattern, -1);
                            }

                            if(!timer_sound.equals("")) {
                                mySound();
                            }
                        }
                        else{
                            gifMyTime.setVisibility(View.GONE);
                            pngMyTime.setVisibility(View.VISIBLE);

                            activeTimer = false;

                            if (myTimer != null)
                                myTimer.cancel();
                            myTimer = null;
                        }

                        counters[0] = setCounters0;
                        counters[1] = setCounters1;
                        counters[2] = setCounters2;
                        counters[3] = setCounters3;

                        onStartTimer();
                    }

                    if(setDisableColor != null){
                        disableColor[setDisableColor] = true;
                    }

                    if(setStopGame){
                        gifMyTime.setVisibility(View.GONE);
                        pngMyTime.setVisibility(View.VISIBLE);

                        activeTimer = false;

                        if (myTimer != null)
                            myTimer.cancel();
                        myTimer = null;
                    }

                    if(pauseStart){
                        gifMyTime.setVisibility(View.GONE);
                        pngMyTime.setVisibility(View.VISIBLE);
                        layoutPause.setVisibility(View.VISIBLE);

                        counters[0] = setCounters0;
                        counters[1] = setCounters1;
                        counters[2] = setCounters2;
                        counters[3] = setCounters3;

                        activeTimer = false;

                        if (myTimer != null)
                            myTimer.cancel();
                        myTimer = null;

                        total_time_pause.setText(getCountToElem(counters[0]));
                        white_time_pause.setText(getCountToElem(counters[1]));
                        brown_time_pause.setText(getCountToElem(counters[2]));
                        black_time_pause.setText(getCountToElem(counters[3]));
                    }

                    if(setStartGame){
                        layoutPause.setVisibility(View.GONE);

                        if(activeColor == setColor){
                            gifMyTime.setVisibility(View.VISIBLE);
                            pngMyTime.setVisibility(View.GONE);

                            activeTimer = true;

                            if(vibroCheck) {
                                vibr.vibrate(pattern, -1);
                            }

                            if(!timer_sound.equals("")) {
                                mySound();
                            }
                        }

                        onStartTimer();
                    }

                    if(backToHome){
                        if(ringtone.isPlaying()){
                            ringtone.stop();
                        }

                        vibr.cancel();

                        if (myTimer != null)
                            myTimer.cancel();
                        myTimer = null;

                        Intent intent = new Intent(TimerActivityServer.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);

                    break;
                case MESSAGE_TOAST:

                    break;
            }
        }
    };

    private final Handler mHandler2 = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case TimerService.STATE_CONNECTED:
                            Log.d("mylog", "STATE_CONNECTED2 - "+mConnectedDeviceName);

                            if(!start_device2){
                                JSONObject jsonObject = new JSONObject();
                                JSONObject jsonObject2 = new JSONObject();

                                switch (device_color1){
                                    case "white":
                                        setColor = 0;
                                        LayoutWhiteTime.setVisibility(View.GONE);
                                        iconMyTime.setImageResource(R.drawable.icon_my_color_white);
                                        iconMyTime.setVisibility(View.VISIBLE);
                                        break;
                                    case "brown":
                                        setColor = 1;
                                        LayoutBrownTime.setVisibility(View.GONE);
                                        iconMyTime.setImageResource(R.drawable.icon_my_color_brown);
                                        iconMyTime.setVisibility(View.VISIBLE);
                                        break;
                                    case "black":
                                        setColor = 2;
                                        LayoutBlackTime.setVisibility(View.GONE);
                                        iconMyTime.setImageResource(R.drawable.icon_my_color_black);
                                        iconMyTime.setVisibility(View.VISIBLE);
                                        break;
                                }

                                start_device2 = true;

                                try {
                                    jsonObject.put("setColor", device_color2);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject2.put("setColor", device_color3);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("gameActive", "true");
                                    jsonObject2.put("gameActive", "true");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setTimeGame", time_game);
                                    jsonObject2.put("setTimeGame", time_game);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setTimeGameUser", time_game_user);
                                    jsonObject2.put("setTimeGameUser", time_game_user);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if(sendCommandAll(String.valueOf(jsonObject), String.valueOf(jsonObject2))){
                                    gameActive = true;
                                }
                            }

                            status_connect_device2 = true;

                            name_success_device2.setText(device_name3);
                            connect_run_device2.setVisibility(View.GONE);
                            connect_success_device2.setVisibility(View.VISIBLE);
                            connect_error_device2.setVisibility(View.GONE);

                            if (status_connect_device1 && status_connect_device2) {
                                layoutLoading.setVisibility(View.GONE);
                            }

                            if(reloadGame2){
                                JSONObject jsonObject = new JSONObject();

                                try {
                                    jsonObject.put("setTimeGame", time_game);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setTimeGameUser", time_game_user);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("reloadTimer", "true");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setCounters0", counters[0]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setCounters1", counters[1]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setCounters2", counters[2]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setCounters3", counters[3]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("setColor", device_color3);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    jsonObject.put("gameActive", "true");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if(sendCommandAll("", String.valueOf(jsonObject))){
                                    reloadGame2 = false;
                                }
                            }

                            break;
                        case TimerService.STATE_CONNECTING:
                            name_run_device2.setText(device_name3);
                            connect_run_device2.setVisibility(View.VISIBLE);
                            connect_success_device2.setVisibility(View.GONE);
                            connect_error_device2.setVisibility(View.GONE);

                            status_connect_device2 = false;

                            Log.d("mylog", "STATE_CONNECTING2 - "+mConnectedDeviceName);
                            break;
                        case TimerService.STATE_LISTEN:
                        case TimerService.STATE_NONE:
                            name_error_device2.setText(device_name3);
                            connect_run_device2.setVisibility(View.GONE);
                            connect_success_device2.setVisibility(View.GONE);
                            connect_error_device2.setVisibility(View.VISIBLE);
                            layoutLoading.setVisibility(View.VISIBLE);

                            status_connect_device2 = false;

                            Log.d("mylog", "STATE_NONE2 - "+mConnectedDeviceName);

                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    Log.d("mylog", "MESSAGE_WRITE2 - "+writeMessage);

                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    Log.d("mylog", "MESSAGE_READ2 - "+readMessage);

                    JSONObject jsonObject = null;
                    Boolean getGameActive = false;
                    String getSetColor = "";
                    Integer setColorActive = null;
                    Boolean setGameStart = false;
                    Boolean setStopGame = false;
                    Boolean pauseStart = false;
                    Boolean setStartGame = false;
                    Boolean backToHome = false;
                    Boolean reloadTimer = false;

                    Integer setCounters0 = 0;
                    Integer setCounters1 = 0;
                    Integer setCounters2 = 0;
                    Integer setCounters3 = 0;

                    Integer setDisableColor = null;
                    Integer setEnableColor = null;

                    Boolean userTimeEnd = false;
                    Boolean timeEnd = false;

                    JSONObject jsonObject2 = new JSONObject();

                    try {
                        jsonObject = new JSONObject(readMessage);
                        userTimeEnd = Boolean.valueOf(jsonObject.getString("userTimeEnd"));
                        jsonObject2.put("userTimeEnd", jsonObject.getString("userTimeEnd"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        timeEnd = Boolean.valueOf(jsonObject.getString("timeEnd"));
                        jsonObject2.put("timeEnd", jsonObject.getString("timeEnd"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setEnableColor = Integer.valueOf(jsonObject.getString("setEnableColor"));
                        jsonObject2.put("setEnableColor", jsonObject.getString("setEnableColor"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setStopGame = Boolean.valueOf(jsonObject.getString("setStopGame"));
                        jsonObject2.put("setStopGame", jsonObject.getString("setStopGame"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        pauseStart = Boolean.valueOf(jsonObject.getString("pauseStart"));
                        jsonObject2.put("pauseStart", jsonObject.getString("pauseStart"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        reloadTimer = Boolean.valueOf(jsonObject.getString("reloadTimer"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        backToHome = Boolean.valueOf(jsonObject.getString("backToHome"));
                        jsonObject2.put("backToHome", jsonObject.getString("backToHome"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setStartGame = Boolean.valueOf(jsonObject.getString("setStartGame"));
                        jsonObject2.put("setStartGame", jsonObject.getString("setStartGame"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        getGameActive = Boolean.valueOf(jsonObject.getString("gameActive"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        getSetColor = jsonObject.getString("setColor");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setColorActive = Integer.valueOf(jsonObject.getString("setColorActive"));
                        jsonObject2.put("setColorActive", jsonObject.getString("setColorActive"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setGameStart = Boolean.valueOf(jsonObject.getString("setGameStart"));
                        jsonObject2.put("setGameStart", jsonObject.getString("setGameStart"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setCounters0 = Integer.valueOf(jsonObject.getString("setCounters0"));
                        jsonObject2.put("setCounters0", jsonObject.getString("setCounters0"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setCounters1 = Integer.valueOf(jsonObject.getString("setCounters1"));
                        jsonObject2.put("setCounters1", jsonObject.getString("setCounters1"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setCounters2 = Integer.valueOf(jsonObject.getString("setCounters2"));
                        jsonObject2.put("setCounters2", jsonObject.getString("setCounters2"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setCounters3 = Integer.valueOf(jsonObject.getString("setCounters3"));
                        jsonObject2.put("setCounters3", jsonObject.getString("setCounters3"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonObject = new JSONObject(readMessage);
                        setDisableColor = Integer.valueOf(jsonObject.getString("setDisableColor"));
                        jsonObject2.put("setDisableColor", jsonObject.getString("setDisableColor"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(!jsonObject2.equals("")) {
                        sendCommandAll(String.valueOf(jsonObject2), "");
                    }

                    if(userTimeEnd){
                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.txt_end_time_user, Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);
                        snack.show();
                        my_time_end_text.setTextColor(ContextCompat.getColor(TimerActivityServer.this, R.color.my_time_end_color));
                    }

                    if(timeEnd){
                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.txt_end_time_total, Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);
                        snack.show();
                        my_time_end_text.setTextColor(ContextCompat.getColor(TimerActivityServer.this, R.color.my_time_end_color));
                    }

                    if(getGameActive){
                        gameActive = true;

                        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.start_game_true, Snackbar.LENGTH_LONG);
                        View view = snack.getView();
                        TextView textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);
                        snack.show();
                    }

                    if(!getSetColor.equals("")){
                        switch (getSetColor){
                            case "white":
                                setColor = 0;
                                LayoutWhiteTime.setVisibility(View.GONE);
                                iconMyTime.setImageResource(R.drawable.icon_my_color_white);
                                iconMyTime.setVisibility(View.VISIBLE);
                                break;
                            case "brown":
                                setColor = 1;
                                LayoutBrownTime.setVisibility(View.GONE);
                                iconMyTime.setImageResource(R.drawable.icon_my_color_brown);
                                iconMyTime.setVisibility(View.VISIBLE);
                                break;
                            case "black":
                                setColor = 2;
                                LayoutBlackTime.setVisibility(View.GONE);
                                iconMyTime.setImageResource(R.drawable.icon_my_color_black);
                                iconMyTime.setVisibility(View.VISIBLE);
                                break;
                        }
                    }

                    if(setGameStart){
                        gameStart = true;
                        onStartTimer();
                    }

                    if(setEnableColor != null){
                        disableColor[setEnableColor] = false;
                    }

                    if(setColorActive != null){
                        activeColor = setColorActive;

                        if(setColorActive == setColor){
                            gifMyTime.setVisibility(View.VISIBLE);
                            pngMyTime.setVisibility(View.GONE);

                            activeTimer = true;

                            if(vibroCheck) {
                                vibr.vibrate(pattern, -1);
                            }

                            if(!timer_sound.equals("")) {
                                mySound();
                            }
                        }
                        else{
                            gifMyTime.setVisibility(View.GONE);
                            pngMyTime.setVisibility(View.VISIBLE);

                            activeTimer = false;

                            if (myTimer != null)
                                myTimer.cancel();
                            myTimer = null;
                        }

                        counters[0] = setCounters0;
                        counters[1] = setCounters1;
                        counters[2] = setCounters2;
                        counters[3] = setCounters3;

                        onStartTimer();
                    }

                    if(setDisableColor != null){
                        disableColor[setDisableColor] = true;
                    }

                    if(setStopGame){
                        gifMyTime.setVisibility(View.GONE);
                        pngMyTime.setVisibility(View.VISIBLE);

                        activeTimer = false;

                        if (myTimer != null)
                            myTimer.cancel();
                        myTimer = null;
                    }

                    if(pauseStart){
                        gifMyTime.setVisibility(View.GONE);
                        pngMyTime.setVisibility(View.VISIBLE);
                        layoutPause.setVisibility(View.VISIBLE);

                        counters[0] = setCounters0;
                        counters[1] = setCounters1;
                        counters[2] = setCounters2;
                        counters[3] = setCounters3;

                        activeTimer = false;

                        if (myTimer != null)
                            myTimer.cancel();
                        myTimer = null;

                        total_time_pause.setText(getCountToElem(counters[0]));
                        white_time_pause.setText(getCountToElem(counters[1]));
                        brown_time_pause.setText(getCountToElem(counters[2]));
                        black_time_pause.setText(getCountToElem(counters[3]));

                        Log.d("mylog", "getCountToElem2: "+getCountToElem(counters[0]));
                    }

                    if(setStartGame){
                        layoutPause.setVisibility(View.GONE);

                        if(activeColor == setColor){
                            gifMyTime.setVisibility(View.VISIBLE);
                            pngMyTime.setVisibility(View.GONE);

                            activeTimer = true;

                            if(vibroCheck) {
                                vibr.vibrate(pattern, -1);
                            }

                            if(!timer_sound.equals("")) {
                                mySound();
                            }
                        }

                        onStartTimer();
                    }

                    if(backToHome){
                        if(ringtone.isPlaying()){
                            ringtone.stop();
                        }

                        vibr.cancel();

                        if (myTimer != null)
                            myTimer.cancel();
                        myTimer = null;

                        Intent intent = new Intent(TimerActivityServer.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);

                    break;
                case MESSAGE_TOAST:

                    break;
            }
        }
    };

    private void connectDevice(String deviceAddr2, String deviceAddr3) {
        Log.d("mylog", "connectDevice");

        BluetoothDevice device1 = mBluetoothAdapter.getRemoteDevice(deviceAddr2);
        BluetoothDevice device2 = mBluetoothAdapter.getRemoteDevice(deviceAddr3);
        // Attempt to connect to the device

        mTimerService.connect(device1);
        mTimerService2.connect(device2);
    }

    private void connectDevice1(String deviceAddr) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddr);
        // Attempt to connect to the device

        mTimerService.connect(device);
    }

    private void connectDevice2(String deviceAddr) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddr);
        // Attempt to connect to the device

        mTimerService2.connect(device);
    }

    private Boolean sendCommandAll(String message1, String message2){
        if (!message1.equals("")) {
            if (mTimerService.getState() != TimerService2.STATE_CONNECTED) {
                return false;
            }
        }

        if (!message2.equals("")) {
            if (mTimerService2.getState() != TimerService2.STATE_CONNECTED) {
                return false;
            }
        }

        if (message1.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message1.getBytes();
            mTimerService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);

            mOutStringBuffer = new StringBuffer("");
        }

        if (message2.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message2.getBytes();
            mTimerService2.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer2.setLength(0);

            mOutStringBuffer2 = new StringBuffer("");
        }

        return true;
    }

    private void mySound(){
        if(ringtone.isPlaying()){
            ringtone.stop();
        }

        ringtone.play();
    }

    private void onStartTimer() {
        Log.d("mylog", "onStartTimer - activeColor - "+activeColor);

        if (myTimer != null)
            myTimer.cancel();
        myTimer = null;

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }
        }, 0, 1000);
    }

    private void TimerMethod() {
        switch(activeColor){
            case 0: this.runOnUiThread(Timer_Tick_White);
                break;
            case 1: this.runOnUiThread(Timer_Tick_Brown);
                break;
            default: this.runOnUiThread(Timer_Tick_Black);
        }
    }

    private void runColor(int index){
        counters[0]++;
        counters[index]++;

        if((counters[index]) > time_game_user && time_game_user > 0) {
            JSONObject jsonObject = new JSONObject();
            int oldActiveColor = 0;

            try {
                jsonObject.put("setDisableColor", setColor);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(activeTimer) {
                oldActiveColor = activeColor;

                activeColor++;

                if (activeColor > 2) {
                    activeColor = 0;
                }

                if (disableColor[activeColor]) {
                    activeColor++;
                }

                if (activeColor > 2) {
                    activeColor = 0;
                }

                disableColor[setColor] = true;

                try {
                    jsonObject.put("setColorActive", activeColor);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    jsonObject.put("setCounters0", counters[0]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    jsonObject.put("setCounters1", counters[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    jsonObject.put("setCounters2", counters[2]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    jsonObject.put("setCounters3", counters[3]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(sendCommandAll(String.valueOf(jsonObject), String.valueOf(jsonObject))){
                if(ringtone.isPlaying()){
                    ringtone.stop();
                }

                vibr.cancel();

                if(activeTimer) {
                    gifMyTime.setVisibility(View.GONE);
                    pngMyTime.setVisibility(View.VISIBLE);

                    activeTimer = false;

                    if (vibroCheck) {
                        vibr.vibrate(pattern, -1);
                    }

                    if (myTimer != null)
                        myTimer.cancel();
                    myTimer = null;
                }

                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.txt_end_time_user, Snackbar.LENGTH_LONG);
                View view = snack.getView();
                TextView textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);
                snack.show();

                my_time_end_text.setTextColor(ContextCompat.getColor(this, R.color.my_time_end_color));
            }
            else{
                activeColor = oldActiveColor;
                disableColor[setColor] = false;
            }
        }

        if((counters[0]) > time_game && time_game > 0) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("setStopGame", "true");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                jsonObject.put("timeEnd", "true");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(sendCommandAll(String.valueOf(jsonObject), String.valueOf(jsonObject))){
                if (!ringtone.isPlaying() && !"".equals(timer_sound)) {
                    mySound();
                }

                if (vibroCheck) {
                    vibr.vibrate(pattern, -1);
                }

                gifMyTime.setVisibility(View.GONE);
                pngMyTime.setVisibility(View.VISIBLE);

                activeTimer = false;

                if (myTimer != null)
                    myTimer.cancel();
                myTimer = null;

                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.txt_end_time_total, Snackbar.LENGTH_LONG);
                View view = snack.getView();
                TextView textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);
                snack.show();

                my_time_end_text.setTextColor(ContextCompat.getColor(this, R.color.my_time_end_color));

                return;
            }
        }

        if(time_game > 0){
            my_time_end_text.setText(getCountToElem(time_game - counters[0]));
        }

        countToElem(counters[index]);
    }

    private Runnable Timer_Tick_White = new Runnable() {
        public void run() {
            runColor(1);
        }
    };

    private Runnable Timer_Tick_Brown = new Runnable() {
        public void run() {
            runColor(2);
        }
    };

    private Runnable Timer_Tick_Black = new Runnable() {
        public void run() {
            runColor(3);
        }
    };

    private void countToElem(int conter){
        String out[] = new String[11];
        int hour, min, sec;
        hour = conter / 3600;

        if(hour > 999){
            conter = 0;
            hour = 0;
        }

        out[0] = Integer.toString(hour);
        out[1] = Integer.toString(hour / 100 % 10);
        out[2] = Integer.toString(hour / 10 % 10);
        out[3] = Integer.toString(hour % 10);

        min = conter / 60 % 60;
        out[4] = Integer.toString(min);
        out[5] = Integer.toString(min / 10 % 10);
        out[6] = Integer.toString(min % 10);

        sec = conter % 60;
        out[7] = Integer.toString(sec);
        out[8] = Integer.toString(sec / 10 % 10);
        out[9] = Integer.toString(sec % 10);

        out[10] = out[1] + out[2] + out[3] + ":" + out[5] + out[6] + ":" + out[8] + out[9];

        if(activeColor == 0){
            if(setColor == 0){
                my_time_text.setText(out[10]);

                if(time_game_user > 0){
                    my_time_end_text.setText(getCountToElem(time_game_user - conter));
                }
            }
            else{
                textViewWhiteTime.setText(out[10]);
            }
        }
        else if(activeColor == 1){
            if(setColor == 1){
                my_time_text.setText(out[10]);

                if(time_game_user > 0){
                    my_time_end_text.setText(getCountToElem(time_game_user - conter));
                }
            }
            else{
                textViewBrownTime.setText(out[10]);
            }
        }
        else{
            if(setColor == 2){
                my_time_text.setText(out[10]);

                if(time_game_user > 0){
                    my_time_end_text.setText(getCountToElem(time_game_user - conter));
                }
            }
            else{
                textViewBlackTime.setText(out[10]);
            }
        }

        conter = counters[0];
        hour = conter / 3600;
        String arr[] = new String[11];

        if(hour > 999){
            conter = 0;
            hour = 0;
        }

        arr[0] = Integer.toString(hour);
        arr[1] = Integer.toString(hour / 100 % 10);
        arr[2] = Integer.toString(hour / 10 % 10);
        arr[3] = Integer.toString(hour % 10);

        min = conter / 60 % 60;
        arr[4] = Integer.toString(min);
        arr[5] = Integer.toString(min / 10 % 10);
        arr[6] = Integer.toString(min % 10);

        sec = conter % 60;
        arr[7] = Integer.toString(sec);
        arr[8] = Integer.toString(sec / 10 % 10);
        arr[9] = Integer.toString(sec % 10);

        arr[10] = arr[1] + arr[2] + arr[3] + ":" + arr[5] + arr[6] + ":" + arr[8] + arr[9];

        textViewAllTime.setText(arr[10]);
    }

    private String getCountToElem(int conter){
        String out[] = new String[11];
        int hour, min, sec;
        hour = conter / 3600;

        if(hour > 999){
            conter = 0;
            hour = 0;
        }

        out[0] = Integer.toString(hour);
        out[1] = Integer.toString(hour / 100 % 10);
        out[2] = Integer.toString(hour / 10 % 10);
        out[3] = Integer.toString(hour % 10);

        min = conter / 60 % 60;
        out[4] = Integer.toString(min);
        out[5] = Integer.toString(min / 10 % 10);
        out[6] = Integer.toString(min % 10);

        sec = conter % 60;
        out[7] = Integer.toString(sec);
        out[8] = Integer.toString(sec / 10 % 10);
        out[9] = Integer.toString(sec % 10);

        out[10] = out[1] + out[2] + out[3] + ":" + out[5] + out[6] + ":" + out[8] + out[9];

        return out[10];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setConnect(View view) {
        Log.d("mylog", "status_connect_device1: "+status_connect_device1);
        if (!status_connect_device1) {
            connectDevice1(device_addr2);
            Log.d("mylog", "device_addr2: "+device_addr2);
        }
    }

    public void setConnect2(View view) {
        Log.d("mylog", "status_connect_device2: "+status_connect_device2);
        if (!status_connect_device2) {
            connectDevice2(device_addr3);
            Log.d("mylog", "device_addr3: "+device_addr3);
        }
    }

    public void startConnectDevice1(View view) {
        setConnect(view);
    }

    public void startConnectDevice2(View view) {
        setConnect2(view);
    }
}
