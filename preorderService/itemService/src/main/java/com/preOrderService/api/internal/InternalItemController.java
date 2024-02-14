package com.preOrderService.api.internal;

import com.preOrderService.dto.StockRequest;
import com.preOrderService.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class InternalItemController {
    private final ItemService itemService;

    /**
     * 재고 추가
     */
    @PatchMapping("/stock/add")
    public void addStock(@RequestBody StockRequest req){
        itemService.addStock(req);
    }

    /**
     * 재고 감소
     */
    @PatchMapping("/stock/reduce")
    public void reduceStock(@RequestBody StockRequest req){
        itemService.reduceStock(req);
    }
}
