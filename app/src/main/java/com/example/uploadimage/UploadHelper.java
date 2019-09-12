package com.example.uploadimage;

import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.File;
import java.util.Date;

public class UploadHelper {
    //与个人的存储区域有关
    private static final String ENDPOINT = "http://oss-cn-hangzhou.aliyuncs.com";
    //上传仓库名
    private static final String BUCKET_NAME = "m-album";

    private static final String AccessKeyId = "STS.NKVSqyFm2X2A4uFUfoUwcLeYj";
    private static final String AccessKeySecret = "8WYuWGS2ftTtCY1on8WLvqGcm4JqqVpMDRHWFSb9Movt";
    private static final String SecurityToken = "CAISkwJ1q6Ft5B2yfSjIr4jjGMvNq7IT7/CqNlP3sWY6WfhPo6DyiDz2IH9LfnZpCOwctfs1mWhQ7/walqB6T55OSAmcNZIoVVSrHLnkMeT7oMWQweEuuv/MQBquaXPS2MvVfJ+OLrf0ceusbFbpjzJ6xaCAGxypQ12iN+/m6/Ngdc9FHHP7D1x8CcxROxFppeIDKHLVLozNCBPxhXfKB0ca3WgZgGhku6Ok2Z/euFiMzn+Ck7VP+NSvcsH6MJMxYM0kA+3YhrImKvDztwdL8AVP+atMi6hJxCzKpNn1ASMKvErcbLOFroc0c1UkPvBjQfZe3/H4lOxlvOvIjJjwyBtLMuxTXj7WWIe62szAFfMfv3z+yEkEURqAAWmAF3iil9zVH7WzquC6uQBFROOJebF+xgVqWZMgLT8Apq9ttI/4QHInd5dG93JRXClE/Lcg5ezNjRKXAR8dli/DSYllt3FvIvaL4e1P48knyRG1qS5uD+AsbY3QISx7z5xNdNG/kxpCFo1TUVw3Z+pDkqNRIPJLabkmAzLBuirO";

    private static OSS getOSSClient() {
        String stsServer = "http://152.136.134.200:9000";
        OSSCredentialProvider credentialProvider = new OSSAuthCredentialsProvider(stsServer);
        //config
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时时间，默认15秒
        conf.setSocketTimeout(15 * 1000); // Socket超时时间，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(LitePalApplication.getContext(), ENDPOINT, credentialProvider, conf);
        Log.d("upMessage-OSS", oss.toString());
        return oss;
//        OSSCredentialProvider credentialProvider =
//                new OSSStsTokenCredentialProvider(AccessKeyId,AccessKeySecret,SecurityToken);
//        return new OSSClient( LitePalApplication.getContext(), ENDPOINT, credentialProvider);
    }

    /**
     * 上传方法
     *
     * @param objectKey 标识
     * @param path      需上传文件的路径
     * @return 外网访问的路径
     */
    private static String upload(String objectKey, String path) {
        Log.d("upMessage", "上传方法");
        // 构造上传请求
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, objectKey, path);
        Log.d("upMessage-1", request.toString());
        try {
            //得到client
            OSS client = getOSSClient();
            Log.d("upMessage-2", client.toString());
            //上传获取结果
            PutObjectResult result = client.putObject(request);
            //获取可访问的url
            String url = client.presignPublicObjectURL(BUCKET_NAME, objectKey);
            //格式打印输出
            Log.d("upMessage-3", result.toString());
            Log.d("upMessage-4", url);
            if (url.substring(0,4).equals("http"))
                Toast.makeText(LitePalApplication.getContext(),"上传成功",Toast.LENGTH_SHORT).show();
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("upMessageError", e.toString());
            Toast.makeText(LitePalApplication.getContext(), "错误：" + e.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    /**
     * 上传普通图片
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadImage(String path) {
        Log.d("upMessage", "上传普通图片的地址：" + path);
        String key = getObjectImageKey(path);
        Log.d("upMessage", "上传普通图片的key：" + key);
        return upload(key, path);
    }

    /**
     * 上传头像
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadPortrait(String path) {
        String key = getObjectPortraitKey(path);
        return upload(key, path);
    }

    /**
     * 上传audio
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadAudio(String path) {
        String key = getObjectAudioKey(path);
        return upload(key, path);
    }


    /**
     * 获取时间
     *
     * @return 时间戳 例如:201805
     */
    private static String getDateString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }

    /**
     * 返回key
     *
     * @param path 本地路径
     * @return key
     */
    //格式: image/201805/sfdsgfsdvsdfdsfs.jpg
    private static String getObjectImageKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("image/%s/%s.jpg", dateString, fileMd5);
    }

    //格式: portrait/201805/sfdsgfsdvsdfdsfs.jpg
    private static String getObjectPortraitKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("portrait/%s/%s.jpg", dateString, fileMd5);
    }

    //格式: audio/201805/sfdsgfsdvsdfdsfs.mp3
    private static String getObjectAudioKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("audio/%s/%s.mp3", dateString, fileMd5);
    }

}