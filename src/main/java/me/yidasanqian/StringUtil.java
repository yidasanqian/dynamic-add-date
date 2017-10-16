package me.yidasanqian;

/**
 * @author Linyu Chen
 */
public class StringUtil {

    private StringUtil() {
    }

    /**
     * 将下划线格式转换成驼峰式
     *
     * @param underline 下划线字符串，形如“gmt_create”
     * @return
     */
    public static String underlineToHump(String underline) {
        StringBuilder humpProperty = new StringBuilder();
        String[] underlineProperty = underline.split("_");
        for (int i = 0; i < underlineProperty.length; i++) {
            if (i > 0) {
                Character initial = underlineProperty[i].charAt(0);
                String upperInitial = initial.toString().toUpperCase();
                String initialProperty = underlineProperty[i].replaceFirst(initial.toString(), upperInitial);
                humpProperty.append(initialProperty);
            } else {
                humpProperty.append(underlineProperty[i]);
            }
        }

        return humpProperty.toString();
    }
}
