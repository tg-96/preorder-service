package com.preOrderService.api.external;

import com.preOrderService.dto.ItemResponseDto;
import com.preOrderService.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    /**
     * 상품 리스트 조회
     */
    @GetMapping
    public List<ItemResponseDto> getAllItems(){
        return itemService.getAllItems();
    }

}
