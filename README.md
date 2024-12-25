# 청주대학교 셔틀버스 예약 시스템

## 1. 개요

### 1.1. 목적
- 청주대 학생들의 효율적인 셔틀버스 이용 지원
- 실시간 좌석 현황 파악 및 관리

### 1.2. 대상
- 청주대학교 재학생

## 2. 프로그램의 중요성 및 필요성

### 2.1 중요성
- 학생들의 통학 편의성 향상
- 셔틀버스 이용 효율성 증대
- 셔틀버스 서비스의 현대화

### 2.2 필요성
- 수동적인 좌석 관리 방식의 한계 극복
- 실시간 좌석 현황 파악 가능
- 예약 및 취소 과정의 간소화

## 3. 프로그램 수행 절차

### 3.1 다이어그램
![java2 기말](https://github.com/user-attachments/assets/19822ba4-0258-4aa5-b9d1-c4fb21c7f578)

### 3.2 클래스 다이어그램
클래스가 하나로 짜여진코드여서 클래스 다이어그램 구현이 어렵습니다.
![클래스](https://github.com/user-attachments/assets/07d235fc-261c-46b3-9999-a0e3abb6da28)

### 3.3 절차 설명
1. 프로그램 시작 시 기존 예약 정보 로드합니다.
2. 사용자에게 GUI를 통해 노선과 시간을 선택하도록 합니다.
3. 선택된 노선과 시간에 대한 좌석 현황을 표시합니다.
4. 사용자는 좌석 번호, 이름, 학번을 입력하여 예약할 수 있습니다.
5. 예약 취소 시 학번과 이름을 입력받아 처리합니다.
6. 모든 예약/취소 작업 후 파일에 정보를 저장합니다.
7. 실시간으로 좌석 현황을 업데이트하여 표시합니다.

## 4. 느낀점
처음에는 단순한 콘솔 프로그램으로 시작했지만, GUI 버전으로 발전시키면서 점진적 개선의 가치를 깨달을 수 있었습니다.
또한, 이 프로그램을 개발을 통해 컬렉션 프레임워크 활용하여 HashMap과 중첩 Map 구조를 활용하여 복잡한 예약 데이터를 효율적으로 관리할 수 있었으며,
List를 활용한 시간표 관리로 유연한과 효율성을 경험할 수 있었습니다.

