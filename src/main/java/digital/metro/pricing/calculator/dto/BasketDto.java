package digital.metro.pricing.calculator.dto;

import org.springframework.lang.Nullable;

import java.util.Set;

public class BasketDto {

    @Nullable
    private final String customerId;
    private final Set<BasketIEntryDto> entries;

    public BasketDto(String customerId, Set<BasketIEntryDto> entries) {
        this.customerId = customerId;
        this.entries = entries;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Set<BasketIEntryDto> getEntries() {
        return entries;
    }
}
