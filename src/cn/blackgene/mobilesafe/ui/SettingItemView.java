package cn.blackgene.mobilesafe.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.blackgene.mobilesafe.R;

/**
 * 自定义View组合控件
 * Created by Robin on 2014-10-26.
 */
public class SettingItemView extends RelativeLayout {

    private static final String NAMESPACE = "http://schemas.android.com/apk/res/cn.blackgene.mobilesafe";
    private TextView tv_title;
    private TextView tv_description;
    private CheckBox cb_status;

    String desc_on;
    String desc_off;

    /**
     * 初始化布局文件，将布局加载到自定义View（SettingItemView）中
     *
     * @param context
     */
    private void initView(Context context) {
        View view = View.inflate(context, R.layout.setting_item_view, this);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_description = (TextView) view.findViewById(R.id.tv_description);
        cb_status = (CheckBox) view.findViewById(R.id.cb_status);
    }

    public SettingItemView(Context context) {
        super(context);
        initView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        String title = attrs.getAttributeValue(NAMESPACE, "title");
        desc_on = attrs.getAttributeValue(NAMESPACE, "desc_on");
        desc_off = attrs.getAttributeValue(NAMESPACE, "desc_off");
        tv_title.setText(title);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 获取 组合控件的选中状态
     *
     * @return
     */
    public boolean isChecked() {
        return cb_status.isChecked();
    }

    /**
     * 设置 组合控件的选中状态
     *
     * @param checked
     */
    public void setChecked(boolean checked) {
        this.setBackgroundColor(checked ? Color.rgb(77, 184, 73) : Color.rgb(235, 235, 235));//设置背景色
        setDescription(checked ? desc_on : desc_off);
        cb_status.setChecked(checked);
    }

    /**
     * 设置 组合控件描述信息
     *
     * @param desc
     */
    public void setDescription(String desc) {
        tv_description.setText(desc);
    }

    /**
     * 切换 组合控件的选中状态
     */
    public void toggleChecked() {
        setChecked(!isChecked());
    }

}
