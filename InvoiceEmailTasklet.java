package com.hashedin.huspark.batch.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InvoiceEmailTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("📧 Simulating sending monthly invoices to all providers...");
        log.info("✅ Invoice emails sent successfully (simulated).");
        return RepeatStatus.FINISHED;
    }
}
