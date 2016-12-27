package yong.tank.Title_Activity.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.gson.Gson;

import rx.Observer;
import yong.tank.Communicate.webConnect.NetWorks;
import yong.tank.LocalRecord.LocalRecord;
import yong.tank.Title_Activity.View.LoginActivity;
import yong.tank.Title_Activity.View.RegisterActivity;
import yong.tank.Title_Activity.View.WebInfoActivity;
import yong.tank.modal.User;
import yong.tank.tool.StaticVariable;

import static yong.tank.tool.StaticVariable.LOCAL_USER_INFO;

/**
 * Created by hasee on 2016/10/27.
 * 处理登陆相关的方法
 */

public class LoginPresenter{
    private static String TAG = "LoginPresenter";
    private Context context;
    private LoginActivity loginActivity;
    private Gson gson =new Gson();
    private LocalRecord<User> localUser = new LocalRecord<User>();
    public LoginPresenter(Context context, LoginActivity loginActivity){
        this.loginActivity=loginActivity;
        this.context=context;
    }

    public void login() {
        ConnectivityManager cwjManager=(ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cwjManager.getActiveNetworkInfo()!=null){
            if(cwjManager.getActiveNetworkInfo().isAvailable()){

                //获取填写的注册信息
                String username =loginActivity.accountText.getText().toString();
                String password =loginActivity.passwordText.getText().toString();
                if(username.trim().length()==0||password.trim().length()==0){
                    loginActivity.showToast("用户名和密码为空");
                }else{
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(password);
                    //设置状态为上线
                    user.setState(1);
                    loginToWeb(user);

                }

            }else{
                loginActivity.showToast("您的设备未联网啊，请检查设备网络状况...");
            }
        }else{
            loginActivity.showToast("您的设备未联网啊，请检查设备网络状况...");
        }


    }

    public void toRegister() {
        loginActivity.showToast("跳转到注册界面");
        Intent intent = new Intent(context,RegisterActivity.class);
        context.startActivity(intent);
        this.loginActivity.finish();
    }

    public void toWebInfo() {
        loginActivity.showToast("跳转到用户个人信息界面");
        Intent intent = new Intent(context,WebInfoActivity.class);
        context.startActivity(intent);
        this.loginActivity.finish();
    }

    public void initLoginInfo() {
        //获取User信息，然后填充到登录信息中
        User local_user = localUser.readInfoLocal(StaticVariable.USER_FILE);
        loginActivity.accountText.setText(local_user.getUsername());
        loginActivity.passwordText.setText(local_user.getPassword());
    }

    public void loginToWeb(User user) {
        //登录用户信息
        NetWorks.userLogin("userLogin",gson.toJson(user),new Observer<String>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                Log.i(TAG,"getUserInfo error :"+e);
                loginActivity.showToast("连接服务器出错-->1");
            }

            @Override
            public void onNext(String info) {
                Log.i(TAG,info);
                if(info.equals("0")){
                    loginActivity.showToast("用户名或者密码出错");
                }
                else{
                    //获取登录相关的信息，并更新本地的信息 ,
                    User loginUser= gson.fromJson(info,User.class);
                    User localUserInfo = localUser.readInfoLocal(StaticVariable.USER_FILE);
                    loginUser.setFrightRecord(localUserInfo.getFrightRecord());
                    localUser.saveInfoLocal(loginUser, StaticVariable.USER_FILE);
                    //赋值个人信息到全局变量中
                    LOCAL_USER_INFO = loginUser;
                    loginActivity.showToast("登录成功，跳转到个人信息界面");
                    Intent intent = new Intent(context,WebInfoActivity.class);
                    context.startActivity(intent);
                    //TODO 对比一下user ID和info是否一致.....
                    toWebInfo();
                }

            }
        });
    }
}
