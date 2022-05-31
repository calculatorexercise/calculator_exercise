package digital.metro.pricing.calculator.repository;

import java.math.BigDecimal;
import java.util.Optional;

public interface PriceRepository {
    Optional<BigDecimal> getPriceByArticleId(String articleId);
    Optional<BigDecimal> getPriceByArticleIdAndCustomerId(String articleId, String customerId);
}
