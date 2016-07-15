package com.xgym.library.util;

/**
 * 字节操作类
 */
public final class ByteUtil {

    private ByteUtil() {
    }

    /**
     * 将字节数组转换成16进制字符串
     *
     * @param bytes 目标字节数组
     * @return 转换结果
     */

    public static String bytesToHex(byte bytes[]) {
        return bytesToHex(bytes, 0, bytes.length);
    }

    /**
     * 将字节数组中指定区间的子数组转换成16进制字符串
     *
     * @param bytes 目标字节数组
     * @param start 起始位置（包括该位置）
     * @param end   结束位置（不包括该位置）
     * @return 转换结果
     */
    public static String bytesToHex(byte[] bytes, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, count = bytes.length <= start + end ? bytes.length : start + end; i < count; i++) {
            sb.append(byteToHex(bytes[i]));
        }
        return sb.toString();
    }

    /**
     * 将单个字符转换为十六进制字符串
     *
     * @param bt 目标字符
     * @return 转换结果
     */
    public static String byteToHex(byte bt) {
        String hex = Integer.toHexString(bt & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex.toUpperCase();
    }

    /**
     * 截取 byte 数组
     *
     * @param src   原 byte 数组
     * @param begin 起始点（包含）
     * @param end   结束点（包含）
     * @return 如起始和结束点设置不正确，将返回 null
     */
    public static byte[] cutBytes(byte[] src, int begin, int end) {
        byte[] result = null;
        if (src != null && begin >= 0 && end < src.length && begin < end) {
            result = new byte[end - begin + 1];
            System.arraycopy(src, begin, result, 0, end + 1 - begin);
        }
        return result;
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] hex2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 查找 tag 在 src 中出现的位置
     *
     * @param tag 待查找的字节数组
     * @param src 原字节数组
     * @return -1 表示未找到
     */
    public static int indexOf(byte[] tag, byte[] src) {
        return indexOf(tag, src, 0);
    }

    /**
     * 查找 tag 在 src 中出现的位置
     *
     * @param tag   待查找的字节数组
     * @param src   原字节数组
     * @param start 查找的起始位置
     * @return -1 表示未找到
     */
    public static int indexOf(byte[] tag, byte[] src, int start) {
        int result = -1;
        if (tag != null && src != null && tag.length <= src.length - start) {
            for (int i = start; i <= src.length - tag.length; i++) {
                if (result != -1) {
                    break;
                }
                if (tag[0] == src[i]) {
                    for (int j = 0; j < tag.length; j++) {
                        if (tag[j] != src[i + j]) {
                            break;
                        }
                        if (j == tag.length - 1) {
                            result = i;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 查找 tag 在 src 中出现的位置
     *
     * @param tag 待查找的字节数组
     * @param src 原字节数组
     * @return -1 表示未找到
     */
    public static int lastIndexOf(byte[] tag, byte[] src) {
        return lastIndexOf(tag, src, 0);
    }

    /**
     * 查找 tag 在 src 中出现的位置
     *
     * @param tag   待查找的字节数组
     * @param src   原字节数组
     * @param start 查找的起始位置，是相对于结束位置的距离
     * @return -1 表示未找到
     */
    public static int lastIndexOf(byte[] tag, byte[] src, int start) {
        int result = -1;
        if (tag != null && src != null && tag.length <= src.length - start) {
            for (int i = src.length - start - tag.length; i >= 0; i--) {
                if (result != -1) {
                    break;
                }
                if (tag[0] == src[i]) {
                    for (int j = 0; j < tag.length; j++) {
                        if (tag[j] != src[i + j]) {
                            break;
                        }
                        if (j == tag.length - 1) {
                            result = i;
                        }
                    }
                }
            }
        }
        return result;
    }
}
