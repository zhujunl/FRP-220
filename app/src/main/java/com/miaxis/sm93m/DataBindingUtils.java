package com.miaxis.sm93m;

import androidx.databinding.InverseMethod;

/**
 * @author ZhuKun
 * @date 2022/5/26
 * @see
 */
public class DataBindingUtils {

    @InverseMethod("convertIntToString")
    public static Integer convertStringToInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String convertIntToString(Integer value) {
        if (value == null) return "";
        if (value == -1) return "";
        return String.valueOf(value);
    }

}
