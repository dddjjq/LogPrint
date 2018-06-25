package com.example.hndingyl.logtest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class MainActivity extends AppCompatActivity {
    private Button start;
    private CheckBox clearCheckBox;
    private boolean isChecked = false;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start =  findViewById(R.id.startLog);
        clearCheckBox = findViewById(R.id.clear);
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        clearCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isChecked = b;
                Log.d("dingyl","isChecked : " + isChecked);
                editor.putBoolean("ischecked",isChecked);
                editor.apply();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MyService.class);
                intent.putExtra("isChecked",isChecked);
                startService(intent);
                finish();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        clearCheckBox.setChecked(sharedPreferences.getBoolean("ischecked",false));
        Log.d("dingyl","activity isChecked : " + sharedPreferences.getBoolean("ischecked",false));
    }
}
