package com.hashedin.huspark.batch.config;

import com.hashedin.huspark.batch.dto.ProviderEarnings;
import com.hashedin.huspark.batch.processor.BookingToEarningsProcessor;
import com.hashedin.huspark.batch.writer.InvoiceWriter;
import com.hashedin.huspark.model.Booking;
import com.hashedin.huspark.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class MonthlyInvoiceJobConfig {

    private final BookingRepository bookingRepository;

    @Bean
    public Job monthlyInvoiceJob(JobRepository jobRepository,
                                 PlatformTransactionManager transactionManager,
                                 BookingToEarningsProcessor processor,
                                 InvoiceWriter writer) {

        Step step = new StepBuilder("generateMonthlyInvoices", jobRepository)
                .<Booking, ProviderEarnings>chunk(50, transactionManager)
                .reader(() -> {
                    LocalDateTime start = LocalDate.now().minusMonths(1).withDayOfMonth(1).atStartOfDay();
                    LocalDateTime end = start.plusMonths(1).minusSeconds(1);
                    var bookings = bookingRepository.findByStatusAndCreatedAtBetween(
                            "COMPLETED", start, end);
                    return bookings.iterator().hasNext() ? bookings.iterator().next() : null;
                })
                .processor(processor)
                .writer(writer)
                .build();

        return new JobBuilder("monthlyInvoiceJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }
}
