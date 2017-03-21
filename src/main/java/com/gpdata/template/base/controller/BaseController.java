package com.gpdata.template.base.controller;

import com.gpdata.template.user.entity.User;
import com.gpdata.template.utils.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class BaseController implements ServletContextAware {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


     protected ServletContext servletContext;

     /**
     * 实现 ServletContextAware 接口后, Spring 容器会注入一个 ServletContext 实例，我试试
     *
     * @param servletContext
     * @see ServletContextAware#setServletContext(ServletContext)
     */
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * @param name
     * @param value
     * @return
     */
    protected Pair<String, Object> pair(String name, Object value) {
        return Pair.of(name, value);
    }


    /**
     * 不是空值 (null) 也不是空串
     *
     * @param input
     * @return
     */
    protected final boolean hasNonNullAndNonEmptyValue(Object input) {

        if (input == null) {
            return false;
        }

        if (input instanceof CharSequence) {
            return StringUtils.isNotBlank((CharSequence) input);
        }

        return true;
    }

    /**
     * 用于过滤出 pair 中的 value 为非空的 pair
     *
     * @param pair
     * @return
     */
    protected final boolean hasNonNullAndNonEmptyValue(Pair<String, Object> pair) {
        return hasNonNullAndNonEmptyValue(pair.getRight());
    }

    /**
     * @param pairs
     * @return
     */
    protected Map<String, Object> createParametersMap(Pair<String, Object>... pairs) {

        final Map<String, Object> params = Stream.of(pairs)
                .filter(this::hasNonNullAndNonEmptyValue)
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        return params;
    }
    /**
     * 判断是否是 XMLHttpRequest 请求。 已知在浏览器中调用 XMLHttpRequest。 会产生
     * {@code X-Requested-With: XMLHttpRequest} 的头信息
     *
     * @param request
     * @return
     */
    protected final boolean isXMLHttpRequest(HttpServletRequest request) {
        // X-Requested-With: XMLHttpRequest
        String xRequestedWith = request.getHeader("X-Requested-With");

        return (xRequestedWith != null);

    }


    /**
     * 获取项目的部署路径.
     *
     * @return
     */
    public String getRootPath() {

        return this.servletContext.getRealPath("/");
    }


    /**
     * 获取项目的上传文件部署路径.
     *
     * @return
     */
    protected final String getFileRootPath() {
        return ConfigUtil.getConfig("filepath");
    }


    public final User getCurrentUser(HttpServletRequest request) {

        User currentUser = (User) request.getAttribute("currentUser");

        if (currentUser == null) {
            throw new RuntimeException("用户不存在!");
        }

        return currentUser;
    }
}
