package yong.tank.Title.presenter;

import android.content.Context;
import android.content.Intent;

import yong.tank.SelectRoom.View.SelectActivity;
import yong.tank.Title.View.ITitleView;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/10/27.
 */

public class TitlePresenter implements ITitlePresenter {
    private Context context;
    private ITitleView titleView;
    public TitlePresenter(Context context,ITitleView titleView){
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
        titleView.showToast("蓝牙模式开发中..");
    }
    @Override
    public void toNet(){
        titleView.showToast("联网模式开发中...");
    }
    @Override
    public void tohelp(){
        titleView.showToast("帮助模块开发中..");
    }
}
