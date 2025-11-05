package com.hashedin.huspark.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderEarnings {
    private Long providerId;
    private String providerEmail;
    private Long totalBookings;
    private BigDecimal totalEarnings;
}
