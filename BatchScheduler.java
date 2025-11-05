package com.hashedin.huspark.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job monthlyInvoiceJob;

    // Run at 1 AM on the 1st of every month
    @Scheduled(cron = "0 0 1 1 * *")
    public void runMonthlyInvoiceJob() {
        try {
            log.info("🕐 Starting Monthly Invoice Batch Job...");
            jobLauncher.run(monthlyInvoiceJob,
                    new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters());
            log.info("✅ Monthly Invoice Batch Job completed successfully!");
        } catch (Exception e) {
            log.error("❌ Failed to execute Monthly Invoice Job", e);
        }
    }
}
