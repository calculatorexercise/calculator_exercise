package digital.metro.pricing.calculator.repository;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A dummy implementation for testing purposes. In production, we would get real prices from a database.
 */
@Repository
public class PriceRepositoryImpl implements PriceRepository {
    public static final String CUSTOMER_1_ID = "customer-1";
    public static final String CUSTOMER_2_ID = "customer-2";
    private static final Map<String, BigDecimal> PRICE_MULTIPLIERS_BY_CUSTOMER_IDS = Map.of(
            CUSTOMER_1_ID, toMultiplier("0.90"),
            CUSTOMER_2_ID, toMultiplier("0.85")
    );

    private final Map<String, BigDecimal> pricesByArticleIds = new ConcurrentHashMap<>();
    private final Random random = new Random();

    private static BigDecimal toMultiplier(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }

    public Optional<BigDecimal> getPriceByArticleId(String articleId) {
        return Optional.of(
                pricesByArticleIds.computeIfAbsent(
                        articleId,
                        key -> BigDecimal.valueOf(0.5d + random.nextDouble() * 29.50d).setScale(2, RoundingMode.HALF_UP)
                )
        );
    }

    public Optional<BigDecimal> getPriceByArticleIdAndCustomerId(String articleId, String customerId) {
        final BigDecimal multiplier = PRICE_MULTIPLIERS_BY_CUSTOMER_IDS.get(customerId);

        return multiplier == null
                ? Optional.empty()
                : getPriceByArticleId(articleId)
                .map(p -> p.multiply(multiplier)
                        .setScale(2, RoundingMode.HALF_UP));

    }
}
