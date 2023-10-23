package com.apt.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class ThreeDES {
	private final String ALGORITHM = "DESede";
	
	//Triple DES with CBC mode
	private final String TRANSFORMATION = "DESede/CBC/PKCS5Padding"; //NoPadding明文長度必須為8的倍數,PKCS5Padding則自動補位
	private final String KEY = "123456ABCDEF!@#$%^abcdef";
	
	private final IvParameterSpec iv = new IvParameterSpec(new byte[8]);

	private Cipher enCipher = null;
	private Cipher deCipher = null;
	
	public ThreeDES(){
		try{
			enCipher = getCipher(Cipher.ENCRYPT_MODE, KEY);
			deCipher = getCipher(Cipher.DECRYPT_MODE, KEY);
		}catch(Exception e){}
	}
	
	private Cipher getCipher(int mode, String key) throws Exception{
		DESedeKeySpec dks = new DESedeKeySpec(key.getBytes());
		
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
		SecretKey securekey = keyFactory.generateSecret(dks);
		
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(mode, securekey, iv);
		
		return cipher;
	}
	
	private String toHex(byte b){return (""+"0123456789ABCDEF".charAt(0xf&b>>4)+"0123456789ABCDEF".charAt(b&0xf));}
	public byte[] hexToBytes(char[] hex) {
		int length = hex.length / 2;
		byte[] raw = new byte[length];
		for (int i = 0; i < length; i++) {
			int high = Character.digit(hex[i * 2], 16);
			int low = Character.digit(hex[i * 2 + 1], 16);
			int value = (high << 4) | low;
			if (value > 127)
				value -= 256;
			raw[i] = (byte) value;
		}
		return raw;
	}
	
	public String encrypt(String data) throws Exception{
		byte bytes[] = enCipher.doFinal(data.getBytes());
		StringBuffer sb = new StringBuffer();
		for (byte b: bytes) sb.append(toHex(b));
		return sb.toString();
	}
	
	public String encrypt(String data, String key) throws Exception{
		Cipher enCipher = getCipher(Cipher.ENCRYPT_MODE, key);
		
		byte bytes[] = enCipher.doFinal(data.getBytes());
		StringBuffer sb = new StringBuffer();
		for (byte b: bytes) sb.append(toHex(b));
		return sb.toString();
	}

	public String decrypt(String data) throws Exception{
		return new String(deCipher.doFinal(hexToBytes(data.toCharArray())));
	}
	
	public String decrypt(String data, String key) throws Exception{
		Cipher deCipher = getCipher(Cipher.DECRYPT_MODE, key);
		return new String(deCipher.doFinal(hexToBytes(data.toCharArray())));
	}
	
	public static void main(String args[]) throws Exception{
		String KEY = "7hKFAAoTc8N3iuxU3eu4MeN4";
		String RAW = "ACER12345";
		
		ThreeDES des = new ThreeDES();
		
		String ENC = des.encrypt(RAW,KEY);
		
		System.out.println("ENC >"+ENC+"<");
		System.out.println("decrypt: >"+des.decrypt(ENC,KEY)+"<");
	}
}
