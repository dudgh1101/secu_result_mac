# MazeGame UI 수정 및 문제점 분석

## 1. UI 레이아웃 수정 내용

### 수정 전 문제점
- 미로가 왼쪽에 치우쳐져 있고, 상태 UI가 오른쪽에 고정 위치(510px)에 그려짐
- 창 크기를 줄이면 **미로와 UI가 겹침**
- 하드코딩된 좌표값으로 반응형 지원 안됨

### 수정 후 구현 (MazeGame.java GamePanel 클래스)

```java
// 수정 전 (문제 코드)
int uiWidth = 220;
int mazeWidth = panelWidth - uiWidth;
int cellSize = Math.min(mazeWidth / maze[0].length, panelHeight / maze.length);
int offsetX = (mazeWidth - mazePixelWidth) / 2;
int offsetY = (panelHeight - mazePixelHeight) / 2;
int uiX = mazeWidth + 10;  // 고정 위치 → 창 크기 줄어들면 미로와 겹침!

// 수정 후 (해결 코드)
int cellSize = Math.min(
    panelWidth / (maze[0].length + 3),  // 여유 공간 고려
    panelHeight / (maze.length + 2)
);

int mazePixelWidth = cellSize * maze[0].length;
int mazePixelHeight = cellSize * maze.length;

// 미로를 중앙에 배치
int mazeStartX = (panelWidth - mazePixelWidth) / 2;
int mazeStartY = (panelHeight - mazePixelHeight) / 2 - 20;

// UI를 미로 아래에 배치 (겹침 방지)
int uiX = (panelWidth - uiWidth) / 2;
int uiY = mazeStartY + mazePixelHeight + 10;
```

### 핵심 변경점

| 항목 | 수정 전 | 수정 후 |
|------|---------|---------|
| 셀 크기 계산 | `mazeWidth / cols` | `panelWidth / (cols + 3)` - 여유 공간 포함 |
| 미로 X 위치 | `mazeWidth 기준` | `panelWidth 기준` 중앙 정렬 |
| 미로 Y 위치 | `panelHeight 기준` | `panelHeight 기준` + UI 공간 확보 |
| UI 위치 | 오른쪽 고정 (510px) | 미로 **아래** 중앙 배치 |
| 반응형 | 창 크기 변경 시 겹침 | 자동 계산, 겹침 없음 |

---

## 2. 발견된 버그 및 개선사항

### 🔴严重 버그 (수정 필요)

#### 버그 1: 하드코딩된 파일 경로
**위치**: `MazeGame.java:34`
```java
private final String path = "/Users/user/Desktop/secu_result/secu_exten/src/secu/game_save/game_save";
```
**문제**: 윈도우 경로 하드코딩 → macOS에서 파일 저장 안됨
**수정建议**: `System.getProperty("user.dir")` 사용

---

#### 버그 2: 배열 경계 검사 오류
**위치**: `MazeGame.java:424`
```java
if (row < 0 || row > maze.length || col < 0 || col > maze[0].length)
```
**문제**: `>` 대신 `>=` 사용해야 함 (maze.length = 10이면 마지막 인덱스는 9)
**영향**: 배열 범위 밖 접근 시 `ArrayIndexOutOfBoundsException` 발생 가능

---

#### 버그 3: R 키가 잘못된 플레이어에게 아이템 발동
**위치**: `MazeGame.java:336-339`
```java
case KeyEvent.VK_R:
    activateItem(player2);  // 버그: player2에게 아이템 사용
    trapTimer.stop();
    break;
```
**문제**: R 키를 누르면 내(player1) 아이템이 아닌 AI(player2) 아이템이 발동
**수정建议**: `activateItem(player1)`로 변경

---

#### 버그 4: 도착 메시지 중복 표시
**위치**: `MazeGame.java:369-370`
```java
JOptionPane.showMessageDialog(this, "플레이어1 도착 시간: "+ gameSeconds);
checkGameEnd();  // 여기서 한번 더 메시지 표시
```
**문제**: `checkGameEnd()` 내부에서도 메시지 표시 → 두 번 표시됨

---

#### 버그 5: 트랩 타이머 디버그 메시지 오류
**위치**: `MazeGame.java:151, 163`
```java
System.out.println("P1 트랩해제까지 남은 시간: " + player1.getItemTimeLeft());
```
**문제**: `getItemTimeLeft()` 대신 `getTrapcountdown()` 사용해야 함

---

#### 버그 6: static 버퍼로 인한 리플레이 데이터 누적
**위치**: `MazeGame.java:47-48`
```java
private static StringBuffer pr_buffer = new StringBuffer();
private static StringBuffer ai_buffer = new StringBuffer();
```
**문제**: static 변수이므로 게임을 여러 번 하면 이전 데이터가 누적됨
**영향**: 리플레이 데이터가 잘못 저장됨

---

#### 버그 7: 파일 카운터 초기화 안됨
**위치**: `MazeGame.java:35`
```java
private int file_count = 2;
```
**문제**: 인스턴스 변수이지만 게임 재시작 시 초기화 안됨 (새 게임마다 2부터 시작)

---

#### 버그 8: 트랩 색상과 시작점 색상 동일
**위치**: `MazeGame.java:519`
```java
case 5: g.setColor(Color.GREEN); break;  // 트랩
case 0: g.setColor(Color.GREEN); break;  // 시작점
```
**문제**: 둘 다 초록색으로 표시되어 구별 불가

---

#### 버그 9: AI와 게임 타이머 불일치
**위치**: `MazeGame.java:136`
```java
aiGameSeconds++;  // 별도로 증가
```
**문제**: 게임 시간과 AI 이동 시간이 다르게 카운트됨
**수정建议**: `gameSeconds`를 그대로 사용하거나, 두 값을 동기화

---

#### 버그 10: 게임 종료 후 EndScreen과 메시지 표시 순서
**위치**: `MazeGame.java:416-419`
```java
this.dispose();
new EndScreen(player1.getFinishTime(),player2.getFinishTime());
JOptionPane.showMessageDialog(this, message);  // this가 dispose됨
```
**문제**: `dispose()` 후 `this` 참조로 메시지 표시 → 경고 또는 오류 가능

---

### 🟡 개선建议 (기능 개선)

1. **키보드 포커스 문제**: 게임 창이 포커스를 잃으면 키 입력 안됨 → `setFocusable(true)` 또는 포커스 리스너 추가

2. **게임 일시정지 기능 없음**: Pause 기능 추가

3. **아이템/트랩 효과 시각적 표시 부족**: 현재 텍스트만 표시 → 플레이어 위에 이펙트 표시

4. ** thérapeut 없는 종료**: 게임 종료 조건 없음 (한 명만 도착해도 계속 진행)

5. **아이템/트랩 중복 획득 방지**: 같은 자리에 다시 들어가면 중복 효과 적용

---

## 3. 수정 우선순위

| 순위 | 항목 | 심각도 |
|------|------|--------|
| 1 | 하드코딩 경로 | 🔴 높음 |
| 2 | 배열 경계 검사 | 🔴 높음 |
| 3 | R 키 버그 | 🔴 높음 |
| 4 | static 버퍼 | 🔴 높음 |
| 5 | 트랩 색상 | 🟡 중간 |
| 6 | 도착 메시지 중복 | 🟡 중간 |
| 7 | 파일 카운터 | 🟢 낮음 |

---

## 4. 수정된 파일

- `secu_exten/src/secu/MazeGame.java` (GamePanel 클래스 전체 수정)
- `secu_exten/src/secu/ReplayScreen.java` (이전 수정)
- `secu_exten/src/secu/MazeReGame.java` (이전 수정)
