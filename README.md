# preorder-service
## 목차
- [프로젝트 설명](#프로젝트-설명)
- [docker compose 실행](#docker-compose-실행)
- [버전](#버전)
- [기술 스택](#기술-스택)
- [구현 요구 사항](#구현-요구-사항)
- [시뮬레이션 상황 설정](#시뮬레이션-상황-설정)
- [테스트 방법](#테스트-방법)
- [ERD](#erd)
- [모듈](#모듈)

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

## 테스트 방법
- Jmeter와 같은 테스트 툴을 사용한다.
- url: http://localhost:8085/api/v1/payment/test
- Method: POST
- body:
```
{
  "userId": "${userId}",
  "itemId": "1",
  "count": "1"
}
```
csv 파일에 userId에 대한 값을 `1` ~ `동시 접속자 수` 까지로 설정해준다.  
ex) 10000명의 쓰레드를 동시에 요청하기 위해서는 1~10000까지의 값을 csv 파일에 저장 해준다.

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

## 트러블 슈팅
- [동시성 처리 과정](https://velog.io/@tg-96/%EB%8F%99%EC%8B%9C%EC%84%B1-%EB%AC%B8%EC%A0%9C-%ED%95%B4%EA%B2%B0-%ED%95%98%EA%B8%B0)
