package cn.say.anything.tool;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;

public class MD5Util {
	public static String encryptMD5(String source) {
		if (StringUtils.isEmpty(source)) {
			return null;
		}
		Md5Hash md5Hash = new Md5Hash(source);
		String encryptValue = md5Hash.toString();
		return encryptValue;
	}
}
