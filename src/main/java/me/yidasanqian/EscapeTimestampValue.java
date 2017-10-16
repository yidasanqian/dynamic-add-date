package me.yidasanqian;

import net.sf.jsqlparser.expression.TimestampValue;

/**
 * 解决与alibaba druid整合时出现的异常：
 * <p>com.alibaba.druid.sql.parser.ParserException: ERROR. token : LBRACE
 *
 * @author Linyu Chen
 */
public class EscapeTimestampValue extends TimestampValue {

    private String value;

    public EscapeTimestampValue(String value) {
        super(value);
        this.value = value;
    }

    @Override
    public String toString() {
        return "'" + value + "'";
    }
}
