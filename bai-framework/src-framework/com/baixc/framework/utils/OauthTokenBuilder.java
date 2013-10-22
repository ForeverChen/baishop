package com.baixc.framework.utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 对用户名和密码进行加密
 * 
 * @author swing
 */
@SuppressWarnings("restriction")
public class OauthTokenBuilder {
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	private static final String[] SPLITS = new String[] { "||", "^^", "**", "$$", "++" };
	private static final String[] SPLITS_REG = new String[] { "\\|\\|", "\\^\\^", "\\*\\*", "\\$\\$", "\\+\\+" };
	private static String ENCRYPT_KEY = "k@d@ngd0tcom";
	
	private volatile static OauthTokenBuilder instance;
	private DESedeEncryptor desEncrypt = new DESedeEncryptor();

	public static OauthTokenBuilder get() {
		if (instance == null) {
			synchronized (OauthTokenBuilder.class) {
				if (instance == null)
					instance = new OauthTokenBuilder();
			}
		}
		return instance;
	}

	/**
	 * 加密 Token对象
	 * 如果加密后的加密串要用于 URL 进行传递，那么就必须
	 * @param token
	 * @return
	 */
	public String buildToken(OauthToken token) {
		StringBuffer sb = new StringBuffer();
		String split = this.findSplit(StringUtils.join(new String[] {
				token.getUsername(), token.getPassword() }));
		sb.append(System.currentTimeMillis()).append(split)
				.append(token.getUsername()).append(split)
				.append(token.getPassword());

		return desEncrypt.encrypt(sb.toString(), ENCRYPT_KEY);
	}

	/**
	 * 根据 token 串解密出 OauthToken 对象
	 * @param token
	 * @return
	 */
	public OauthToken parseToken(String token) {
		String deToken = desEncrypt.decrypt(token, ENCRYPT_KEY);
		if (deToken == null)
			return null;
		String[] tokens = this.findSplitsToken(deToken);
		if (tokens != null && tokens.length == 3) {
			OauthToken oauthToken = new OauthToken();
			oauthToken.setUsername(tokens[1]);
			oauthToken.setPassword(tokens[2]);
			try {// token时间限制，半小时
				long time = Long.parseLong(tokens[0]);
				if ((System.currentTimeMillis() - time) > 1800000){
					logger.error("令牌已超时");
					return null;
				}
			} catch (NumberFormatException e) {
				return null;
			}
			return oauthToken;
		} else {
			logger.error("令牌已超时");
		}
		return null;
	}

	// 查找出合适的分隔符
	private String findSplit(String name) {
		for (String split : SPLITS) {
			if (name.indexOf(split) < 0)
				return split;
		}
		return SPLITS[0];
	}

	// 同样，反解析时，也需要查找到合适的分隔符
	private String[] findSplitsToken(String deToken) {
		String[] tokens = null;
		for (String reg : SPLITS_REG) {
			tokens = deToken.split(reg);
			if (tokens.length == 3)
				return tokens;
		}
		return null;
	}

	public static class OauthToken {
		private String username;
		private String password;
		
		public OauthToken(){
		}
		
