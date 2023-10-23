/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

import org.apache.log4j.Logger;
import static java.lang.System.out;
import org.apache.commons.codec.binary.Base64;


/**
 *
 * @author kevinchang
 */
public class KeyUtil {

    private KeyPair generatedKeyPair;
    private static String keyAlgorithm = "RSA";
    private static final Logger log = Logger.getLogger("EPAY");
    
    public void genKey(String keyPath, String pvKeyName, String pbKeyName, String refMsg) throws NoSuchAlgorithmException, IOException{
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(keyAlgorithm);
        SecureRandom random = new SecureRandom();
        random.setSeed(refMsg.getBytes());
        keygen.initialize(1024, random);
        generatedKeyPair = keygen.generateKeyPair();
        dumpKeyPair(generatedKeyPair);
        SaveKeyPair(keyPath,pvKeyName,pbKeyName,generatedKeyPair);
    }
    private void dumpKeyPair(KeyPair keyPair){
        PublicKey pubKey = keyPair.getPublic();
        PrivateKey privKey = keyPair.getPrivate();
        log.info( keyAlgorithm+" Public Key："+getHexString(pubKey.getEncoded()));
        log.info( keyAlgorithm+" Private Key："+getHexString(privKey.getEncoded()));
    }
    public void setKeyPair(KeyPair keyPair){
        this.generatedKeyPair = keyPair;
    }
    public String getPublicKeyHex(){
        return getHexString(generatedKeyPair.getPublic().getEncoded());
    }
    public PublicKey getPublicKey(){
        return generatedKeyPair.getPublic();
    }
    public String getPrivateKeyHex(){
        return getHexString(generatedKeyPair.getPrivate().getEncoded());
    }
    public PrivateKey getPrivateKey(){
        return generatedKeyPair.getPrivate();
    }
    private void SaveKeyPair(String keyPath, String pvKeyName, String pbKeyName, KeyPair keyPair) throws FileNotFoundException, IOException{
        PrivateKey privKey = keyPair.getPrivate();
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privKey.getEncoded());
        FileOutputStream fos = new FileOutputStream(keyPath+pvKeyName);
        fos.write(pkcs8EncodedKeySpec.getEncoded());
        fos.close();
        
        PublicKey pubKey = keyPair.getPublic();
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(pubKey.getEncoded());
        fos = new FileOutputStream(keyPath+pbKeyName);
        fos.write(x509EncodedKeySpec.getEncoded());
        fos.close();
    }
    public KeyPair LoadKeyPair(String path, String pvKeyName, String pbKeyName) throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException{
        File filePrivateKey = new File(path+pvKeyName);
        FileInputStream  fis = new FileInputStream(filePrivateKey);
        byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
        fis.read(encodedPrivateKey);
        fis.close();

        File filePublicKey = new File(path+pbKeyName);
        fis = new FileInputStream(filePublicKey);
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();
        
        return genKeyPair(encodedPublicKey,encodedPrivateKey);
    }
    
    private KeyPair genKeyPair(byte[] encodedPublicKey,byte[] encodedPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException{
        KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
        PublicKey publickKey = keyFactory.generatePublic(publicKeySpec);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        
        return new KeyPair(publickKey,privateKey);
    }
//    private KeyPair genKeyPairFromHexKey(String pubkeyHex,String privkeyHex) throws NoSuchAlgorithmException, InvalidKeySpecException{
//        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(pubkeyHex.getBytes());
//        PublicKey publickKey = keyFactory.generatePublic(publicKeySpec);
//        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privkeyHex.getBytes());
//        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
//        
//        return new KeyPair(publickKey,privateKey);
//    }
    public String getHexString(byte[] b){
        StringBuilder result = new StringBuilder();
        for(int i=0;i<b.length; i++)
            result.append(Integer.toString((b[i]&0xff)+0x100,16).substring(1));
        return result.toString();
    }
    private byte[] encode(Key key, byte[] srcBytes) throws Exception {
        Cipher cipher = Cipher.getInstance(keyAlgorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(srcBytes);
    }

    private byte[] decode(Key key, byte[] srcBytes) throws Exception {
        Cipher cipher = Cipher.getInstance(keyAlgorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(srcBytes);
    }

    public String encodeBase64(Key key, byte[] srcBytes) throws Exception {
        return  Base64.encodeBase64String(encode(key, srcBytes));
    }

    public byte[] decodeBase64(Key key, String base64String) throws Exception {
        return decode(key, Base64.decodeBase64(base64String));
    }

    public static void main(String[] args) throws Exception {
        String keyPath = "./";
        String pvKeyName = "private.key";
        String pbKeyName = "public.key";
        KeyUtil rsaU = new KeyUtil();
        rsaU.genKey(keyPath,pvKeyName,pbKeyName,"Hello World");
        
        String msg = "1234567890123456";
        out.println("明文:" + msg);

        out.println("私鑰:");
        out.println("\tkey:" + rsaU.getPrivateKeyHex());

        out.println("公鑰:");
        out.println("\tkey:" + rsaU.getPublicKeyHex());
        
        rsaU.setKeyPair(rsaU.LoadKeyPair(keyPath,pvKeyName,pbKeyName));
        {
            out.println("公鑰加密，私鑰解密:");
            String base64Str = rsaU.encodeBase64(rsaU.getPublicKey(), msg.getBytes());
            out.println("\t加密後:" + base64Str);
            byte[] decBytes = rsaU.decodeBase64(rsaU.getPrivateKey(), base64Str);
            out.println("\t解密後:" + new String(decBytes));
        }
        {
            out.println("私鑰加密，公鑰解密:");
            String base64Str = rsaU.encodeBase64(rsaU.getPrivateKey(), msg.getBytes());
            out.println("\t加密後:" + base64Str);
            byte[] decBytes = rsaU.decodeBase64(rsaU.getPublicKey(), base64Str);
            out.println("\t解密後:" + new String(decBytes));
        }
    }

}
