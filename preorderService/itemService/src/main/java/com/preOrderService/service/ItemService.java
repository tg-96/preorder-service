package com.preOrderService.service;

import com.preOrderService.dto.ItemResponseDto;
import com.preOrderService.entity.Item;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.ItemServiceException;
import com.preOrderService.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    /**
     * 상품 목록 조회
     */
    public List<ItemResponseDto> getAllItems() {
        List<Item> items = itemRepository.findAll();

        if (items.isEmpty()) {
            throw new ItemServiceException(ErrorCode.NO_ITEMS);
        }

        List<ItemResponseDto> collect = items.stream().map(
                item -> ItemResponseDto.builder()
                        .id(item.getId())
                        .stock(item.getStock())
                        .name(item.getName())
                        .type(item.getType())
                        .price(item.getPrice())
                        .reserveTime(item.getReserveTime())
                        .content(item.getContent())
                        .build()
        ).collect(Collectors.toList());

        return collect;
    }

    /**
     * 상품 상세 페이지 조회
     */

    /**
     * 상품 추가
     */

    /**
     * 상품 삭제
     */

    /**
     * 재고 추가
     */

    /**
     * 재고 삭제
     */
}
