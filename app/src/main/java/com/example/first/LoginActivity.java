package com.example.first;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class LoginActivity extends AppCompatActivity {
    //1.定义控件对象
    private EditText etUsername;
    private EditText etPassword;
    private CheckBox cbRemember;
    private Button btnLogin;
    private String fileName = "login.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //2.获取控件对象
        etUsername = findViewById(R.id.et_userName);
        etPassword = findViewById(R.id.et_password);
        cbRemember = findViewById(R.id.cb_remember);

        //3.获取存储的用户信息，若有则写入
        //final String username = readPrfs();
        //String username = readData(fileName);
        String username = readPrivateExStorage(fileName);
        if (username != null){
            etUsername.setText(username);
        }

        //4.设置登录按钮的点击事件的监听器
        Button btnLogin = findViewById(R.id.btn_login);
        //5.处理点击事件：
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (cbRemember.isChecked()){
                    savePref(username);
                    saveData(fileName,username);
                    readData(fileName);
                    saveDataPrivate(fileName,username);
                    readPrivateExStorage(fileName);
                }else{
                    clearPref();
                    deleteDataFile(fileName);
                    deletePrivateExStorage(fileName);
                }
                if("ding".equals(username)&&"123".equals(password)){
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("username",username);
                    startActivity(intent);

                }else {
                    Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                    etUsername.setFocusable(true);
                }


            }

            private void saveDataPrivate(String fileName, String username) {
                try {
                    //1.打开文件输出流
                    File file = new File(getExternalFilesDir(""),fileName);
                    FileOutputStream out = new FileOutputStream(file);
                    //2.创建BufferedWriter对象
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                    //3.写入数据
                    writer.write(username);
                    //4.关闭输出流
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            private void saveData(String fileName, String username) {
                //内部存储目录：data/data/包名/files/
                try {
                    //1.打开文件输出流
                    FileOutputStream out = openFileOutput(fileName,MODE_PRIVATE);
                    //2.创建BufferedWriter对象
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                    //3.写入数据
                    writer.write(username);
                    //4.关闭输出流
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String readPrivateExStorage(String fileName) {
        String data = null;
        try {
            //打开文件输出流
            File file = new File(getExternalFilesDir(""), fileName);//path+fileName
            FileInputStream in = new FileInputStream(file);
            //创建BufferedWriter对象
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            //读出数据
            data = reader.readLine();
            //关闭输入流
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;

    }

    private String readData(String fileName) {
        String data = null;
        try {
            FileInputStream in = openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            data = reader.readLine();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    // 删除内部存储文件
    private void deleteDataFile(String fileName) {
        if (deleteFile(fileName)) {
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
        }
    }


    private void clearPref(){
        SharedPreferences.Editor editor = getSharedPreferences("username",MODE_PRIVATE).edit();
        editor.clear().apply();
    }
    private void savePref(String username){
        SharedPreferences.Editor editor = getSharedPreferences("userInfo",MODE_PRIVATE).edit();
        editor.putString("username",username);
        editor.apply();
    }

    private String readPrfs() {
        SharedPreferences sp = getSharedPreferences("userInfo",MODE_PRIVATE);
        return sp.getString("usermane","");
    }

    // 删除外部私有存储的文件
    private void deletePrivateExStorage(String fileName) {
        File file = new File(getExternalFilesDir(""), fileName);
        if (file.isFile()) {
            if (file.delete()) {
                Toast.makeText(this, "删除外部公有文件成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "删除外部公有文件失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
