package com.preOrderService.service;

import com.preOrderService.dto.StockRequest;
import com.preOrderService.entity.Item;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.ItemServiceException;
import com.preOrderService.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {
    private final ItemRepository itemRepository;
    private final RedisTemplate<String,Long> redisTemplate;

    /**
     * 재고 조회
     */
    public Long getStockByItemId(Long itemId) {
        //캐시에 저장된 재고가 있는지 확인
        String key = "item:stock:"+itemId;
        Long stock = redisTemplate.opsForValue().get(key);

        // 캐시에 저장된 재고가 있으면 값 리턴
        if(stock != null){
            return stock;
        }

        //캐시에 저장된 재고가 없으면 RDB에서 재고 조회
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemServiceException(ErrorCode.NO_ITEMS));

        //캐시에 재고 저장
        redisTemplate.opsForValue().set(key,item.getStock());

        return item.getStock();
    }

    /**
     * 재고 추가
     */
    @Transactional
    public void addStock(StockRequest req) {
        //증가시킬 재고가 0이하이면, 진행 할 필요가 없다.
        if (req.getCount() <= 0) {
            throw new ItemServiceException(ErrorCode.ADD_STOCK_ZERO_ERROR);
        }

        String key = "item:stock:"+req.getItemId();
        Long stock = redisTemplate.opsForValue().get(key);

        //재고가 캐시에 없다면 예외 처리
        if(stock == null){
            throw new ItemServiceException(ErrorCode.STOCK_NOT_IN_CACHE);
        }

        //캐시 재고 증가
        Long incrementStock = redisTemplate.opsForValue().increment(key, req.getCount());

        //RDB와 캐시 동기화
        try {
            Item item = itemRepository.findById(req.getItemId()).orElseThrow(() -> new ItemServiceException(ErrorCode.NO_ITEMS));
            item.addStock(req.getCount());
        }catch (Exception e){
            //RDB 업데이트 실패 시 cache 원상 복귀
            redisTemplate.opsForValue().decrement(key,req.getCount());
            throw new ItemServiceException(ErrorCode.FAIL_SYNC_DATABASE);
        }
    }

    /**
     * 재고 감소
     */
    @Transactional
    public void reduceStock(StockRequest req) {
        log.info("재고 감소 시작");
        //증가시킬 재고가 0이하이면, 진행 할 필요가 없다.
        if (req.getCount() <= 0) {
            throw new ItemServiceException(ErrorCode.REDUCE_STOCK_ZERO_ERROR);
        }

        String key = "item:stock:"+req.getItemId();

        Long stock = redisTemplate.opsForValue().get(key);

        //재고가 캐시에 없다면 예외 처리
        if(stock == null){
            throw new ItemServiceException(ErrorCode.STOCK_NOT_IN_CACHE);
        }

        //캐시 재고 감소
        log.info("캐시의 재고 값:{}",stock);
        log.info("판매 재고 수:{}",req.getCount());
        Long decrementStock = redisTemplate.opsForValue().decrement(key, req.getCount());
        log.info("판매 후 재고 수:{}",decrementStock);

        //RDB와 캐시 동기화
        try {
            Item item = itemRepository.findById(req.getItemId()).orElseThrow(() -> new ItemServiceException(ErrorCode.NO_ITEMS));
            item.minusStock(req.getCount());
        }catch (Exception e){
            //RDB 업데이트 실패 시, cache 원상 복귀
            redisTemplate.opsForValue().increment(key,req.getCount());
            throw new ItemServiceException(ErrorCode.FAIL_SYNC_DATABASE);
        }
    }
}
