# preorder-service
## 목차
- [프로젝트 설명](#프로젝트-설명)
- [docker compose 실행](#docker-compose-실행)
- [버전](#버전)
- [기술 스택](#기술-스택)
- [구현 요구 사항](#구현-요구-사항)
- [시뮬레이션 상황 설정](#시뮬레이션-상황-설정)
- [ERD](#erd)
- [모듈](#모듈)
- [API 명세](#api-명세)
    - [itemService](#itemservice)
    - [orderService](#orderservice)
    - [payService](#payservice)
## 프로젝트 설명
- 상품의 예약 구매 상황을 시뮬레이션 한다.
- 상품 재고에 비해 훨씬 많은 구매자가 몰리는 상황에서 중복 결제를 방지하고 상품 재고 수 만큼만 결제가 진행되도록 한다.

## docker compose 실행
```
docker-compose up -d
```

## 버전 
- springboot: 3.2.2
- java: 17

## 기술 스택
- Java
- Springboot
- Jpa
- Mysql
- Redis
- FeignClient
- Eureka
- ApiGateway

## 구현 요구 사항
- 상품은 특정 시간에 구매가 활성화 된다.
- 상품 수에 비해 월등하게 많은 구매 시도자 수
- 구매자들은 특정 시간에 몰려서 구매한다.

## 시뮬레이션 상황 설정
- 예약 구매 상품 재고 10개, 동시 결제 시도자 10000명
- 판매 예정 시간 이후로만 결제 시도 가능
- 결제 화면에서 20% 이용자가 이탈(ex. 고객 변심)
- 결제 진행 중 20% 결제 취소(ex. 카드사에 의한 결제 취소: 잔액 부족)

## ERD
![image](https://github.com/tg-96/preorder-service/assets/98454438/6b503f30-2a01-4d4a-bda8-49a2ec51ee62)

## 모듈

|서비스 이름|포트|역할|
|---|---|---|
|gateway|8085|API Gateway|
|itemService|8084|상품 재고 관리 서버|
|orderService|8083|주문 관리 서버|
|payService|8082|결제 관리 서버|
|eurekaServer|8761|eureka server|

## API 명세
### itemService
1. 상품 리스트 조회
- **URL**: `/api/v1/items`
- **Method**: `GET`
- **Response**:
```
[
  {
    “id” : “1”,
    “name” : “냉장고”,
    “content” : “좋은 냉장고”,
    “price” : "10000",
    “stock” : "10",
    “reserveTime” : "2024-02-09T17:41:29.578418"
    “type” : “reserve”
  },
  {
    “id” : “2”,
    “name” : “TV”,
    “content” : “좋은 TV”,
    “price” : "10000",
    “stock” : "10",
    “reserveTime” : “”
    “type” : “general”
   }
]`
```
- **Error Response**:
  - **Status**: `400 BAD REQUEST`
  - **Code**: `NO_ITEMS`
---
2. 상품 상세 페이지 조회
- **URL**: `/api/v1/items/{itemId}`
- **Method**: `GET`
- **Response**:
```
{
  “id” : “2”,
  “name” : “TV”,
  “content” : “좋은 TV”,
  “price” : "10000",
  “stock” : "10",
  “reserveTime” : “”
  “type” : “general”
}
```
- **Error Response**:
  - **Status**: `400 BAD REQUEST`
  - **Code**: `NO_ITEMS`
---
3. 상품 추가
- **URL**: `/api/v1/items`
- **Method**: `POST`
- **Request**:
```
{
   “name” : “냉장고”,
    “content” : “좋은 냉장고”,
    “price” : "10000",
    “stock” : "10",
    “reserveTime” : “2024-02-15T10:30:00” 
}
```
- **Response**:
  - **Status**: `200 OK`
    
- **Error Response**:
  - **Status**: `400 BAD REQUEST`
  - **Code**: `CREATE_ITEM_ERROR`
---
4. 상품 삭제
- **URL**: `/api/v1/items/{itemId}`
- **Method**: `DELETE`
- **Response**:
  - **Status**: `200 OK`
    
- **Error Response**:
  - **Status**: `400 BAD REQUEST`
  - **Code**: `NO_ITEMS`
---
5. 상품 정보 변경
- **URL**: `/api/v1/items/{itemId}`
- **Method**: `PATCH`
- **Request**:
```
{
   “name” : “”,
    “content” : “”,
    “price” : "10000",
    “stock” : "10",
    “reserveTime” : “2024-02-15T10:30:00”,
    "type" : "reserve" 
}
```
- **Response**:
  - **Status**: `200 OK`
    
- **Error Response**:
  - **Status**: `400 BAD REQUEST`
  - **Code**: `NO_ITEMS`
---
6.  재고 예약
- **URL**: `/items/stock/reserve`
- **Method**: `POST`
- **Request**:
```
{
   “userId” : “1”,
    “itemId” : “1”,
    “count” : "2" 
}
```
- **Response**:
  - **Status**: `200 OK`
  - **body**: `true(재고 예약 성공)`,
              `false(재고 예약 실패)`
    
- **Error Response**:
  - **Status**: `400 BAD REQUEST`
  - **Code**: `NO_ITEMS`
---
7. 재고 예약 취소
- **URL**: `/items/stock/cancel`
- **Method**: `POST`
- **Request**:
```
{
   “userId” : “1”,
    “itemId” : “1”,
    “count” : "2" 
}
```
- **Response**:
  - **Status**: `200 OK`
    
- **Error Response**:
  - **Status**: `400 BAD REQUEST`
  - **Code**: `NO_ITEMS`
---
8. 상품 타입 및 예약 시간 조회
- **URL**: `/items/type/{itemId}`
- **Method**: `GET`
- **Response**:
  - **Status**: `200 OK`
  - **body**:
  ```
  {
    "type": "reserve",
    "reserveTime":"2024-02-15T10:30:00"
  }
  ```
- **Error Response**:
  - **Status**: `400 BAD REQUEST`
  - **Code**: `NO_ITEMS`
---

### orderService
1. 주문 생성
- **URL**: `/order`
- **Method**: `POST`
- **Request**:
```
{
  "itemId" : "1",
  "userId" : "1".
  "quantity" : "1"
}
```
- **Response**:
  - **Status**: `200 OK`
  - **body**: `2(orderId)`
- **Error Response**:
  - **Status**: `400 BAD REQUEST`
  - **Code**: `CREATE_ORDER_ERROR`
---
2. 주문 삭제
- **URL**: `/order/{orderId}`
- **Method**: `DELETE`
- **Response**:
  - **Status**: `200 OK`
  - **body**: `"orderId:2 가 삭제 되었습니다."`
---
3. 주문 상태 변경
- **URL**: `/order/changeStatus`
- **Method**: `POST`
- **Request**:
```
{
  "orderId" : "1",
  "status" : "PAYMENT_IN_PROGRESS".
}
```
- **Response**:
  - **Status**: `200 OK`
  - **body**: `"orderId:2의 주문상태가 PAYMENT_IN_PROGRESS 로 변경되었습니다."`
- **Error Response**:
  - **Status**: `400 BAD REQUEST`
  - **Code**: `ORDER_STATUS_ERROR`
---
4. 주문 정보 조회
- **URL**: `/order/{orderId}`
- **Method**: `GET`
- **Response**:
  - **Status**: `200 OK`
  - **body**:
```
{
  “orderId” : “1”,
  “itemId” : “2”,
  “userId” : “1”,
  “quantity” : “2”,
  “price” : 1000,
  “orderStatus” :    “PAYMENT_CANCEL”,
  “createAt” : “2024-02-23T10:00:00”,
  “updatedAt” : “2024-02-24T10:00:00”    
}
```
- **Error Response**:
  - **Status**: `400 BAD REQUEST`
  - **Code**: `NO_EXIST_ORDER_ID`
---
### PayService
1. 결제 진입
- **URL**: `/api/v1/payment/enter`
- **Method**: `POST`
- **Request**:
```
{
  "userId" : "1",
  "itemId" : "1",
  "count" : "1"
}
```
- **Response**:
  - **Status**: `200 OK`
  - **body**:
```
{
  “orderId” : “1”,
  “itemId” : “2”,
  “userId” : “1”,
  “count” : “2”,
}
```
- **Error Response**:
  - 구매 가능 시간이 아님. 
    - **Status**: `400 BAD REQUEST`
    - **Code**: `NOT_AVAILABLE_TIME_TO_PURCHASE`
  - 재고 부족
    - **Status**: `400 BAD REQUEST`
    - **Code**: `OUT_OF_STOCK`
---
2. 결제
- **URL**: `/api/v1/payment/pay`
- **Method**: `POST`
- **Request**:
```
{
  "userId" : "1",
  "itemId" : "1",
  "orderId" : "2",
  "count" : "1"
}
```
- **Response**:
  - **Status**: `200 OK`
  - **body**: `"complete pay"`
- **Error Response**:
  - 주문 상태가 "PAYMENT_VIEW" 상태가 아님 -> 결제 진입을 통과하지 않음.
    - **Status**: `400 BAD REQUEST`
    - **Code**: `IS_NOT_PAYMENT_VIEW_STATUS`
  - 잔액 부족으로 인한 결제 실패
    - **Status**: `400 BAD REQUEST`
    - **Code**: `FAIL_TO_PAY_BECAUSE_OUT_OF_BALANCE`
