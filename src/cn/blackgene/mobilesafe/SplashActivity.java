package cn.blackgene.mobilesafe;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;
import cn.blackgene.mobilesafe.constants.ConfigConstant;
import cn.blackgene.mobilesafe.utils.StreamTool;

public class SplashActivity extends Activity {
	private static final String TAG = "SplashActivity";// 用于LOG日志的TAG
	private static final int UPDATE = 0;
	private static final int URL_ERROR = 1;
	private static final int PROTOCOL_ERROR = 2;
	private static final int JSON_ERROR = 3;
	private static final int NETWORK_ERROR = 4;
	private static final int ENTER_HOME = 5;
	String version = "";
	String description = "";
	String apkurl = "";
	private TextView tv_splash_update_progress;
	private SharedPreferences sp;
	private HttpHandler<File> httpHandler;//用于APK文件下载的HttpHandler

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tv_splash_update_progress = (TextView) findViewById(R.id.tv_splash_update_progress);
		TextView tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("Version:" + getVersionName());
		sp = getSharedPreferences(ConfigConstant.PREFERENCE_NAME, MODE_PRIVATE);
		checkUpdate();// 检查版本更新
		// 开启延迟动画
		AlphaAnimation animation = new AlphaAnimation(0.2f, 1.0f);
		animation.setDuration(1000);
		findViewById(R.id.splash_root).startAnimation(animation);
	}

	/* Handler对象，用于处理后台线程返回的Message消息 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE:
				// 读取配置，看是否需要自动更新
				if (sp.getBoolean(ConfigConstant.IS_AUTO_UPDATE_KEY, ConfigConstant.AUTO_UPDATE_DEFAULT)) {
					update();// 升级
				} else {
					enterHome();
				}
				break;
			case URL_ERROR:
				Toast.makeText(getApplicationContext(), "URL解析异常！",
						Toast.LENGTH_LONG).show();
				enterHome();
				break;
			case PROTOCOL_ERROR:
				Toast.makeText(getApplicationContext(), "网络协议异常！",
						Toast.LENGTH_LONG).show();
				enterHome();
				break;
			case JSON_ERROR:
				Toast.makeText(getApplicationContext(), "数据解析异常！",
						Toast.LENGTH_LONG).show();
				enterHome();
				break;
			case NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), "网络连接异常！",
						Toast.LENGTH_LONG).show();
				enterHome();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 升级应用（弹出对话框）
	 */
	private void update() {
		Log.i(TAG, "开始升级...");
		showUpdateDialog();
	}

	/**
	 * 弹出升级对话框
	 */
	private void showUpdateDialog() {
		if (SplashActivity.this.isFinishing())
			return;
		Log.i(TAG, "弹出升级对话框：");
		AlertDialog.Builder builder = new AlertDialog.Builder(
				SplashActivity.this);
		builder.setTitle("升级喽！");
		// builder.setCancelable(false);//升级对话框不可取消
		// 设置取消事件
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				Log.i(TAG, "升级被取消了！");
				dialog.dismiss();
				enterHome();// 进入主页面
			}
		});
		// 设置确认点击事件
		builder.setPositiveButton("立刻升级",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.i(TAG, "用户确认升级！");
						downloadAPK();// 下载并提示安装新APK
					}
				});
		// 设置取消点击事件
		builder.setNegativeButton("下次再说",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.i(TAG, "用户本次没有选择升级！");
						dialog.dismiss();
						enterHome();
					}
				});
		builder.create().show();
	}

	/**
	 * 下载并提示安装新APK
	 */
	private void downloadAPK() {
		Log.i(TAG, "即将下载新的APK文件...");
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(getApplicationContext(), "SD卡不可用！下载失败！", Toast.LENGTH_LONG).show();
			enterHome();
			return;
		}
		FinalHttp fh = new FinalHttp();
		httpHandler = fh.download(apkurl,//
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobilesafe-2.0.apk",//
				true,//
				new AjaxCallBack<File>() {
					@Override
					public void onLoading(long count, long current) {
						Log.i(TAG,"下载中。。。");
						tv_splash_update_progress.setVisibility(View.VISIBLE);
						tv_splash_update_progress.setText("正在升级：" + current * 100 / count + "%");
						if(SplashActivity.this.isFinishing()){
						}
					}

					@Override
					public void onSuccess(File t) {
						Log.i(TAG,"下载完成！！！");
						installAPK(t);
					}
					
					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						Log.i(TAG,"下载失败！！！");
						Toast.makeText(getApplicationContext(), "下载更新失败！", Toast.LENGTH_LONG).show();
						super.onFailure(t, errorNo, strMsg);
					}

				});
	}

	/**
	 * 安装新版本APK文件
	 * @param t 新版本APK文件
	 */
	private void installAPK(File t) {
		Log.i(TAG,"开始安装APK了。。。");
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
		startActivity(intent);
		finish();
	}

	/**
	 * 进入主页面
	 */
	private void enterHome() {
		if (SplashActivity.this.isFinishing())
			return;
		Log.i(TAG, "即将进入主页面...");
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 检测应用版本升级（延迟2秒）
	 */
	private void checkUpdate() {
		Log.i(TAG, "开始检查更新。。。");
		new Thread(new Runnable() {
			Message message = Message.obtain();// 得到一个Message对象

			@Override
			public void run() {
				long startTime = System.currentTimeMillis();// URL连接前的时间
				try {
					URL url = new URL(getString(R.string.serverurl));// 得到服务器更新URL
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();// 开启HTTP连接
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(3000);
					int code = conn.getResponseCode();
					if (code == 200) {// 联网成功
						Log.i(TAG, "联网成功！ResponseCode = " + code);
						String updateInfo = StreamTool.readFromStream(conn
								.getInputStream());// 读取conn数据，转换成String
						JSONObject json = new JSONObject(updateInfo);// 转成JSONObject格式
						version = json.getString("version");// 版本名称
						description = json.getString("description");// 版本描述
						apkurl = json.getString("apkurl");// 新版本的下载地址
					}
					if (getVersionName().equals(version)) {
						message.what = ENTER_HOME;// 版本一致，进入主页面
					} else {
						message.what = UPDATE;// 版本不一致，升级
					}
				} catch (MalformedURLException e) {
					message.what = URL_ERROR;// URL异常
					Log.i(TAG, "URL_ERROR");
					e.printStackTrace();
				} catch (ProtocolException e) {
					message.what = PROTOCOL_ERROR;// 协议异常
					Log.i(TAG, "PROTOCOL_ERROR");
					e.printStackTrace();
				} catch (JSONException e) {
					message.what = JSON_ERROR;// JSON异常
					Log.i(TAG, "JSON_ERROR");
					e.printStackTrace();
				} catch (IOException e) {
					message.what = NETWORK_ERROR;// IO异常
					Log.i(TAG, "NETWORK_ERROR");
					e.printStackTrace();
				} finally {
					/* SplashActivity延迟 */
					long time = System.currentTimeMillis() - startTime;// URL连接所耗的时间
					if (time < 3000) {// 如果URL耗时不够 2 秒
						try {
							Thread.sleep(3000 - time);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendMessage(message);// 用handler发送Message消息
					// message.recycle();//不能再recycle()，否则UI线程接收不到了message
				}
			}
		}).start();
	}

	/**
	 * 获取应用的版本名称
	 * 
	 * @return
	 */
	private String getVersionName() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pinfo = pm.getPackageInfo(getPackageName(), 0);
			Log.i(TAG, "得到已安装应用的版本信息：VersionName = " + pinfo.versionName);
			return pinfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			Log.i(TAG, "获取应用版本信息出现异常，未能获得！");
			return "";
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(httpHandler != null){
			httpHandler.stop();
		}
	}
}
