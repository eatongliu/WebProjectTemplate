package utils;

import org.junit.Test;

/**
 * Created by acer_liuyutong on 2017/3/20.
 */
public class SimpleTest {
    @Test
    public void test1(){
        String s = "abcdefg我是lyt";
        s.chars().forEach(System.out::println);
    }
}
