package yong.tank.modal;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import yong.tank.R;

/**
 * Created by hasee on 2016/12/10.
 */

public class WebInfo extends LinearLayout {
    private Context context;
    public TextView userAccount_info;
    public TextView withComputerTime_info;
    public TextView withBlueTime_info;
    public TextView withInternetTime_info;
    public TextView winComputerTime_info;
    public TextView winBlueTime_info;
    public TextView winInternetTime_info;
    public TextView allRoomNum;
    public TextView roomAvailable;
    public ListView availableRoomList ;
    public ArrayAdapter<String> roomListInfo;
    public static final String TAG = "WebInfo";

    public WebInfo(Context context) {
        super(context);
    }

    public WebInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WebInfo(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void initWebInfo(){
        userAccount_info= (TextView) findViewById(R.id.userAccount_info);
        withComputerTime_info= (TextView) findViewById(R.id.withComputerTime_info);
        withBlueTime_info= (TextView) findViewById(R.id.withBlueTime_info);
        withInternetTime_info= (TextView) findViewById(R.id.withInternetTime_info);
        winComputerTime_info= (TextView) findViewById(R.id.winComputerTime_info);
        winBlueTime_info= (TextView) findViewById(R.id.winBlueTime_info);
        winInternetTime_info= (TextView) findViewById(R.id.winInternetTime_info);
        allRoomNum= (TextView) findViewById(R.id.allRoomNum);
        roomAvailable= (TextView) findViewById(R.id.roomAvailable);
        availableRoomList =(ListView) findViewById(R.id.list_room);
    }
}
