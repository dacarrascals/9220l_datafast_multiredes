package com.datafast.pinpad.cmd.Tools;

import com.newpos.libpay.utils.ISOUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class encryption {

    /**
     * Compute the SHA-256 hash of the given byte array
     *
     * @param hashThis
     *            byte[]
     * @return byte[]
     */
    public static String hashSha256(byte[] hashThis) {
        try {
            byte[] hash = new byte[20];
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            hash = md.digest(hashThis);

            return  ISOUtil.byte2hex(hash);
            //return bytesToHexString(hash);
        } catch (NoSuchAlgorithmException nsae) {
            System.err.println("SHA-1 algorithm is not available...");
            System.exit(2);
        }
        return null;
    }

    /**
     * Compute the SHA-256 hash of the given byte array
     *
     * @param hashThis
     *            byte[]
     * @return byte[]
     */
    public static String hashSha256(String hashThis) {
        try {
            byte[] hash;
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            if (hashThis==null)
                return null;

            hash = md.digest(hashThis.getBytes());

            return  ISOUtil.bcd2str(hash,0,hash.length);
            //return ISOUtil.hexString(hashThis.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException nsae) {
            System.err.println("SHA-1 algorithm is not available...");
            System.exit(2);
        }
        return null;
    }

    public static String sha256(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA256");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
