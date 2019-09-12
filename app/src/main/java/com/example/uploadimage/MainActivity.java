package com.example.uploadimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO = 1;
    private Uri imageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputImage = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/DCIM/Camera/"+System.currentTimeMillis() + ".jpg");
                if (!outputImage.getParentFile().exists()){
                    outputImage.getParentFile().mkdirs();
                }
                Log.d("upMessage-拍照图片路径", outputImage.getPath());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.myapplication.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
        CameraReceiver cameraReceiver=new CameraReceiver();
        IntentFilter filter=new IntentFilter();
        filter.setPriority(1000);//优先级
        filter.addAction("com.android.camera.NEW_PICTURE");//动作
        filter.addAction("android.hardware.action.NEW_PICTURE");//动作
        try {
            filter.addDataType("image/*");//动作
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        registerReceiver(cameraReceiver, filter);//注册广播

    }



    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Log.d("upMessage-拍照路径", "/storage/emulated/0" + imageUri.getPath().substring(15, imageUri.getPath().length()));
                    }else {
                        Log.d("upMessage-拍照路径", imageUri.getPath());
                    }
                    uploadImage();
                }
                break;
            default:
                break;
        }
    }

    private void uploadImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }).start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(MainActivity.this,"开始上传",Toast.LENGTH_SHORT).show();
                UploadHelper uploadHelper = new UploadHelper();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uploadHelper.uploadImage("/storage/emulated/0" + imageUri.getPath().substring(15, imageUri.getPath().length()));
                } else {
                    uploadHelper.uploadImage(imageUri.getPath());
                }
            }
        }
    };
}
