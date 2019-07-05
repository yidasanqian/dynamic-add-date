package io.github.yidasanqian.dynamicadddate;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class IgnoreTableTest {

    @Test
    public void testMatchesIgnoreTable() {
        String tableName = "userAuthc";
        String ignore = "^user.*, ^permission.*";
        List<String> ignoreTableList = Arrays.asList(ignore.split(","));
        for (String ignoreTable : ignoreTableList) {
            if (tableName.matches(ignoreTable)) {
                System.out.println("IgnoreTableTest.testMatchesIgnoreTable 匹配");
            }
        }
    }

    @Test
    public void testMatchesIgnoreTable2() {
        String tableName = "user";
        String ignore = "^user.*, ^permission.*";
        List<String> ignoreTableList = Arrays.asList(ignore.split(","));
        for (String ignoreTable : ignoreTableList) {
            if (tableName.matches(ignoreTable)) {
                System.out.println("IgnoreTableTest.testMatchesIgnoreTable2 匹配");
            }
        }
    }

    @Test
    public void testMatchesIgnoreTable3() {
        String tableName = "user";
        String ignore = "^user.*, ^permission.*, ";
        List<String> ignoreTableList = Arrays.asList(ignore.split(","));
        for (String ignoreTable : ignoreTableList) {
            if (tableName.matches(ignoreTable)) {
                System.out.println("IgnoreTableTest.testMatchesIgnoreTable3 匹配");
            }
        }
    }

    @Test
    public void testMatchesIgnoreTable4() {
        String tableName = "user";
        String ignore = "^user.*";
        List<String> ignoreTableList = Arrays.asList(ignore.split(","));
        for (String ignoreTable : ignoreTableList) {
            if (tableName.matches(ignoreTable)) {
                System.out.println("IgnoreTableTest.testMatchesIgnoreTable4 匹配");
            }
        }
    }

    @Test
    public void testMatchesIgnoreTable5() {
        String tableName = "user";
        String ignore = "";
        List<String> ignoreTableList = Arrays.asList(ignore.split(","));
        for (String ignoreTable : ignoreTableList) {
            if (tableName.matches(ignoreTable)) {
                System.out.println("IgnoreTableTest.testMatchesIgnoreTable5 匹配");
            }
        }
    }

    @Test
    public void testMatchesIgnoreTable6() {
        String tableName = "user";
        String ignore = "user,,permission";
        List<String> ignoreTableList = Arrays.asList(ignore.split(","));
        for (String ignoreTable : ignoreTableList) {
            if (tableName.matches(ignoreTable)) {
                System.out.println("IgnoreTableTest.testMatchesIgnoreTable6 匹配");
            }
        }
    }

    @Test
    public void testMatchesIgnoreTable7() {
        String tableName = "user_role";
        String ignore = "user.*,, permission";
        List<String> ignoreTableList = Arrays.asList(ignore.split(","));
        for (String ignoreTable : ignoreTableList) {
            if (tableName.matches(ignoreTable)) {
                System.out.println("IgnoreTableTest.testMatchesIgnoreTable7 匹配");
            }
        }
    }
}
