package com.thanhtuan.delivery.view.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.rey.material.widget.CheckBox;
import com.thanhtuan.delivery.R;
import com.thanhtuan.delivery.data.remote.ApiHelper;
import com.thanhtuan.delivery.data.remote.JsonRequest;
import com.thanhtuan.delivery.model.User;
import com.thanhtuan.delivery.util.SharePreferenceUtil;
import com.thanhtuan.delivery.util.NetworkUtils;
import com.thanhtuan.delivery.util.NewtonLoadingUtil;
import com.victor.loading.newton.NewtonCradleLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edtUserName) EditText edtUserName;
    @BindView(R.id.edtPassword) EditText edtPassword;
    @BindView(R.id.btnLogin)    Button btnLogin;
    @BindView(R.id.ckbSave)     CheckBox ckbSaveUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        addViews();
        addEvents();
    }

    private void addViews() {
        SharePreferenceUtil.loadUser(this,edtUserName,edtPassword);
    }

    private void addEvents() {
        JsonRequest.Request(getApplication(), null, ApiHelper.ApiVersion(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("Success")){
                        Log.e("LoginActivity", "onResponse: " + response);
                    }else {
                        Log.e("LoginActivity", "onResponse: " + "ERR");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Check connect internet*/
                if (!NetworkUtils.isNetworkAvailable(getApplication())){
                    Toast.makeText(LoginActivity.this, "Bạn chưa bật kết nối mạng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                /*Set view cho NewtonLoadingUtil*/
                btnLogin.setText("Waiting...");

                HashMap<String, String> param = ApiHelper.paramLoGin(
                        edtUserName.getText().toString(),
                        edtPassword.getText().toString()
                );

                JsonRequest.Request(getApplication(), null, ApiHelper.ApiLogin(), new JSONObject(param), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("Success")){
                                JSONObject data = response.getJSONObject("Data");
                                User user = new User();
                                user.setID(data.getString("EmployeeId"));
                                user.setToken(data.getString("SessionToken"));

                                SharePreferenceUtil.setValueId(getApplication(),user.getID());
                                SharePreferenceUtil.setValueToken(getApplication(),user.getToken());

                                btnLogin.setText("Login");
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                                SaveLogin();
                                Intent intent;
                                if (SharePreferenceUtil.getValueStatus(getApplication()) != 0)
                                {
                                    intent = new Intent(LoginActivity.this, DetailActivity.class);
                                }else {
                                    intent = new Intent(LoginActivity.this, MainActivity.class);
                                }
                                startActivity(intent);
                                finish();
                            }else {
                                btnLogin.setText("Login");
                                Toast.makeText(LoginActivity.this, response.getString("Message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void SaveLogin(){
        if (ckbSaveUser.isChecked()){
            String username = edtUserName.getText().toString();
            String password = edtPassword.getText().toString();
            SharePreferenceUtil.saveUser(getApplication(),username,password);
        }
    }

    public void setFullScreen() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}
