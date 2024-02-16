package com.preOrderService.service;

import com.preOrderService.dto.StockRequest;
import com.preOrderService.dto.ItemRequestDto;
import com.preOrderService.dto.ItemResponseDto;
import com.preOrderService.entity.Item;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.ItemServiceException;
import com.preOrderService.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    public ItemResponseDto getItemInfo(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new ItemServiceException(ErrorCode.NO_ITEMS));
        return ItemResponseDto.builder()
                .content(item.getContent())
                .reserveTime(item.getReserveTime())
                .price(item.getPrice())
                .type(item.getType())
                .stock(item.getStock())
                .name(item.getName())
                .build();
    }

    /**
     * 상품 추가
     */
    @Transactional
    public Item createItem(ItemRequestDto req) {
        //일반 상품일 경우
        if (req.getType().equals("general")) {
            Item newItem = Item.generalItemCreate(
                    req.getName(),
                    req.getContent(),
                    req.getPrice(),
                    req.getStock()
            );
            return itemRepository.save(newItem);
        }

        //예약 상품일 경우
        else if (req.getType().equals("reserve")) {

            //예약 시간이 현재 시간보다 이전인 경우 or 예약 시간을 지정하지 않은 경우
            if (req.getReserveTime() == null || req.getReserveTime().isBefore(LocalDateTime.now())
                    ) {
                throw new ItemServiceException(ErrorCode.RESERVE_TIME_ERROR);
            }

            Item newItem = Item.reserveItemCreate(
                    req.getName(),
                    req.getContent(),
                    req.getPrice(),
                    req.getStock(),
                    req.getReserveTime()
            );
            return itemRepository.save(newItem);
        }
        //상품 타입이 올바르지 않은 경우
        else {
            throw new ItemServiceException(ErrorCode.ITEM_TYPE_ERROR);
        }
    }
    /**
     * 상품 삭제
     */
    @Transactional
    public Long deleteItem(Long itemId){
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemServiceException(ErrorCode.NO_ITEMS));
        itemRepository.delete(item);
        return itemId;
    }
    /**
     * 재고 추가
     */
    @Transactional
    public Long addStock(StockRequest req){
        Item item = itemRepository.findByIdWithWriteLock(req.getItemId()).orElseThrow(() -> new ItemServiceException(ErrorCode.NO_ITEMS));

        if(req.getCount() <= 0){
            throw new ItemServiceException(ErrorCode.ADD_STOCK_ZERO_ERROR);
        }

        item.addStock(req.getCount());

        return item.getStock();
    }

    /**
     * 재고 감소
     */
    @Transactional
    public Long reduceStock(StockRequest req){
        Item item = itemRepository.findByIdWithWriteLock(req.getItemId()).orElseThrow(() -> new ItemServiceException(ErrorCode.NO_ITEMS));

        if(req.getCount() <= 0){
            throw new ItemServiceException(ErrorCode.ADD_STOCK_ZERO_ERROR);
        }

        item.minusStock(req.getCount());

        return item.getStock();
    }

    /**
     * 상품 정보 변경
     */
    @Transactional
    public void changeItemInfo(Long itemId, ItemRequestDto req) {
        Item item = itemRepository.findByIdWithWriteLock(itemId).orElseThrow(() -> new ItemServiceException(ErrorCode.NO_ITEMS));

        if(req.getName() != null && !req.getName().isBlank()){
            item.changeName(req.getName());
        }

        if(req.getContent() != null && !req.getContent().isBlank()){
            item.changeContent(req.getContent());
        }

        if(req.getPrice() != null && req.getPrice() > 0){
            item.changePrice(req.getPrice());
        }

        if(req.getType() != null && !req.getType().isBlank()){
            item.changeType(req.getType());
        }

        if(req.getStock() != null && req.getStock() > 0){
            item.changeStock(req.getStock());
        }

        if(req.getReserveTime() != null && !req.getReserveTime().isBefore(LocalDateTime.now())){
            item.changeReserveTime(req.getReserveTime());
        }
    }

    /**
     * 재고 조회
     */
    public Long getStock(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemServiceException(ErrorCode.NO_ITEMS));
        return item.getStock();
    }
}
