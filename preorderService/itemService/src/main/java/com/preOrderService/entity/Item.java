package com.preOrderService.entity;

import com.preOrderService.exception.ErrorCode;
import com.preOrderService.exception.ItemServiceException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String content;
    private int price;
    private int stock;
    private LocalDateTime reserveTime;
    @Enumerated
    private ItemType type;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    private Item(String name, String content, int price, int stock, LocalDateTime reserveTime, ItemType type) {
        this.name = name;
        this.content = content;
        this.price = price;
        this.stock = stock;
        this.reserveTime = reserveTime;
        this.type = type;
    }

    /**
     * 일반 상품 생성
     */
    static public Item generalItemCreate(String name, String content, int price, int stock) {
        Item generalItem = new Item(name, content, price, stock, null, ItemType.GENERAL);
        return generalItem;
    }

    /**
     * 예약 상품 생성
     */
    static public Item reserveItemCreate(String name, String content, int price, int stock, LocalDateTime reserveTime) {
        Item reserveItem = new Item(name, content, price, stock, reserveTime, ItemType.RESERVE);
        return reserveItem;
    }

    /**
     * 재고 추가
     */
    public void addStock(int count) {
        this.stock = this.stock + count;
    }

    /**
     * 재고 감소
     */
    public void minusStock(int count) {
        int value = this.stock - count;
        if (value < 0) {
            throw new ItemServiceException(ErrorCode.OUT_OF_STOCK);
        }
        this.stock = value;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changePrice(int price) {
        this.price = price;
    }
    public void changeStock(int stock){
        this.stock = stock;
    }
    public void changeReserveTime(LocalDateTime reserveTime){
        this.reserveTime = reserveTime;
    }
    public void changeType(String type){
        if(type.equals("general")){
            this.type = ItemType.GENERAL;
        }
        if(type.equals("reserve")){
            this.type = ItemType.RESERVE;
    }
        }
}
