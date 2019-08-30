package io.github.yidasanqian.dynamicadddate.utils;

/**
 * @author yidasanqian@gmail.com
 */
public class StringUtil {

    private StringUtil() {
    }

    /**
     * 将蛇形格式转换成驼峰式
     *
     * @param snake 蛇形字符串，形如“gmt_create”
     * @return 返回驼峰式属性，"gmtCreate"
     */
    public static String camelCase(String snake) {
        StringBuilder camelCaseProperty = new StringBuilder();
        String[] snakeProperty = snake.split("_");
        for (int i = 0; i < snakeProperty.length; i++) {
            if (i > 0) {
                Character initial = snakeProperty[i].charAt(0);
                String upperInitial = initial.toString().toUpperCase();
                String initialProperty = snakeProperty[i].replaceFirst(initial.toString(), upperInitial);
                camelCaseProperty.append(initialProperty);
            } else {
                camelCaseProperty.append(snakeProperty[i]);
            }
        }

        return camelCaseProperty.toString();
    }
}
