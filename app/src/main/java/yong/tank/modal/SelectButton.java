package yong.tank.modal;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import yong.tank.R;

/**
 * Created by hasee on 2016/11/13.
 */

public class SelectButton extends LinearLayout{
    private Context context;
    private ImageView imageView;
    private TextView textView;
    //TODO 这里注意插入其他的属性，将button和选择的子弹联系起来......

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
    //设置button显示的数量
    public void setButtonNum(String text){
        textView.setText(text);
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

}
