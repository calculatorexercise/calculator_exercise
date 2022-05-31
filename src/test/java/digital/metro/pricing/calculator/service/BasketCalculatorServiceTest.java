package digital.metro.pricing.calculator.service;

import digital.metro.pricing.calculator.model.Basket;
import digital.metro.pricing.calculator.model.BasketCalculationResult;
import digital.metro.pricing.calculator.model.BasketEntry;
import digital.metro.pricing.calculator.repository.PriceRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BasketCalculatorServiceTest {

    @Mock
    private PriceRepositoryImpl mockPriceRepository;

    private BasketCalculatorService service;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        service = new BasketCalculatorService(mockPriceRepository);
    }

    @Test
    public void testCalculateArticle() {
        // GIVEN
        String articleId = "article-1";
        BigDecimal price = new BigDecimal("34.29");
        when(mockPriceRepository.getPriceByArticleId(articleId)).thenReturn(Optional.of(price));

        // WHEN
        BigDecimal result = service.calculateArticle(new BasketEntry(articleId, BigDecimal.ONE), null);

        // THEN
        assertThat(result).isEqualByComparingTo(price);
    }

    @Test
    public void testCalculateArticleForCustomer() {
        // GIVEN
        String articleId = "article-1";
        BigDecimal standardPrice = new BigDecimal("34.29");
        BigDecimal customerPrice = new BigDecimal("29.99");
        String customerId = "customer-1";

        when(mockPriceRepository.getPriceByArticleId(articleId)).thenReturn(Optional.of(standardPrice));
        when(mockPriceRepository.getPriceByArticleIdAndCustomerId(articleId, customerId)).thenReturn(Optional.of(customerPrice));

        // WHEN
        BigDecimal result = service.calculateArticle(new BasketEntry(articleId, BigDecimal.ONE), "customer-1");

        // THEN
        assertThat(result).isEqualByComparingTo(customerPrice);
    }

    @Test
    public void testCalculateBasket() {
        // GIVEN
        Basket basket = new Basket("customer-1", Set.of(
                new BasketEntry("article-1", BigDecimal.ONE),
                new BasketEntry("article-2", BigDecimal.ONE),
                new BasketEntry("article-3", BigDecimal.ONE)));

        Map<String, BigDecimal> prices = Map.of(
                "article-1", new BigDecimal("1.50"),
                "article-2", new BigDecimal("0.29"),
                "article-3", new BigDecimal("9.99"));

        when(mockPriceRepository.getPriceByArticleIdAndCustomerId("article-1", "customer-1"))
                .thenReturn(Optional.of(prices.get("article-1")));
        when(mockPriceRepository.getPriceByArticleIdAndCustomerId("article-2", "customer-1"))
                .thenReturn(Optional.of(prices.get("article-2")));
        when(mockPriceRepository.getPriceByArticleIdAndCustomerId("article-3", "customer-1"))
                .thenReturn(Optional.of(prices.get("article-3")));

        // WHEN
        BasketCalculationResult result = service.calculateBasket(basket);

        // THEN
        assertThat(result.getCustomerId()).isEqualTo("customer-1");
        assertThat(result.getPricedBasketEntries()).isEqualTo(prices);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("11.78"));
    }

    @Test
    public void testCalculateBasketNoCustomer() {
        // GIVEN
        Basket basket = new Basket(null, Set.of(
                new BasketEntry("article-1", BigDecimal.ONE),
                new BasketEntry("article-2", BigDecimal.ONE),
                new BasketEntry("article-3", BigDecimal.ONE)));

        Map<String, BigDecimal> prices = Map.of(
                "article-1", new BigDecimal("1.50"),
                "article-2", new BigDecimal("0.29"),
                "article-3", new BigDecimal("9.99"));

        when(mockPriceRepository.getPriceByArticleId("article-1")).thenReturn(Optional.of(prices.get("article-1")));
        when(mockPriceRepository.getPriceByArticleId("article-2")).thenReturn(Optional.of(prices.get("article-2")));
        when(mockPriceRepository.getPriceByArticleId("article-3")).thenReturn(Optional.of(prices.get("article-3")));

        // WHEN
        BasketCalculationResult result = service.calculateBasket(basket);

        // THEN
        assertThat(result.getCustomerId()).isNull();
        assertThat(result.getPricedBasketEntries()).isEqualTo(prices);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("11.78"));
    }

    @Test
    public void testCalculateBasketUnknownCustomer() {
        // GIVEN
        Basket basket = new Basket("unknownCustomerIdForArticle-1", Set.of(
                new BasketEntry("article-1", BigDecimal.TEN),
                new BasketEntry("article-2", BigDecimal.TEN)));

        Map<String, BigDecimal> prices = Map.of(
                "article-1", new BigDecimal("9.99"),
                "article-2", new BigDecimal("69.69"));

        when(mockPriceRepository.getPriceByArticleIdAndCustomerId("article-1", "unknownCustomerIdForArticle-1"))
                .thenReturn(Optional.empty());
        when(mockPriceRepository.getPriceByArticleId("article-1"))
                .thenReturn(Optional.of(prices.get("article-1")));
        when(mockPriceRepository.getPriceByArticleIdAndCustomerId("article-2", "unknownCustomerIdForArticle-1"))
                .thenReturn(Optional.of(prices.get("article-2")));

        // WHEN
        BasketCalculationResult result = service.calculateBasket(basket);

        // THEN
        assertThat(result.getCustomerId()).isEqualTo("unknownCustomerIdForArticle-1");
        assertThat(result.getPricedBasketEntries().size()).isEqualTo(2);
        assertThat(result.getPricedBasketEntries().get("article-1")).isEqualByComparingTo(new BigDecimal("99.90"));
        assertThat(result.getPricedBasketEntries().get("article-2")).isEqualByComparingTo(new BigDecimal("696.90"));

        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("796.80"));
    }

    @Test
    public void testCalculateBasketUnknownArticleId() {
        // GIVEN
        Basket basket = new Basket("customer-1", Set.of(
                new BasketEntry("article-1", BigDecimal.TEN)));

        when(mockPriceRepository.getPriceByArticleIdAndCustomerId("article-1", "unknownCustomerIdForArticle-1"))
                .thenReturn(Optional.empty());
        when(mockPriceRepository.getPriceByArticleId("article-1"))
                .thenReturn(Optional.empty());

        // WHEN
        try {
            service.calculateBasket(basket);
            fail("expected RuntimeException due to not being able to find price of article");
        } catch (RuntimeException e) {
            // THEN
            assertThat(e.getMessage()).contains("Price not found for article article-1 and customer customer-1");
        }
    }

    @Test
    public void testCalculateBasketDuplicateItems() {
        // GIVEN
        Basket basket = new Basket("customer-1", Set.of(
                new BasketEntry("article-1", BigDecimal.ONE),
                new BasketEntry("article-1", BigDecimal.ONE),
                new BasketEntry("article-3", BigDecimal.ONE)));

        when(mockPriceRepository.getPriceByArticleId("article-1")).thenReturn(Optional.of(new BigDecimal("1.50")));
        when(mockPriceRepository.getPriceByArticleId("article-3")).thenReturn(Optional.of(new BigDecimal("0.29")));

        // WHEN
        BasketCalculationResult result = service.calculateBasket(basket);

        // THEN
        assertThat(result.getCustomerId()).isEqualTo("customer-1");
        assertThat(result.getPricedBasketEntries().size()).isEqualTo(2);
        assertThat(result.getPricedBasketEntries().get("article-1")).isEqualByComparingTo(new BigDecimal("3.00"));
        assertThat(result.getPricedBasketEntries().get("article-3")).isEqualByComparingTo(new BigDecimal("0.29"));
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("3.29"));
    }

    @Test
    public void testCalculateBasketMultipleItems() {
        // GIVEN
        Basket basket = new Basket("customer-1", Set.of(
                new BasketEntry("article-1", BigDecimal.ONE),
                new BasketEntry("article-2", BigDecimal.TEN)));

        when(mockPriceRepository.getPriceByArticleId("article-1")).thenReturn(Optional.of(new BigDecimal("1.50")));
        when(mockPriceRepository.getPriceByArticleId("article-2")).thenReturn(Optional.of(new BigDecimal("9.99")));

        // WHEN
        BasketCalculationResult result = service.calculateBasket(basket);

        // THEN
        assertThat(result.getCustomerId()).isEqualTo("customer-1");
        assertThat(result.getPricedBasketEntries().size()).isEqualTo(2);
        assertThat(result.getPricedBasketEntries().get("article-1")).isEqualByComparingTo(new BigDecimal("1.50"));
        assertThat(result.getPricedBasketEntries().get("article-2")).isEqualByComparingTo(new BigDecimal("99.90"));
        assertThat(result.getTotalAmount()).isEqualByComparingTo(new BigDecimal("101.40"));
    }

}
