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
        Item item = Item.generalItemCreate("세탁기","성능좋은 세탁기",10000,10);

        //then
        assertThat(item.getName()).isEqualTo("세탁기");
        assertThat(item.getStock()).isEqualTo(10);
        assertThat(item.getType()).isEqualTo(ItemType.GENERAL);
    }

    @Test
    void reserveItemCreate() {
        //given,when
        Item item = Item.reserveItemCreate("밥솥","맛있는 쿠쿠",100000,50, LocalDateTime.of(2024,2,20,12,00));

        //then
        assertThat(item.getType()).isEqualTo(ItemType.RESERVE);
    }

    @Test
    void addStock() {
        //given
        Item item = Item.generalItemCreate("세탁기","성능좋은 세탁기",10000,10);
        Item item2 = Item.generalItemCreate("세탁기","성능좋은 세탁기",10000,2);

        //when
        item.addStock(2);

        //then
        assertThat(item.getStock()).isEqualTo(12);
        assertThrows(ItemServiceException.class, () ->{
            item2.minusStock(3);
        });
    }

    @Test
    void minusStock() {
        //given
        Item item = Item.generalItemCreate("세탁기","성능좋은 세탁기",10000,10);

        //when
        item.minusStock(3);

        //then
        assertThat(item.getStock()).isEqualTo(7);
    }
}