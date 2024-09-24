package org.xlm.jmsstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;

import javax.management.MBeanServer;
import java.io.ObjectInputStream;
import java.lang.management.*;
import java.util.List;

@SpringBootApplication
public class JmsStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmsStudyApplication.class, args);


        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();

        long used = heapMemoryUsage.getUsed();
        System.out.println("heap memory used = " + (used/1024/1024/1024));
        long init = heapMemoryUsage.getInit();
        System.out.println("heap memory init = " + init);

        com.sun.management.OperatingSystemMXBean osBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = osBean.getCpuLoad();
        System.out.println("cpuLoad = " + cpuLoad);
        String arch = osBean.getArch();
        System.out.println("arch = " + arch);
        int availableProcessors = osBean.getAvailableProcessors();
        System.out.println("availableProcessors = " + availableProcessors);
        long totalMemorySize = osBean.getTotalMemorySize();
        System.out.println("totalMemorySize = " + (totalMemorySize/1024/1024/1024));
        long freeMemorySize = osBean.getFreeMemorySize();
        System.out.println("freeMemorySize = " + (freeMemorySize/1024/1024/1024));

    }

}
