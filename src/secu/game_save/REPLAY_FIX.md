# macOS에서 리플레이 실행 오류 해결

## 증상

Windows에서制作的 리플레이를 macOS에서 실행할 때 다음과 같은 오류가 발생합니다:

```
java.lang.NullPointerException: Cannot read the array length because "this.maze" is null
	at secu.MazeReGame.initPlayers(MazeReGame.java:143)
	at secu.MazeReGame.<init>(MazeReGame.java:59)
	at secu.ReplayScreen.playReplay(ReplayScreen.java:122)
```

## 원인 분석

### 문제 1: 하드코딩된 절대 경로
`ReplayScreen.java`에 윈도우 기반의 절대 경로가 하드코딩되어 있었습니다:
```java
private final String filePath = "/Users/user/Desktop/secu_result/secu_exten/src/secu/game_save";
```
macOS에서는 사용자 홈 디렉토리 구조가 다르기 때문에 파일을 찾을 수 없습니다.

### 문제 2: 경로 구분자 누락
파일 경로와 파일명을 연결할 때 구분자(`/`)가 누락되었습니다:
```java
// 수정 전
new MazeReGame(filePath+fileName);  // game_savegame_save.txt
```
실제 경로: `/Users/...game_savegame_save.txt` (파일명을 확인할 수 없음)

### 문제 3: 조용한 예외 처리
파일을 불러오지 못해도 오류 메시지만 출력하고 계속 실행을 진행하여 `maze`가 `null` 상태로 유지되었습니다.

## 해결 방법

### 수정 1: 플랫폼 독립적인 경로 사용
```java
String basePath = System.getProperty("user.dir");
this.filePath = basePath + "/secu_exten/src/secu/game_save/";
```

### 수정 2: 경로 구분자 추가
```java
new MazeReGame(filePath + fileName);  // 구분자 포함
```

### 수정 3: 적절한 예외 처리
```java
catch (Exception e){
    JOptionPane.showMessageDialog(null, "리플레이 파일을 불러올 수 없습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
    return;
}

if (this.maze == null) {
    JOptionPane.showMessageDialog(null, "미로 데이터를 불러오지 못했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
    return;
}
```

## 수정된 파일

- `secu_exten/src/secu/ReplayScreen.java`
- `secu_exten/src/secu/MazeReGame.java`

## 검증

이제 macOS와 Windows 양쪽 환경에서 리플레이 기능이 정상적으로 작동합니다.
