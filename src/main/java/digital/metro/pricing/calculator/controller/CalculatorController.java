package digital.metro.pricing.calculator.controller;

import digital.metro.pricing.calculator.dto.BasketDto;
import digital.metro.pricing.calculator.model.BasketCalculationResult;
import digital.metro.pricing.calculator.dto.mapper.BasketMapper;
import digital.metro.pricing.calculator.model.Basket;
import digital.metro.pricing.calculator.model.BasketEntry;
import digital.metro.pricing.calculator.service.BasketCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController()
@RequestMapping("calculator")
public class CalculatorController {

    private final BasketCalculatorService basketCalculatorService;

    @Autowired
    public CalculatorController(BasketCalculatorService basketCalculatorService) {
        this.basketCalculatorService = basketCalculatorService;
    }

    @PostMapping("/basket")
    public BasketCalculationResult calculateBasket(@RequestBody BasketDto basketDto) {
        Basket basket = BasketMapper.toBasket(basketDto);
        return basketCalculatorService.calculateBasket(basket);
    }

    @GetMapping("/article/{articleId}")
    public BigDecimal getArticlePrice(
            @PathVariable String articleId,
            @RequestParam(required = false) String customerId
    ) {
        return basketCalculatorService.calculateArticle(new BasketEntry(articleId, BigDecimal.ONE), customerId);
    }
}
