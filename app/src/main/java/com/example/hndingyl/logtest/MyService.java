package com.example.hndingyl.logtest;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.storage.StorageManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;

public class MyService extends Service{

    private String path = null;
    private String sdCardDir;
    private String fileName = "Log.txt";
    private boolean isServiceActive = false;
    private static final String clearLog = "logcat -c";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        sharedPreferences = getSharedPreferences("user",Context.MODE_PRIVATE);
        if(getVolumePaths(MyService.this).length > 1)
            sdCardDir = getVolumePaths(MyService.this)[1];
        path = sdCardDir + "/" + fileName;
        Log.d("dingyl",path);
        if(getVolumePaths(MyService.this).length>1) {
            Log.d("dingyl",getVolumePaths(MyService.this).length+"");
            Toast.makeText(MyService.this,"Print Log to : " + getVolumePaths(MyService.this)[1],Toast.LENGTH_SHORT).show();
            if(!isServiceActive) {
                Log.d("dingyl","MyThread start");
                MyThread myThread = new MyThread();
                myThread.start();
            }
        }else{
            Toast.makeText(MyService.this,"Please inser a USB Device First!",
                    Toast.LENGTH_SHORT).show();
        }
        return super.onStartCommand(intent,flags,startId);
    }

    class MyThread extends  Thread{

        public MyThread(){

        }

        @Override
        public void run(){
            try {
                writeLog("logcat -v time",path);
            }catch (IOException e){
                e.printStackTrace();
                Log.d("dingyl","IOException");
                stopSelf();
            }

        }
    }

    private void writeLog(String cmd,String path) throws IOException {
        if(sharedPreferences.getBoolean("isChecked",false)) {
            Runtime.getRuntime().exec(clearLog);
            Log.d("dingyl","service isChecked is : " + sharedPreferences.getBoolean("isChecked",false));
        }
        Process process = Runtime.getRuntime().exec(cmd);
        InputStream stream = process.getInputStream();
        File file = new File(path);
        if(!file.exists()){
            file.createNewFile();
        }else{
            if(file.length() > 50*1024*1024){
                file.delete();
                file.createNewFile();
                Log.d("dingyl","File is too large!!!");
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(path,true);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream,"UTF-8");
        if(stream != null){
            InputStreamReader reader = new InputStreamReader(stream,"UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader,1024);
            String line;
            while ((line = bufferedReader.readLine())!= null) {
                outputStreamWriter.write(line + "\n");
            }
            bufferedReader.close();
            reader.close();
            stream.close();
            fileOutputStream.close();
            outputStreamWriter.close();
            process.destroy();
            isServiceActive = false;
            Log.d("dingyl","release");
        }
    }

    public static String[] getVolumePaths(Context context) {
        String[] paths = null;
        StorageManager mStorageManager;
        Method mMethodGetPaths = null;
        try {
            mStorageManager = (StorageManager) context.getSystemService(Activity.STORAGE_SERVICE);
            mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
            paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paths;
    }
}
