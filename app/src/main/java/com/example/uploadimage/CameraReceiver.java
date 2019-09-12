package com.example.uploadimage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

public class CameraReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("upMessage-广播", "你拍照了");
        Cursor cursor = context.getContentResolver().query(intent.getData(),
                null, null, null, null);
        cursor.moveToFirst();
        String image_path = cursor.getString(cursor.getColumnIndex("_data"));

        Toast.makeText(LitePalApplication.getContext(), "开始上传路径为：" + image_path + "的图片到OSS", Toast.LENGTH_SHORT).show();
        Log.d("upMessage-广播", "开始上传路径为：" + image_path + "的图片到OSS");

        UploadHelper uploadHelper = new UploadHelper();
        String uuu = uploadHelper.uploadImage(image_path);
        Log.d("upMessage-广播", uuu);

    }

}