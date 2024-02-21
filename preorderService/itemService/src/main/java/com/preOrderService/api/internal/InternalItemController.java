package com.preOrderService.api.internal;

import com.preOrderService.dto.CheckReserveResponseDto;
import com.preOrderService.dto.StockRequest;
import com.preOrderService.service.ProductService;
import com.preOrderService.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class InternalItemController {
    private final ProductService productService;
    private final StockService stockService;
    /**
     * 재고 추가
     */
    @PatchMapping("/stock/add")
    public ResponseEntity<Void> addStock(@RequestBody StockRequest req){
        stockService.addStock(req);
        return ResponseEntity.ok().build();
    }

    /**
     * 재고 감소
     */
    @PatchMapping("/stock/reduce")
    public ResponseEntity<Void> reduceStock(@RequestBody StockRequest req){
        stockService.reduceStock(req);
        return ResponseEntity.ok().build();
    }

    /**
     * 재고 조회
     */
    @GetMapping("/stock/{itemId}")
    public ResponseEntity<Long> getStock(@PathVariable("itemId")Long itemId){
        Long response = stockService.getStockByItemId(itemId);
        return ResponseEntity.ok().body(response);
    }

    /**
     * 상품 타입 조회
     */
    @GetMapping("/type/{itemId}")
    public ResponseEntity<CheckReserveResponseDto> getItemType(@PathVariable("itemId")Long itemId){
<<<<<<< HEAD
        CheckReserveResponseDto res = productService.getItemTypeAndTime(itemId);
=======
        CheckReserveResponseDto res = itemService.getItemTypeAndTime(itemId);
>>>>>>> origin/dev
        return ResponseEntity.ok().body(res);
    }
}
