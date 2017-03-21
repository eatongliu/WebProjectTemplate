package com.gpdata.template.base.filter;

import com.gpdata.template.base.core.TicketTokenHandler;
import com.gpdata.template.user.entity.User;
import com.gpdata.template.utils.ConfigUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

/**
 * 记录用户操作的过滤器
 * 
 */
public class UserTraceFilter extends BaseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTraceFilter.class);
    private static final  Boolean SAVE_TO_OSS = Boolean.valueOf(ConfigUtil.getConfig("log.save.oss").trim());
    
    /*
     * 一个不可变的 持有要过滤的前缀的正则表达式的 Set
     */
    private final List<Pattern> excludePrefixPattern;
    
    /*
     * cookie 的名称
     */
    private String webIdName = "_gp_rdsid";
    
	
	/*
	 * 过期时间， 一年 = 31_536_000
	 */
	private int expireTimeSecond = 60 * 60 * 24 * 365;
	
    /*
     * 用户，Cookie，票据加密的处理工具
     */
    private TicketTokenHandler ticketTokenHandler;
	
//	/*
//	 * 记录日志的 Service
//	 */
//    private TradeLogService tradeLogService;
	
    @Resource
    public void setTicketTokenHandler(TicketTokenHandler ticketTokenHandler) {
        this.ticketTokenHandler = ticketTokenHandler;
    }

//    @Resource
//    public void setTradeLogService(TradeLogService tradeLogService) {
//        this.tradeLogService = tradeLogService;
//    }
    

    public void setWebIdName(String webIdName) {
        this.webIdName = webIdName;
    }

    
	
	public void setExpireTimeSecond(int expireTimeSecond) {
        this.expireTimeSecond = expireTimeSecond;
    }








    /**
     * 构造函数 参数是一个<strong>不</strong>需要过滤的路径的正则表达式，例如 {@code ^/test/ }
     * 
     * @param excludePrefix
     */
    public UserTraceFilter(Set<String> excludePrefix) {

        super();
        if (excludePrefix.isEmpty()) {
            this.excludePrefixPattern = Arrays.asList();
        } else {
            List<Pattern> temp = excludePrefix.stream()
                    .filter(StringUtils::isNotBlank)
                    .sorted(Comparator.comparing(String::length))
                    .map(Pattern::compile)
                    .collect(toList());

            this.excludePrefixPattern = Collections.unmodifiableList(temp);
        }

    }
    
    /**
     * 检查是否是不需要过滤的地址
     * 
     * @param servletPath
     * @return true: 不需要过滤； false: 需要过滤
     */
    private boolean isExcludePath(String servletPath) {
        
//        Matcher matcher = null;
//        
//        for (Pattern pattern : this.excludePrefixPattern) {
//            matcher = pattern.matcher(servletPath);
//            if (matcher.find()) {
//                return true;
//            }
//        }
        
        return this.excludePrefixPattern
                .parallelStream()
                .map(pattern -> pattern.matcher(servletPath))
                .filter(matcher -> matcher.find())
                .findAny()
                .isPresent();
    }
    
    /**
     * 对新用户生成一个随机字符串。
     * 此方法首先获取这个串，　如果没有则生成这个串。
     * @param request
     * @param response
     * @return
     */
    private String getWebId(HttpServletRequest request, HttpServletResponse response) {
       
        Cookie[] cookies = request.getCookies();
        
        String result = null;

        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (this.webIdName.equals(cookie.getName())) {
                    result = cookie.getValue();
                    break;
                }
            }
        }

        if (result == null) {
            result = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(this.webIdName, result);
            cookie.setMaxAge(this.expireTimeSecond);
            cookie.setPath("/");
            cookie.setMaxAge(31536000);
            response.addCookie(cookie);
        }
        
        return result;
        
    }
    
    private void recordingLog(HttpServletRequest request, HttpServletResponse response, String webId) {
        
        Long userId = Long.valueOf(0L);
        Optional<User> userBaseinfoOptional = ticketTokenHandler.getCurrentUser(request);
        if (userBaseinfoOptional.isPresent()) {
            User userBaseinfo = userBaseinfoOptional.get();
            userId = userBaseinfo.getUserId();
        }
        
        Enumeration<String> headerNames = request.getHeaderNames();
        
        StringBuilder requestHeader = new StringBuilder();
        String name = null;
        while (headerNames.hasMoreElements()) {
            name = headerNames.nextElement();
            requestHeader.append(name);
            requestHeader.append(" : ");
            requestHeader.append(request.getHeader(name));
            requestHeader.append("||||");
            //requestHeader.append("\n");
        }
        if (requestHeader.length() > 3) {
            requestHeader.delete(requestHeader.length() - 4, requestHeader.length());
        }
        
        /*TODO  用户日志记录*/
//        LogUserTraceWithBLOBs logUserTraceWithBLOBs = new LogUserTraceWithBLOBs();
//        logUserTraceWithBLOBs.setOt(new Date());
//        logUserTraceWithBLOBs.setRequestHeader(requestHeader.toString());
//        logUserTraceWithBLOBs.setRequestMethod(request.getMethod());
//        logUserTraceWithBLOBs.setRequestQuery(request.getQueryString());
//        logUserTraceWithBLOBs.setRequestUrl(request.getRequestURL().toString());
//        logUserTraceWithBLOBs.setUserId(userId);
//        logUserTraceWithBLOBs.setUserIp(request.getRemoteAddr());
//        logUserTraceWithBLOBs.setWebId(webId);
//
//        String requestReferrer = request.getHeader("Referer");
//        logUserTraceWithBLOBs.setRequestReferrer(requestReferrer);
        
        
        
        /*
         * 此处时异步保存，在 Service 中实现
         */
        /* this.tradeLogService.saveAsyncTradeLog(logUserTraceWithBLOBs); */
        
        /* 正式上线后, 不记录日志到数据库, 而是写到文件中. 
         * 后台定时将日志上传的 OSS 中
         */
        
//        if (SAVE_TO_OSS.booleanValue() == true) {
//            this.tradeLogService.saveUserTraceLogToOSS(logUserTraceWithBLOBs);
//        } else {
//            LOGGER.info("{} - {}", logUserTraceWithBLOBs.getUserIp(), logUserTraceWithBLOBs.toStringForLog());
//        }
            
    }

    /**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		
	    

	    HttpServletRequest request = (HttpServletRequest) req;
	    HttpServletResponse response = (HttpServletResponse) resp;
	    
	    String servletPath = request.getServletPath();
	    
	    if (!this.isExcludePath(servletPath)) {
	        //LOGGER.debug("servletPath: {}", servletPath);
	        final String webId = this.getWebId(request, response);
	        this.recordingLog(request, response, webId);
	    }
	    
	    //Access-Control-Allow-Origin:*
	    //Access-Control-Allow-Headers:Origin, X-Requested-With, Content-Type, Accept
	    //response.addHeader("Access-Control-Allow-Origin", "*");
	    //response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
	    //response.addHeader("Access-Control-Allow-Method", "GET, POST");

	    chain.doFilter(req, resp);
	}


}
