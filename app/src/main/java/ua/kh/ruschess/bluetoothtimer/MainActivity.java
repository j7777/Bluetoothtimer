package ua.kh.ruschess.bluetoothtimer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private CoordinatorLayout rootLayout;

    private BluetoothAdapter mBluetoothAdapter = null;
    private Boolean enable_bt_adapter_flag = false;
    ArrayList<HashMap<String, String>> deviceList = new ArrayList<HashMap<String, String>>();
    private static final int REQUEST_ENABLE_BT = 3;
    private DeviceAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView recyclerView;

    private String device_name1 = null;
    private String device_name2 = null;
    private String device_name3 = null;
    private String device_addr1 = null;
    private String device_addr2 = null;
    private String device_addr3 = null;

    private TimerService mTimerService = null;
    private TimerService2 mTimerService2 = null;

    private StringBuffer mOutStringBuffer;
    private StringBuffer mOutStringBuffer2;

    private String mConnectedDeviceName = null;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";

    private Boolean connected_device1 = false;
    private Boolean connected_device2 = false;

    private com.github.clans.fab.FloatingActionButton fab1;
    private com.github.clans.fab.FloatingActionButton fab2;
    private com.github.clans.fab.FloatingActionButton fab3;
    private com.github.clans.fab.FloatingActionButton fab4;
    private com.github.clans.fab.FloatingActionButton fab5;

    private FloatingActionMenu menuLabels;

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

    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {
            startActivity(new Intent(MainActivity.this, InfoActivity.class));
            finish();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        rootLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

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

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.device_contentView);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        recyclerView = (RecyclerView) findViewById(R.id.scrollableview);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        device_name1 = mBluetoothAdapter.getName();
        device_addr1 = mBluetoothAdapter.getAddress();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(deviceList.get(position).get("sel") == "sel") {
                            deviceList.get(position).put("sel", "");
                            deviceList.get(position).put("back_anim", "anim");
                        }
                        else{
                            deviceList.get(position).put("sel", "sel");
                        }

                        for(int i = 0; i < deviceList.size(); i++){
                            if(i != position) {
                                if (deviceList.get(i).get("back_anim") == "anim") {
                                    deviceList.get(i).put("back_anim", "");
                                }
                            }
                        }

                        adapter = new DeviceAdapter(deviceList);
                        recyclerView.setAdapter(adapter);

                        device_name2 = null;
                        device_name3 = null;
                        device_addr2 = null;
                        device_addr3 = null;

                        for(int i = 0; i < deviceList.size(); i++){
                            if(deviceList.get(i).get("sel") == "sel"){
                                if(device_addr2 == null){
                                    device_name2 = deviceList.get(i).get("name");
                                    device_addr2 = deviceList.get(i).get("addr");
                                }
                                else{
                                    device_name3 = deviceList.get(i).get("name");
                                    device_addr3 = deviceList.get(i).get("addr");
                                    break;
                                }
                            }
                        }

                        if(device_addr2 != null && device_addr3 != null){
                            connectDevice(device_addr2, device_addr3);
                        }

                        Log.d("mylog", "device_name2: "+device_name2);
                        Log.d("mylog", "device_name3: "+device_name3);
                        Log.d("mylog", "device_addr2: "+device_addr2);
                        Log.d("mylog", "device_addr3: "+device_addr3);
                    }
                })
        );

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        menuLabels = (FloatingActionMenu) findViewById(R.id.menu_labels);

        fab1 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab4);
        fab5 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab5);

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLabels.toggle(true);

                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);

                        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                        deviceList = new ArrayList<HashMap<String, String>>();

                        if (pairedDevices.size() > 0) {
                            HashMap<String, String> hm;
                            for (BluetoothDevice device : pairedDevices) {
                                hm = new HashMap<>();
                                hm.put("name", device.getName());
                                hm.put("addr", device.getAddress());
                                hm.put("sel", "");
                                hm.put("back_anim", "");
                                hm.put("color", String.valueOf(getRandomMaterialColor("400")));
                                deviceList.add(hm);
                            }

                            adapter = new DeviceAdapter(deviceList);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.no_connect_device, Snackbar.LENGTH_LONG);
                            View view = snack.getView();
                            TextView textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);
                            snack.show();
                        }

                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        fab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuLabels.toggle(true);

                if (mTimerService != null) {
                    mTimerService.stop();
                }

                if (mTimerService2 != null) {
                    mTimerService2.stop();
                }

                /*mBluetoothAdapter.disable();
                mBluetoothAdapter.enable();

                if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivity(discoverableIntent);
                }*/

                finish();
                startActivity(getIntent());
                setupTimer();
            }
        });

        final Snackbar snackBarExit = Snackbar.make(findViewById(android.R.id.content), R.string.dialog_confirm_close2, Snackbar.LENGTH_LONG);

        snackBarExit.setAction(R.string.dialog_confirm_close_ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBarExit.show();
            }
        });

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Intent intent = new Intent(MainActivity.this, BluetoothAdapterErrorActivity.class);
            startActivity(intent);
        }

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            HashMap<String, String> hm;
            for (BluetoothDevice device : pairedDevices) {
                hm = new HashMap<>();
                hm.put("name", device.getName());
                hm.put("addr", device.getAddress());
                hm.put("sel", "");
                hm.put("back_anim", "");
                hm.put("color", String.valueOf(getRandomMaterialColor("400")));
                deviceList.add(hm);
            }

            adapter = new DeviceAdapter(deviceList);
            recyclerView.setAdapter(adapter);
        } else {
            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.no_connect_device, Snackbar.LENGTH_LONG);
            View view = snack.getView();
            TextView textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);
            snack.show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mTimerService == null || mTimerService2 == null) {
            setupTimer();
        }

        if(requestCode == REQUEST_ENABLE_BT){
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            deviceList = new ArrayList<HashMap<String, String>>();

            if (pairedDevices.size() > 0) {
                HashMap<String, String> hm;
                for (BluetoothDevice device : pairedDevices) {
                    hm = new HashMap<>();
                    hm.put("name", device.getName());
                    hm.put("addr", device.getAddress());
                    hm.put("sel", "");
                    hm.put("back_anim", "");
                    hm.put("color", String.valueOf(getRandomMaterialColor("400")));
                    deviceList.add(hm);
                }

                adapter = new DeviceAdapter(deviceList);
                recyclerView.setAdapter(adapter);
            } else {
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.no_connect_device, Snackbar.LENGTH_LONG);
                View view = snack.getView();
                TextView textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);
                snack.show();
            }

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        deviceList = new ArrayList<HashMap<String, String>>();

        if (pairedDevices.size() > 0) {
            HashMap<String, String> hm;
            for (BluetoothDevice device : pairedDevices) {
                hm = new HashMap<>();
                hm.put("name", device.getName());
                hm.put("addr", device.getAddress());
                hm.put("sel", "");
                hm.put("back_anim", "");
                hm.put("color", String.valueOf(getRandomMaterialColor("400")));
                deviceList.add(hm);
            }

            adapter = new DeviceAdapter(deviceList);
            recyclerView.setAdapter(adapter);
        } else {
            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.no_connect_device, Snackbar.LENGTH_LONG);
            View view = snack.getView();
            TextView textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);
            snack.show();
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            enable_bt_adapter_flag = true;
        }
        else if(mTimerService == null || mTimerService2 == null) {
            setupTimer();
        }

        mSwipeRefreshLayout.setRefreshing(true);

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        deviceList = new ArrayList<HashMap<String, String>>();

        if (pairedDevices.size() > 0) {
            HashMap<String, String> hm;
            for (BluetoothDevice device : pairedDevices) {
                hm = new HashMap<>();
                hm.put("name", device.getName());
                hm.put("addr", device.getAddress());
                hm.put("sel", "");
                hm.put("back_anim", "");
                hm.put("color", String.valueOf(getRandomMaterialColor("400")));
                deviceList.add(hm);
            }

            adapter = new DeviceAdapter(deviceList);
            recyclerView.setAdapter(adapter);
        } else {
            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.no_connect_device, Snackbar.LENGTH_LONG);
            View view = snack.getView();
            TextView textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);
            snack.show();
        }

        mSwipeRefreshLayout.setRefreshing(false);

        layoutLoading.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimerService != null) {
            mTimerService.stop();
        }

        if (mTimerService2 != null) {
            mTimerService2.stop();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

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

    private void setupTimer() {
        mTimerService = new TimerService(MainActivity.this, mHandler);
        mTimerService2 = new TimerService2(MainActivity.this, mHandler2);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
        mOutStringBuffer2 = new StringBuffer("");
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case TimerService.STATE_CONNECTED:
                            connected_device1 = true;

                            Log.d("mylog", "STATE_CONNECTED - "+mConnectedDeviceName);

                            if(device_addr2 != null && device_addr3 != null){
                                if(connected_device1 && connected_device2) {
                                    if(sendCommandAll("go_timer", "go_timer")){
                                        mTimerService.stop();
                                        mTimerService2.stop();
                                        mTimerService = null;
                                        mTimerService2 = null;

                                        Intent intent = new Intent(MainActivity.this, StepOneActivity.class);
                                        intent.putExtra("device_name1", device_name1);
                                        intent.putExtra("device_name2", device_name2);
                                        intent.putExtra("device_name3", device_name3);
                                        intent.putExtra("device_addr1", device_addr1);
                                        intent.putExtra("device_addr2", device_addr2);
                                        intent.putExtra("device_addr3", device_addr3);
                                        intent.putExtra("enable_bt_adapter_flag", enable_bt_adapter_flag);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }

                            name_success_device1.setText(device_name2);
                            connect_run_device1.setVisibility(View.GONE);
                            connect_success_device1.setVisibility(View.VISIBLE);

                            break;
                        case TimerService.STATE_CONNECTING:
                            Log.d("mylog", "STATE_CONNECTING - "+mConnectedDeviceName);
                            name_run_device1.setText(device_name2);
                            connect_run_device1.setVisibility(View.VISIBLE);
                            connect_success_device1.setVisibility(View.GONE);
                            connect_error_device1.setVisibility(View.GONE);
                            break;
                        case TimerService.STATE_LISTEN:
                        case TimerService.STATE_NONE:
                            name_error_device1.setText(device_name2);
                            connect_run_device1.setVisibility(View.GONE);
                            connect_success_device1.setVisibility(View.GONE);
                            connect_error_device1.setVisibility(View.VISIBLE);

                            connected_device1 = false;

                            Log.d("mylog", "STATE_CONNECTED - "+mConnectedDeviceName);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    if(readMessage.equals("go_timer")) {
                        mTimerService.stop();
                        mTimerService = null;

                        Intent intent = new Intent(MainActivity.this, TimerActivityClient1.class);
                        intent.putExtra("enable_bt_adapter_flag", enable_bt_adapter_flag);
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
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case TimerService.STATE_CONNECTED:
                            connected_device2 = true;

                            Log.d("mylog", "STATE_CONNECTED2 - "+mConnectedDeviceName);

                            if(device_addr2 != null && device_addr3 != null){
                                if(connected_device1 && connected_device2) {
                                    if(sendCommandAll("go_timer", "go_timer")){
                                        mTimerService.stop();
                                        mTimerService2.stop();
                                        mTimerService = null;
                                        mTimerService2 = null;

                                        Intent intent = new Intent(MainActivity.this, StepOneActivity.class);
                                        intent.putExtra("device_name1", device_name1);
                                        intent.putExtra("device_name2", device_name2);
                                        intent.putExtra("device_name3", device_name3);
                                        intent.putExtra("device_addr1", device_addr1);
                                        intent.putExtra("device_addr2", device_addr2);
                                        intent.putExtra("device_addr3", device_addr3);
                                        intent.putExtra("enable_bt_adapter_flag", enable_bt_adapter_flag);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }

                            name_success_device2.setText(device_name3);
                            connect_run_device2.setVisibility(View.GONE);
                            connect_success_device2.setVisibility(View.VISIBLE);

                            break;
                        case TimerService.STATE_CONNECTING:
                            Log.d("mylog", "STATE_CONNECTING2 - "+mConnectedDeviceName);
                            name_run_device2.setText(device_name3);
                            connect_run_device2.setVisibility(View.VISIBLE);
                            connect_success_device2.setVisibility(View.GONE);
                            connect_error_device2.setVisibility(View.GONE);
                            break;
                        case TimerService.STATE_LISTEN:
                        case TimerService.STATE_NONE:
                            name_error_device2.setText(device_name3);
                            connect_run_device2.setVisibility(View.GONE);
                            connect_success_device2.setVisibility(View.GONE);
                            connect_error_device2.setVisibility(View.VISIBLE);

                            connected_device2 = false;

                            Log.d("mylog", "STATE_NONE2 - "+mConnectedDeviceName);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    if(readMessage.equals("go_timer")) {
                        mTimerService2.stop();
                        mTimerService2 = null;

                        Intent intent = new Intent(MainActivity.this, TimerActivityClient2.class);
                        intent.putExtra("enable_bt_adapter_flag", enable_bt_adapter_flag);
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
        BluetoothDevice device1 = mBluetoothAdapter.getRemoteDevice(deviceAddr2);
        BluetoothDevice device2 = mBluetoothAdapter.getRemoteDevice(deviceAddr3);
        // Attempt to connect to the device

        layoutLoading.setVisibility(View.VISIBLE);

        mTimerService.connect(device1);
        mTimerService2.connect(device2);
    }

    private Boolean sendCommandAll(String message1, String message2){
        if ((mTimerService.getState() != TimerService2.STATE_CONNECTED) || (mTimerService2.getState() != TimerService2.STATE_CONNECTED)) {
            return false;
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

    public void closeLoading(View view) {
        mSwipeRefreshLayout.setRefreshing(true);

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        deviceList = new ArrayList<HashMap<String, String>>();

        if (pairedDevices.size() > 0) {
            HashMap<String, String> hm;
            for (BluetoothDevice device : pairedDevices) {
                hm = new HashMap<>();
                hm.put("name", device.getName());
                hm.put("addr", device.getAddress());
                hm.put("sel", "");
                hm.put("back_anim", "");
                hm.put("color", String.valueOf(getRandomMaterialColor("400")));
                deviceList.add(hm);
            }

            adapter = new DeviceAdapter(deviceList);
            recyclerView.setAdapter(adapter);
        } else {
            Snackbar snack = Snackbar.make(findViewById(android.R.id.content), R.string.no_connect_device, Snackbar.LENGTH_LONG);
            view = snack.getView();
            TextView textView = (TextView)view .findViewById(android.support.design.R.id.snackbar_text);
            snack.show();
        }

        mSwipeRefreshLayout.setRefreshing(false);

        layoutLoading.setVisibility(View.GONE);
    }

    /**
     * chooses a random color from array.xml
     */
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }
}
