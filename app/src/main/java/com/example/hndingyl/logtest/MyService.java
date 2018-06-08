package com.example.hndingyl.logtest;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import java.lang.reflect.Method;

public class MyService extends Service{

    private String path = null;
    private String sdCardDir;
    private String fileName = "Log.txt";
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
        if(getVolumePaths(MyService.this).length > 1)
            sdCardDir = getVolumePaths(MyService.this)[1];
        path = sdCardDir + "/" + fileName;
        Log.d("dingyl",path);
        if(getVolumePaths(MyService.this).length>1) {
            Log.d("dingyl",getVolumePaths(MyService.this).length+"");
            Toast.makeText(MyService.this,"Print Log to : " + getVolumePaths(MyService.this)[1],Toast.LENGTH_SHORT).show();
            MyThread myThread = new MyThread();
            myThread.start();
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
                writeLog("logcat",path);
            }catch (IOException e){
                e.printStackTrace();
                Log.d("dingyl","IOException");
                stopSelf();
            }

        }
    }

    private void writeLog(String cmd,String path) throws IOException {
        Process process = Runtime.getRuntime().exec(cmd);
        InputStream stream = process.getInputStream();
        File file = new File(path);
        if(!file.exists()){
            file.createNewFile();
        }else{
            if(file.length() > 20*1024*1024){
                file.delete();
                file.createNewFile();
                Log.d("dingyl","File is too large!!!");
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(path,true);
        if(stream != null){
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(reader, 1024);
            int line;
            while (((line = bufferedReader.read())+"")!= null) {
                fileOutputStream.write(line);
            }
            bufferedReader.close();
            reader.close();
            stream.close();
            fileOutputStream.close();
            process.destroy();
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
