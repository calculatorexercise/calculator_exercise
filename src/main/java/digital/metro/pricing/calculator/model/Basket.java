package digital.metro.pricing.calculator.model;

import org.springframework.lang.Nullable;

import java.util.Set;

public class Basket {

    @Nullable
    private final String customerId;
    private final Set<BasketEntry> entries;

    public Basket(String customerId, Set<BasketEntry> entries) {
        this.customerId = customerId;
        this.entries = entries;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Set<BasketEntry> getEntries() {
        return entries;
    }
}
