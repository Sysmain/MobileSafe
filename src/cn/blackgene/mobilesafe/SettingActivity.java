package cn.blackgene.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import cn.blackgene.mobilesafe.constants.ConfigConstant;
import cn.blackgene.mobilesafe.ui.SettingItemView;

/**
 * Created by Robin on 2014-10-22.
 */
public class SettingActivity extends Activity {
    SettingItemView siv_setting_checkupdate;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        siv_setting_checkupdate = (SettingItemView) findViewById(R.id.siv_setting_checkupdate);
        sp = getSharedPreferences(ConfigConstant.PREFERENCE_NAME, MODE_PRIVATE);
        siv_setting_checkupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siv_setting_checkupdate.toggleChecked();
                editor = sp.edit();
                editor.putBoolean(ConfigConstant.IS_AUTO_UPDATE_KEY, siv_setting_checkupdate.isChecked());
                editor.apply();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //读取配置文件中的组合控件siv_setting_checkupdate的选中状态,默认为选中
        boolean checked = sp.getBoolean(ConfigConstant.IS_AUTO_UPDATE_KEY, ConfigConstant.AUTO_UPDATE_YES);
        siv_setting_checkupdate.setChecked(checked);
    }
}
