package com.preOrderService.api.external;

import com.preOrderService.dto.ItemRequestDto;
import com.preOrderService.dto.ItemResponseDto;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.ItemServiceException;
import com.preOrderService.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ExternalItemController {
    private final ItemService itemService;

    /**
     * 상품 리스트 조회
     */
    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getAllItems() {
        List<ItemResponseDto> response = itemService.getAllItems();
        return ResponseEntity.ok().body(response);
    }

    /**
     * 상품 상세페이지 조회
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getItemInfo(@PathVariable("itemId") Long itemId) {
        ItemResponseDto response = itemService.getItemInfo(itemId);

        return ResponseEntity.ok().body(response);
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

    /**
     * 상품 삭제
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable("itemId") Long itemId){
        itemService.deleteItem(itemId);
        return ResponseEntity.ok().build();
    }

    /**
     * 상품 정보 변경
     */
    @PatchMapping("/{itemId}")
    public ResponseEntity<Void> changeItemInfo(@PathVariable("itemId") Long itemId,
                                               @RequestBody @Validated ItemRequestDto req){
        itemService.changeItemInfo(itemId,req);
        return ResponseEntity.ok().build();
    }

}
