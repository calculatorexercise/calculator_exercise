package digital.metro.pricing.calculator.dto;

import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

public class BasketCalculationResultDto {

    @Nullable
    private final String customerId;
    private final Map<String, BigDecimal> pricedBasketEntries;
    private final BigDecimal totalAmount;

    public BasketCalculationResultDto(String customerId, Map<String, BigDecimal> pricedBasketEntries, BigDecimal totalAmount) {
        this.customerId = customerId;
        this.pricedBasketEntries = Collections.unmodifiableMap(pricedBasketEntries);
        this.totalAmount = totalAmount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Map<String, BigDecimal> getPricedBasketEntries() {
        return pricedBasketEntries;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
