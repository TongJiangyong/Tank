package yong.tank.SelectTank_Activity.present;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;

import com.google.gson.Gson;

import rx.Observer;
import yong.tank.Communicate.webConnect.NetWorks;
import yong.tank.Game_Activity.GameActivity;
import yong.tank.LocalRecord.LocalRecord;
import yong.tank.SelectTank_Activity.View.SelectActivity;
import yong.tank.SelectTank_Activity.View.SelectView;
import yong.tank.Title_Activity.View.MainActivity;
import yong.tank.modal.Room;
import yong.tank.modal.User;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/10/28.
 */

public class SelectPresent {
    private SelectView selectView;
    private LocalRecord<User> localUser = new LocalRecord<User>();
    private Gson gson =new Gson();
    private Context context;
    private String roomName = null;
    private SelectActivity selectActivity;
    private static String TAG ="SelectPresent";
    //TODO 定义一个最好去定义一个view，而不是这个.....
    public SelectPresent(Context context, SelectView selectView, SelectActivity SelectActivity){
        this.context= context;
        this.selectView=selectView;
        this.selectActivity = SelectActivity;
    }


    public void returnToTitle() {
        Intent intent = new Intent(this.context, MainActivity.class);
        this.context.startActivity(intent);
    }

    public void setSelect(MotionEvent event) {
        for(int i=0;i<selectView.getMapPictures().length;i++)
        {
            if(selectView.getMapPictures()[i].isContainPoint((int)event.getX(),(int)event.getY())){
                //Toast.makeText(this.context, "picture", Toast.LENGTH_SHORT).show();
                this.showMapSelected(i);
            }
        }
        for(int j =0;j<selectView.getTankPictures().length;j++){
            if(selectView.getTankPictures()[j].isContainPoint((int)event.getX(),(int)event.getY())){
                //Toast.makeText(this.context, "picture——1:"+j, Toast.LENGTH_SHORT).show();
                this.showTankSelected(j);
            }
        }
    }
    public void showTankSelected(int i){
        this.selectView.showTankSelected(i);

    }
    public void showMapSelected(int i){
        this.selectView.showMapSelected(i);
    }

    //进入game界面，并传递参数,如果是网络模式，并为主动，则还需要在服务器创建房间,如果是被动模式，则需要在服务器更新房间信息
    public void gotoBattle() {
        boolean isOkToGoBattle = true;
        //创建房间信息
        if(StaticVariable.CHOSED_MODE ==StaticVariable.GAME_MODE.INTERNET){
            //如果是主动模式 则需要跳出一个对话框来输入房间名字
            if(StaticVariable.CHOSED_RULE== StaticVariable.GAME_RULE.ACTIVITY){
                isOkToGoBattle = false;
                chooseNameDialog();
            }

        }
        //对其它模式来说
        if(isOkToGoBattle){
            Intent intent = new Intent(this.context, GameActivity.class);
            intent.putExtra("tankType",this.selectView.getSelectedTank());
            intent.putExtra("mapType",this.selectView.getSelectedMap());
            this.context.startActivity(intent);
        }

    }


    private void creatNewRoom() {
        Room newRoom = new Room();
        User user = localUser.readInfoLocal(StaticVariable.USER_FILE);
        newRoom.setServerUser(user.getId());
        newRoom.setRoomName(roomName);
        newRoom.setState(1);
        Log.i(TAG,"ROOM:"+newRoom.toString());
        //创建新的房间
        NetWorks.addNewRoom("addNewRoom",gson.toJson(newRoom),new Observer<String>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                Log.i(TAG,"addNewRoom error :"+e);
                selectActivity.showToast("连接服务器出错-->1");
            }

            @Override
            public void onNext(String info) {
                Log.i(TAG,"recive room info :"+info);
                if(info.equals("0")){
                    selectActivity.showToast("创建房间出错-->2");
                }
                else{
                    selectActivity.showToast("创建房间成功，等待用户接入....");
                    StaticVariable.REMOTE_ROOM_ID =info;
                    //跳转
                    Intent intent = new Intent(context, GameActivity.class);
                    intent.putExtra("tankType",selectView.getSelectedTank());
                    intent.putExtra("mapType",selectView.getSelectedMap());
                    context.startActivity(intent);
                }

            }
        });
    }




    public void chooseNameDialog() {
        boolean isNameFirmFlag = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("请输入新建房间名称：");
//创建一个EditText对象设置为对话框中显示的View对象
        final EditText editText = new EditText(this.context);
        builder.setView(editText);
//用户选好要选的选项后，点击确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                roomName = editText.getText().toString();
                if (roomName.equals("")) {
                    selectActivity.showToast("房间名不能为空，请重新输入房间名");
                } else {
                    //创建房间
                    creatNewRoom();
                }
            }
        });
// 取消选择
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


           }
        });
        builder.show();
    }


}
