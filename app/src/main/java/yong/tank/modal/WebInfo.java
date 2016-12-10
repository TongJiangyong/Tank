package yong.tank.modal;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by hasee on 2016/12/10.
 */

public class WebInfo extends LinearLayout {
    private Context context;
    private ImageView imageView;
    private TextView textView;
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
}
