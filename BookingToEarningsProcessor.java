package com.hashedin.huspark.batch.processor;

import com.hashedin.huspark.batch.dto.ProviderEarnings;
import com.hashedin.huspark.model.Booking;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class BookingToEarningsProcessor implements ItemProcessor<Booking, ProviderEarnings> {

    private final Map<Long, ProviderEarnings> providerSummary = new HashMap<>();

    @Override
    public ProviderEarnings process(Booking booking) {
        Long providerId = booking.getProvider().getId();
        String providerEmail = booking.getProvider().getUser().getEmail();
        BigDecimal amount = booking.getAmount() != null ? booking.getAmount() : BigDecimal.ZERO;

        ProviderEarnings earnings = providerSummary.getOrDefault(providerId,
                ProviderEarnings.builder()
                        .providerId(providerId)
                        .providerEmail(providerEmail)
                        .totalBookings(0L)
                        .totalEarnings(BigDecimal.ZERO)
                        .build());

        earnings.setTotalBookings(earnings.getTotalBookings() + 1);
        earnings.setTotalEarnings(earnings.getTotalEarnings().add(amount));

        providerSummary.put(providerId, earnings);

        // Return only the final summary for each provider at the end
        return earnings;
    }
}
