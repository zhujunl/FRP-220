package com.miaxis.sm93m;

import java.util.Arrays;

/**
 * @author ZhuKun
 * @date 2022/9/27
 * @see
 */
public class MxImage {
    public final int error;
    public final int width;
    public final int height;
    /**Number of channels [1：Grayscale] [3: RGB] [4：ARGB]*/
    public final int channels;
    public final byte[] data;
    public Object tag;

    public MxImage(int error, int width, int height, int channels, byte[] data) {
        this.error = error;
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
                "width=" + width +
                ", height=" + height +
                ", channels=" + channels +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
