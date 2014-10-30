package cn.blackgene.mobilesafe.constants;

public interface ConfigConstant {
	public static final String PREFERENCE_NAME = "config";// 配置文件名称

	public static final String IS_AUTO_UPDATE_KEY = "isAutoUpdate";// 是否自动更新的键值
	public static final boolean AUTO_UPDATE_YES = true;// 自动更新
	public static final boolean AUTO_UPDATE_NO = false;// 不自动更新
	public static final boolean AUTO_UPDATE_DEFAULT = AUTO_UPDATE_YES;// 是否自动更新的默认值(YES)

	public static final String SAFE_PASSWORD_KEY = "safePassword";// 手机防盗页面的安全密码的键值
	public static final String SAFE_PASSWORD_VALUE_NONE = "";// 安全密码的空字符串值

	public static final String IS_CONFIGED_KEY = "isConfiged";// 是否经过了引导配置的键值
	public static final boolean IS_CONFIGED_YES = true;// 已配置
	public static final boolean IS_CONFIGED_NO = false;// 未配置
}
