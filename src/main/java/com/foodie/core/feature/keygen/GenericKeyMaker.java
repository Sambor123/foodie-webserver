package com.foodie.core.feature.keygen;

import java.math.BigInteger;
import java.util.Random;
import java.util.zip.CRC32;

/**
 * GenericKeyMaker : JetBrains 系列产品 KegGen
 *
 * @author StarZou
 * @since 2015-04-04 13:56
 */
public class GenericKeyMaker {

    public static final int LICENSETYPE_COMMERCIAL = 0;
    public static final int LICENSETYPE_NON_COMMERCIAL = 1;
    public static final int LICENSETYPE_SITE = 2;
    public static final int LICENSETYPE_OPENSOURCE = 3;
    public static final int LICENSETYPE_PERSONAL = 4;
    public static final int LICENSETYPE_YEARACADEMIC = 5;

    public static final int PRODUCTID_RubyMine = 4;
    public static final int PRODUCTID_PyCharm = 5;
    public static final int PRODUCTID_WebStorm = 6;
    public static final int PRODUCTID_PhpStorm = 7;
    public static final int PRODUCTID_AppCode = 8;

    private Random random = new Random();

    private String getLicenseId() {
        return String.format("D%sT", Integer.toString(random.nextInt(90000) + 10000));
    }

    private short getCRC(String s, int i, byte bytes[]) {
        CRC32 crc32 = new CRC32();
        if (s != null) {
            for (int j = 0; j < s.length(); j++) {
                char c = s.charAt(j);
                crc32.update(c);
            }
        }
        crc32.update(i);
        crc32.update(i >> 8);
        crc32.update(i >> 16);
        crc32.update(i >> 24);
        for (int k = 0; k < bytes.length - 2; k++) {
            byte byte0 = bytes[k];
            crc32.update(byte0);
        }
        return (short) (int) crc32.getValue();
    }

    private byte[] generateKeyBytes(int licenseType, int productId,
                                    int minorVersion, int majorVersion,
                                    String userName, int customerId) {
        byte[] keyBytes = new byte[14];
        keyBytes[0] = (byte) ((licenseType << 4) + (productId & 0xFF));
        keyBytes[1] = (byte) ((minorVersion << 4) + (majorVersion & 0xFF));
        long time = System.currentTimeMillis() >> 16;
        keyBytes[2] = (byte) (int) (time & 0xFF);
        keyBytes[3] = (byte) (int) (time >> 8 & 0xFF);
        keyBytes[4] = (byte) (int) (time >> 16 & 0xFF);
        keyBytes[5] = (byte) (int) (time >> 24 & 0xFF);
        long timeDiff = 99 * 365;

        timeDiff &= 65535L;
        keyBytes[6] = (byte) (int) (timeDiff & 0xFF);
        keyBytes[7] = (byte) (int) (timeDiff >> 8 & 0xFF);
        keyBytes[8] = 0;
        keyBytes[9] = 1;
        keyBytes[10] = 2;
        keyBytes[11] = 3;
        keyBytes[12] = 4;
        keyBytes[13] = 5;

        int crc32 = getCRC(userName, customerId, keyBytes);
        keyBytes[12] = (byte) (crc32 & 0xFF);
        keyBytes[13] = (byte) (crc32 >> 8 & 0xFF);

        return keyBytes;
    }

    public String generateKey(BigInteger privKey, BigInteger pubKey,
                              int licenseType, int productId,
                              int minorVersion, int majorVersion,
                              String userName) {

        int customerId = random.nextInt(9000) + 1000;
        byte[] keyBytes = generateKeyBytes(licenseType, productId, minorVersion, majorVersion, userName, customerId);

        RSAEncoder encoder = new RSAEncoder(privKey, pubKey, 64, false);
        String serial = encoder.encode(keyBytes);

        serial = "===== LICENSE BEGIN =====\n" + customerId + "-" + getLicenseId() + "\n" + serial + "\n===== LICENSE END =====";
        return serial;
    }

