package com.preOrderService.service;
import com.preOrderService.dto.EnterPayRequestDto;
import com.preOrderService.entity.Item;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.ItemServiceException;
import com.preOrderService.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockService {
    private final ItemRepository itemRepository;
    private final RedisTemplate<String, Long> redisTemplate;

    //재고 예약
    public Boolean reserveStock(EnterPayRequestDto payRequestDto) {
        // 캐시에서 재고 조회
        String key = "itemId:stock:" + payRequestDto.getItemId();
        Long stock = redisTemplate.opsForValue().get(key);

        //캐시에 재고가 존재 한다면
        if (stock != null) {
            //재고를 줄인다.
            if (stock - payRequestDto.getCount() >= 0) {
                Long newStock = redisTemplate.opsForValue().decrement(key, payRequestDto.getCount());

                return true;
            } else {
                //재고 부족
                return false;
            }
        }
        //캐시에 재고가 존재하지 않는다면
        else {
            Item item = itemRepository.findById(payRequestDto.getItemId()).orElseThrow(() -> new ItemServiceException(ErrorCode.NO_ITEMS));
            Long newStock = item.getStock() - payRequestDto.getCount();
            //재고 예약이 가능한 경우
            if (newStock >= 0) {
                //DB 재고 변경
                item.changeStock(newStock);

                //cache에 추가
                redisTemplate.opsForValue().set(key, newStock);
                return true;

            } else {
                return false;
            }
        }
    }

    //재고 예약 취소
    public void cancelStock(EnterPayRequestDto payRequestDto) {
        // 캐시에서 재고 조회
        String key = "itemId:stock:" + payRequestDto.getItemId();
        Long stock = redisTemplate.opsForValue().get(key);

        //캐시에 재고가 존재 한다면
        if (stock != null) {
            //재고를 증가 시킨다.
            redisTemplate.opsForValue().increment(key, payRequestDto.getCount());
        }
        //캐시에 재고 존재하지 않는다면
        else {
            Item item = itemRepository.findById(payRequestDto.getItemId()).orElseThrow(() -> new ItemServiceException(ErrorCode.NO_ITEMS));
            Long newStock = item.getStock() + payRequestDto.getCount();
            item.changeStock(newStock);
            redisTemplate.opsForValue().set(key, newStock);
        }
    }

    /**
     * DB와 캐시를 동기화 하는 배치
     */
    @Scheduled(fixedRate = 5000)
    public void syncDB() {
        Set<String> keys = redisTemplate.keys("itemId:stock:*");

        if(!keys.isEmpty()){
            for(String key:keys){
                //캐시에서 itemId의 재고 조회
                Long stock = redisTemplate.opsForValue().get(key);

                //itemId 파싱
                String itemId = key.split(":")[2];

                //DB 업데이트
                Item item = itemRepository.findById(Long.parseLong(itemId)).orElseThrow(() -> new ItemServiceException(ErrorCode.NO_ITEMS));
                item.changeStock(stock);
            }
        }
    }
}
