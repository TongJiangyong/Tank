package yong.tank.Title.presenter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import yong.tank.Communicate.bluetoothCommunicate.ClientBluetooth;
import yong.tank.Help.View.HelpActivity;
import yong.tank.SelectTank.View.SelectActivity;
import yong.tank.Title.View.ITitleView;
import yong.tank.Title.View.ListDevice;
import yong.tank.Title.View.MainActivity;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/10/27.
 */

public class TitlePresenter implements ITitlePresenter {
    private static String TAG = "TitlePresenter";
    private MainActivity context;
    private ITitleView titleView;
    private BluetoothAdapter bluetoothadpter=null;
    private ClientBluetooth clientBluetooth = null;
    public TitlePresenter(MainActivity context, ITitleView titleView){
        this.titleView=titleView;
        this.context=context;
    }
    @Override
    public void toComputer(){
        //titleView.showToast("开始人机大战");
        //1表示人机，2表示蓝牙，3表示普通
        Intent intent = new Intent(context,SelectActivity.class);
        intent.putExtra("type", StaticVariable.GAMEMODE[0]);
        context.startActivity(intent);
    }
    @Override
    public void toBluetooth(){
        //titleView.showToast("蓝牙模式开发中..");
        bluetoothadpter = BluetoothAdapter.getDefaultAdapter();


        if(!bluetoothadpter.isEnabled()){
            titleView.showToast("此程序会默认打开你的蓝牙");
            //	bluetoothadpter.enable();
        }
        // TODO 这里的逻辑整理一下.....
        if(StaticVariable.BLUE_STATE != 1){
            if(bluetoothadpter.getState() == bluetoothadpter.STATE_ON){
                Intent intent = new Intent(this.context,ListDevice.class);
                //打开蓝牙的线程
                clientBluetooth=this.setupChat();
                //没办法，为了使用这个starrfor result 只能传入MainActivity
                //打开蓝牙list的显示线程
                this.context.startActivityForResult(intent,StaticVariable.CHOSED_BLUT_DEVICE);
            }
            else{
                titleView.showToast("等待蓝牙开启...");
                // 请求打开 Bluetooth
                Intent requestBluetoothOn = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);

                // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
                requestBluetoothOn
                        .setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

                // 设置 Bluetooth 设备可见时间
                requestBluetoothOn.putExtra(
                        BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                        200);
                // 请求开启 Bluetooth
                this.context.startActivityForResult(requestBluetoothOn,
                        StaticVariable.REQUEST_CODE_BLUETOOTH_ON);

            }
            //titleView.showToast("请先连接蓝牙");
        }
        else{
            titleView.showToast("蓝牙连接成功....进入选择要连接的设备");
        }
    }




    @Override
    public void toNet(){
        //TODO 测试蓝牙方法
        if(clientBluetooth.mConnectedThread==null){
            titleView.showToast("联网模式开发中...&& 蓝牙连接线程失败");
        }else{
            Log.i(TAG,"test to send infos");
            clientBluetooth.sendInfo("test blutooth");
            titleView.showToast("联网模式开发中...&& 蓝牙发送消息");
        }

    }
    @Override
    public void tohelp(){
        //titleView.showToast("帮助模块开发中..");
        Intent intent = new Intent(context,HelpActivity.class);
        intent.putExtra("type", StaticVariable.GAMEMODE[0]);
        context.startActivity(intent);
    }

    @Override
    public void enableBluetooth() {
        bluetoothadpter.isEnabled();
    }

    @Override
    public void toBlueTankChose(int resultCode, Intent data) {
        //TODO 准备blue_socket的连接 跳转到tank的选择界面即可.....
        //这个如何和BluetoothChatService结合起来，可以看一些代码
        Uri address = data.getData();
        System.out.println("我是那个远程设备的地址"+address.toString());
        //测试蓝牙的连接情况
        // Get the BLuetoothDevice object
        BluetoothDevice device = bluetoothadpter.getRemoteDevice(address.toString());
        clientBluetooth.connectDevice(device,false);
        // Attempt to connect to the device
    }

    private ClientBluetooth setupChat() {
        if(clientBluetooth==null){
            // Initialize the BluetoothChatService to perform bluetooth connections
            clientBluetooth = new ClientBluetooth();
            //clientBluetooth.setMyHandle(myHandler);
            clientBluetooth.startCommunicate();
        }
        return clientBluetooth;
    }

}
