package com.gitee.starblues.core.launcher.jar;

/**
 * copy from spring-boot-loader
 * @author starBlues
 * @version 3.0.0
 */
public class Bytes {

    private Bytes() {
    }

    static long littleEndianValue(byte[] bytes, int offset, int length) {
        long value = 0;
        for (int i = length - 1; i >= 0; i--) {
            value = ((value << 8) | (bytes[offset + i] & 0xFF));
        }
        return value;
    }


}
