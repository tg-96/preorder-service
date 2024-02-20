package com.preOrderService.service;

import com.preOrderService.dto.CheckReserveResponseDto;
import com.preOrderService.dto.OrderRequestDto;
import com.preOrderService.dto.PayRequestDto;
import com.preOrderService.dto.StockRequest;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.PayServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PayService {
    private final RedisTemplate<String,Integer> redisTemplate;
    private final ItemServiceClient itemServiceClient;
    private final OrderServiceClient orderServiceClient;
    /**
     * 결제 진입
     */
    @Transactional
    public void startPay(PayRequestDto requestDto){
        //예약 구매 상품이라면, 결제 가능한 시간인지 체크
        checkReserveTime(requestDto);

        //캐시에 재고 저장되어 있는지 확인
        String key = "item:stock:"+requestDto.getItemId();
        Integer realTimeStock = redisTemplate.opsForValue().get(key);

        //레디스에 정보가 없다면 실제 재고를 조회해서 레디스에 저장
        if(realTimeStock == null){
            //실제 재고 조회
            ResponseEntity<Long> curStock;

            try{
                 curStock = itemServiceClient.getStockByItemId(requestDto.getItemId());
            }catch (Exception e){
                throw new PayServiceException(ErrorCode.GET_ITEM_STOCK_API_ERROR);
            }

            Long stock = curStock.getBody();

            //캐시에 상품의 재고 저장
            redisTemplate.opsForValue().set(key,stock.intValue());
        }

        //캐시에서 재고 조회
        realTimeStock = redisTemplate.opsForValue().get(key);

        //구매하고자 하는 수 보다 재고가 많은 지 확인
        if(realTimeStock - requestDto.getCount() < 0){
            throw new PayServiceException(ErrorCode.OUT_OF_STOCK);
        }

        //실시간 재고 줄여줌
        redisTemplate.opsForValue().decrement(key, requestDto.getCount());

        //실제 재고와 실시간 재고 동기화
        StockRequest req = new StockRequest(requestDto.getItemId(), requestDto.getCount());
        try {
            itemServiceClient.reduceStock(req);
        }catch (Exception e){
            //에러 발생 시, 실시간 재고 원상복귀
            redisTemplate.opsForValue().increment(key, requestDto.getCount());
            throw new PayServiceException(ErrorCode.REDUCE_STOCK_API_ERROR);
        }

        //주문 생성
        OrderRequestDto orderRequestDto = new OrderRequestDto(requestDto.getItemId(), requestDto.getUserId(), requestDto.getCount());
        try {
            orderServiceClient.createOrder(orderRequestDto);
        }catch (Exception e){
            //에러 발생 시, 실시간 재고 원상복귀
            redisTemplate.opsForValue().increment(key, requestDto.getCount());
            throw new PayServiceException(ErrorCode.CREATE_ORDER_API_ERROR);
        }
    }

    /**
     * 예약 상품 구매 가능 시간 체크
     */
    public void checkReserveTime(PayRequestDto requestDto) {
        ResponseEntity<CheckReserveResponseDto> response = itemServiceClient.getItemTypeAndTime(requestDto.getItemId());
        if(response.getBody().getType().equals("reserve")){
            if(response.getBody().getReserveTime().isAfter(LocalDateTime.now())){
                throw new PayServiceException(ErrorCode.NOT_AVAILABLE_TIME_TO_PURCHASE);
            }
        }
    }
}
