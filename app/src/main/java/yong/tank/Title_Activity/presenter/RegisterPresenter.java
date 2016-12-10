
package yong.tank.Title_Activity.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.gson.Gson;

import rx.Observer;
import yong.tank.Communicate.webConnect.NetWorks;
import yong.tank.LocalRecord.LocalRecord;
import yong.tank.Title_Activity.View.RegisterActivity;
import yong.tank.Title_Activity.View.WebInfoActivity;
import yong.tank.modal.User;

/**
 * Created by hasee on 2016/10/27.
 * 处理注册相关的方法
 */

public class RegisterPresenter {
    private static String TAG = "RegisterPresenter";
    private Context context;
    private RegisterActivity registerActivity;
    private Gson gson =new Gson();
    private LocalRecord<User> localUser = new LocalRecord<User>();
    public RegisterPresenter(Context context, RegisterActivity registerActivity){
        this.registerActivity=registerActivity;
        this.context=context;
    }

    public void register() {
        ConnectivityManager cwjManager=(ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cwjManager.getActiveNetworkInfo().isAvailable()){

            //获取填写的注册信息
            String username =registerActivity.accountText.getText().toString();
            String password =registerActivity.passwordText.getText().toString();
            if(username.trim().length()==0||password.trim().length()==0){
                registerActivity.showToast("用户名和密码为空");
            }else{
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                registerToWeb(user);
            }


        }else{
            registerActivity.showToast("您的设备未联网啊，请检查设备网络状况...");
        }
    }

    public void toWebInfo() {
        registerActivity.showToast("跳转到用户个人信息界面");
        Intent intent = new Intent(context,WebInfoActivity.class);
        context.startActivity(intent);
    }

    private void registerToWeb(User user) {
        //注册用户信息
        NetWorks.addNewUser("addNewUser",gson.toJson(user),new Observer<String>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                Log.i(TAG,"getUserInfo error :"+e);
                registerActivity.showToast("连接服务器出错-->1");
            }

            @Override
            public void onNext(String info) {
                Log.i(TAG,info);
                if(info.equals("1"))
                    registerActivity.showToast("连接服务器出错-->2");
                else if(info.equals("2")){
                    registerActivity.showToast("服务器已被注册相同的用户名");
                }else{
                    registerActivity.showToast("注册用户信息成功");
                    toWebInfo();
                }

            }
        });
    }
}
