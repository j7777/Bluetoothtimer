package ua.kh.ruschess.bluetoothtimer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StepOneActivity extends AppCompatActivity {
    private String device_name1 = null;
    private String device_name2 = null;
    private String device_name3 = null;
    private String device_addr1 = null;
    private String device_addr2 = null;
    private String device_addr3 = null;
    private String device_color2 = null;
    private Boolean enable_bt_adapter_flag;
    private TimerService mTimerService = null;
    private TimerService2 mTimerService2 = null;
    private TextView device_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_one);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        device_name = (TextView) findViewById(R.id.device_name);

        device_name1 = getIntent().getStringExtra("device_name1");
        device_name2 = getIntent().getStringExtra("device_name2");
        device_name3 = getIntent().getStringExtra("device_name3");
        device_addr1 = getIntent().getStringExtra("device_addr1");
        device_addr2 = getIntent().getStringExtra("device_addr2");
        device_addr3 = getIntent().getStringExtra("device_addr3");
        enable_bt_adapter_flag = getIntent().getExtras().getBoolean("enable_bt_adapter_flag");
        device_name.setText(device_name2);
    }

    public void clickWhite(View view) {
        device_color2 = "white";
        Intent intent = new Intent(StepOneActivity.this, StepTwoActivity.class);
        intent.putExtra("device_name1", device_name1);
        intent.putExtra("device_name2", device_name2);
        intent.putExtra("device_name3", device_name3);
        intent.putExtra("device_addr1", device_addr1);
        intent.putExtra("device_addr2", device_addr2);
        intent.putExtra("device_addr3", device_addr3);
        intent.putExtra("device_color2", device_color2);
        intent.putExtra("enable_bt_adapter_flag", enable_bt_adapter_flag);
        startActivity(intent);
        finish();
    }

    public void clickBrown(View view) {
        device_color2 = "brown";
        Intent intent = new Intent(StepOneActivity.this, StepTwoActivity.class);
        intent.putExtra("device_name1", device_name1);
        intent.putExtra("device_name2", device_name2);
        intent.putExtra("device_name3", device_name3);
        intent.putExtra("device_addr1", device_addr1);
        intent.putExtra("device_addr2", device_addr2);
        intent.putExtra("device_addr3", device_addr3);
        intent.putExtra("device_color2", device_color2);
        intent.putExtra("enable_bt_adapter_flag", enable_bt_adapter_flag);
        startActivity(intent);
        finish();
    }

    public void clickBlack(View view) {
        device_color2 = "black";
        Intent intent = new Intent(StepOneActivity.this, StepTwoActivity.class);
        intent.putExtra("device_name1", device_name1);
        intent.putExtra("device_name2", device_name2);
        intent.putExtra("device_name3", device_name3);
        intent.putExtra("device_addr1", device_addr1);
        intent.putExtra("device_addr2", device_addr2);
        intent.putExtra("device_addr3", device_addr3);
        intent.putExtra("device_color2", device_color2);
        intent.putExtra("enable_bt_adapter_flag", enable_bt_adapter_flag);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(StepOneActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
