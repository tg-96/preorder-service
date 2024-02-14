package com.preOrderService.service;

import com.preOrderService.dto.AddStockRequest;
import com.preOrderService.dto.ItemRequestDto;
import com.preOrderService.dto.ItemResponseDto;
import com.preOrderService.entity.Item;
import com.preOrderService.entity.ItemType;
import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.ItemServiceException;
import com.preOrderService.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    @DisplayName("상품 리스트 조회")
    void getAllItems() {
        //given
        when(itemRepository.findAll()).thenReturn(
                Arrays.asList(Item.generalItemCreate(
                                "세탁기",
                                "드럼 세탁기 입니다.",
                                10000,
                                10
                        ), Item.reserveItemCreate(
                                "냉장고",
                                "좋은 냉장고",
                                50000,
                                3,
                                LocalDateTime.of(2024, 2, 15, 10, 00))
                )
        );
        //when
        List<ItemResponseDto> items = itemService.getAllItems();

        //then
        assertThat(items.get(0).getName()).isEqualTo("세탁기");
        assertThat(items.size()).isEqualTo(2);

        //exception
        when(itemRepository.findAll()).thenReturn(Arrays.asList());
        assertThrows(ItemServiceException.class, () -> {
            itemService.getAllItems();
        });
    }

    @Test
    @DisplayName("상품 정보 조회")
    void getItemInfo() {
        //given
        when(itemRepository.findById(1L)).thenReturn(Optional.of(
                        Item.generalItemCreate(
                                "세탁기",
                                "좋은 세탁기 입니다.",
                                1000,
                                10
                        )
                )
        );
        //when
        ItemResponseDto itemInfo = itemService.getItemInfo(1L);

        //then
        assertThat(itemInfo.getName()).isEqualTo("세탁기");

        //exception
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(ItemServiceException.class, () -> {
            itemService.getItemInfo(1L);
        });
    }

    @Test
    @DisplayName("일반 상품 추가")
    public void createGeneralItem() {
        //given
        ItemRequestDto req = new ItemRequestDto("세탁기", "드럼 세탁기", 10000, 10, null, "general");
        when(itemRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        Item item = itemService.createItem(req);

        //then
        assertThat(item.getName()).isEqualTo("세탁기");
        assertThat(item.getType()).isEqualTo(ItemType.GENERAL);
    }

    @Test
    @DisplayName("예약 상품 추가")
    public void createReserveItem() {
        //given
        ItemRequestDto req = new ItemRequestDto("세탁기", "드럼 세탁기", 10000, 10, LocalDateTime.of(2024, 2, 15, 12, 00), "reserve");
        ItemRequestDto req_null = new ItemRequestDto("세탁기", "드럼 세탁기", 10000, 10, null, "reserve");
        ItemRequestDto req_before = new ItemRequestDto("세탁기", "드럼 세탁기", 10000, 10, LocalDateTime.of(2023, 2, 15, 12, 00), "reserve");

        when(itemRepository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        Item item = itemService.createItem(req);

        ItemServiceException ex_null = assertThrows(ItemServiceException.class, () -> {
            itemService.createItem(req_null);
        });

        ItemServiceException ex_before = assertThrows(ItemServiceException.class, () -> {
            itemService.createItem(req_before);
        });

        //then
        assertThat(item.getName()).isEqualTo("세탁기");
        assertThat(item.getType()).isEqualTo(ItemType.RESERVE);

        assertThat(ex_null.getErrorCode()).isEqualTo(ErrorCode.RESERVE_TIME_ERROR);
        assertThat(ex_before.getErrorCode()).isEqualTo(ErrorCode.RESERVE_TIME_ERROR);
    }

    @Test
    @DisplayName("잘못된 상품 타입으로 상품 생성")
    public void createWrongItem() {
        //given
        ItemRequestDto req = new ItemRequestDto("세탁기", "드럼 세탁기", 10000, 10, LocalDateTime.of(2024, 2, 15, 12, 00), "aa");

        //when
        ItemServiceException ex = assertThrows(ItemServiceException.class, () -> {
            itemService.createItem(req);
        });

        //then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ITEM_TYPE_ERROR);
    }

    @Test
    @DisplayName("상품 삭제")
    public void deleteItem() {
        //given
        when(itemRepository.findById(1L)).thenReturn(Optional.of(Item.generalItemCreate(
                "세탁기",
                "드럼 세탁기 입니다.",
                10000,
                10
        )));

        when(itemRepository.findById(2L)).thenReturn(Optional.empty());

        //when
        Long itemId = itemService.deleteItem(1L);
        ItemServiceException ex = assertThrows(ItemServiceException.class, () -> {
            itemService.deleteItem(2L);
        });

        //then
        assertThat(itemId).isEqualTo(1L);
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.NO_ITEMS);
    }

    @Test
    @DisplayName("재고 추가: 성공")
    public void addStock() {
        //given
        Item item = Item.generalItemCreate("세탁기", "좋은 세탁기", 10000, 10);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        //when
        AddStockRequest req = new AddStockRequest(1L,2);
        int stock = itemService.addStock(req);

        //then
        assertThat(stock).isEqualTo(12);
    }
    @Test
    @DisplayName("재고 추가:실패 - 추가할 값이 0 이하")
    public void addStock_(){
        //given
        Item item = Item.generalItemCreate("세탁기", "좋은 세탁기", 10000, 10);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        //when
        AddStockRequest req = new AddStockRequest(1L,0);
        ItemServiceException ex = assertThrows(ItemServiceException.class, () -> {
            itemService.addStock(req);
        });

        //then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ADD_STOCK_ZERO_ERROR);
    }
}