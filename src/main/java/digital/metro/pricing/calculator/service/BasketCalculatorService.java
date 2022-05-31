package digital.metro.pricing.calculator.service;

import digital.metro.pricing.calculator.model.Basket;
import digital.metro.pricing.calculator.model.BasketCalculationResult;
import digital.metro.pricing.calculator.model.BasketEntry;
import digital.metro.pricing.calculator.repository.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BasketCalculatorService {

    private final PriceRepository priceRepository;

    @Autowired
    public BasketCalculatorService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public BasketCalculationResult calculateBasket(Basket basket) {
        Map<String, BigDecimal> pricedArticles = basket.getEntries().stream()
                .collect(Collectors.toMap(
                        BasketEntry::getArticleId,
                        entry -> calculateArticle(entry, basket.getCustomerId()),
                        BigDecimal::add));

        BigDecimal totalAmount = pricedArticles.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BasketCalculationResult(basket.getCustomerId(), pricedArticles, totalAmount);
    }

    public BigDecimal calculateArticle(BasketEntry basketEntry, String customerId) {
        final String articleId = basketEntry.getArticleId();

        Optional<BigDecimal> articlePrice = customerId == null
                ? priceRepository.getPriceByArticleId(articleId)
                : priceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId)
                .or(() -> priceRepository.getPriceByArticleId(articleId));

        return articlePrice.orElseThrow(() -> new RuntimeException(
                        String.format("Price not found for article %s and customer %s", articleId, customerId)))
                .multiply(basketEntry.getQuantity());
    }
}