    public String genericPyCharmKey(int minorVersion, int majorVersion, String userName) {
        BigInteger pubKey = new BigInteger("D57B0596A03949D9A3BB0CD1F7931E405AE27D0E0AF4E562072B487B0DAB7F0874AA982E5383E75FF13D36CA9D8531AC1FA2ED7B11C8858E821C2D5FB48002DD", 16);
        BigInteger privKey = new BigInteger("406047D02363033D295DB7C0FD8A94DDCD4A6D71B5A622220C8D65DF0DC1409E0BDE26AF66B0AD717406C22FC8BEC3ED88C1B7091BA3443B6BFBA26120DE6A15", 16);

        return generateKey(privKey, pubKey, LICENSETYPE_NON_COMMERCIAL, PRODUCTID_PyCharm, minorVersion, majorVersion, userName);
    }

    public String genericAppCodeKey(int minorVersion, int majorVersion, String userName) {
        BigInteger pubKey = new BigInteger("F0DD6995C4BD3223641C79C8608D74F32ED54A8BDAE468EB5AC53F1F1C8925E263F82317356BC73B1C82B520630250212416C99CB39A8B7C2611E35552E166B9", 16);
        BigInteger privKey = new BigInteger("81B5EAEF61A4B584839C26253781D63243CD4F38E3A74FAD3713B3FB7025978538F10E743456F24BB20D5792BFDCB76DB6162C3D5C77DB7B29906CBFC9114EA5", 16);

        return generateKey(privKey, pubKey, LICENSETYPE_NON_COMMERCIAL, PRODUCTID_AppCode, minorVersion, majorVersion, userName);
    }

    public String genericPhpStormKey(int minorVersion, int majorVersion, String userName) {
        BigInteger pubKey = new BigInteger("BB62FBB57F105CD61B47AE2290FCB3CE1179942DE171BEDDF6BAA1A521B9368B735C7C931902EBA8DE6D160711A6ECC40F4A5E766E9FCDEE8A38715DB572AD3D", 16);
        BigInteger privKey = new BigInteger("7BFADCB153F59E86E69BC1820B4DB72573786E6B00CB824E57AD59BFE915231972746F47C6FBE0D8D88809DA313C1E4BEAD305AD8AFD31AE116ABCB181FF4F21", 16);

        return generateKey(privKey, pubKey, LICENSETYPE_NON_COMMERCIAL, PRODUCTID_PhpStorm, minorVersion, majorVersion, userName);
    }

    public String genericRubyMineKey(int minorVersion, int majorVersion, String userName) {
        BigInteger pubKey = new BigInteger("BB62FBB57F105CD61B47AE2290FCB3CE1179942DE171BEDDF6BAA1A521B9368B735C7C931902EBA8DE6D160711A6ECC40F4A5E766E9FCDEE8A38715DB572AD3D", 16);
        BigInteger privKey = new BigInteger("7BFADCB153F59E86E69BC1820B4DB72573786E6B00CB824E57AD59BFE915231972746F47C6FBE0D8D88809DA313C1E4BEAD305AD8AFD31AE116ABCB181FF4F21", 16);

        return generateKey(privKey, pubKey, LICENSETYPE_NON_COMMERCIAL, PRODUCTID_RubyMine, minorVersion, majorVersion, userName);
    }

    public String genericWebStormKey(int minorVersion, int majorVersion, String userName) {
        BigInteger pubKey = new BigInteger("BB62FBB57F105CD61B47AE2290FCB3CE1179942DE171BEDDF6BAA1A521B9368B735C7C931902EBA8DE6D160711A6ECC40F4A5E766E9FCDEE8A38715DB572AD3D", 16);
        BigInteger privKey = new BigInteger("7BFADCB153F59E86E69BC1820B4DB72573786E6B00CB824E57AD59BFE915231972746F47C6FBE0D8D88809DA313C1E4BEAD305AD8AFD31AE116ABCB181FF4F21", 16);

        return generateKey(privKey, pubKey, LICENSETYPE_NON_COMMERCIAL, PRODUCTID_WebStorm, minorVersion, majorVersion, userName);
    }

    public static void main(String[] args) {
        GenericKeyMaker keyMaker = new GenericKeyMaker();
        System.out.println(keyMaker.genericAppCodeKey(1, 13, "StarZou"));
    }
}