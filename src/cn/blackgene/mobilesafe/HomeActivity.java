package cn.blackgene.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.blackgene.mobilesafe.constants.ConfigConstant;
import cn.blackgene.mobilesafe.utils.StringUtils;

/**
 * Created by Robin on 2014-10-20.
 */
public class HomeActivity extends Activity implements
		AdapterView.OnItemClickListener {

	private static final String TAG = "HomeActivity";
	private GridView gv_functions;
	private int[] imageIds = { R.drawable.safe, R.drawable.callmsgsafe,
			R.drawable.app, //
			R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan, //
			R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings };
	private String[] names = { "手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计", "手机杀毒",
			"缓存清理", "高级工具", "设置中心" };
	private Intent itemIntent;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences(ConfigConstant.PREFERENCE_NAME, MODE_PRIVATE);
		gv_functions = (GridView) findViewById(R.id.gv_home_functions);
		gv_functions.setAdapter(new FunctionsAdapter());
		gv_functions.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			// 进入手机防盗页面
			// 先判断是否已经设置了密码（是否加了锁）
			View dialogView;
			AlertDialog.Builder builder = new Builder(HomeActivity.this);
			AlertDialog dialog;
			if (isLocked()) {
				dialogView = View.inflate(HomeActivity.this,
						R.layout.unlock_dialog_view, null);
				builder.setView(dialogView);
				dialog = builder.create();
				showUnlockDialog(dialogView, dialog);// 如果已经加了锁，就显示解锁对话框
			} else {
				dialogView = View.inflate(HomeActivity.this,
						R.layout.setup_lock_dialog_view, null);
				builder.setView(dialogView);
				dialog = builder.create();
				showSetupLockDialog(dialogView, dialog);// 未加锁，显示设置加锁对话框
			}
			break;
		case 8:
			// 进入设置中心界面
			Log.i(TAG, "正在进入SettingActivity");
			itemIntent = new Intent(HomeActivity.this, SettingActivity.class);
			startActivity(itemIntent);
			break;
		default:
			break;
		}
	}

	/**
	 * 弹出设置加锁对话框
	 */
	private void showSetupLockDialog(View view, final AlertDialog dialog) {
		final EditText et_setup_pwd = (EditText) view
				.findViewById(R.id.et_setup_pwd);
		final EditText et_setup_pwd_confirm = (EditText) view
				.findViewById(R.id.et_setup_pwd_confirm);
		view.findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String pwd = et_setup_pwd.getText().toString().trim();
				String pwd_confirm = et_setup_pwd_confirm.getText().toString()
						.trim();
				if (TextUtils.isEmpty(pwd)) {
					Toast.makeText(HomeActivity.this, "请输入密码！", 0).show();
					return;
				}
				if (TextUtils.isEmpty(pwd_confirm)) {
					Toast.makeText(HomeActivity.this, "请确认您的密码！", 0).show();
					return;
				}
				if (pwd.equals(pwd_confirm)) {
					Editor editor = sp.edit();
					editor.putString(ConfigConstant.SAFE_PASSWORD_KEY,
							StringUtils.md5(pwd));
					editor.apply();
					Toast.makeText(HomeActivity.this, "密码设置成功！", 0).show();
					// 进入手机防盗页面
					enterLostFindActivity();
					dialog.dismiss();
				} else {
					Toast.makeText(HomeActivity.this, "密码输入不一致，请重新输入！", 0)
							.show();
					et_setup_pwd.setText("");
					et_setup_pwd_confirm.setText("");
				}
			}
		});
		view.findViewById(R.id.cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
		dialog.show();
	}

	/**
	 * 弹出解锁对话框
	 */
	private void showUnlockDialog(View view, final AlertDialog dialog) {
		final EditText et_enter_pwd = (EditText) view
				.findViewById(R.id.et_enter_pwd);
		view.findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String pwd = et_enter_pwd.getText().toString().trim();
				if (TextUtils.isEmpty(pwd)) {
					Toast.makeText(HomeActivity.this, "需要输入密码！", 0).show();
					return;
				}
				if (sp.getString(ConfigConstant.SAFE_PASSWORD_KEY,
						ConfigConstant.SAFE_PASSWORD_VALUE_NONE).equals(
						StringUtils.md5(pwd))) {
					Toast.makeText(HomeActivity.this, "成功解锁！", 0).show();
					// 进入手机防盗页面
					enterLostFindActivity();
					dialog.dismiss();
				} else {
					Toast.makeText(HomeActivity.this, "密码不对哦！", 0).show();
					return;
				}
			}
		});
		view.findViewById(R.id.cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
		dialog.show();
	}

	/**
	 * 进入手机防盗页面
	 */
	private void enterLostFindActivity() {
		Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
		startActivity(intent);
	}

	/**
	 * 检查是否已经加了锁
	 * 
	 * @return
	 */
	private boolean isLocked() {
		String safePassword = sp.getString(ConfigConstant.SAFE_PASSWORD_KEY,
				ConfigConstant.SAFE_PASSWORD_VALUE_NONE);
		return !TextUtils.isEmpty(safePassword);
	}

	// ///////////////////////////////////////////////////////
	private class FunctionsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder viewHolder;
			if (convertView != null) {
				view = convertView;
				viewHolder = (ViewHolder) convertView.getTag();
			} else {
				view = View.inflate(HomeActivity.this,
						R.layout.list_home_function_item, null);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) view
						.findViewById(R.id.iv_function_item);
				viewHolder.textView = (TextView) view
						.findViewById(R.id.tv_function_item);
				view.setTag(viewHolder);
			}
			viewHolder.imageView.setImageResource(imageIds[position]);
			viewHolder.textView.setText(names[position]);
			return view;
		}

		private class ViewHolder {
			ImageView imageView;
			TextView textView;
		}
	}
}
