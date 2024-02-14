package com.preOrderService.api.external;

import com.preOrderService.dto.AddStockRequest;
import com.preOrderService.dto.ItemRequestDto;
import com.preOrderService.dto.ItemResponseDto;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.ItemServiceException;
import com.preOrderService.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<ItemResponseDto> getAllItems() {
        return itemService.getAllItems();
    }

    /**
     * 상품 상세페이지 조회
     */
    @GetMapping("/{itemId}")
    public ItemResponseDto getItemInfo(@PathVariable("itemId") Long itemId) {
        return itemService.getItemInfo(itemId);
    }

    /**
     * 상품 추가
     * req의 필드 type이 general 이면,ItemType.general
     * req의 필드 type이 reserve 이면,ItemType.reserve로 변경
     * 둘다 아니라면 error
     */
    @PostMapping
    public ResponseEntity<Void> createItem(@RequestBody ItemRequestDto req) {
        //요청폼이 바르게 입력되었는지 확인
        if (req.getName().isBlank() ||
                req.getContent().isBlank() ||
                req.getPrice() <= 0 ||
                req.getStock() <= 0 ||
                req.getType().isBlank()) {
            throw new ItemServiceException(ErrorCode.CREATE_ITEM_ERROR);
        }

        itemService.createItem(req);

        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable("itemId") Long itemId){
        itemService.deleteItem(itemId);
        return ResponseEntity.ok().build();
    }

}
