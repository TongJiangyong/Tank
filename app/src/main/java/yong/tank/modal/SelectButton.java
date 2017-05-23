package yong.tank.modal;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import yong.tank.R;
import yong.tank.tool.StaticVariable;

/**
 * Created by hasee on 2016/11/13.
 */

public class SelectButton extends LinearLayout{
    private Context context;
    private ImageView imageView;
    private TextView textView;
    public static final String TAG = "SelectButton";
    //TODO 这里注意插入其他的属性，将button和选择的子弹联系起来......
    private Integer bulletType;
    private Integer bulletNum;
    //设置selectButton有没有被使用，如果没有被使用，则为false
    private boolean isFilled=false;
    //设置handle的处理
    private Handler myHandler = new Handler() {
        public void handleMessage (Message msg) {//此方法在ui线程运行
            switch(msg.what){
                case StaticVariable.MSG_UPDATE_SELECTBUTTON:
                    Integer bullletNum = msg.getData().getInt("bullletNum");//接受msg传递过来的参数
                    Integer bullletPicture = msg.getData().getInt("bullletPicture");//接受msg传递过来的参数
                    Integer bullletType= msg.getData().getInt("bullletType");//接受msg传递过来的参数
                    setBulletType(bullletType);
                    setButtonPic(bullletPicture);
                    setBulletNum(bullletNum);
                    break;
                case StaticVariable.MSG_UPDATE_LEFT_BULLET_NUM:
                    subtractBulletNum();
                    setBulletNum(bulletNum);
                    break;
            }

        }
    };


    //这些方法都需要继承，不知道为什么
    public SelectButton(Context context) {
        super(context);
        this.context = context;
    }

    public SelectButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public SelectButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SelectButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    public void initSelectButton(){
        imageView = (ImageView) findViewById(R.id.imageButton);
        textView = (TextView) findViewById(R.id.buttonNum);
    }

    //TODO 这里显示的差异性在于，
    //设置bullet的实际数量
    public void setBulletNum(Integer bulletNum) {
        this.bulletNum = bulletNum;
        textView.setText(String.valueOf(bulletNum));
    }
    //设置button显示的图片
    public void setButtonPic(int pictureSrc){
        this.imageView.setImageResource(pictureSrc);
    }
    //设置button被选中
    public void setButtonSelected(){
        this.imageView.setBackgroundResource(R.drawable.with_border);
    }
    //设置button没有被选中
    public void setButtonNoSelected(){
        this.imageView.setBackgroundResource(R.drawable.no_border);
    }

    public Integer getBulletType() {
        return bulletType;
    }

    public void setBulletType(Integer bulletType) {
        this.bulletType = bulletType;
    }

    public Integer getBulletNum() {
        return bulletNum;
    }


    public boolean isFilled() {
        return isFilled;
    }

    public void setFilled(boolean filled) {
        isFilled = filled;
    }

    //发射子弹后，更新bullet的参数
    public void subtractBulletNum() {
        bulletNum=bulletNum-1;
        if(bulletNum<0){
            bulletNum=0;
        }
        //排除掉初始化的影响
        if (bulletNum>1000){
            bulletNum=bulletNum+1;
        }
        this.setBulletNum(bulletNum);
    }

    public Handler getMyHandler() {
        return myHandler;
    }
}
