package yong.tank.Game.View;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import yong.tank.R;
import yong.tank.modal.SelectButton;

/**
 * Created by jiangyong_tong on 2016/10/31.
 */

public class SelectView extends LinearLayout{
    private Context context;
    private static String TAG = "SelectView";
    private SelectButton selectButton_1;
    private SelectButton selectButton_2;
    //这些方法都需要继承，不知道为什么
    public SelectView(Context context) {
        super(context);
        this.context = context;
    }

    public SelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public SelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SelectView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    public void initButton(){
        //TODO 也可以通过配置文件设置，这里暂时就用硬编码
        selectButton_1 = (SelectButton)findViewById(R.id.selectButton_1);
        selectButton_1.initSelectButton();
        selectButton_1.setButtonNoSelected();
        selectButton_2 = (SelectButton)findViewById(R.id.selectButton_2);
        selectButton_2.initSelectButton();
        selectButton_2.setButtonSelected();
        selectButton_2.setButtonNum("10");
    }

    public SelectButton getSelectButton_1() {
        return selectButton_1;
    }

    public void setSelectButton_1(SelectButton selectButton_1) {
        this.selectButton_1 = selectButton_1;
    }

    public SelectButton getSelectButton_2() {
        return selectButton_2;
    }

    public void setSelectButton_2(SelectButton selectButton_2) {
        this.selectButton_2 = selectButton_2;
    }
}
