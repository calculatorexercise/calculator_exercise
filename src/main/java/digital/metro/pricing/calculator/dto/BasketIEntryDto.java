package digital.metro.pricing.calculator.dto;

import java.math.BigDecimal;

public class BasketIEntryDto {

    private final String articleId;
    private final BigDecimal quantity;

    public BasketIEntryDto(String articleId, BigDecimal quantity) {
        this.articleId = articleId;
        this.quantity = quantity;
    }

    public String getArticleId() {
        return articleId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
}
