package com.preOrderService.service;

import com.preOrderService.dto.ItemResponseDto;
import com.preOrderService.entity.Item;
import com.preOrderService.exception.ItemServiceException;
import com.preOrderService.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    @DisplayName("상품 리스트 조회 테스트")
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
}