package yong.tank.Title_Activity.presenter;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.gson.Gson;

import rx.Observer;
import yong.tank.Communicate.bluetoothCommunicate.ClientBluetooth;
import yong.tank.Communicate.webConnect.NetWorks;
import yong.tank.Help_Activity.View.HelpActivity;
import yong.tank.LocalRecord.LocalRecord;
import yong.tank.SelectTank_Activity.View.SelectActivity;
import yong.tank.Title_Activity.View.ITitleView;
import yong.tank.Title_Activity.View.LoginActivity;
import yong.tank.Title_Activity.View.MainActivity;
import yong.tank.Title_Activity.View.RegisterActivity;
import yong.tank.modal.User;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/10/27.
 * 不知道为啥，其他的蓝牙只能采用被动连接的方式.....otz
 */

public class TitlePresenter implements ITitlePresenter {
    private static String TAG = "TitlePresenter";
    private MainActivity context;
    private ITitleView titleView;
    private BluetoothAdapter bluetoothadpter=null;
    private ClientBluetooth clientBluetooth ;
    private Gson gson = new Gson();
    private LocalRecord<User> localUser = new LocalRecord<User>();
    public TitlePresenter(MainActivity context, ITitleView titleView){
        this.titleView=titleView;
        this.context=context;
    }
    @Override
    public void toComputer(){
        //titleView.showToast("开始人机大战");
        //1表示人机，2表示蓝牙，3表示普通
        Intent intent = new Intent(context,SelectActivity.class);
        //TODO intenet借用
        StaticVariable.CHOSED_MODE = StaticVariable.GAME_MODE.LOCAL;
        intent.putExtra("type", StaticVariable.GAMEMODE[0]);
        context.startActivity(intent);
    }
    @Override
    public void toBluetooth(){
        //这一部分，在game的地方配置
        //titleView.showToast("蓝牙模式开发中..");
        Intent intent = new Intent(context,SelectActivity.class);
        //TODO intenet借用
        StaticVariable.CHOSED_MODE = StaticVariable.GAME_MODE.BLUETOOTH;
        intent.putExtra("type", StaticVariable.GAMEMODE[2]);
        context.startActivity(intent);
    }




    @Override
    public void toNet(){
        //首先出来选择服务器的dialog，跳出来选择
        //TODO 1、判断是否联网 ，2、选择连入的服务器 ，3、判断 是否有用户信息，没有，则区分跳转 4、跳转页面
        ConnectivityManager cwjManager=(ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cwjManager.getActiveNetworkInfo()!=null){
            if(cwjManager.getActiveNetworkInfo().isAvailable()){
                this.serviceChoseDialog();

            }else{
                titleView.showToast("您的设备未联网啊，请检查设备网络状况...");
            }
        }else{
            titleView.showToast("您的设备未联网啊，请检查设备网络状况...");
        }

    }

    public void serviceChoseDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(this.context);
        //normalDialog.setIcon(R.drawable.icon_dialog);
        normalDialog.setTitle("服务器选择");
        normalDialog.setMessage("请选择要接入的服务器");
        normalDialog.setPositiveButton("西安服务器",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO 测试服务器连接情况，并连接 以后处理
                        //API_SERVER = API_SERVER_XIAN;
                        serviceConnectTest();
                        toLoginOrRegister();
                    }
                });
        normalDialog.setNegativeButton("苏州服务器",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO 测试服务器连接情况，默认服务器
                        //API_SERVER = API_SERVER;
                        serviceConnectTest();
                        toLoginOrRegister();
                    }
                });
        // 显示
        normalDialog.show();
    }

    public void toLoginOrRegister(){
        Log.i(TAG,"选择服务器完毕");
        StaticVariable.CHOSED_MODE = StaticVariable.GAME_MODE.INTERNET;
        //下面一句注释的代码是测试用的，没实际意义
        //localUser.saveInfoLocal(new User(),StaticVariable.USER_FILE);
        User user = localUser.readInfoLocal(StaticVariable.USER_FILE);
        if(user==null||user.getId()==0){
            titleView.showToast("检测到您未在该服务器注册用户，将跳转到注册界面");
            Intent intent = new Intent(context,RegisterActivity.class);
            context.startActivity(intent);
        }else{
            titleView.showToast("检测到您已经在该服务器注册过用户，将跳转到登录界面");
            //titleView.showToast("帮助模块开发中..");
            Intent intent = new Intent(context,LoginActivity.class);
            context.startActivity(intent);
        }
    }


    public void serviceConnectTest(){
        NetWorks.connectTest("connectedTest",new Observer<String>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                Log.i(TAG,"connected error :"+e);
                titleView.showToast("服务器连接失败--->错误1");
            }

            @Override
            public void onNext(String info) {
                Log.i(TAG,"connected info :"+info);
                if(info.equals("connected")){
                    titleView.showToast("服务器连接成功");
                }else{
                    titleView.showToast("服务器连接失败--->错误2");
                }
            }
        });
    }

    @Override
    public void tohelp(){
        //titleView.showToast("帮助模块开发中..");
        Intent intent = new Intent(context,HelpActivity.class);
        intent.putExtra("type", StaticVariable.GAMEMODE[0]);
        context.startActivity(intent);
    }

}
