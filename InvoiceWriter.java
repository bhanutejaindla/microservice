package com.hashedin.huspark.batch.writer;

import com.hashedin.huspark.batch.dto.ProviderEarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class InvoiceWriter implements ItemWriter<ProviderEarnings> {

    @Override
    public void write(Chunk<? extends ProviderEarnings> chunk) throws IOException {
        String fileName = "invoices_" + LocalDate.now() + ".csv";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("Provider ID, Provider Email, Total Bookings, Total Earnings\n");
            for (ProviderEarnings e : chunk.getItems()) {
                writer.write(String.format("%d,%s,%d,%.2f\n",
                        e.getProviderId(),
                        e.getProviderEmail(),
                        e.getTotalBookings(),
                        e.getTotalEarnings()));
            }
        }
        log.info("✅ Monthly invoice CSV generated successfully: {}", fileName);
    }
}