		public OauthToken(String username, String password){
			this.username = username;
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	/**
	 * DES加密内部类
	 */
	class DESedeEncryptor {
		private static final String DESEDE = "DESede";

		private SecretKeyFactory keyFactory;
		private Cipher encryptCipher;
		private Cipher decryptCipher;

		// private IvParameterSpec IvParameters;

		private Map<String, SecretKey> secretKeyCache = new HashMap<String, SecretKey>();

		private DESedeEncryptor() {
			try {
				// 检测是否有 TripleDES 加密的供应程序
				// 如无，明确地安装SunJCE 供应程序
				try {
					Cipher.getInstance(DESEDE);
				} catch (Exception e) {
					logger.error("Installling SunJCE provider.");
					Provider sunjce = new com.sun.crypto.provider.SunJCE();
					Security.addProvider(sunjce);
				}
				// 得到 DESSede keys
				keyFactory = SecretKeyFactory.getInstance(DESEDE);
				// 创建一个 DESede 密码
				encryptCipher = Cipher.getInstance(DESEDE);
				decryptCipher = Cipher.getInstance(DESEDE);
				// 为 CBC 模式创建一个用于初始化的 vector 对象
				// IvParameters = new IvParameterSpec(new byte[] { 12, 34, 56,
				// 78, 90,
				// 87, 65, 43 });
			} catch (Exception e) {
				// 记录加密或解密操作错误
				logger.error(
						"It's not support DESede encrypt arithmetic in this system!",
						e);
			}
		}

		/**
		 * 加密算法
		 * 
		 * @param data
		 *            等待加密的数据，不能为空
		 * @param key
		 *            加密的密钥，不能为空
		 * @return 加密以后的数据
		 */
		public String encrypt(String data, String key) {
			if (data == null || key == null)
				return null;
			// 补全24位
			key = StringUtils.rightPad(key, 24);
			byte[] encrypted_pwd = null;

			try {
				SecretKey secretKey = buildSecretKey(key);
				synchronized (encryptCipher) {
					// 以加密模式初始化密钥
					encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
					// 加密密码
					encrypted_pwd = encryptCipher.doFinal(data.getBytes());
				}
				// 转成字符串，得到加密后的密码（新）
				return base64Encode(encrypted_pwd);
			} catch (Exception e) {
				logger.error(
						"When encrypt error occurs. Maybe your secretKey is not right",
						e);
				return null;
			}
		}

		/**
		 * 解密算法
		 * 
		 * @param data
		 *            已加过密过的数据
		 * @param key
		 *            解密的密钥，不能为空，与加密的密钥相同
		 * @return 解密后的密码
		 */
		public String decrypt(String data, String key) {
			if (data == null || key == null)
				return null;
			// 补全24位
			key = StringUtils.rightPad(key, 24);
			try {
				SecretKey secretKey = buildSecretKey(key);
				byte[] decrypted_pwd;
				synchronized (decryptCipher) {
					// 以解密模式初始化密钥
					decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
					// 解密密码
					decrypted_pwd = decryptCipher.doFinal(base64Decode(data));
				}
				// 得到结果
				return new String(decrypted_pwd);
			} catch (Exception e) {
				logger.error(
						"When encrypt error occurs. Maybe your secretKey is not right",
						e);
				return null;
			}
		}

		private SecretKey buildSecretKey(String key) throws Exception {
			SecretKey secretKey = secretKeyCache.get(key);
			if (secretKey == null) {
				// 为上一密钥创建一个指定的 DESSede key
				DESedeKeySpec spec = new DESedeKeySpec(key.getBytes());
				// 生成一个 DESede 密钥对象
				secretKey = keyFactory.generateSecret(spec);
				secretKeyCache.put(key, secretKey);
			}
			return secretKey;
		}

		private String base64Encode(byte[] bytes) {
			BASE64Encoder b64e = new BASE64Encoder();
			String rs = b64e.encodeBuffer(bytes);
			if (rs != null)
				return rs.replaceAll("\\n|\\r", "");
			return rs;
		}

		private byte[] base64Decode(String data) {
			if (data == null)
				return null;

			BASE64Decoder dec = new BASE64Decoder();
			try {
				return dec.decodeBuffer(data);
			} catch (IOException e) {
				logger.warn("Couldn't decode form [ " + data + " ] for base64");
				return null;
			}
		}
	}

	public static void main(String[] args) throws Exception {
		OauthTokenBuilder.OauthToken oauthToken = new OauthTokenBuilder.OauthToken();
		oauthToken.setUsername("swingfasdfadsf");
		oauthToken.setPassword("abcde");
		List<String> tokens = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			String token = OauthTokenBuilder.get().buildToken(oauthToken);
			System.out.println(token + " |||| "
					+ URLEncoder.encode(token, "UTF-8"));
			tokens.add(token);
			Thread.sleep(10);
		}
		for (String token : tokens) {
			OauthTokenBuilder.OauthToken t = OauthTokenBuilder.get()
					.parseToken(token);
			System.out.print(t.getUsername() + " --- ");
			System.out.println(t.getPassword());
		}
	}
}
