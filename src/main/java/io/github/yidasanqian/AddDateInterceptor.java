package io.github.yidasanqian;

import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Linyu Chen
 */
@Intercepts({@Signature(type = StatementHandler.class,
        method = "prepare", args = {Connection.class, Integer.class}),
        @Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class})
})
public class AddDateInterceptor implements Interceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String METHOD_PREPARE = "prepare";
    private static final String METHOD_SETPARAMETERS = "setParameters";

    private static String createDateColumnName;
    private static String updateDateColumnName;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (METHOD_PREPARE.equals(invocation.getMethod().getName())) {
            // pattern前面要加空格，避免IllegalArgumentException异常
            DateFormat dateFormat = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            final String currentDate = dateFormat.format(new Date());
            StatementHandler handler = (StatementHandler) PluginUtil.processTarget(invocation.getTarget());
            MetaObject metaObject = SystemMetaObject.forObject(handler);
            MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
            SqlCommandType sqlCommandType = ms.getSqlCommandType();
            String sql = metaObject.getValue("delegate.boundSql.sql").toString();
            logger.debug("intercept 原始sql : " + sql);
            if (SqlCommandType.INSERT == sqlCommandType) {
                Insert insert = (Insert) CCJSqlParserUtil.parse(sql);
                boolean isContainsCreateDateColumn = false, isContainsModifyDateColumn = false;
                int createDateColumnIndex = 0, modifyDateColumnIndex = 0;
                for (int i = 0; i < insert.getColumns().size(); i++) {
                    Column column = insert.getColumns().get(i);
                    if (column.getColumnName().equals(createDateColumnName)) {
                        // sql中包含了设置的列名，则只需要设置值
                        isContainsCreateDateColumn = true;
                        createDateColumnIndex = i;
                    }

                    if (column.getColumnName().equals(updateDateColumnName)) {
                        isContainsModifyDateColumn = true;
                        modifyDateColumnIndex = i;
                    }
                }

                if (isContainsCreateDateColumn) {
                    intoValueWithIndex(createDateColumnIndex, currentDate, insert);
                } else {
                    intoValue(createDateColumnName, currentDate, insert);
                }

                if (isContainsModifyDateColumn) {
                    intoValueWithIndex(modifyDateColumnIndex, currentDate, insert);
                } else {
                    intoValue(updateDateColumnName, currentDate, insert);
                }

                logger.debug("intercept 插入sql : " + insert.toString());
                metaObject.setValue("delegate.boundSql.sql", insert.toString());
            } else if (SqlCommandType.UPDATE == sqlCommandType) {
                Update update = (Update) CCJSqlParserUtil.parse(sql);
                boolean isContainsModifyDateColumn = false;
                int modifyDateColumnIndex = 0;
                for (int i = 0; i < update.getColumns().size(); i++) {
                    Column column = update.getColumns().get(i);
                    if (column.getColumnName().equals(updateDateColumnName)) {
                        isContainsModifyDateColumn = true;
                        modifyDateColumnIndex = i;
                    }
                }

                if (isContainsModifyDateColumn) {
                    updateValueWithIndex(modifyDateColumnIndex, currentDate, update);
                } else {
                    updateValue(updateDateColumnName, currentDate, update);
                }

                logger.debug("intercept 更新sql : " + update.toString());
                metaObject.setValue("delegate.boundSql.sql", update.toString());
            }
        } else if (METHOD_SETPARAMETERS.equals(invocation.getMethod().getName())) {
            // 解决原始sql语句已包含自动添加的列导致参数数量映射异常的问题
            ParameterHandler handler = (ParameterHandler) PluginUtil.processTarget(invocation.getTarget());
            MetaObject metaObject = SystemMetaObject.forObject(handler);
            MappedStatement ms = (MappedStatement) metaObject.getValue("mappedStatement");
            SqlCommandType sqlCommandType = ms.getSqlCommandType();
            if (SqlCommandType.INSERT == sqlCommandType || SqlCommandType.UPDATE == sqlCommandType) {
                BoundSql boundSql = (BoundSql) metaObject.getValue("boundSql");
                List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
                Iterator<ParameterMapping> it = parameterMappingList.iterator();
                String humpCreateDateProperty = StringUtil.underlineToHump(createDateColumnName);
                String humpUpdateDateProperty = StringUtil.underlineToHump(updateDateColumnName);
                while (it.hasNext()) {
                    ParameterMapping pm = it.next();
                    if (pm.getProperty().equals(humpCreateDateProperty)) {
                        logger.warn("原始Insert Sql语句已包含自动添加的列 : " + createDateColumnName);
                        it.remove();
                    }
                    if (pm.getProperty().equals(humpUpdateDateProperty)) {
                        logger.warn("原始Update Sql语句已包含自动添加的列 : " + updateDateColumnName);
                        it.remove();
                    }
                }
            }
        }

        return invocation.proceed();
    }

    private void updateValueWithIndex(int modifyDateColumnIndex, String currentDate, Update update) {
        update.getExpressions().set(modifyDateColumnIndex, new EscapeTimestampValue(currentDate));
    }

    private void updateValue(String updateDateColumnName, String currentDate, Update update) {
        // 添加列
        update.getColumns().add(new Column(updateDateColumnName));
        update.getExpressions().add(new EscapeTimestampValue(currentDate));
    }

    private void intoValueWithIndex(final int index, final String columnValue, Insert insert) {
        // 通过visitor设置对应的值
        insert.getItemsList().accept(new ItemsListVisitor() {
            @Override
            public void visit(SubSelect subSelect) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void visit(ExpressionList expressionList) {
                expressionList.getExpressions()
                        .set(index, new EscapeTimestampValue(columnValue));
            }

            @Override
            public void visit(MultiExpressionList multiExpressionList) {
                for (ExpressionList expressionList : multiExpressionList.getExprList()) {
                    expressionList.getExpressions()
                            .set(index, new EscapeTimestampValue(columnValue));
                }
            }
        });
    }

    private void intoValue(String columnName, final String columnValue, Insert insert) {
        // 添加列
        insert.getColumns().add(new Column(columnName));
        // 通过visitor设置对应的值
        insert.getItemsList().accept(new ItemsListVisitor() {
            @Override
            public void visit(SubSelect subSelect) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void visit(ExpressionList expressionList) {
                expressionList.getExpressions()
                        .add(new EscapeTimestampValue(columnValue));
            }

            @Override
            public void visit(MultiExpressionList multiExpressionList) {
                for (ExpressionList expressionList : multiExpressionList.getExprList()) {
                    expressionList.getExpressions()
                            .add(new EscapeTimestampValue(columnValue));
                }
            }
        });
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        createDateColumnName = properties.getProperty("createDateColumnName", "gmt_create");
        updateDateColumnName = properties.getProperty("updateDateColumnName", "gmt_modified");
    }
}
