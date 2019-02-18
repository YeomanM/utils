package com.yeoman.file;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * code is far away from bug with the animal protecting
 * ┏┓　　　┏┓
 * ┏┛┻━━━┛┻┓
 * ┃　　　　　　  ┃
 * ┃　　　━　　  ┃
 * ┃　┳┛　┗┳  ┃
 * ┃　　　　　　  ┃
 * ┃　　　┻　　  ┃
 * ┃　　　　　    ┃
 * ┗━┓　　  ┏━┛
 * 　  ┃　　　┃神兽保佑
 * 　┃　　　┃代码无BUG！
 * 　  ┃　　　┗━━━┓
 * 　  ┃　　　　     ┣┓
 * 　  ┃　　　　　   ┏┛
 * 　  ┗┓┓┏━┓┓┏┛
 * 　    ┃┫┫　┃┫┫
 * 　    ┗┻┛　┗┻┛
 *
 * @Description :
 * ---------------------------------
 * @Author : Yeoman
 * @Date : Create in 2018/9/6
 */
public class PinyinUtil {

    private static final HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    static {
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    public static String toPinyin(String str) throws BadHanyuPinyinOutputFormatCombination {
        return toPinyin(str,"",PinyinType.UPPER_CASE);
    }

    public static String toPinyin(String str,String spera) throws BadHanyuPinyinOutputFormatCombination {
        return toPinyin(str,spera,PinyinType.UPPER_CASE);
    }

    public static String toPinyin(String str,PinyinType type) throws BadHanyuPinyinOutputFormatCombination {
        return toPinyin(str,"",type);
    }

    public static String toPinyin(String str,String spera,PinyinType type) throws BadHanyuPinyinOutputFormatCombination {
        if (str == null || str.trim().length() == 0){
            return "";
        }
        if (type == PinyinType.UPPER_CASE){
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        } else {
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        }

        StringBuilder py = new StringBuilder();
        String temp = "";
        String[] t;
        for (int i = 0,len = str.length();i < len;i++){
            char c = str.charAt(i);
            if (String.valueOf(c).matches("[\u4e00-\u9fa5]+")) {
                t = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if (t == null) {
                    py.append(c);
                } else {
                    temp = t[0];
                    if(type == PinyinType.FIRST_UPPER) {
                        temp = t[0].toUpperCase().charAt(0) + temp.substring(1);
                    }
                    py.append(temp).append(i == str.length() - 1 ? "" : spera);

                }
            } else {
                py.append(c);
            }
        }
        return py.toString();
    }

    public enum PinyinType{
        UPPER_CASE,
        LOWER_CASE,
        FIRST_UPPER
    }

    private PinyinUtil(){
    }

}
