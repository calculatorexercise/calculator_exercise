package digital.metro.pricing.calculator.dto.mapper;

import digital.metro.pricing.calculator.dto.BasketCalculationResultDto;
import digital.metro.pricing.calculator.dto.BasketIEntryDto;
import digital.metro.pricing.calculator.dto.BasketDto;
import digital.metro.pricing.calculator.model.Basket;
import digital.metro.pricing.calculator.model.BasketCalculationResult;
import digital.metro.pricing.calculator.model.BasketEntry;

import java.util.stream.Collectors;

public class BasketMapper {

    public static Basket toBasket(BasketDto basketDto) {
        return new Basket(
                basketDto.getCustomerId(),
                basketDto.getEntries()
                        .stream()
                        .map(BasketMapper::toBasketItem)
                        .collect(Collectors.toSet())
        );
    }

    public static BasketEntry toBasketItem(BasketIEntryDto basketIEntryDto) {
        return new BasketEntry(
                basketIEntryDto.getArticleId(),
                basketIEntryDto.getQuantity()
        );
    }

    public static BasketCalculationResultDto toBasketCalculationResultDto(
            BasketCalculationResult basketCalculationResult
    ) {
        return new BasketCalculationResultDto(
                basketCalculationResult.getCustomerId(),
                basketCalculationResult.getPricedBasketEntries(),
                basketCalculationResult.getTotalAmount()
        );
    }
}
