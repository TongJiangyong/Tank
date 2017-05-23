package yong.tank.Title_Activity.View;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import yong.tank.R;
import yong.tank.Title_Activity.presenter.LoginPresenter;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/12/10.
 */

public class LoginActivity extends Activity implements View.OnClickListener{
    private static String TAG = "LoginActivity";
    private LoginPresenter loginPresenter;
    private Button loginButton;
    private Button registerButton;
    public TextView accountText;
    public TextView passwordText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        loginPresenter = new LoginPresenter(this,this);
        setContentView(R.layout.login_layout);  //注意，这一句话一定要在initButton之前，否则，会找不到button，即findViewById会找不到....
        loginButton = (Button)findViewById(R.id.login);
        registerButton = (Button)findViewById(R.id.to_register);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        accountText=(TextView)findViewById(R.id.loginAccount_id);
        passwordText=(TextView)findViewById(R.id.loginPassword_id);
        loginPresenter.initLoginInfo();
        if(StaticVariable.DEBUG) {
            Log.i(TAG, "Test  LoginActivity");
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                //Log.i(TAG,"battle_computer");
                loginPresenter.login();
                break;
            case R.id.to_register:
                //Log.i(TAG,"battle_computer");
                loginPresenter.toRegister();
                break;
            default:
                break;
        }
    }

    public void showToast(String info){
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
    }
}
