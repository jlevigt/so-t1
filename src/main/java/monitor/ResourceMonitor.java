package monitor;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import com.sun.management.OperatingSystemMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ResourceMonitor {
    private final ThreadMXBean threadBean;
    private final OperatingSystemMXBean osBean;
    private final ScheduledExecutorService scheduler;
    
    private volatile ResourceStats latestStats;

    public ResourceMonitor() {
        this.threadBean = ManagementFactory.getThreadMXBean();
        this.osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.latestStats = ResourceStats.empty();
        
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ResourceMonitor-Collector");
            t.setDaemon(true);
            return t;
        });
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::collectMetrics, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private void collectMetrics() {
        double cpu = osBean.getProcessCpuLoad() * 100.0;
        if (cpu < 0) cpu = 0.0; 

        ThreadInfo[] threadInfos = threadBean.dumpAllThreads(false, false);
        int total = threadInfos.length;
        int runnable = 0, waiting = 0, blocked = 0;

        for (ThreadInfo info : threadInfos) {
            if (info == null) continue;
            switch (info.getThreadState()) {
                case RUNNABLE -> runnable++;
                case WAITING, TIMED_WAITING -> waiting++;
                case BLOCKED -> blocked++;
                default -> {}
            }
        }

        this.latestStats = new ResourceStats(cpu, total, runnable, waiting, blocked);
    }

    public ResourceStats getLatestStats() {
        return latestStats;
    }
}
