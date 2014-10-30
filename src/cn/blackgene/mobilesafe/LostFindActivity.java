package cn.blackgene.mobilesafe;

import cn.blackgene.mobilesafe.constants.ConfigConstant;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class LostFindActivity extends Activity {
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences(ConfigConstant.PREFERENCE_NAME, MODE_PRIVATE);
		boolean isConfiged = sp.getBoolean(ConfigConstant.IS_CONFIGED_KEY,
				ConfigConstant.IS_CONFIGED_NO);// 是否已经过引导配置
		if (isConfiged) {// 已配置，进入防盗主页面
			setContentView(R.layout.activity_lost_find);
		} else {// 未配置，开启引导配置"SetupActivity1"
			startActivity(new Intent(LostFindActivity.this,
					SetupActivity1.class));
			finish();
		}
	}

}
