package com.preOrderService.api.internal;

import com.preOrderService.dto.AddStockRequest;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.ItemServiceException;
import com.preOrderService.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class internalItemService {
    private final ItemService itemService;
    @PatchMapping("/stock/add")
    public void addStock(@RequestBody AddStockRequest req){
        itemService.addStock(req);
    }
}
