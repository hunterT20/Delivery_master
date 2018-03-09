package com.thanhtuan.delivery.ui.login;

import android.Manifest;
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
import com.thanhtuan.delivery.data.model.VersionApp;
import com.thanhtuan.delivery.data.model.api.ApiListResult;
import com.thanhtuan.delivery.data.model.api.ApiResult;
import com.thanhtuan.delivery.data.remote.ApiHelper;
import com.thanhtuan.delivery.data.AppConst;
import com.thanhtuan.delivery.data.remote.ApiUtils;
import com.thanhtuan.delivery.data.remote.JsonRequest;
import com.thanhtuan.delivery.data.model.User;
import com.thanhtuan.delivery.ui.detail.DetailActivity;
import com.thanhtuan.delivery.data.local.prefs.SharePreferenceUtil;
import com.thanhtuan.delivery.ui.detail.DetailFragment;
import com.thanhtuan.delivery.utils.NetworkUtils;
import com.thanhtuan.delivery.ui.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.edtUserName) EditText edtUserName;
    @BindView(R.id.edtPassword) EditText edtPassword;
    @BindView(R.id.btnLogin)    Button btnLogin;
    @BindView(R.id.ckbSave)     CheckBox ckbSaveUser;

    private Toast toast;
    private final CompositeDisposable disposable = new CompositeDisposable();

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

    @Override
    protected void onStop() {
        super.onStop();
        disposable.clear();
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
                .setPositiveButton("Update", (dialogBox, id) -> initDownloadNewVersion(URL))
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

        Observable<ApiResult<User>> login = ApiUtils.getAPIservices().login(param);

        disposable.add(
                login.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<ApiResult<User>>() {
                        @Override
                        public void onNext(ApiResult<User> result) {
                            if (result.getSuccess()) {
                                User user = result.getData();
                                SharePreferenceUtil.setValueId(getApplication(), user.getEmployeeId());
                                SharePreferenceUtil.setValueToken(getApplication(), user.getSessionToken());

                                btnLogin.setText("Login");
                                setToastShow("Đăng nhập thành công!");
                                SaveLogin();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);;
                                startActivity(intent);
                                finish();
                            }else {
                                setToastShow(result.getMessage());
                                btnLogin.setText("Login");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            btnLogin.setText("Login");
                            Log.e(TAG, "onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    })
        );
    }

    private void SaveLogin(){
        if (ckbSaveUser.isChecked()){
            String username = edtUserName.getText().toString();
            String password = edtPassword.getText().toString();
            SharePreferenceUtil.saveUser(getApplication(),username,password);
        }
    }

    private void checkVersion(){
        String token = SharePreferenceUtil.getValueToken(getApplication());
        Observable<ApiListResult<VersionApp>> checkVersion = ApiUtils.getAPIservices().checkVerson(token);
        disposable.add(
                checkVersion.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<ApiListResult<VersionApp>>() {
                            @Override
                            public void onNext(ApiListResult<VersionApp> result) {
                                try {
                                    VersionApp versionApp = result.getData().get(0);
                                    PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);

                                    String versionCurrent = packageInfo.versionName;
                                    String linkUpdate = versionApp.getLinkUpdate();

                                    if (!versionCurrent.equals(versionApp.getVersionNo())){
                                        //initDialogUpdate(linkUpdate);
                                    }else {
                                        Log.e("LinkDownload", "Phiên bản mới rồi!");
                                    }
                                } catch (PackageManager.NameNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
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
