package cn.blackgene.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {

	/**
	 * MD5加密算法
	 * @param s
	 * @return MD5摘要字符串
	 */
	public static String md5(String s){
		StringBuffer buffer = new StringBuffer();
		try {
			MessageDigest digest = MessageDigest.getInstance("md5");
			byte [] result = digest.digest(s.getBytes());
			for(byte b : result){
			     //0xff是十六进制，十进制为255
				int nuber =  b & 0xff;
				String str = Integer.toHexString(nuber);
				if(str.length()==1){
					buffer.append("0");
				}
				buffer.append(str);
				
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
		return buffer.toString();
	}
}
