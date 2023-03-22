# [사전과제] **서버 개발자 - 데이터플랫폼_문찬용**

| Github | https://github.com/chanyong-moon/im-in-jeju (여행중이었어서..) |
| --- | --- |
| 키워드 | #헥사고날_아키텍쳐 #확률적_자료구조론(probabilistic_data_structures) #Count-Min_Sketch #Redis #Http_Interface |
| 노션 | https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c |

# Index

1. [들어가기에 앞서](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
2. [구조](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
    1. [유연성](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
    2. [확장성](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
    3. [가용성](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
3. [기능 구현](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
    1. [장소 검색](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
        1. [요구사항](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
        2. [전제조건](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
        3. [구현](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
    2. [검색 키워드 목록](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
        1. [요구사항](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
        2. [전제조건](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
        3. [구현](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
4. [개선 가능성](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
    1. [레디스 Bloom 이용](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
    2. [CMS 슬라이딩 윈도우 cron job 분리](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
    3. [검색된 횟수 증가시 메세지 큐 이용](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
5. [Appendix](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
    1. [헥사고날  아키텍쳐](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)
    2. **[Count-Min Sketch](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)**

# 들어가기에 앞서

본 과제가 단일 애플리케이션으로 개발하여 제출해야하는 과제라서 원하는 설계대로 개발하기에는 문제가 있다고 생각했습니다.

그래서 제출하는 [개선 가능성 섹션](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)에서 더 디벨롭 가능 할 수있는 방안을 언급하며 어떤 방향으로 설계를 하고싶었는지에 대해서 남겨두고자 합니다.

**전제조건** 항목에서는 문제에 나타나지 않았지만 상세한 스펙을 추가한 내용입니다.

# 구조

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/00b4ea76-c694-4bd5-a511-62cf0a0a87ad/Untitled.png)

- 헥사고날 아키텍쳐로 구현

## 유연성

헥사고날의 장점으로 어댑터(adaptor)와 도메인(domain) 로직 사이에 포트(port)를 넣음으로서 결합도를 낮추어 추후 구현이 바뀌어도 유연한 구조를 가질 수 있습니다.

- 도메인의 영역과 redistemplate 사이에 port를 넣지 않았는데, 현재 생각한 방안으로 redis에 크게 의존적이라고 생각해서 그렇습니다.
- 더 맞는 방향으로는 사이에 port를 넣는것이 더 유연하다고 생각합니다.

카카오, 네이버 외 **새로운 provider를 추가 할 경우**

1. http interface를 추가,
2. locationProviderPort를 구현하여 provider의 응답을 LocationDto로 매핑
3. configuration에 locationProviderPort 구현체를 우선순위에 맞춰서 providers 리스트에 추가

추가하는 provider의 내용과 관련된 부분만 추가하여 변경할 수 있도록 하였습니다.

## 확장성

현재는 레디스가 하나가 standalone으로 구성되어 있습니다.

트래픽증가시 레디스 하나가 처리할수 있는 요청 (처리량, 네트워크 대역폭)에 제한이 있을 수 있습니다.

- 레디스를 클러스터로 구성하여 레디스가 네트워크 대역폭 사용량을 분산 하도록합니다. (스케일 아웃)
- 레디스 클러스터는 key를 crc16으로 해싱하여 슬롯을 정한후 해당 슬롯을 담당한 레디스 노드에 저장을 하므로 클라이언트 사이드 로드밸런싱을 통해 (타겟 노드를 클라이언트에서 라이브러리에서 정해주는것으로 알고 있습니다.) 확장성을 더 높일 수 있습니다.

## 가용성

트래픽의 증가에 따라서 우려 되는 부분은 레디스에서 장애시 spof(single-point-of-failure)로 인한 검색어 목록 기능의 가용성이 낮습니다.

- 레디스의 레플리케이션으로 secondary 노드 운용
- primary부터 읽도록 하고 장애시 secondary 레디스 노드를 읽도록 하는쪽으로 생각하고 있습니다.

장소정보를 제공하는 provider가 제공하는 api 상태를 다음과 같이 정의 할 수 있습니다.

1. 모두 정상
2. 일부 장애
3. 모두 장애

위와 같은 상황에서 **일부 장애**시에는 성공한 api 요청으로 응답을 하여 가용성을 지킬 수 있지만 **모두 장애**시에는 응답을 하지못해 가용성이 떨어질 수 있습니다.

- 이러한 경우 캐시로 api의 결과값을 저장해두고 **모두 장애**시 응답을하면 요청률을 기반하여 가용성을 계산한다면 레디스에 캐싱한 데이터 만큼 가용성을 지킬 수 있다고 생각합니다.

위와 같이 구성하면 가용성이 훨씬 좋아질거라 생각합니다.

# 기능 구현

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/3f753531-616d-4cdb-8efc-732ffe867bac/Untitled.png)

`imin.jeju.rank.depth, imin.jeju.rank.width`: Count-Min Sketch 사이즈

`imin.jeju.rank.minute-threshold`:  검색된 키워드 순위를 업데이트 할 단위시간

`imin.jeju.rank.cache-times`: 위의 단위시간과 곱하여 어느정도 시간 전까지의 검색된 키워드 횟수를 유지할지 정하는 횟수 (단위시간: 1, 횟수: 2인경우 2분간의 조회한 횟수만 사용)

## 장소 검색

### 요구사항

- 응답은 우선순위가 있다.
    1. 여러 장소제공 provider의 api를 통해 공통의 장소를 가지는 경우 우선순위를 가진다.
    2. provider 사이에 우선순위를 가진다.
    3. 단일 provider 내에서는 응답받은 결과의 순서대로 우선순위를 가진다.
- 각각 5개 최대 10개의 응답한다.
    - application properties를 통해 설정하도록 개발

### 전제조건

- 키워드의 카디널리티가 높다.
    - 검색시 키워드에 제한이 없어 검색어의 집합이 크다고 생각합니다.
- Provider가 N개인 경우 공통으로 나온 결과값이 우선순위를 가진다. (google등 장소검색 provider가 추가되는 경우 고려)

### 구현

위의 가용성 섹션에서 일부 설명이 되어있습니다.

**로컬 캐시**

검색이 많이 되는 검색어는 로컬캐시로 저장합니다.

특징적으로 많이 사용되는 데이터를 로컬캐시로 사용하면 deserialization 비용과 레디스까지의 네트워크 요청의 시간을 줄일 수 있어서 크게 효과적입니다.

## 검색 키워드 목록

### 요구사항

- 많이 요청되는 검색 키워드의 목록을 응답한다.

### 전제조건

- 많이 요청되는 검색 키워드의 분포는 그 때의 유행  등의 사건으로 인해 일부의 분포가 크다.
    - 카디널리티가 커서 일반적으로 특정 키워드의 분포만 클거라고 예상했다. (근처 카페, 식당, 편의점 등)
- “**사용자들이 많이 검색한 순서대로”**라는 부분에서 단순하게 **총** **서비스 타임동안의 검색된 횟수**로 볼 수도 있고 인기검색어 처럼 **일정 시간동안의 검색된 횟수**로 볼 수 있을 수 있다고 생각합니다.
    - 서비스 타임동안 검색된 횟수같은 케이스보다 **일정시간 동안의 검색된 횟수 순으로** 목록을 구성하는게 일반적인 서비스에서 맞는 스펙이라고 생각했습니다.

### 구현

**[Count-Min Sketch](https://www.notion.so/_-58449f1587a9401a94c97f0b2f612b5c)라는 확률적 자료구조를 채택하였습니다. (**통칭 CMS, RedisBloom 등 레디스 스택을 사용하면 간단하게 표현 가능 했을거라 생각합니다.**)**

1. 모든 키워드에 대해서 각각 1개의 number값을 가져서 카운팅하는 것보다 좋은 메모리 효율을 가집니다.
2. 적은 메모리 효율로 인하여 카디널리티가 높은 데이터인 경우 모두 저장하여야 하는 결정론적 자료구조(HashMap) 같은 경우는 메모리에 모두 저장하지 못한경우 디스크공간에도 저장이 되어야하므로 페이지 miss가 일어나는 경우 느릴 수 있습니다.
3. 분포가 특정 키워드가 클거라고 예상했습니다. (전제조건)
4. **검색된 횟수**가 작은오차가 아닌 정확한 숫자를 가질 이유가 없고 작은 오차를 가져서 얻는게 이점이 더 많다고 생각했습니다..

위와 같은 이유로 결정론적 자료구조보다 확률적 자료구조가 위의 Fit에 더 맞다고 생각했습니다.

**일정시간동안의 검색된 횟수 구현**

단순한 CMS의 경우 런타임 동안의 빈도를 체크하여 일정시간동안의 검색된 횟수를 구현하려면 슬라이딩 윈도우 기법을 넣어야 했습니다.

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/349c143e-aecd-4db0-b939-244e440564d9/Untitled.png)

위와 같이 단순히 하나의 CMS를 사용하는 경우에는 서비스 타임의 모든 요청의 키워드의 검색된 횟수가 누적됩니다.

- 구현이 간단하다.
- 서비스타임이 길어지는경우 지금까지 누적된 숫자가 overflow가 일어날 수 있다. (과거 유튜브  강남스타일 재생횟수 int 값 overflow)

**슬라이딩 윈도우 적용**

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/91398195-fc98-411e-ba38-04707f07100a/Untitled.png)

요청의 시간에따라 조횟수를 증가하는 CMS가 다릅니다.

- 위는 1분단위로 CMS를 갱신하고 이전 2개의 CMS를 사용하도록 하는경우를 그린겁니다. ⇒ 최근 2분까지의  검색된 횟수대로 정렬하여 응답
- 여기서 CMS 뿐 아니라 각 시간대에서의 검색된 키워드도 빈도 순으로 따로 저장합니다. (레디스 sorted set 사용)
    - 각 시간대에 많이 들어온 키워드 중 하나가 **캐시타임** 최대로 검색된 횟수인 키워드가 있을것이 자명하므로 각 시간대에서 그 시간대에 가장많이 들어온 것을 기록합니다.
    - 각 시간대에서 응답 해야하는 키워드 목록의 길이만큼 순서를 유지하여 저장합니다.

위 기능을 위한 **구성요소를 정리**하자면

- 각 단위시간대의 CMS (1분 마다 1개, 이전 CMS를 정리하도록하면 캐시타임 길이만큼의 CMS를 가짐)
- **캐시타임**동안 카운트를 가지고 있는 CMS ⇒ api 요청시 응답
- 각 단위시간대의 검색된 횟수순의 sorted set (최대 응답해야하는 키워드 목록의 길이만큼만 유지)
- **캐시타임**동안 검색된 횟수순의 sorted set (최대 응답해야하는 키워드 목록의 길이만큼만 유지) ⇒ api 요청시 응답

**각 단위시간에서 CMS를 업데이트 하는 과정**

1. **캐시타임**을 벗어난 CMS의 카운팅만큼 (**캐시타임동안 카운트를 가지고 있는 CMS**)에서 차감
2. 캐시타임동안의 (**각 단위시간대의 검색된 횟수순의 sorted set**)의 빈도수가 높은 키워드들의 검색된 횟수를 찾아서 (**캐시타임동안 검색된 횟수순의 sorted set**) 업데이트

현재는 빠른테스트를 위해서 1분단위로 갱신하며 2번, 현재로부터 2분전까지의 검색된 횟수로만 많이 검색된 횟수로 카운팅하도록 개발했습니다.

# 개선 가능성

## 레디스 Bloom 이용

현재 직접 구현을 하여 CMS 쪽 코드가 만족스러울정도로 깔끔한게 아니라고 느껴집니다.

## CMS 슬라이딩 윈도우 cron job 분리

api를 스케일 아웃하여 사용할때, cron job은 한곳에서 각 시간대별로 한번만 실행하면 되어서 분리해서 따로 돌도록 변경

## 검색된 횟수 증가시 메세지 큐 이용

검색된 횟수 증가시 사용하면 메세지를 발행하여 그 메세지로 다른곳에서 CMS의 카운트를 증가시키는 방법을 생각했습니다.



**이러한 방법을 사용하면 장점으로는**

- 메세지 큐에서 보존기간동안의(보통 1주일) 메세지를 저장하고 있으므로 중간에 유실이 있다고 하여도 다시 카운팅 할 수 있다. (fault tolerance)
    - 이러한 경우는 현재는 각 시간대에서 들어온 카운트만 저장하지만 스냅샷도 따로 저장하여 이벤트 소싱처럼 복구가 가능할 듯 합니다.

# Appendix

## 헥사고날  아키텍쳐

[헥사고날(Hexagonal) 아키텍처 in 메쉬코리아             :: MESH KOREA | VROONG 테크 블로그](https://mesh.dev/20210910-dev-notes-007-hexagonal-architecture/)

## Count-Min Sketch
[Redis Stack use cases](https://redis.io/docs/stack/use-cases/#count-min-sketch)

[Big Data with Sketchy Structures, Part 1 — the Count-Min Sketch](https://towardsdatascience.com/big-data-with-sketchy-structures-part-1-the-count-min-sketch-b73fb3a33e2a)

[확률적 자료구조를 이용한 추정 - 빈도(Frequency) 추정을 위한 Count-Min Sketch](https://d2.naver.com/helloworld/799782)
