package com.foodie.core.feature.keygen;

import java.math.BigInteger;

/**
 * RSAEncoder :
 *
 * @author StarZou
 * @since 2015-04-04 13:54
 */
public class RSAEncoder {
    private final BigInteger privKey;
    private final BigInteger pubKey;
    private final int c;
    private final int d;
    private int e = 0;
    private final BigInteger f;
    private final boolean g;

    public RSAEncoder(BigInteger privKey, BigInteger pubKey, int len, boolean newLine) {
        this.privKey = privKey;
        this.pubKey = pubKey;
        this.g = newLine;
        int privKeyLen = privKey.bitLength();
        this.f = new BigInteger(String.valueOf(len));
        int i = (int) Math.ceil(privKeyLen / Math.log(len) * Math.log(2.0D));
        if (i % 5 != 0) {
            i = (i / 5 + 1) * 5;
        }
        this.d = i;
        this.c = (privKeyLen / 8 - 1);
    }

    public String encode(byte[] bytes) {
        int i = bytes.length % this.c;
        byte[] arrayOfByte = new byte[i == 0 ? bytes.length : bytes.length + this.c - i];
        System.arraycopy(bytes, 0, arrayOfByte, this.c - i, bytes.length);

        StringBuffer stringBuffer = new StringBuffer();
        for (int j = 0; j < arrayOfByte.length; j += this.c) {
            encode(arrayOfByte, stringBuffer, j, this.c);
        }
        return stringBuffer.toString();
    }

    private void encode(byte[] bytes, StringBuffer stringBuffer, int off, int len) {
        if (len == 0) {
            return;
        }
        byte[] arrayOfByte = new byte[this.c];
        System.arraycopy(bytes, off, arrayOfByte, 0, len);
        BigInteger localBigInteger1 = new BigInteger(1, arrayOfByte);
        if (localBigInteger1.compareTo(this.pubKey) >= 0) {
            throw new IllegalArgumentException("result is too long");
        }
        BigInteger localBigInteger2 = localBigInteger1.modPow(this.privKey, this.pubKey);
        stringBuffer.append(a(a(localBigInteger2)));
    }

    private String a(String paramString) {
        StringBuffer localStringBuffer = new StringBuffer();
        for (int i = 0; i < paramString.length(); i++) {
            a(localStringBuffer);
            localStringBuffer.append(paramString.charAt(i));
        }
        return localStringBuffer.toString();
    }

    private String a(BigInteger paramBigInteger) {
        StringBuffer localStringBuffer = new StringBuffer();
        for (int i = 0; i < this.d; i++) {
            localStringBuffer.insert(0, b(paramBigInteger.mod(this.f)));
            paramBigInteger = paramBigInteger.divide(this.f);
        }
        return localStringBuffer.toString();
    }

    private void a(StringBuffer paramStringBuffer) {
        if ((this.e > 0) && (this.e % 5 == 0)) {
            if (this.e % 30 == 0)
                paramStringBuffer.append('\n');
            else if (this.g) {
                paramStringBuffer.append('-');
            }
        }
        this.e += 1;
    }

    private static char b(BigInteger paramBigInteger) {
        int i = paramBigInteger.intValue();
        char c1;
        if (i < 10) {
            c1 = (char) (48 + i);
        } else {
            if (i < 36) {
                c1 = (char) (65 + i - 10);
            } else {
                if (i < 62)
                    c1 = (char) (97 + i - 36);
                else {
                    c1 = (char) (33 + i - 62);
                }
            }
        }
        return c1;
    }
}

