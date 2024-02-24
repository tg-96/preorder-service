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

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockService {
    private final ItemRepository itemRepository;
    private final RedisTemplate<String, Long> redisTemplate;

    /**
     * 락 획득
     */
    private boolean lock(String key) {
        return redisTemplate.opsForValue().setIfAbsent(key, 1L, Duration.ofSeconds(10));
    }

    /**
     * 락 반환
     */
    private void unlock(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 재고 조회
     */

    public Long getStockByItemId(Long itemId) throws InterruptedException {
        String lockKey = "lockKey:" + itemId;
        //락 획득
        while (!lock(lockKey)) {
            Thread.sleep(100L);
        }

        //캐시에 저장된 재고가 있는지 확인
        String key = "item:stock:" + itemId;
        Long stock = redisTemplate.opsForValue().get(key);

        // 캐시에 저장된 재고가 있으면 값 리턴
        if (stock != null) {
            return stock;
        }

        //캐시에 저장된 재고가 없으면 RDB에서 재고 조회
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemServiceException(ErrorCode.NO_ITEMS));

        //캐시에 재고 저장
        redisTemplate.opsForValue().set(key, item.getStock());

        //락 반환
        unlock(lockKey);

        return item.getStock();
    }

    /**
     * 재고 추가
     */

    public void addStock(StockRequest req) throws InterruptedException {
        String lockKey = "lockKey:" + req.getItemId();
        //락 획득
        while (!lock(lockKey)) {
            Thread.sleep(100L);
        }


        //증가시킬 재고가 0이하이면, 진행 할 필요가 없다.
        if (req.getCount() <= 0) {
            throw new ItemServiceException(ErrorCode.ADD_STOCK_ZERO_ERROR);
        }

        String key = "item:stock:" + req.getItemId();
        Long stock = redisTemplate.opsForValue().get(key);

        //재고가 캐시에 없다면 예외 처리
        if (stock == null) {
            throw new ItemServiceException(ErrorCode.STOCK_NOT_IN_CACHE);
        }

        //캐시 재고 증가
        Long incrementStock = redisTemplate.opsForValue().increment(key, req.getCount());

        //RDB와 캐시 동기화
        try {
            Item item = itemRepository.findById(req.getItemId()).orElseThrow(() -> new ItemServiceException(ErrorCode.NO_ITEMS));
            item.changeStock(incrementStock);
        } catch (Exception e) {
            //RDB 업데이트 실패 시 cache 원상 복귀
            redisTemplate.opsForValue().decrement(key, req.getCount());
            throw new ItemServiceException(ErrorCode.FAIL_SYNC_DATABASE);
        }

        //락 반환
        unlock(lockKey);
    }

    /**
     * 재고 감소
     */

    public void reduceStock(StockRequest req) throws InterruptedException {

        String lockKey = "lockKey:" + req.getItemId();
        //락 획득
        while (!lock(lockKey)) {
            Thread.sleep(100L);
        }


        log.info("재고 감소 시작");
        //증가시킬 재고가 0이하이면, 진행 할 필요가 없다.
        if (req.getCount() <= 0) {
            throw new ItemServiceException(ErrorCode.REDUCE_STOCK_ZERO_ERROR);
        }

        String key = "item:stock:" + req.getItemId();

        Long stock = redisTemplate.opsForValue().get(key);

        //재고가 캐시에 없다면 예외 처리
        if (stock == null) {
            throw new ItemServiceException(ErrorCode.STOCK_NOT_IN_CACHE);
        }

        //캐시 재고 감소
        log.info("캐시의 재고 값:{}", stock);
        log.info("판매 재고 수:{}", req.getCount());
        Long decrementStock = redisTemplate.opsForValue().decrement(key, req.getCount());
        log.info("재고 감소 후 캐시 재고 수:{}", decrementStock);

        //RDB와 캐시 동기화
        try {
            log.info("RDB와 캐시 동기화");

            Item item = itemRepository.findById(req.getItemId()).orElseThrow(() -> new ItemServiceException(ErrorCode.NO_ITEMS));
            item.changeStock(decrementStock);
        } catch (Exception e) {
            //RDB 업데이트 실패 시, cache 원상 복귀
            log.info("RDB와 캐시 동기화 실패");
            Long increment = redisTemplate.opsForValue().increment(key, req.getCount());
            log.info("RDB와 캐시 동기화 실패 후 캐시 재고(원상복귀):{}", increment);

            throw new ItemServiceException(ErrorCode.FAIL_SYNC_DATABASE);
        }

        //락 반환
        unlock(lockKey);
    }
}
