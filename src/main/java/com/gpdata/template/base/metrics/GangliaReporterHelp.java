package com.gpdata.template.base.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;


/**
 * 
 * @author chengchaos
 *
 */
public class GangliaReporterHelp {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GangliaReporterHelp.class);

    private String ipAddress = null; //"192.168.1.147";
    private int ipPort = 0; // 8647;
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public void setIpPort(int ipPort) {
        this.ipPort = ipPort;
    }

    public GangliaReporterHelp() {
        super();
    }
    
    public GangliaReporterHelp(String ipAddress, int ipPort) {
        super();
        this.ipAddress = ipAddress;
        this.ipPort = ipPort;
    }
    
    @PostConstruct
    public void init() {
        
//        LOGGER.debug("GangliaReporterHelper init on post construct ...");
//        Assert.isTrue(StringUtils.isNotBlank(ipAddress));
//        Assert.isTrue(ipPort != 0);
//
//        LOGGER.debug("Ganglia Report ip address : {}", ipAddress);
//        LOGGER.debug("Ganglia Report ip port : {}", ipPort);
//        /* "192.168.1.147", 8647 */
//        GMetric ganglia;
//        try {
//            ganglia = new GMetric(ipAddress, ipPort, GMetric.UDPAddressingMode.UNICAST, 1);
//            final GangliaReporter gangliaReporter = GangliaReporter
//                    .forRegistry(MetricRegistryInstance.getInstance())
//                    .convertRatesTo(TimeUnit.SECONDS)
//                    .convertDurationsTo(TimeUnit.MILLISECONDS)
//                    .build(ganglia);
//            LOGGER.debug("Ganglia Reporter polling starting after the 3 seconds. ");
//            gangliaReporter.start(3, TimeUnit.SECONDS);
//            LOGGER.debug("Ganglia Report started !");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        
    }
    
    
}
