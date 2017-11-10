package com.thanhtuan.delivery.view.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.thanhtuan.delivery.data.remote.AppConst;
import com.thanhtuan.delivery.data.remote.JsonRequest;
import com.thanhtuan.delivery.model.User;
import com.thanhtuan.delivery.util.SharePreferenceUtil;
import com.thanhtuan.delivery.util.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.edtUserName) EditText edtUserName;
    @BindView(R.id.edtPassword) EditText edtPassword;
    @BindView(R.id.btnLogin)    Button btnLogin;
    @BindView(R.id.ckbSave)     CheckBox ckbSaveUser;

    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (!NetworkUtils.isNetworkAvailable(getApplication())){
            setToastShow("Bạn chưa bật kết nối mạng!");
            return;
        }

        checkPermission();
        checkVersion();

        addViews();
    }

    private void addViews() {
        SharePreferenceUtil.loadUser(this,edtUserName,edtPassword);
    }

    private void initDialogUpdate(final String URL){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_update_version, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        initDownloadNewVersion(URL);
                    }
                })
                .setTitle("Phiên bản mới!");

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    private void initDownloadNewVersion(String URL){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        startActivity(browserIntent);
    }

    @OnClick(R.id.btnLogin)
    public void onLoginClick() {
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
                        setToastShow("Đăng nhập thành công!");

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
                        setToastShow(response.getString("Message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void checkVersion(){
        JsonRequest.Request(getApplication(), SharePreferenceUtil.getValueToken(getApplication()), ApiHelper.ApiVersion(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getBoolean("Success")){
                        JSONObject data = response.getJSONArray("Data").getJSONObject(0);
                        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
                        String versionCurrent = packageInfo.versionName;
                        String linkUpdate = data.getString("LinkUpdate");
                        if (!versionCurrent.equals(data.getString("VersionNo"))){
                            //initDialogUpdate(linkUpdate);
                        }else {
                            Log.e("LinkDownload", "Phiên bản mới rồi!");
                        }
                    }else {
                        Log.e("LoginActivity", "onResponse: " + "ERR");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
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

    private void setToastShow(String text){
        if (toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, AppConst.PerLocaRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        checkPermission();
    }
}
