package io.github.yidasanqian.utils;

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
     * @return 返回驼峰式属性，"gmtCreate"
     */
    public static String camelCase(String underline) {
        StringBuilder camelCaseProperty = new StringBuilder();
        String[] underlineProperty = underline.split("_");
        for (int i = 0; i < underlineProperty.length; i++) {
            if (i > 0) {
                Character initial = underlineProperty[i].charAt(0);
                String upperInitial = initial.toString().toUpperCase();
                String initialProperty = underlineProperty[i].replaceFirst(initial.toString(), upperInitial);
                camelCaseProperty.append(initialProperty);
            } else {
                camelCaseProperty.append(underlineProperty[i]);
            }
        }

        return camelCaseProperty.toString();
    }
}
