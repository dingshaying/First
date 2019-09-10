package com.example.first;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);

        Button btnSave = findViewById(R.id.btn_save_sd);
        Button btnRead = findViewById(R.id.btn_read_sd);

        btnSave.setOnClickListener(this);
        btnRead.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save_sd:
                saveToSD("demo.jpg");
                break;
            case R.id.btn_read_sd:
                readFromSD("a.jpg");
                break;
        }

    }

    private void readFromSD(String filename) {
        // 1. 申请读SD的权限，要求android的版本大于6.0（Build.VERSION_CODES.M）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
                return;
            }
        }
        // 1. 读取SD卡上的文件
        String path = Environment.getExternalStoragePublicDirectory("").getPath()
                + File.separator
                + Environment.DIRECTORY_PICTURES;
        File file = new File(path,filename);

        try {
            //2.创建file的文件输入流
            FileInputStream inputStream = new FileInputStream(file);
            //3.将文件流写入imageview
            imageView.setImageBitmap(BitmapFactory.decodeStream(inputStream));
            //4.
            // 关闭文件输入流
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //SD卡：外部的公有文件夹，需要申请运行时权限
    private void saveToSD(String filename) {
        //1.申请写SD的权限，要求android的版本大于6.0（Build.VERSION_CODES.M）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                return;
            }
        }
        // 写SD卡的步骤
        // 1. 获取SD的Download目录，创建需要存储的文件
        String path = Environment.getExternalStoragePublicDirectory("").getPath()
                + File.separator
                + Environment.DIRECTORY_PICTURES;
        File file = new File(path,filename);
        try {
            if (file.createNewFile()){
                //2.获取ImageView的Bitmap图片对象
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                //3.将Bitmap对象写入SD卡
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                //4.关闭请求的资源
                outputStream.flush();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将assets目录下的db文件写入/data/data/包名/databases/数据库文件名
    private void saveDBFile(String dbName) {
        String destPath = "/data" + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases";
        File filePath = new File(destPath);
        // 判断目录是否存在
        if(!filePath.exists()) {
            filePath.mkdirs();
        }

        // 创建目标目录的文件
        File file = new File(destPath, dbName);
        try {
            // 创建输入、输出流对象
            InputStream input = this.getAssets().open(dbName);
            FileOutputStream output = new FileOutputStream(file);

            // 将输入流的数据写入输出流（二进制流文件的通用写法）
            int len = -1;
            byte[] buffer = new byte[1024];

            while((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            output.flush();

            // 关闭输入、输出流
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"权限申请被拒绝", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (requestCode){
            case 1:
                saveToSD("demo.jpg");
                break;
        }
    }
}
