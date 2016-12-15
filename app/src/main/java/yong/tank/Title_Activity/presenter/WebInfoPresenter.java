
package yong.tank.Title_Activity.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import yong.tank.Communicate.webConnect.NetWorks;
import yong.tank.LocalRecord.LocalRecord;
import yong.tank.R;
import yong.tank.SelectTank_Activity.View.SelectActivity;
import yong.tank.Title_Activity.View.WebInfoActivity;
import yong.tank.modal.Room;
import yong.tank.modal.User;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/10/27.
 * 处理Web相关的方法
 */

public class WebInfoPresenter {
    private static String TAG = "WebInfoPresenter";
    private Context context;
    private WebInfoActivity webInfoActivity;
    private LocalRecord<User> localUser = new LocalRecord<User>();
    private User userInfo;
    private List<Room> onePersonRooms = new ArrayList<>();
    private List<Room> twoPersonRooms = new ArrayList<>();
    private int opponentId =0;
    private Gson gson = new Gson();
    //User userinfo =localUser.readInfoLocal(StaticVariable.USER_FILE);
    //Log.i(TAG, "print personnal info："+userinfo.toString() );
    public WebInfoPresenter(Context context, WebInfoActivity webInfoActivity){
        this.webInfoActivity=webInfoActivity;
        this.context=context;
        userInfo =localUser.readInfoLocal(StaticVariable.USER_FILE);
    }



    //初始化记录信息
    public void recordInit() {
        this.webInfoActivity.webInfo.winBlueTime_info.setText(String.valueOf(userInfo.getFrightRecord().getWinBlueTime()));
        this.webInfoActivity.webInfo.winComputerTime_info.setText(String.valueOf(userInfo.getFrightRecord().getWinComputerTime()));
        this.webInfoActivity.webInfo.winInternetTime_info.setText(String.valueOf(userInfo.getFrightRecord().getWinInternetTime()));
        this.webInfoActivity.webInfo.withBlueTime_info.setText(String.valueOf(userInfo.getFrightRecord().getWithBlueTime()));
        this.webInfoActivity.webInfo.withComputerTime_info.setText(String.valueOf(userInfo.getFrightRecord().getWithComputerTime()));
        this.webInfoActivity.webInfo.withInternetTime_info.setText(String.valueOf(userInfo.getFrightRecord().getWithInternetTime()));
        this.webInfoActivity.webInfo.userAccount_info.setText(String.valueOf(userInfo.getUsername()));
        //初始化adapt信息
        this.webInfoActivity.webInfo.roomListInfo= new ArrayAdapter<String>(this.context,R.layout.device_name);
        this.webInfoActivity.webInfo.availableRoomList.setAdapter(this.webInfoActivity.webInfo.roomListInfo);
        this.webInfoActivity.webInfo.availableRoomList.setOnItemClickListener(mRoomClickListener);  //设置监听的方法
    }

    //初始化房间信息
    public void roomInfoInit() {
        //获取有一个人的room信息
        this.refreshAvailableRoomData();
        //获取有两个人的room信息
        NetWorks.getRoomList("getRoomInfoByState",3,new Observer<String>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                Log.i(TAG,"getRoomList error :"+e);
                webInfoActivity.showToast("连接服务器出错-->1");
            }

            @Override
            public void onNext(String info) {
                Log.i(TAG,info);
                if(info.trim().equals("[]")){
                    //webInfoActivity.showToast("暂时没有可接入的房间");
                    webInfoActivity.webInfo.allRoomNum.setText("0");
                }
                else{
                    twoPersonRooms = gson.fromJson(info,  new TypeToken<List<Room>>() {}.getType());
                    //以后再改吧.....
                    //TODO 通过这里理解一下多线程的内部类特点
                    //Log.i(TAG,"onePersonRooms:"+onePersonRooms.size());
                    webInfoActivity.webInfo.allRoomNum.setText(String.valueOf(twoPersonRooms.size()));
                }

            }
        });
    }
    public void refreshAvailableRoomData(){
        NetWorks.getRoomList("getRoomInfoByState",1,new Observer<String>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                Log.i(TAG,"getUserInfo error :"+e);
                webInfoActivity.showToast("连接服务器出错-->1");
            }

            @Override
            public void onNext(String info) {
                Log.i(TAG,info);
                if(info.trim().equals("[]")){
                    webInfoActivity.showToast("暂时没有可接入的房间");
                    webInfoActivity.webInfo.roomAvailable.setText("0");
                }
                else{
                    onePersonRooms = gson.fromJson(info,  new TypeToken<List<Room>>() {}.getType());
                    webInfoActivity.webInfo.roomAvailable.setText(String.valueOf(onePersonRooms.size()));
                    //设置roomList
                    webInfoActivity.webInfo.roomListInfo.clear();
                    for(Room r:onePersonRooms){
                        webInfoActivity.webInfo.roomListInfo.add("房间名："+r.getRoomName()+" # "+"房间编号："+r.getId()+" # "+"对手Id号："+r.getServerUser());
                    }
                    //更新数据信息
                    webInfoActivity.webInfo.roomListInfo.notifyDataSetChanged();
                }

            }
        });
    }

    //TODO 接入别人的房间触发的方法
    private AdapterView.OnItemClickListener mRoomClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            String  info=((TextView) v).getText().toString();
            String[] infos =info.split("\\#");
            String opponentUserId =infos[1].split("\\：")[1];
            String roomId =infos[2].split("\\：")[1];

            Intent intent = new Intent(context,SelectActivity.class);
            //确定为被动状态
            StaticVariable.CHOSED_RULE = StaticVariable.GAME_RULE.PASSIVE;
            intent.putExtra("type", StaticVariable.GAMEMODE[1]); //1为网络模式
            Log.i(TAG,"opponentUserId:"+opponentUserId+" roomId:"+roomId);
            //passive状态，记录对方的ID
            StaticVariable.REMOTE_DEVICE_ID =  opponentUserId;
            context.startActivity(intent);

            //TODO 跳转连接到相应的房间
/*            bluetoothadapter.cancelDiscovery();// 取消搜索
            // 获取设备的MAC地址
            String msg = ((TextView) v).getText().toString();
            String address = msg.substring(msg.length() - 17);


            Uri data = Uri.parse(address);
            Intent intent = new Intent(null,data);
            // 设备结果并退出Activity
            setResult(Activity.RESULT_OK, intent);
            finish();*/
        }
    };

    //跳转到创建房间界面
    public void creatRoom() {
        //titleView.showToast("开始人机大战");  这里还应该增加其他的数据，比如是主动还是被动 如果是被动，则还需要增加userId的选项....
        //1表示人机，2表示蓝牙，3表示普通
        Intent intent = new Intent(context,SelectActivity.class);
        //确定为主动状态
        StaticVariable.CHOSED_RULE = StaticVariable.GAME_RULE.ACTIVITY;
        intent.putExtra("type", StaticVariable.GAMEMODE[1]); //1为网络模式
        context.startActivity(intent);
    }


    public void refreshRoom() {
        this.roomInfoInit();
    }
}
