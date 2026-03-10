# MazeGame 버그 수정 상세 보고서

## 수정 완료 목록

| 순위 | 버그 | 상태 | 수정 내용 |
|------|------|------|----------|
| **1** | 하드코딩된 파일 경로 | ✅ 완료 | `System.getProperty("user.dir")` 사용 |
| **2** | 배열 경계 검사 오류 | ✅ 완료 | `>` → `>=` 변경 |
| **3** | static 버퍼 (데이터 누적) | ✅ 완료 | `static` 제거 → 인스턴스 변수 |
| **4** | 파일 카운터 초기화 | ✅ 완료 | `file_count = 0` (game_save.txt부터) |
| **5** | 도착 메시지 중복 | ✅ 완료 | player1 도착 메시지 제거 |
| **6** | AI/게임 타이머 불일치 | ⏭️ 스킵 | 표시만 다름, 기능에는 문제 없음 |
| **7** | dispose 후 this 참조 | ✅ 완료 | `null` 전달로 수정 |

---

## 상세 수정 내용

### 1순위: 하드코딩된 파일 경로

#### 문제점
```java
// 수정 전 (MazeGame.java:33-34)
private final StringBuffer buffer = new StringBuffer();
private final String path = "/Users/user/Desktop/secu_result/secu_exten/src/secu/game_save/game_save";
```
- 윈도우 환경의 절대 경로가 하드코딩되어 있음
- macOS에서는 `/Users/user/...` 경로가完全不同하여 파일을 저장할 수 없음
- 다른 PC에서도 사용 불가
#### 수정 내용
```java
// 수정 후
private final StringBuffer buffer = new StringBuffer();
private final String basePath = System.getProperty("user.dir");
private final String path = basePath + "/secu_exten/src/secu/game_save/game_save";
```

#### 수정 이유
- `System.getProperty("user.dir")`는 현재 작업 디렉토리를 반환
- 플랫폼에 따라 자동으로 경로가 설정됨
- macOS/Windows/Linux 어디서든 동작

---

### 2순위: 배열 경계 검사 오류

#### 문제점
```java
// 수정 전 (MazeGame.java:424)
boolean canMove(int row, int col){
    if (row < 0 || row > maze.length || col < 0 || col > maze[0].length) {
        return false;
    }
```
- `>` (초과)를 사용하여 배열 크기보다 **1 큰 값**까지 허용함
- 예: maze.length = 10일 때, row = 10은 허용됨
- 하지만 배열 인덱스는 0~9까지이므로 **ArrayIndexOutOfBoundsException** 발생

#### 수정 내용
```java
// 수정 후
boolean canMove(int row, int col){
    if (row < 0 || row >= maze.length || col < 0 || col >= maze[0].length) {
        return false;
    }
```
- `>=` (이상/이상)를 사용하여 정확한 범위 검사

#### 수정 이유
- 배열 인덱스 범위는 0부터 `length-1`까지
- `>=` 로 수정해야 배열 범위 내의 값만 허용

---

### 3순위: static 버퍼 (데이터 누적)

#### 문제점
```java
// 수정 전 (MazeGame.java:47-48)
private static StringBuffer pr_buffer = new StringBuffer();
private static StringBuffer ai_buffer = new StringBuffer();
```
- `static` 변수: 클래스 전체에서 공유됨
- 게임을 여러 번 플레이하면 이전 게임의 데이터가 누적됨
- 리플레이 저장 시 잘못된 데이터가 기록됨

#### 수정 내용
```java
// 수정 후
private StringBuffer pr_buffer = new StringBuffer();
private StringBuffer ai_buffer = new StringBuffer();
```

#### 수정 이유
- 인스턴스 변수로 변경: 각 게임마다 새로운 버퍼 생성
- 게임 시작 시 빈 상태로 시작 → 리플레이 데이터 정확성 보장

---

### 4순위: 파일 카운터 초기화

#### 문제점
```java
// 수정 전
private int file_count = 2;
```
- 처음 게임부터 game_save2.txt부터 저장됨
- game_save.txt가 비어 있게 됨

#### 수정 내용
```java
// 수정 후
private int file_count = 0;
```
- game_save.txt → game_save1.txt → game_save2.txt 순서로 저장

#### 수정 이유
- 기존 파일 명명 규칙과 일치시킴

---

### 5순위: 도착 메시지 중복 표시

#### 문제점
```java
// 수정 전 (MazeGame.java:369-370)
if(maze[newRow][newCol] == 9){
    pr_buffer.append("e");
    player1.setArrived(true);
    player1.setFinishTime(gameSeconds);
    JOptionPane.showMessageDialog(this, "플레이어1 도착 시간: "+ gameSeconds);  // 중복!
    checkGameEnd();  // checkGameEnd() 내부에서도 메시지 표시
    return;
}
```
- player1 도착 시 메시지 표시
- `checkGameEnd()`에서도 최종 결과 메시지 표시
- **총 2번의 메시지 표시**

#### 수정 내용
```java
// 수정 후
if(maze[newRow][newCol] == 9){
    pr_buffer.append("e");
    player1.setArrived(true);
    player1.setFinishTime(gameSeconds);
    checkGameEnd();  //
    return;
}
```

#### 수정 최종 결과만 표시 이유
- 최종 결과는 `checkGameEnd()`에서 이미 표시하므로 중복 제거
- 사용자 경험 개선

---

### 6순위: AI/게임 타이머 불일치 (스킵)

#### 문제점
```java
// 실제 코드
int gameSeconds = 0;   // 게임 시작 후 경과 시간
int aiGameSeconds = 0; // AI 이동 횟수 (別도로 증가)
```
- `gameTimer`: 1초마다 gameSeconds++
- `aiTimer`: 1초마다 aiGameSeconds++

####现状
- 실제로는 같은 값이 표시되지만, 코드가 혼란스러움
- 기능적으로는 정상 작동하므로 수정 보류

---

### 7순위: dispose 후 this 참조

#### 문제점
```java
// 수정 전 (MazeGame.java:416-419)
this.dispose();
new EndScreen(player1.getFinishTime(),player2.getFinishTime());
JOptionPane.showMessageDialog(this, message);  // this는 이미 dispose됨
```
- `dispose()` 호출 후 JFrame이 소멸됨
- `this` 참조가 유효하지 않을 수 있음

#### 수정 내용
```java
// 수정 후
this.dispose();
JOptionPane.showMessageDialog(null, message);  // null: 독립 윈도우
new EndScreen(player1.getFinishTime(),player2.getFinishTime());
```

#### 수정 이유
- `null`을 전달하면 메시지가 독립 윈도우로 표시됨
- 이미 소멸된 Frame 참조 문제 해결

---

## 수정된 파일

| 파일 | 수정 라인 | 내용 |
|------|----------|------|
| MazeGame.java | 33-35 | 하드코딩 경로 → 플랫폼 독립적 |
| MazeGame.java | 47-48 | static 제거 |
| MazeGame.java | 51 | file_count = 0 |
| MazeGame.java | 365-371 | 중복 메시지 제거 |
| MazeGame.java | 416-419 | dispose 순서 변경 |
| MazeGame.java | 424-426 | 배열 경계 검사 수정 |

---

## 제외된 항목 (사용자 확인)
- R 키: player2 아이템 테스트용으로 의도적 ✅
- 트랩 색상: 플레이어/AI 배치 후 가려지므로 의도적 ✅
