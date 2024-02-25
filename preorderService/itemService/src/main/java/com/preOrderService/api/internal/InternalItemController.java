package com.preOrderService.api.internal;

import com.preOrderService.dto.CheckReserveResponseDto;
import com.preOrderService.dto.EnterPayRequestDto;
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
     * return : true -> 재고 예약 완료
     * return : false -> 재고 예약 불가능
     */
    @PostMapping("/stock/reserve")
    public ResponseEntity<Boolean> reserveStock(@RequestBody EnterPayRequestDto req) throws InterruptedException {
        Boolean ok = stockService.reserveStock(req);

        return ResponseEntity.ok().body(ok);
    }

    /**
     * 재고 감소
     */
    @PostMapping("/stock/cancel")
    public ResponseEntity<Void> cancelStock(@RequestBody EnterPayRequestDto req) throws InterruptedException {
        stockService.cancelStock(req);
        return ResponseEntity.ok().build();
    }

    /**
     * 상품 타입 조회
     */
    @GetMapping("/type/{itemId}")
    public ResponseEntity<CheckReserveResponseDto> getItemType(@PathVariable("itemId")Long itemId){
        CheckReserveResponseDto res = productService.getItemTypeAndTime(itemId);
        return ResponseEntity.ok().body(res);
    }
}