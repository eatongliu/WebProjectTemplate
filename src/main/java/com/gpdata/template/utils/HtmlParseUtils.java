package com.gpdata.template.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.web.util.HtmlUtils;

/**
 * 将带有非法注入的字符转义
 */
public class HtmlParseUtils {
    /**
     * 将带有非法注入的字符转义
     */
    public static <T> T htmlParseCode(T t, Class<T> clazz) {
        String text = HtmlUtils.htmlUnescape(JSON.toJSONString(t));
        return JSON.parseObject(text, clazz);
    }
}
