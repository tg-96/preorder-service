package com.preOrderService.entity;

import com.preOrderService.exception.ItemServiceException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


class ItemTest {

    @Test
    void generalItemCreate() {
        //given,when
        Item item = Item.generalItemCreate("세탁기","성능좋은 세탁기",10000L,10L);

        //then
        assertThat(item.getName()).isEqualTo("세탁기");
        assertThat(item.getStock()).isEqualTo(10);
        assertThat(item.getType()).isEqualTo(ItemType.GENERAL);
    }

    @Test
    void reserveItemCreate() {
        //given,when
        Item item = Item.reserveItemCreate("밥솥","맛있는 쿠쿠",100000L,50L, LocalDateTime.of(2024,2,20,12,00));

        //then
        assertThat(item.getType()).isEqualTo(ItemType.RESERVE);
    }

    @Test
    void addStock() {
        //given
        Item item = Item.generalItemCreate("세탁기","성능좋은 세탁기",10000L,10L);
        Item item2 = Item.generalItemCreate("세탁기","성능좋은 세탁기",10000L,2L);

        //when
        item.addStock(2L);

        //then
        assertThat(item.getStock()).isEqualTo(12);
        assertThrows(ItemServiceException.class, () ->{
            item2.minusStock(3L);
        });
    }

    @Test
    void minusStock() {
        //given
        Item item = Item.generalItemCreate("세탁기","성능좋은 세탁기",10000L,10L);

        //when
        item.minusStock(3L);

        //then
        assertThat(item.getStock()).isEqualTo(7);
    }
}