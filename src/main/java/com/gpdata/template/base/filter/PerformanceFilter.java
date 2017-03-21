package com.gpdata.template.base.filter;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.gpdata.template.base.metrics.MetricRegistryInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * 
 * @author chengchaos
 *
 */
public class PerformanceFilter extends BaseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceFilter.class);
    
    private static final com.codahale.metrics.MetricRegistry MetricRegistry = MetricRegistryInstance.getInstance();
    
//    private static final ConsoleReporter reporter = ConsoleReporter.forRegistry(MetricRegistry).build();
//    private static final Timer requests = MetricRegistry.timer(name(PerformanceFilter.class, "request"));
    
    private static final Pattern SERVLET_PATH_PATTERN = Pattern.compile("^/api$");
    private static final Pattern REQUEST_URI_PATTERN = Pattern.compile("(api)/(\\d+)");
    private static final Pattern SERVLET_COUNTER_PATTERN = Pattern.compile("/(index)");
    
    private static final Counter apiInvokeCounter =
            MetricRegistry.counter(name(PerformanceFilter.class, "apiInvokeCounter"));
    
    @SuppressWarnings("unused")
    private static final void handleRequest(int sleep) {
//        Timer.Context context = requests.time();
//        try {
//            // some operator
//            Thread.sleep(sleep);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            context.stop();
//        }
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        chain.doFilter(request, response);
        
    }
    
    
    /**
     * zan shi xian bu wang ganglia shang xie le ..
     * 
     * 
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    private void perform(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        
        /*  例如: (/center/index)  */
        String servletPath = req.getServletPath();
        String requestUri = req.getRequestURI();
        
        LOGGER.debug("servletPath : {}", servletPath);
        LOGGER.debug("requestUri : {}", requestUri);
        
        //chain.doFilter(request, response);
        if (SERVLET_PATH_PATTERN.matcher(servletPath).find()) {
            Matcher matcher = REQUEST_URI_PATTERN.matcher(requestUri);
            
            /* 对 api 调用测试加一 */
            apiInvokeCounter.inc();
            
            if (matcher.find()) {
                LOGGER.debug("REQUEST_URI_PATTERN (api)/(\\d+) found !"); 
                Timer requests = MetricRegistry.timer(name("APIPerformanc", matcher.group(1), matcher.group(2)));
                Timer.Context context = requests.time();
                Counter apiInfokeCounter = MetricRegistry.counter(name("ApiInvokeCounter", "api"));
                apiInfokeCounter.inc();
                chain.doFilter(request, response);
                context.stop();
            } else {
                chain.doFilter(request, response);
            }
        } else {
            Matcher matcher = SERVLET_COUNTER_PATTERN.matcher(servletPath);
            LOGGER.debug("servletPath : {}", servletPath);
            LOGGER.debug("matcher : {}", matcher);
            if (matcher.find()) {
                LOGGER.debug("matcher.find : true !");
                Counter apiInfokeCounter = MetricRegistry.counter(name("pageVisitCounter", servletPath));
                apiInfokeCounter.inc();
            }
            chain.doFilter(request, response);
        }
        
        Counter servletPathCounter = MetricRegistry.counter(name(PerformanceFilter.class, servletPath));
        servletPathCounter.inc();
        
    }
    
}
