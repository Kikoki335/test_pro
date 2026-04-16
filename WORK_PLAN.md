# Sky Blaster — 개발 작업 계획 (Master Plan)

> **이 문서는 작업 재개용 마스터 플랜이다.** 컴퓨터를 껐다 켜거나 Claude 세션이 새로 시작돼도 이 문서만 읽고 다음 작업을 이어갈 수 있도록 작성되었다.

---

## 0. 이 문서를 처음 보는 사람을 위한 안내 (READ FIRST)

### 0.1 읽는 순서

1. **§1** — 무엇을 만드는지 (게임 개요, 더 자세한 시안은 `README.md`)
2. **§2** — **절대 어기지 말 것** (framework cutoff 규칙). 모든 작업의 전제.
3. **§3** — 어디에 무엇이 있는지 (디렉토리 구조)
4. **§9** — 지금 어디까지 했고, 다음에 뭘 할지
5. 다음 작업 sub-task 의 §6 항목 자세히 읽고 시작

### 0.2 한 줄 요약

E:/test 의 Sky Blaster (종스크롤 슈팅 × 로그라이크) 안드로이드 텀프로젝트를 `E:/SmartphoneGame/spgp_2026` (교수님 강의 framework) 의 주차별 진도에 맞춰 만든다. 그 주 cutoff 까지 framework 가 가르친 도구만 쓴다. 새 도구를 안 가르친 주차에는 후속 주차의 화면/UI 작업을 당겨와 채운다. 리소스(그림/사운드)는 7주차에 한꺼번에 처리하므로, 1~6주차는 도형/색 placeholder 로 동작 검증.

### 0.3 이 문서의 역할 분담

| 문서 | 역할 |
|---|---|
| `README.md` | 게임 사양, 시안, 원본 8주 일정 (게임 디자인 문서) |
| `WORK_PLAN.md` (이 문서) | **실제 작업 계획·진척·framework cutoff 규칙** (작업 문서) |
| `process.md` | (있다면) 과거 진행 로그 |
| `Homework.md` | spgp_2026 의 과제 자료 (참고용) |

---

## 1. 프로젝트 개요

### 1.1 무엇을 만드는가

- **이름**: Sky Blaster
- **장르**: 종스크롤 슈팅 × 로그라이크
- **플랫폼**: Android (Kotlin)
- **타겟 디바이스**: 가상 좌표계 900×1600 (세로)
- **핵심 메카닉**: 터치 드래그로 자유 이동, 자동 발사, 적 처치로 EXP 획득 → 레벨업 시 무기·스킬·능력치 3카드 중 선택, 1분마다 보스전 진입 선택, 매 판 다른 빌드 (로그라이크)

### 1.2 정량 목표 (`README.md` §2)

- Player 1, 일반 적 3종 (자폭/원거리/분열), 보스 1종
- 무기 3종 (Dual / Triple / Laser), 스킬 3종, 능력치 3종 (데미지/공속/치명) — 총 9~10 종 보상
- 배경 2종 (스크롤 배경 + 구름 parallax), 이펙트 10종+, BGM 2종

### 1.3 기간

8주, 2026-04-06 시작 ~ 2026-05-31 종료. 현재 3주차 진입 직전 (1주차 #1~#6 + 2주차 #1~#5 완료, 2026-05-01 기준).

---

## 2. 핵심 규칙 (절대 깨지 말 것)

### 2.1 framework cutoff

> **`E:/SmartphoneGame/spgp_2026` 의 커밋을 7일 단위로 끊어서 그 주의 framework 진도로 본다. cutoff 은 누적이다 — N주차 작업은 N주차 cutoff 까지의 모든 커밋(이전 주차 포함)을 자유롭게 활용할 수 있다.**

| 주차 | spgp_2026 cutoff (누적) | 그 주에 새로 도입된 framework |
|---|---|---|
| 1주차 | ~ 2026-04-10 | a2dg 거의 전부, `VertScrollBackground` (cutoff commit `9e232ab` on 4/9) |
| 2주차 | ~ 2026-04-17 | 구름(2번째 배경), `Scene.clipRect`, `screenOrientation`+`appCategory` |
| 3주차 | ~ 2026-04-24 | (없음 — 0건) |
| 4주차 | ~ 2026-05-01 | `SheetSprite`, `MapObject` 공통 부모, Registry 패턴 |
| 5주차 이후 | 동일하게 7일씩 늘려 잡음 — 그 시점에 다시 산정 | TBD |

예: 2주차 작업에서는 `VertScrollBackground` (1주차 도입) 도 당연히 쓰고, 거기에 구름 두 번째 레이어 (2주차 신규) 를 얹는다. 3주차 sub-task 도 1·2주차 framework 모두 사용 가능. §6 의 "주차별 framework 추가 항목" 표는 **그 주에 새로 추가된 것** 만 보여 주므로, 사용 가능한 도구 풀은 항상 그 주 cutoff 까지의 누적값이다.

### 2.2 작업 시 4대 원칙

1. **그 주 cutoff 까지의 spgp_2026 커밋(이전 주차 포함, 누적)만 본다.** 그 cutoff 이후의 framework 는 사용 금지. 작업 시작 전 `git log --until=<해당주차_cutoff+1일> ...` 로 그 시점까지 누적된 framework 도입 항목을 확인.
2. **framework 가 가르친 패턴/클래스/유틸을 활용하는 것이 우선.** DragonFlight (1~3주차 reference) / CookieRun (4주차~ reference) 의 같은 시점 코드를 보고 그대로 응용. 2주차에 1주차 도구를 다시 써도 OK (오히려 자연스러움).
3. **새로운 방식 도입 시 확실한 이유 필요** — §8 표에 사유 추가. 사유 예: 게임 사양상 불가피 (예: Sky Blaster Player 는 X+Y 자유 이동, DragonFlight 는 좌우만), 장르 차이.
4. **같은 주차 안에서 sub-task 분리 시에도 그 주 cutoff 까지의 누적 framework 만 사용.** 즉 1주차 sub-task 들은 모두 4/10 까지의 framework 안에서, 2주차 sub-task 들은 모두 4/17 까지의 framework 안에서 만든다.
5. **Scene 작업과 Character/Object 작업은 별도 sub-task 로 분리한다.** 한 sub-task = 한 commit = 한 가지 주제. 예: "배경 적용" 과 "Player 등장+이동" 은 두 sub-task. "보스 클래스 추가" 와 "보스 Scene 전환 흐름" 도 두 sub-task. 같은 commit 에 화면 구조 변경과 신규 게임오브젝트가 섞이면 git 히스토리·리뷰·롤백이 어려워짐. 분리 가능한지 애매할 때는 분리하는 쪽으로 기운다.

### 2.3 리소스 수집 미룸

1주차 README 에 "리소스 수집"이 명시돼 있지만 **7주차로 미룬다.** 1~6주차는 도형/색 placeholder 로 동작 검증. 7주차에 그래픽/사운드/이펙트를 한꺼번에 폴리싱.

### 2.4 당김/미룸 가능

framework 추가가 적은 주차나 효율상 좋을 때 후속 주차 작업을 당겨와도 된다. 그러나 §7 표에 이유 명시. 절대 §2.1 cutoff 를 어기지는 않는다.

### 2.5 placeholder 컨벤션

- **Player**: 파란 삼각 (`#3B82F6`), 한 변 약 80px
- **Bullet (Player)**: 노란 작은 사각 (`#FACC15`), 24×48px (가시성 위해 12×24→24×48 조정)
- **Enemy 자폭병**: 빨간 원 (`#EF4444`), 지름 70px
- **Enemy 원거리**: 주황 삼각 (`#F97316`), 한 변 80px (꼭짓점 아래)
- **Enemy 분열형**: 보라 사각 (`#A855F7`), 60×60px
- **EXP 구슬**: 초록 작은 원 (`#22C55E`), 지름 20px
- **배경**: 짙은 남색 단색 `#0B1B3A` (별 parallax 시인성 위해 그라데이션 X)
- **별 parallax**: 투명 배경 + 작은 흰 점들 (알파 100~220), 일부 후광/십자 빛줄기로 깊이감
- **HUD HP Gauge**: 빨강 → 초록 (남은 비율 따라)
- **HUD EXP Gauge**: 파랑

7주차에 위 색/도형을 실제 그래픽으로 한꺼번에 교체.

---

## 3. 디렉토리 구조

```
E:/test/                                 # 텀프로젝트 작업 디렉토리 (이 프로젝트)
├── README.md                            # 게임 사양 / 시안
├── WORK_PLAN.md                         # 이 문서 (작업 마스터 플랜)
├── process.md                           # 과거 진행 로그 (있을 수 있음)
├── 01_title.png ~ 06_result.png         # 화면 시안 이미지
├── settings.gradle.kts                  # rootProject.name = "test_pro", :app, :a2dg
├── build.gradle.kts                     # 최상위 build (alias plugins only)
├── gradle.properties / gradle/          # gradle 설정
├── local.properties                     # sdk.dir (Android SDK 경로) — git 무시
├── a2dg/                                # 라이브러리 모듈 (framework 사본, 패키지 kr.ac.tukorea.ge.spgp2026.a2dg)
│   ├── build.gradle.kts                 # android-library plugin
│   └── src/main/java/.../a2dg/
│       ├── activity/BaseGameActivity.kt # Activity ↔ GameView 연결, pause/resume frametime
│       ├── view/
│       │   ├── GameView.kt              # CustomView, doFrame(), Scene 호출, 디버그 grid/info/graph
│       │   ├── GameContext.kt           # frameTime, sceneStack, metrics, resources 묶음
│       │   └── GameMetrics.kt           # 가상 좌표계 900×1600, transform/inverseTransform
│       ├── scene/
│       │   ├── Scene.kt                 # 추상 Scene, world?, onTouchEvent, push/pop, onBackPressed
│       │   ├── World.kt                 # World<TLayer>, Layer enum 별 게임오브젝트 컨테이너 + recycleBin
│       │   └── SceneStack.kt            # push/pop/change
│       ├── objects/
│       │   ├── IGameObject.kt           # update / draw 인터페이스
│       │   ├── IRecyclable.kt           # onRecycle() — World.obtain<T>() 풀에 들어가기 전 정리
│       │   ├── IBoxCollidable.kt        # collisionRect: RectF + collidesWith(other)
│       │   ├── Sprite.kt                # bitmap + dstRect, public 함수, 비율 유지 center helper
│       │   ├── AnimSprite.kt            # 프레임 애니메이션
│       │   ├── JoyStick.kt              # 가상 조이스틱 (Sky Blaster 안 씀, framework 1주차 도구)
│       │   ├── ImageNumber.kt           # 폰트 시트 기반 숫자 (7주차에 사용 예정)
│       │   └── VertScrollBackground.kt  # 종스크롤 배경 (4/9 도입)
│       ├── util/
│       │   ├── Gauge.kt                 # 가로 막대 게이지 (HP/EXP)
│       │   └── LabelUtil.kt             # Paint 기반 텍스트 라벨
│       └── res/
│           ├── BitmapPool.kt
│           └── GameResources.kt
└── app/                                 # 앱 모듈 (Sky Blaster 본체, 패키지 com.example.smartphonetermproject)
    ├── build.gradle.kts                 # viewBinding + buildConfig 켬, :a2dg 의존
    └── src/main/
        ├── AndroidManifest.xml          # MainActivity (LAUNCHER) + SkyBlasterActivity
        ├── java/com/example/smartphonetermproject/
        │   ├── MainActivity.kt          # XML 시작화면, GAME START 클릭 → SkyBlasterActivity
        │   ├── SkyBlasterActivity.kt    # extends BaseGameActivity, debug 플래그, createRootScene → MainScene
        │   └── MainScene.kt             # Layer enum + World — 현재 빈 World
        └── res/
            ├── layout/activity_main.xml # 시작화면 (남색 배경 + Sky Blaster TextView + GAME START 버튼)
            ├── values/strings.xml       # app_name="Sky Blaster", start_sky_blaster="GAME START"
            ├── values/themes.xml
            ├── values-night/themes.xml
            ├── mipmap-*                 # 기본 아이콘
            └── xml/                     # backup_rules, data_extraction_rules
```

---

## 4. 빌드 / 실행 환경

### 4.1 빌드 명령

```powershell
# JAVA_HOME 이 설정 안 돼 있으면 (Windows, Android Studio 의 JBR 사용)
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"

# debug APK 빌드
& "E:\test\gradlew.bat" :app:assembleDebug --no-daemon

# 또는 bash 에서
cd /e/test && JAVA_HOME="C:/Program Files/Android/Android Studio/jbr" ./gradlew :app:assembleDebug
```

### 4.2 가상 좌표계

- 기본 900×1600 (`a2dg/.../view/GameMetrics.kt:8-9`). 세로형.
- 화면 비율 안 맞으면 letterbox 처리. 좌표는 항상 가상 좌표계 기준으로 작업.

### 4.3 디버그 플래그 (현재값)

`SkyBlasterActivity.kt`:
```kotlin
override val drawsDebugGrid = false                  // 빨간 테두리 + 회색 격자 (끔)
override val drawsDebugInfo = BuildConfig.DEBUG      // 좌상단 "0 [], FPS: 60.0" (debug 빌드에서 켬)
override val drawsFpsGraph = false                   // 마젠타 FPS 그래프 (끔)
```

성능 점검 필요할 때만 `drawsFpsGraph = BuildConfig.DEBUG` 로 잠시 켰다 끄기.

### 4.4 자동 진입 (없음)

DragonFlight 의 `MainActivity` 는 debug 빌드에서 1초 후 자동 진입했지만, **Sky Blaster 는 의도적으로 제거.** 무조건 GAME START 버튼 클릭으로만 진입. (사용자 요청, 시작 흐름 직접 검증용)

---

## 5. 참조 위치

### 5.1 framework 디렉토리

- **1주차 reference**: `E:/SmartphoneGame/spgp_2026/andprj/DragonFlight` (cutoff `9e232ab`, 4/9)
- **2주차 reference 스냅샷**: `E:/2_Project/andprj/DragonFlight` (cutoff `4347a8b`, 4/15. spgp_2026 의 2주차 스냅샷 — `git archive` 로 추출된 작업 트리. 자세한 내용은 `E:/2_Project/주차정보.md`)
- **3주차~ reference**: 해당 주차 진입 시 spgp_2026 의 cutoff commit 으로 보거나 그 시점 별도 스냅샷이 있다면 그것을 사용
- **4주차~ framework reference**: `E:/SmartphoneGame/spgp_2026/andprj/CookieRun` (또는 그 시점 스냅샷)
- **a2dg 사본 (이 프로젝트용)**: `E:/test/a2dg/src` — framework 와 같은 패키지 경로 `kr.ac.tukorea.ge.spgp2026.a2dg`. 새 framework 도입 시 a2dg 도 같이 갱신 (§11#10).

### 5.2 자주 쓰는 git 명령

```bash
cd E:/SmartphoneGame/spgp_2026

# 그 주 cutoff 까지 누적된 모든 commit (이전 주차 포함)
git log --until=2026-04-11 --pretty=format:"%h %ad %s" --date=short    # 1주차 (~4/10)
git log --until=2026-04-18 --pretty=format:"%h %ad %s" --date=short    # 2주차 (~4/17, 1주차 commit 도 포함됨)
git log --until=2026-04-25 --pretty=format:"%h %ad %s" --date=short    # 3주차 (~4/24)
git log --until=2026-05-02 --pretty=format:"%h %ad %s" --date=short    # 4주차 (~5/1)

# 그 주에 새로 추가된 commit 만 보고 싶으면 (--since/--until 둘 다)
git log --since=2026-04-11 --until=2026-04-18 --pretty=format:"%h %ad %s" --date=short    # 2주차 신규

# 그 시점 reference 파일 보기
git show 9e232ab:andprj/DragonFlight/app/src/main/java/kr/ac/tukorea/ge/spgp2026/dragonflight/MainScene.kt

# 특정 파일 history
git log --pretty=format:"%h %ad %s" --date=short -- andprj/DragonFlight/app/src/main/java/.../MainScene.kt
```

---

## 6. 주차별 framework 추가 항목 (그 주에 신규 도입된 것만)

> 표는 **그 주에 새로 추가된 framework 만** 나열한다. 실제로 N주차 작업에서 사용 가능한 도구 풀은 §2.1 의 누적 cutoff 까지 — 1주차 도구도 2주차에서 다시 쓰고, 1·2주차 도구를 3·4주차에서도 쓴다.

### 1주차 (~4/10) — BIG

a2dg 핵심 거의 전부:

| commit | 날짜 | 도구 |
|---|---|---|
| (~4/2) | 4/2 | `MainScene` enum Layer 패턴 (`4ee7be7`), JoyStick → Player 직접 처리 (`1c45154`), Enemy/Bullet/EnemyGenerator/CollisionChecker 기본 클래스들 (`86321b2`, `4e4bc48`, `f67c1e9`, `c5291c8`) |
| (~4/3) | 4/3 | `IBoxCollidable` + `collidesWith` (`8bde3e8`), World collision box debug draw (`fc58c0f`), `Gauge` (`15ed809`), Enemy life Gauge (`be491bf`), `LabelUtil` (`b1128b6`, `00e203a`), `ImageNumber` (`beaf932`), ScoreNumber → ImageNumber (`5994ce2`) |
| `4001b16` | 4/4 | World update/draw iterator-free |
| `336bfec`, `223da6d` | 4/4 | `IRecyclable` + World recycle bin |
| `3008ca4`, `120ff73` | 4/4 | Bullet/Enemy 재활용 패턴 |
| `aaccf28` | 4/4 | Activity pause/resume frametime 끊기 |
| `b0d963a` | 4/5 | Background Sprite 도입 |
| `a916361` | 4/9 | Sprite 비율 유지 center helper |
| `91f4fb0` | 4/9 | a2dg `VertScrollBackground` 공통 클래스 |
| `adf8a99` | 4/9 | VertScrollBackground 자동 스크롤 |
| `29c42a1`, `bad4f9a`, `9e232ab` | 4/9 | DragonFlight ForestBackground 적용 → MainScene 직접 사용 |

### 2주차 (~4/17, 누적) — SMALL (그 주 신규 3건)

| commit | 날짜 | 도구 |
|---|---|---|
| `5a2126b` | 4/13 | DragonFlight 구름 배경 레이어 추가 (두 번째 VertScrollBackground) |
| `f5b4c4f` | 4/13 | README 에 세로 스크롤 background 일반화 단계 반영 |
| `7867d49` | 4/13 | 게임 Activity `screenOrientation="nosensor"` + `appCategory="game"` |
| `3300abc` | 4/13 | a2dg `Scene.clipRect` 지원 |
| `10492d7` | 4/13 | DragonFlight MainScene clipRect 적용 |

### 3주차 (~4/24, 누적) — NONE (그 주 신규 framework 추가 0건, 1·2주차 도구는 모두 사용 가능)

이 주차에는 framework 가 새 도구를 추가하지 않았다. → 새 시스템 도입 근거 없음. 1주차에 깐 SceneStack 위에서 화면 (Title/Pause/Result) 만 추가하는 작업으로 채움 (7주차에서 당겨옴, §7).

### 4주차 (~5/1, 누적) — MEDIUM (CookieRun 으로 옮겨감)

framework 가 DragonFlight 종료, CookieRun (가로스크롤 러닝) 새 게임으로 이동. CookieRun 작업이지만 도구는 Sky Blaster 에 응용 가능:

| commit | 날짜 | 도구 |
|---|---|---|
| `f1ecc5e` ~ `e14f6fa` | 4/25 | CookieRun 프로젝트 셋업 (가로 좌표 1600×900) |
| `1fe8b97`, `39e53f5` | 4/25 | a2dg `HorzScrollBackground` (Sky Blaster 직접 사용 X — 종스크롤이라) |
| `884b231` | 4/25 | a2dg Sprite 함수 public 화 |
| `c59b15b` | 4/25 | Layer 를 Int 대신 enum 으로 (이미 1주차 DragonFlight 에서 도입돼 있던 패턴 재정리) |
| `e81b3e2`, `eab58bf` | 4/25 | AnimSprite gctx protected, 기반 정리 |
| `0880c18`, `f742515`, `0439834`, `64fcbb4` | 4/25 | **`SheetSprite`** — 상태별 프레임 시트 ⭐ |
| `f286fb5`, `e29f663` | 4/25 | Player 상태별 Sprite Sheet (RUN/JUMP) |
| `ddf9e9e`, `68c6e37` | 4/28 | **`MapObject` 공통 부모** — 다양한 spawned 객체 통합 ⭐ |
| `e11c251`, `48140b0` | 4/28 | MapObject/JellyItem 재활용 + Floor Factory |
| `75e3e48`, `5da3e08`, `1c44448` | 4/28 | MapLoader + World 주입 + 매 프레임 spawn |
| `2c289af`, `cadaa26`, `9ddc3eb` | 4/28 | 충돌 박스 inset 등 정리 |
| `b8493b2`, `30ae0dd`, `bccb9cc` | 4/28 | Player FALL/착지 (Sky Blaster 안 씀) |
| `ca4171b`, `363e746`, `9181006` | 4/28 | 특수 젤리 → Player 효과 (Magnification) |
| `327c170`, `e55bce8` | 4/28 | MapLoader 가 stage text 파일 로드 + stage 선택 (Sky Blaster 안 씀) |
| `11d0aa7` | 4/28 | MapLoader 진행률 Gauge |
| `8017a48` | 4/29 | **`MapObject` 생성 규칙을 Registry 로 분리** ⭐ |
| `850147d` | 4/29 | 기본 Obstacle stage 에서 생성 |

**Sky Blaster 로 가져올 핵심 (§7 4주차 sub-task 참조):**
- `SheetSprite` — Player/Enemy 상태 애니메이션 (단, 1주차에는 placeholder 라 미사용)
- `MapObject` 공통 부모 + Registry 패턴 — EXP 구슬 / 무기 / 보상 카드 종류 관리에 응용

### 5~8주차 — TBD

해당 주차 진입 시점에 spgp_2026 커밋 다시 끊어서 본 문서 §6, §7 갱신.

---

## 7. 주차별 sub-task 분리안

각 sub-task 는 1 commit (사용자의 텀프로젝트 repo 기준). 주당 3~7 commit 범위.

### 1주차 (~4/10) — 6 commits

#### [x] #1 — 타이틀 → GAME START → 빈 게임 화면 *(완료, 2026-05-01)*

- **활용 framework**: `BaseGameActivity` (4/4), `GameView`/`Scene`/`World<TLayer>` (~4/2), Layer enum (4/2), DragonFlight `MainActivity` 패턴
- **만든 것**:
  - `app/build.gradle.kts` — `viewBinding = true`, `buildConfig = true` 추가
  - `AndroidManifest.xml` — `SkyBlasterActivity` 등록 (orientation 잠금은 4/13 framework 라 미룸)
  - `SkyBlasterActivity.kt` — `BaseGameActivity` 상속, debug 플래그 주입, `createRootScene()` → `MainScene`
  - `MainScene.kt` — Layer enum + 빈 `World`. **(주의: 처음에는 6개 enum 을 미리 박았다가 #4 직후 framework incremental 패턴(§10.5, §11#12)에 맞춰 그 시점 클래스가 있는 것만 두는 형태로 수정. #1 시점에는 사실 enum 0개 또는 1개여야 함 — 향후 비슷한 패턴 만들 땐 §10.5 표 참고하여 incremental 로 추가.)**
  - `MainActivity.kt` — `ActivityMainBinding`, GAME START 버튼 클릭 시 `SkyBlasterActivity` Intent (자동 진입 없음)
  - `activity_main.xml` — 남색 배경, "Sky Blaster" TextView, GAME START Button
  - `strings.xml` — `app_name`, `start_sky_blaster`
- **기준 동작**: 앱 실행 시 타이틀 화면, GAME START 누르면 빈 게임 화면 (좌상단 FPS 숫자만), 뒤로가기로 타이틀 복귀
- **빌드 확인**: `gradlew :app:assembleDebug` 성공

#### [x] #2 — VertScrollBackground 종스크롤 배경 *(완료, 2026-05-01)*

- **활용 framework**:
  - `VertScrollBackground` (`a2dg/.../objects/VertScrollBackground.kt`, 4/9 도입 — `91f4fb0` 클래스 + `adf8a99` 자동 스크롤)
  - `Sprite` 비율 유지 center helper (4/9 `a916361`)
  - DragonFlight reference: `git show 9e232ab:andprj/DragonFlight/app/src/main/java/.../MainScene.kt` 의 `private val background = VertScrollBackground(gctx, R.mipmap.df_bg, BACKGROUND_SPEED)` 패턴
- **만든 것**:
  - `app/src/main/res/mipmap-xxxhdpi/sky_bg.png` (900×1600 짙은 남색 단색 `#0B1B3A`. 처음에는 별 점 placeholder + 그라데이션도 시도했으나, 2주차 #1 에서 별을 별도 parallax 레이어로 옮기고 그라데이션이 별의 시인성을 떨어뜨려 단색으로 정착 — 별이 주인공인 화면이라 배경은 캔버스 역할만)
  - `MainScene.kt` 에 `private val background = VertScrollBackground(gctx, R.mipmap.sky_bg, BACKGROUND_SPEED)` 추가, `BACKGROUND_SPEED = 80f`, `world.add(background, Layer.BACKGROUND)`
- **기준 동작**: 게임 화면 진입 시 배경이 위→아래로 자동 스크롤
- **placeholder 색**: §2.5 참조

#### [x] #3 — Player 클래스 + 터치 드래그 이동 *(완료, 2026-05-01)*

- **활용 framework**:
  - `Sprite` (`a2dg/.../objects/Sprite.kt`, public 함수, `syncDstRect` 패턴)
  - Player 가 직접 `onTouchEvent` 처리 패턴 (4/2 `1c45154`) — 단, DragonFlight 는 좌우만, Sky Blaster 는 X+Y
  - `gctx.metrics.fromScreen(...)` 으로 화면 좌표 → 가상 좌표 변환
  - DragonFlight reference: `git show 9e232ab:andprj/DragonFlight/app/src/main/java/.../Player.kt` (`targetX` + `coerceIn` 패턴)
- **만든 것**:
  - `app/src/main/res/mipmap-xxxhdpi/player_placeholder.png` (160×160 파란 삼각형 placeholder)
  - `app/.../Player.kt` — `Sprite(R.mipmap.player_placeholder)` 상속, 100×100, 시작 `(450, 1450)`. `targetX/Y` + `hypot` 거리 기반 SPEED 1500f 보간. `onTouchEvent(ACTION_DOWN/MOVE)` 에서 `metrics.fromScreen` → `targetX/Y` 갱신, 화면 경계 clamp.
  - `MainScene.kt` 에 `private val player = Player(gctx)` 추가, `world.add(player, Layer.PLAYER)`, `onTouchEvent(event)` 를 `player.onTouchEvent(event)` 로 위임
- **기준 동작**:
  - 화면 어디든 터치/드래그하면 Player 가 그 위치로 따라옴 (X+Y 자유 이동)
  - Player 는 가상 좌표계 경계 (50~850, 50~1550) 안에 머무름
- **placeholder 색**: §2.5 — 파란 삼각

#### [x] #4 — Player 자동 발사 + Bullet (Recyclable + ObjectPool) *(완료, 2026-05-01)*

- **활용 framework**:
  - `IRecyclable` (4/4 `336bfec`) + `World.obtain<T>(clazz)` 풀 (4/4 `223da6d`)
  - DragonFlight Bullet (4/4 `3008ca4` 재활용 패턴 적용 후) reference: `git show 9e232ab:andprj/DragonFlight/app/src/main/java/.../Bullet.kt` — `private constructor` + `companion get()` 로 풀 조회/생성 캡슐화
  - `gctx.scene as? MainScene` 으로 World 참조 획득 (DragonFlight Bullet 패턴)
- **만든 것**:
  - `app/src/main/res/mipmap-xxxhdpi/bullet_placeholder.png` (24×48 노란 사각 placeholder)
  - `app/.../Bullet.kt` — `Sprite(R.mipmap.bullet_placeholder) + IRecyclable`. `private constructor`, `companion Bullet.get(gctx, x, y)` 가 `scene.world.obtain(Bullet::class.java) ?: Bullet(gctx)` 후 `init(x, y)`. `update()` 에서 `y -= SPEED * frameTime`, `syncDstRect()`, 화면 위 벗어나면 (`y + height/2 < 0`) `scene.world.remove(this, MainScene.Layer.BULLET)`. `onRecycle()` 빈 구현 (1주차에는 정리할 상태 없음).
  - `Player.kt` 에 `fireCooldown` 추가. `update()` 마지막에 `fireBullet(gctx)` 호출. `fireBullet()` 에서 cooldown 차감, 0 이하면 `FIRE_INTERVAL = 0.3f` 로 리셋 후 `Bullet.get(gctx, x, y - PLAYER_HEIGHT/2 - BULLET_OFFSET)` → `scene.world.add(bullet, MainScene.Layer.BULLET)`. `BULLET_OFFSET = 8f` 로 Player 머리 위 살짝 띄움.
- **기준 동작**:
  - Player 가 0.3초 간격으로 위로 노란 Bullet 발사
  - 화면 밖 Bullet 은 self-remove + 재활용. debug 좌상단 layer counts 가 안정적 (Bullet 풀 누적되어 1개 풀에서 돌려쓰는 것을 확인 가능)
- **`IBoxCollidable` 미포함, `power` 미포함**: `IBoxCollidable` 은 다음 #5 (몬스터 소환 + Bullet 충돌 + Enemy HP gauge) 에서 Enemy 와 같이 추가. `power` 는 4주차 무기 시스템에서 도입 (그 전까지는 `Bullet.DAMAGE = 1` 상수). DragonFlight 4/9 Bullet 은 `power` 까지 포함되어 있지만, Sky Blaster 1주차에는 무기 종류가 1종이라 상수로 시작.

#### [x] #5 — Enemy 3종 + EnemyGenerator + Bullet↔Enemy 충돌 + Enemy HP Gauge (`CONTROLLER` layer) *(완료, 2026-05-01)*

- **활용 framework**:
  - DragonFlight `Enemy` (4/2 `4e4bc48` → 4/3 `8bde3e8` IBoxCollidable + `15ed809` Gauge + `be491bf` Enemy life Gauge → 4/4 `120ff73` IRecyclable) — `private constructor` + `companion get()` + `bitmap = gctx.res.getBitmap(...)` 재활용 시 swap, `companion object` 에 정적 `Gauge` 공유
  - DragonFlight `EnemyGenerator` (4/2 `f67c1e9`) — `IGameObject`, `update()` 에서 `enemyTime -= frameTime`, 0 이하면 spawn 후 `enemyTime = GEN_INTERVAL`
  - DragonFlight `CollisionChecker` (4/3 `c5291c8` 후 `IBoxCollidable` 적용형) — `forEachReversedAt(ENEMY) { forEachReversedAt(BULLET) { ... } }` 이중 reverse 순회로 self-remove 안전 보장
  - `IBoxCollidable` (4/3 `8bde3e8`) + `collidesWith` extension
  - `Gauge` (4/3 `15ed809`) — Enemy HP bar
  - `CONTROLLER` layer 에 EnemyGenerator + CollisionChecker 배치 (화면에 안 그려지지만 update 받음)
  - Layer enum 에 `ENEMY`, `CONTROLLER` 신규 추가 (incremental 원칙 §11#12)
- **만들 것**:
  - placeholder PNG 3장 (`app/src/main/res/mipmap-xxxhdpi/`):
    - `enemy_suicide.png` (80×80 캔버스 안 70×70 빨간 원, `#EF4444`)
    - `enemy_ranged.png` (80×80 주황 삼각형 꼭짓점 아래, `#F97316`)
    - `enemy_split.png` (70×70 캔버스 안 60×60 보라 사각, `#A855F7`)
  - `app/.../Enemy.kt` — `Sprite + IBoxCollidable + IRecyclable`. `enum Type(resId, width, height, hp, speed)` 로 3종 스탯 묶음 (SUICIDE 70×70/HP1/400f, RANGED 80×80/HP2/200f, SPLIT 60×60/HP3/300f). `private ctor` + `companion get(gctx, x, type)` 패턴, `init(x, type)` 가 비트맵·width·height·life·maxLife·speed 재초기화. `update()` 에서 `y += speed*frameTime`, 화면 아래로 완전히 빠지면 self-remove. `decreaseLife(damage)`, `val dead get() = life <= 0` 노출. `companion object` 에 정적 `Gauge` (lazy 초기화, 모든 Enemy 공유 — DragonFlight 패턴), `draw()` 에서 `super.draw()` 후 머리 위 또는 발 아래에 `gauge.draw(canvas, gaugeX, gaugeY, gaugeWidth, life/maxLife)`. 색은 fg=`Color.GREEN`, bg=`Color.argb(180,0,0,0)` 으로 placeholder 단순화. `collisionRect = RectF()` + `updateCollisionRect()` 에서 dstRect 그대로 사용 (placeholder 단계라 inset 없음 — §8.2 사유 추가)
  - `app/.../Bullet.kt` — `IBoxCollidable` 추가. `collisionRect: RectF get() = dstRect` 패턴 (DragonFlight Bullet 4/9 와 동일 — 별도 RectF 안 만들고 dstRect 직통)
  - `app/.../EnemyGenerator.kt` — `IGameObject`. `enemyTime` 카운트다운 → 0 이하면 `Enemy.Type.entries.random()` + `Random.nextFloat() * (width - 2*margin) + margin` 으로 X 결정 → `Enemy.get()` 후 `world.add(enemy, ENEMY)`. `GEN_INTERVAL = 1.0f`. `draw()` 빈 구현.
  - `app/.../CollisionChecker.kt` — `IGameObject`. `update()` 에서 `scene.world.forEachReversedAt(ENEMY) { enemy → forEachReversedAt(BULLET) { bullet → if (bullet.collidesWith(enemy)) { remove bullet, enemy.decreaseLife(Bullet.DAMAGE); if (enemy.dead) remove enemy } } }`. Player↔Enemy 충돌은 #6 에서 추가. `draw()` 빈 구현 (collisionRect 디버그 표시는 World.draw() 가 자동 처리, `a2dg/.../World.kt:184-198`).
  - `Bullet.kt` 에 `companion const val DAMAGE = 1` 추가 (4주차 무기 시스템에서 power 로 교체 예정).
  - `MainScene.kt` — Layer enum 에 `ENEMY, CONTROLLER` 추가, `enemyGenerator = EnemyGenerator(gctx)` + `collisionChecker = CollisionChecker(gctx)` 인스턴스 생성, 둘 다 `world.add(_, Layer.CONTROLLER)`.
- **기준 동작**:
  - 위에서 1초 간격으로 3종 중 랜덤 적이 내려옴
  - 화면 아래로 빠진 Enemy 는 self-remove + recycle bin → 재사용
  - 노란 Bullet 이 Enemy 에 닿으면 Bullet 사라지고 Enemy HP 1 깎임. Enemy 머리 위(또는 발 아래) HP gauge 가 줄어듦. HP 0 되면 Enemy 도 사라짐 + recycle (점수 가산은 #6 에서)
  - debug 좌상단 `[1, 1, N, M, 2]` 형태로 보임 (BG/Player/Bullet/Enemy/CONTROLLER). CONTROLLER 가 EnemyGenerator + CollisionChecker 둘이라 2.
  - debug 모드에서 IBoxCollidable 객체들의 collisionRect 가 자동으로 빨간 테두리로 표시됨 (`a2dg/.../World.kt:184`)
- **DragonFlight 4/9 와 다른 점 (§8.2)**:
  - `AnimSprite` 대신 `Sprite` — placeholder 가 단일 프레임이라 framesheet 불필요. 7주차 리소스 시 `AnimSprite`/`SheetSprite` 로 교체.
  - 1종(level 다양화) 대신 **Type enum 으로 3종 분리** — 사양상 자폭/원거리/분열이 분리된 종류이므로. 1주차에는 외형/스탯만 다르고 동작은 동일 (단순 하강), 5주차에 자폭 데미지·원거리 발사·분열 동작 추가
  - DragonFlight 의 wave 시스템(5마리/wave + 속도 점증) 대신 단순 1마리/1초 spawn — 1주차 분량 조정. wave 시스템은 8주차 밸런스 단계에서 검토
  - Bullet `power` 미포함 — `Bullet.DAMAGE = 1` 상수로 시작, 4주차에 power 도입
  - Enemy collisionRect inset 없음 — DragonFlight 는 11f inset 으로 시각보다 안쪽 충돌, Sky Blaster placeholder 는 단순 도형이라 그대로 dstRect 사용. 7주차 그래픽 교체 시 inset 재검토
  - Player↔Enemy 충돌 미포함 — #6 에서 분리 추가
  - 화면 아래로 빠진 Enemy 는 재활용

#### [x] #6 — Player↔Enemy 충돌 + HUD (Player HP Gauge + Score) *(완료, 2026-05-01)*

- **활용 framework**:
  - `IBoxCollidable` (4/3 8bde3e8) — Player 에 추가
  - 점수 표시: 일단 `LabelUtil` (4/3 b1128b6, 00e203a) 텍스트. 7주차에 `ImageNumber` 로 교체 예정
  - `Gauge` (4/3 15ed809) — Player HP 표시 (Enemy 와 별도 인스턴스, 색 빨강/회색)
- **만들 것**:
  - `Player` 가 `IBoxCollidable` 구현 — `collisionRect: RectF get() = dstRect`
  - `Player` 에 `life: Int = 5`, `decreaseLife()`, `dead` getter. `MainScene` 의 별도 HUD 객체에서 화면 하단에 HP gauge (빨강) 표시.
  - `app/.../CollisionChecker.kt` — Bullet↔Enemy 외부 루프 안에 또는 별도 루프로 PLAYER↔ENEMY 추가. 부딪히면 `player.decreaseLife(1)`, Enemy self-remove. (DragonFlight 패턴은 ENEMY 외부 루프 시작점에서 player 와 비교 후 충돌 시 enemy remove + return — 그대로 적용)
  - 점수: `MainScene` 에 `var score = 0` + `addScore(s)`. Enemy 처치 시 `Enemy.Type.score` 가산. 별도 `Score` 객체 (LabelUtil 사용) 를 `UI` layer 에 배치해 화면 상단 표시.
  - `Enemy.Type` 에 `score: Int` 컬럼 추가 (SUICIDE 10, RANGED 20, SPLIT 30).
  - Layer enum 에 `UI` 추가 (incremental — Score 객체 도입과 함께).
  - Player.life ≤ 0 이면 일단 Activity finish (3주차에 Result Scene 으로 교체 예정).
- **기준 동작**:
  - Player 가 Enemy 에 닿으면 HP 1 깎임, Enemy 사라짐
  - 하단 HP gauge 가 줄어들고, 0 되면 게임 종료 (앱 종료)
  - 상단에 점수 표시 — Enemy 처치 시 점수 가산 (10/20/30)
- **1주차 종료 후 게임 한 사이클**: 배경 흐름 + Player 자유 이동 + 자동 발사 + 적 spawn + Bullet↔Enemy 충돌 + Player↔Enemy 충돌 + 점수/HP 변동.

### 2주차 (~4/17, 누적) — 5 commits *(6주차 보스 Scene 진입 흐름 일부 당겨옴)*

#### [x] #1 — 별 parallax 레이어 *(완료, 2026-05-01)*

- **활용 framework**:
  - 두 번째 `VertScrollBackground` (4/13 `5a2126b`) — 같은 클래스, 다른 비트맵 + 다른 속도로 두 번째 인스턴스. DragonFlight 는 **구름**으로 적용했지만 Sky Blaster 는 우주 컨셉에 맞춰 **별**로 적용 (§8.2)
  - `STARS` layer 를 `ENEMY` 와 `CONTROLLER` 사이에 추가 — DragonFlight 4/13 enum 의 `CLOUD` 자리에 해당. 즉 **별이 Player/Bullet/Enemy 위에** 그려져 캐릭터들 위로 별빛이 흐르는 parallax 효과 (우주선이 빠르게 별빛 사이를 통과하는 느낌)
- **만든 것**:
  - `app/src/main/res/mipmap-xxxhdpi/sky_bg.png` 재생성 — 별 점 빼고 짙은 남색 단색 `#0B1B3A` 만 (1주차 #2 의 placeholder 별을 이 레이어로 옮김. 그라데이션도 시도했으나 별 parallax 시인성 때문에 단색으로 정착)
  - `app/src/main/res/mipmap-xxxhdpi/sky_star.png` (900×1200, 투명 배경 + 별 103개: 작은 별 80개(알파 100~220) + 중간 별 18개(후광 + 코어) + 큰 빛나는 별 5개(2단 후광 + 십자 빛줄기). edge 100px buffer 로 tileable, §11#13)
  - `app/src/main/res/mipmap-xxxhdpi/sky_clouds.png` 삭제 (잠깐 시도했던 구름은 별로 교체)
  - `MainScene.kt` — Layer enum 에 `STARS` 추가 (DragonFlight 의 `CLOUD` 자리), `stars = VertScrollBackground(gctx, R.mipmap.sky_stars, STARS_SPEED)` 인스턴스, `world.add(stars, Layer.STARS)`, `STARS_SPEED = 120f` (배경 80f 의 1.5배)
- **기준 동작**: 짙은 남색 그라데이션 배경(80f) 위로 별 텍스처(120f) 가 더 빠르게 흘러 내려옴. Player/Bullet/Enemy 위로 별빛이 지나감 (parallax)
- **DragonFlight 4/13 reference**: `git show 5a2126b:andprj/DragonFlight/app/src/main/java/.../MainScene.kt` (구름 추가). Sky Blaster 는 같은 패턴을 별로 적용.

#### [x] #2 — a2dg framework 갱신 (Scene.clipRect + GameMetrics.borderRect + GameView clip) *(완료, 2026-05-01)*

- **활용 framework** (출처: `E:/2_Project` — 2주차 cutoff `4347a8b`, 2026-04-15 스냅샷):
  - `Scene.clipsRect` 지원 (4/13 `3300abc`) — `Scene` 에 `open val clipsRect = false` 한 줄 추가
  - `GameMetrics.borderRect` (4/13 `3300abc` 의 일부) — 가상 좌표계 박스 RectF 를 metrics 에 모음
  - `GameView` 의 clipRect 적용 (4/13 `3300abc`) — Scene 그리기 직전 `if (topScene.clipsRect) canvas.clipRect(borderRect)`. 디버그 borderRect 도 `gctx.metrics.borderRect` 출처로 통일
  - `MainScene.clipsRect = true` (4/13 `10492d7`)
- **만든 것**:
  - `a2dg/.../scene/Scene.kt` — `open val clipsRect = false` 추가 + 주석
  - `a2dg/.../view/GameMetrics.kt` — `val borderRect = RectF(0f, 0f, DEFAULT_VIRTUAL_WIDTH, DEFAULT_VIRTUAL_HEIGHT)` 추가 + `onSize()` 에서 `borderRect.set(0f, 0f, width, height)` 갱신
  - `a2dg/.../view/GameView.kt` — `Scene.draw` 호출을 `?.let { topScene -> if (topScene.clipsRect) canvas.clipRect(...) }` 로 감쌈. 디버그용 borderRect lazy 삭제, `gctx.metrics.borderRect` 로 교체. 사용 안 하는 `RectF` import 제거
  - `MainScene.kt` — `override val clipsRect = true` 추가
- **기준 동작**: 화면 비율이 가상 좌표계 (900×1600) 와 안 맞아도 letterbox 영역으로 배경 비트맵이나 별 이미지가 새 나가지 않음. 디버그 grid/border 도 metrics 의 borderRect 한 곳에서 옴.
- **이 commit 의 범위**: framework 코드만. 비트맵 변경 없음.

#### [x] #3 — sky_star.png seamless 재생성 (clouds 패턴 적용) *(완료, 2026-05-01)*

- **활용 reference**: `E:/2_Project/.../res/mipmap-xxxhdpi/clouds.png` (900×600). 알파 분포 분석 결과 **상단 80px 완전 투명, 80~200px fade in (알파 12→95), 200~500px full(~95), 500~600px fade out** — 즉 **전체 50% 가 buffer/fade**. seamless 효과의 정체는 `VertScrollBackground` 코드가 아니라 **비트맵 디자인**.
- **만든 것**:
  - `sky_star.png` 재생성 (900×1200) — 2_Project 의 clouds 비율을 그대로 옮김:
    - **0~150px**: 완전 투명 (12.5% — buffer)
    - **150~300px**: fade in (12.5% — 별 알파 0→full 선형 감쇠)
    - **300~900px**: full (50% — 별 full alpha)
    - **900~1050px**: fade out (12.5% — 별 알파 full→0)
    - **1050~1200px**: 완전 투명 (12.5% — buffer)
  - 별 종류 3단계로 깊이감:
    - 작은 별 100개 (랜덤 위치, 알파 100~220, 반지름 1~2.5px)
    - 중간 별 22개 (반지름 2.5~4px, 후광 + 코어 2단)
    - 큰 빛나는 별 6개 (반지름 4~6px, 2단 후광 + 십자 빛줄기, full 영역에만 배치)
  - 별 위치별 알파 감쇠로 fade 영역 진입 시 자연스럽게 사라짐 → tile 경계가 안 보임
- **기준 동작**: 별 parallax 의 두 tile 이 만나는 경계 부분에 시각 요소가 없으므로 끊기는 줄이 안 보임. 화면을 통과하는 동안 별이 자연스럽게 등장 → 가운데 환하게 → 자연스럽게 사라짐.
- **§11#15 함정 추가** — `VertScrollBackground` 비트맵 design 가이드.

#### [x] #4 — AndroidManifest 정리 + HUD 자리 잡기 *(완료, 2026-05-01)*

- **활용 framework**: `screenOrientation="nosensor"` + `appCategory="game"` (4/13 `7867d49`)
- **만든 것**:
  - `AndroidManifest.xml` — `application` 에 `android:appCategory="game"`, `SkyBlasterActivity` 에 `android:screenOrientation="nosensor"` 추가. `appCategory` 는 API 26+ 라 minSdk 24 환경에선 lint 경고 가능하나 동작상 문제 없음.
  - `PlayerHpHud.kt` — 가운데 → **좌하단**으로 이동 (시안 02_normal_stage.png 기준). 색을 `Color.RED` → `Color.GREEN` 으로 변경 (시안의 full HP 상태). "HP" 텍스트 라벨도 게이지 위에 추가. gaugeWidth 을 화면 60% → 35% 로 축소 (좌하단 영역 적정 크기).
  - `BossTimerHud.kt` 신규 — 상단 중앙 둥근 사각형 박스 + 게임 경과 시간 카운트업 (00:00 부터 시작, mm:ss 포맷). 박스 = 어두운 반투명 fill + 주황 stroke, 텍스트 = 주황 굵은 글씨. 사양상 01:00 도달 시점이 보스 진입 가능 시점 (README §1) — 진입 선택 분기 UI 는 5주차 #4 에서 이 카운터 값을 읽어 처리.
  - `MainScene.kt` — `bossTimerHud = BossTimerHud(gctx)` 인스턴스, `world.add(bossTimerHud, Layer.UI)` 추가.
  - `R.mipmap.sky_stars` → `R.mipmap.sky_star` 로 코드 참조 변경 — 사용자가 별 에셋 파일명을 `sky_star.png` (s 없음) 로 정착하면서 코드 일관 맞춤.
- **기준 동작**:
  - 좌상단: `Score: N` 텍스트
  - 상단 중앙: 보스 진입 타이머 박스 + "1:00" placeholder
  - 좌하단: "HP" 라벨 + 초록 게이지 (남은 비율 따라 줄어듦)
  - 가로 회전 시 활성 자세 잠금 (nosensor)
- **§7 §2.3 정책 어긋남 인정**: 1~6주차 placeholder 약속에서 — 이번 주차에서 이미 진짜 에셋 PNG 들이 들어와 있음. §10.5 placeholder 컨벤션은 그대로 두되 §2.3 에 "별 / 캐릭터 에셋은 2주차에 사용자 결정으로 당겨옴" 메모는 추후 정리.

#### [x] #5 — MainScene → BossScene 전환 흐름 (placeholder) *(완료, 2026-05-01)*

> 6주차 #2 ("보스 Scene 진입 흐름") 의 핵심 — 시간 경과 시 Scene 전환 — 만 placeholder 형태로 당겨옴. 실제 보스 타일맵·패턴·HP·전투는 6주차 sub-task 에서 채움. 새 framework 도입 없이 1주차 도구(`Scene`, `SceneStack.change`) 만 사용.

- **활용 framework**:
  - `Scene` 추상 클래스 + `Scene.change()` (= `gctx.sceneStack.change(this)`) — 1주차 framework
  - `Scene.clipsRect` — 2주차 #2 framework
- **만든 것**:
  - `BossScene.kt` 신규 — `Scene(gctx)` 상속, `clipsRect = true`. `world` 없이 `draw()` 만 override (어두운 보라 배경 `#140020` + "BOSS STAGE" 큰 주황 텍스트 + "(boss content arrives in week 6)" 안내). update 는 base 의 `world?.update(gctx)` (null 이라 no-op) 그대로 사용.
  - `MainScene.kt` — `var elapsedSec = 0f` (private set, public read) + `private var bossEntered = false`. `override fun update(gctx)` 추가: `super.update(gctx)` 호출 후 `elapsedSec += frameTime`, `BOSS_ENTER_TIME (= 10f)` 도달 시 `BossScene(gctx).change()` 한 번 호출 (bossEntered 플래그로 중복 방지).
  - `BossTimerHud.kt` — 자체 `elapsedSec` 제거. `draw()` 에서 `(scene as MainScene).elapsedSec` 를 읽어 mm:ss 표시 (= "값은 Scene, 표시는 HUD" 패턴, ScoreLabel/score 와 동일).
- **기준 동작**:
  - 게임 시작 후 상단 타이머가 00:00 → 00:10 까지 카운트업
  - 10초 도달 시 화면이 어두운 보라 배경의 "BOSS STAGE" placeholder 로 즉시 전환
  - back 버튼: stack 크기 1 (MainScene 이 BossScene 으로 change 되어 stack 에 1개) → Scene.onBackPressed default 로 false 반환 → Activity 가 default back 동작 (앱 종료)
- **테스트 임시값**: `BOSS_ENTER_TIME = 10f`. 사양(README §1)은 60초. 6주차 #2 에서 60f 로 되돌리고 "보스 진입 선택" UI (즉시 진입 vs 더 기다리기) 분기 추가.

### 3주차 (~4/24, 누적) — 4 commits *(7주차 화면 작업 당겨옴)*

framework 추가 0건이므로 새 시스템 도입 X. 1주차에 깐 SceneStack 위에서 화면만 추가.

#### [ ] #1 — Title Scene 다듬기

- 옵션 A: 현재 XML `MainActivity` 유지하고 디자인만 정리 (배경 색조, 버튼 위치, 깜빡이는 별 효과 등)
- 옵션 B: XML 시작화면을 게임 내 `TitleScene` (Scene 상속) 으로 교체 — `MainActivity` 는 단순 컨테이너, `BaseGameActivity` 가 처음부터 `TitleScene` 을 root scene 으로 push, GAME START 클릭 시 `MainScene.change()`
- 권장: 옵션 A (XML 유지) — 옵션 B 는 4주차 이후 화면 전환이 많아질 때 검토. 옵션 A 가 framework 패턴 (DragonFlight 도 XML MainActivity)

#### [ ] #2 — Pause Scene (반투명 overlay)

- `PauseScene : Scene(gctx)` — `world` 없이 `update`/`draw` 직접 override. `draw` 에서 반투명 검정 사각 (`canvas.drawColor(0x80000000.toInt())`) 위에 Resume / Restart / Quit 버튼 (텍스트로) 그리기
- `MainScene.onBackPressed()` 에서 `PauseScene(gctx).push()` — Scene.kt 의 기본 구현은 pop 이므로 override 필요
- Pause Scene 이 active 일 때 MainScene 의 update 는 stack top 만 update 받으므로 자연히 멈춤

#### [ ] #3 — Result Scene 골격

- `ResultScene : Scene(gctx)` — GAME OVER / BOSS CLEAR 텍스트, 점수·플레이 시간 표시. Retry → MainScene change, Title → MainActivity 로 finish

#### [ ] #4 — Scene 전환 흐름 정리

- Player.life ≤ 0 → `gctx.sceneStack.change(ResultScene(gctx, isWin = false, score, playTime))`
- Pause Scene 의 Restart → `gctx.sceneStack.change(MainScene(gctx))` (현재 Pause 와 그 아래 MainScene 둘 다 교체)
- Pause Scene 의 Quit → 전부 pop 해서 Activity finish

### 4주차 (~5/1, 누적) — 6 commits *(5주차 일부 당김)*

#### [ ] #1 — EXP 구슬 클래스

- **활용**: `MapObject` 공통 부모 패턴 (4/28 ddf9e9e, 68c6e37) — Sky Blaster 식으로 응용. 또는 단순히 `IRecyclable` + 자동 흡수 로직만 구현해도 OK
- placeholder: 초록 작은 원
- 자동 흡수: Player 와의 거리 < 200px 이면 Player 쪽으로 lerp 이동, 충돌 (collisionRect) 시 흡수되어 사라짐 + Player.exp 증가

#### [ ] #2 — Player 레벨/EXP Gauge + 보스 진입 타이머 (5주차에서 당김)

- 상단 HUD 에 레벨 텍스트 + EXP gauge (파랑) + 보스 진입까지 타이머
- exp 가 maxExp 도달하면 다음 sub-task 로 트리거 (이번 sub-task 에서는 표시까지)

#### [ ] #3 — 레벨업 보상 카드 Scene

- `LevelUpScene : Scene(gctx)` — 반투명 overlay, 가운데 카드 3장. 카드 종류 = (Weapon, Skill, Stat) 중 무작위 3개
- `MainScene.update()` 에서 Player.exp ≥ maxExp 면 `LevelUpScene(gctx, [card1, card2, card3]).push()`
- 카드 클릭 시 보상 적용 후 pop

#### [ ] #4 — 무기 시스템 베이스 (Weapon Registry) + Dual Shot

- **활용**: `MapObject` Registry 패턴 (4/29 8017a48) 응용. `object WeaponRegistry { val all = listOf(DualShot, ...); fun random3() = ... }`
- 각 Weapon 은 `fire(player, world)` 함수로 Bullet 생성 패턴 다르게 (Dual = 2발, Triple = 3발, Laser = 긴 직선)
- 1주차 의 자동 발사를 `currentWeapon.fire(...)` 로 교체

#### [ ] #5 — Triple Shot + Laser

- WeaponRegistry 에 추가
- 보상 카드에서 선택 가능

#### [ ] #6 — 능력치 증가 (5주차에서 당김)

- Stat (데미지/공속/치명) 보상 카드에 추가. Player 멤버 `damageMul`, `fireRateMul`, `critRate`. Bullet 생성 시 / 데미지 계산 시 곱셈 적용

### 5주차 (~5/8, 누적) — 4 commits *(framework 진도 재확인 후 조정)*

#### [ ] #1 — 스킬 베이스 (터치 발동) + Skill Registry

#### [ ] #2 — 스킬 1: Shield Burst

#### [ ] #3 — 스킬 2: Homing Missile

#### [ ] #4 — 보스 진입 선택 Scene

### 6주차 (~5/15, 누적) — 7 commits

#### [ ] #1 — 보스 클래스 (Character: HP, 기본 위치, placeholder 도형)

#### [ ] #2 — 보스 Scene 진입 흐름 (MainScene → BossScene 전환, WARNING 연출)

#### [ ] #3 — 보스 Bezier 이동

#### [ ] #4 — 보스 패턴 1 (탄막 산개)

#### [ ] #5 — 보스 패턴 2 (돌진/레이저)

#### [ ] #6 — 보스 HP Gauge (HUD 상단)

#### [ ] #7 — 보스 클리어 → Result Scene 전환

### 7주차 (~5/22, 누적) — 7 commits *(리소스 수집 한꺼번에)*

#### [ ] #1 — 그래픽 리소스 수집 + import

- Player/Enemy 3종/Bullet/배경/UI 시트 모음
- 각 placeholder 색·모양에 맞춘 그림으로 교체
- `app/src/main/res/mipmap-xxxhdpi/` 에 배치

#### [ ] #2 — SheetSprite 로 Player 상태 애니메이션

#### [ ] #3 — SheetSprite 로 Enemy 종류별 애니메이션

#### [ ] #4 — Frame Animation 이펙트 (피격/폭발/레벨업)

#### [ ] #5 — ImageNumber 로 점수 표시 교체

- 현재 LabelUtil → number_24x32.png 같은 폰트 시트 + ImageNumber

#### [ ] #6 — BGM 2종

#### [ ] #7 — 효과음 (발사/피격/레벨업/클리어)

### 8주차 (~5/29, 누적) — 3 commits

#### [ ] #1 — 버그 라운드업

#### [ ] #2 — 밸런스 조정

#### [ ] #3 — 최종 정리

**총 42 commits** — 1주차 6 + 2주차 5 + 3주차 4 + 4주차 6 + 5주차 4 + 6주차 7 + 7주차 7 + 8주차 3. (Scene 작업과 Character 작업 분리 원칙 §2.2#5 적용)

---

## 8. 당김/미룸 항목 + 이유

### 8.1 작업 시점 이동

| 항목 | 원래 (README §4) | 옮긴 곳 | 이유 |
|---|---|---|---|
| 리소스 수집 (그래픽/사운드) | 1주차 | 7주차 | 1주차 framework 가 그림 없이 만들 만큼 풍부. placeholder 도형으로 동작 먼저 검증한 뒤 7주차에 이펙트·BGM 과 함께 폴리싱 묶기 |
| Player + 배경 + 자동 발사 | 2주차 | 1주차 | 1주차 framework 커밋이 모두 1주차 cutoff 안에 있음 (`VertScrollBackground` 4/9, Player 직접 터치 처리 4/2). 미루면 1주차 backlog 비어 framework 진도와 어긋남 |
| 적 3종 + 충돌 + Recycle | 3주차 | 1주차 | 위와 동일. `IRecyclable` 4/4, `IBoxCollidable` 4/3, EnemyGenerator 4/2 모두 4/10 cutoff 안 |
| 타이틀/Pause/Result 화면 | 7주차 | 3주차 | 3주차 framework 추가 0건. 새 시스템 도입 근거 없음 → 1주차에 깐 SceneStack 위에 화면만 올리는 작업으로 채움 |
| 능력치 증가 (데미지/공속/치명) | 5주차 | 4주차 | 단순 변수 곱셈. 4주차 Registry 패턴으로 보상 카드 종류만 추가하면 같은 framework 사용 |
| 보스 진입 타이머 UI 자리 | 5주차 | 4주차(자리), 5주차(동작), 6주차(실제 진입) | UI 자리는 4주차 HUD 작업과 같이, 진입 선택 동작은 5주차, 보스 자체는 6주차 |
| 보스 Scene 진입 흐름 (placeholder Scene 으로 전환) | 6주차 #2 | 2주차 #5 | 사용자가 보스 타일맵 에셋 받아오는 동안 화면 전환 흐름만 미리 만들어 두고 싶다는 결정. `Scene.change` 호출 패턴 자체는 1주차 framework 로 가능 (새 도구 도입 X). 보스 본체(타일맵/패턴/HP) 는 6주차에 그대로 남음 — 그때 `BossScene` placeholder 만 채우면 됨 |

### 8.2 DragonFlight 4/10 패턴과 의도적으로 다른 부분

같은 시점 framework 코드와 다르게 가는 모든 곳은 여기에 이유 명시.

| 항목 | DragonFlight 4/10 | Sky Blaster | 이유 |
|---|---|---|---|
| Player 이동축 | 좌/우만 (수평 슈팅 단계) | X+Y 자유 이동 | Sky Blaster 는 종스크롤. README §1 "캐릭터를 자유롭게 이동" 명시 |
| Player HP | 없음 (1격사) | HP 5 + 부딪히면 감소 | Sky Blaster 사양상 자폭병이 부딪혀 데미지 주는 구조. Enemy 의 Gauge 패턴을 Player 에 적용 |
| Enemy 종류 | 1종 (사망 시 자식 spawn) | 3종 (placeholder 모양·HP·속도만 차이) | 1주차 BIG 이 원래 2+3주차 흡수. 1주차에는 동작 차이 미루고 외형·스탯만 분리, 5주차에 자폭/원거리/분열 동작 추가 |
| 점수 표시 | `ImageNumber` + `number_24x32.png` | 일단 `LabelUtil` (텍스트) | 1주차 리소스 수집 제외. 폰트 시트 들어오면 7주차에 `ImageNumber` 로 교체 (7주차 sub-task #5) |
| 배경 | DragonFlight 이미지 (`df_bg.png`) | placeholder 색/단순 비트맵 | 같은 리소스 수집 제외 규칙 |
| 입력 시작점 | JoyStick → Player 직접 처리로 진화 | 처음부터 Player 직접 처리 | 4/10 시점 framework 가 이미 도달한 최종형(`9e232ab`)을 따라감. 중간 JoyStick 단계는 학습용이라 안 거침 |
| MainScene Layer 순서 | BACKGROUND, PLAYER, BULLET, ENEMY, CONTROLLER, UI | (1주차 #1 에서 동일하게 맞춤) | framework 와 동일 — Player 가 아래, Bullet/Enemy 가 위에 그려짐 |
| Bullet `power` 필드 | `init(...power)` 로 받아 `enemy.decreaseLife(bullet.power)` | `Bullet.DAMAGE = 1` 상수 | 1주차에는 무기 1종 (자동 직진 발사) 뿐이라 데미지 다양화 불필요. 4주차 무기 시스템(Dual/Triple/Laser) 도입 시 `power` 필드로 교체 |
| collisionRect inset | dstRect 기준 11f 절대값 inset (Enemy 만) | **모든 IBoxCollidable (Player/Bullet/Enemy) 에 width·height × 0.8 비율 inset** — 양쪽 10% 씩 안쪽으로 | 처음에는 placeholder 단순 도형이라 inset 없이 시작 → 진짜 캐릭터 PNG 도입(2주차 #4) 후 투명 여백/시각 외곽이 충돌에 잡혀 "스쳤는데 부딪힘" 느낌 발생 → 모든 객체 일률 0.8 비율로 적용 (절대값 11f 보다 비율이 더 robust — 사이즈 변경 시 자동 추종) |
| 충돌 시점 분리 | 4/9 시점에 Bullet↔Enemy + Player↔Enemy 동시 도입 | #5 (Bullet↔Enemy + Enemy gauge) 와 #6 (Player↔Enemy + HUD) 로 분리 | §2.2#5 분리 정책. "Enemy 자체의 self-contained 동작 (소환/체력/처치)" 과 "플레이어 피격/HUD" 를 다른 commit 으로 |
| 두 번째 parallax 레이어 (2주차 #1) | 구름 (`clouds.png`) | 별 (`sky_star.png`) — `STARS` layer | Sky Blaster 의 우주/밤하늘 컨셉. 별이 빠르게 흐르면 우주선이 별빛 사이를 통과하는 느낌이 살아남. framework 학습 목표(두 번째 `VertScrollBackground` 인스턴스)는 그대로 충족 |

---

## 9. 현재 진척

> **마지막 갱신**: 2026-05-01 (2주차 #5 완료 — MainScene → BossScene placeholder 전환. 사용자 보스 타일맵 수집 중이라 6주차 #2 의 진입 흐름만 먼저 당겨옴. BOSS_ENTER_TIME 은 테스트용 10f. 2주차 전체(#1~#5) 종료. 다음은 3주차 #1 — Title Scene 다듬기)

### 9.1 완료 / 진행 / 다음

| 상태 | sub-task | 비고 |
|---|---|---|
| ✅ 완료 | 1주차 #1 — 타이틀 → 빈 게임 화면 | 빌드 확인됨, GAME START 클릭으로만 진입 |
| ✅ 완료 | 1주차 #2 — VertScrollBackground 종스크롤 배경 | placeholder `sky_bg.png` (900×1600) |
| ✅ 완료 | 1주차 #3 — Player 클래스 + 터치 드래그 이동 | placeholder `player_placeholder.png`, X+Y 드래그 follow |
| ✅ 완료 | 1주차 #4 — Player 자동 발사 + Bullet (Recyclable + ObjectPool) | placeholder `bullet_placeholder.png`, `Bullet.get()` 풀 패턴, `FIRE_INTERVAL = 0.3f` |
| ✅ 완료 | 1주차 #5 — Enemy 3종 + EnemyGenerator + Bullet↔Enemy 충돌 + Enemy HP Gauge | placeholder PNG 3장, `Enemy.Type` enum, 1초 간격 spawn, `Bullet`/`Enemy` 가 `IBoxCollidable`, `CollisionChecker` 가 BULLET↔ENEMY 이중 reverse 순회, Enemy 머리 위 HP gauge (정적 공유) |
| ✅ 완료 | 1주차 #6 — Player↔Enemy 충돌 + HUD (Player HP Gauge + Score) | `Player` IBoxCollidable + `life=5`/`decreaseLife`/`dead`, `Enemy.Type` 에 `score` 컬럼(10/20/30), `MainScene.score`+`addScore`, `ScoreLabel`(LabelUtil) + `PlayerHpHud`(Gauge 빨강) UI layer, `CollisionChecker` 에 PLAYER↔ENEMY 추가 + dead 시 Activity.finish |
| ✅ 완료 | 2주차 #1 — 별 parallax 레이어 (구름 대신 별, §8.2) | `sky_bg.png` 별 빼고 단색 (`#0B1B3A`) + `sky_star.png` 신규, `STARS` layer (ENEMY 뒤·CONTROLLER 앞), `STARS_SPEED = 120f` |
| ✅ 완료 | 2주차 #2 — a2dg framework 갱신 (Scene.clipRect + GameMetrics.borderRect + GameView clip) | 2_Project 4/13 `3300abc`+`10492d7` 의 변경분 그대로 가져옴, MainScene `clipsRect = true` |
| ✅ 완료 | 2주차 #3 — sky_star.png seamless 재생성 (clouds 패턴 적용) | clouds.png 의 50% buffer/fade 비율을 별 비트맵에 옮김. 이후 사용자가 진짜 별 에셋으로 교체 |
| ✅ 완료 | 2주차 #4 — AndroidManifest 정리 + HUD 자리 잡기 | `appCategory="game"` + `screenOrientation="nosensor"`, `PlayerHpHud` 좌하단·초록 + HP 라벨, `BossTimerHud` 신규, code reference `sky_stars` → `sky_star` |
| ✅ 완료 | 2주차 #5 — MainScene → BossScene 전환 흐름 (placeholder, 6주차에서 당겨옴) | `BossScene` placeholder 신규, `MainScene.elapsedSec` + 10초 임시값 도달 시 `Scene.change()`, `BossTimerHud` 가 Scene 의 elapsedSec 읽기로 통일 |
| ▶ **다음** | **3주차 #1 — Title Scene 다듬기** | 3주차 framework 신규 도입 0건이라 7주차 화면 작업 당겨옴. §7 3주차 #1 참조 |
| ⏸ 대기 | 3주차 #2~#4, 그리고 4~8주차 전부 | |

### 9.2 빌드 / 동작 확인 상태

- `gradlew :app:assembleDebug` 성공 (마지막 검증: 2026-05-01)
- 디버그 옵션: `drawsDebugInfo` 만 켬 (좌상단 FPS 숫자), grid/graph 끔
- 자동 진입 제거됨 (의도적, §4.4)
- Layer enum 순서: framework (DragonFlight 4/9) 와 동일

### 9.3 작업 재개 절차 (NEXT TIME I OPEN THIS)

1. 이 문서 §0, §2 다시 읽기
2. §9.1 의 "다음" sub-task 확인
3. 그 sub-task 의 §7 항목 자세히 읽기
4. 작업 시작 전 framework cutoff 확인:
   ```bash
   cd E:/SmartphoneGame/spgp_2026
   git log --until=<해당주차_cutoff+1일> --pretty=format:"%h %ad %s" --date=short | head -30
   ```
5. 필요하면 reference 파일 보기:
   ```bash
   git show <cutoff_commit>:andprj/DragonFlight/app/src/main/java/.../<file>.kt
   ```
6. 작업 진행
7. 작업 완료 후:
   - 빌드 확인 (`gradlew :app:assembleDebug`)
   - **§7 의 해당 sub-task 체크박스 `[ ]` → `[x]` 로 갱신**
   - **§9.1 "다음" 항목 갱신**
   - **§9 "마지막 갱신" 날짜 갱신**
   - 새로운 framework 미사용 패턴 도입했으면 §8.2 표에 이유 추가
   - DragonFlight reference 와 의도적으로 다르게 간 부분 있으면 §8.2 추가

---

## 10. 게임 디자인 파라미터 (현재 가정값, 조정 가능)

### 10.1 가상 좌표계

- 900 (가로) × 1600 (세로). `GameMetrics.DEFAULT_VIRTUAL_WIDTH/HEIGHT` 기본값.
- Player 가상 위치 시작: `(450, 1300)` (화면 하단 중앙)

### 10.2 Player

- 크기: **140×140** (placeholder 80→100→140 단계로 키움. 진짜 캐릭터 PNG 로 교체 후 모바일 종스크롤 슈팅 표준 비율(화면 폭의 ~15%)에 맞춰 140 으로 정착, `Player.PLAYER_WIDTH/HEIGHT`)
- 시작 위치: `(metrics.width / 2, metrics.height - PLAYER_HEIGHT * 1.5f)` = `(450, 1390)` (화면 하단 중앙)
- 시작 HP: 5 (Enemy 1마리 부딪히면 1 감소) — 5번 sub-task 에서 도입
- 이동: framework lerp 패턴 (`targetX/Y`, `SPEED = 1100f` per second. 1500→1100 으로 줄임 — 캐릭터가 커진 만큼 회피 난이도 보정). 화면 경계 안에서 clamp.
- 발사 간격: 0.3초 (FIRE_INTERVAL) — 3번 sub-task 에서 도입
- Bullet 데미지: 1 (4주차 능력치 곱셈 적용)

### 10.3 Bullet

- 크기: **24×48** (`Bullet.BULLET_WIDTH/HEIGHT`, 가시성 위해 §2.5 의 12×24 에서 키움)
- 속도: 1500f/s (위 방향, `Bullet.SPEED`)
- 발사 위치: Player 머리 위 (`y - PLAYER_HEIGHT/2 - BULLET_OFFSET`, `BULLET_OFFSET = 8f`)
- 발사 간격: 0.3초 (`Player.FIRE_INTERVAL`)
- 화면 위로 벗어나면 (`y + height/2 < 0`) `world.remove(this, BULLET)` → recycle bin 으로
- 풀 패턴: `Bullet.get(gctx, x, y)` — `private constructor`, 외부에서 `Bullet(gctx)` 직접 생성 금지

### 10.4 Enemy 종류별

| 종류 | placeholder | width×height | HP | 속도 (아래 방향) | 점수 (#6 에서 부여) |
|---|---|---|---|---|---|
| `Type.SUICIDE` | 빨간 원 (`#EF4444`, 진짜 에셋 교체됨) | 95×95 | 1 | 280f/s | 10 |
| `Type.RANGED` | 주황 삼각 (`#F97316`, 진짜 에셋 교체됨) | 110×110 | 2 | 150f/s | 20 |
| `Type.SPLIT` | 보라 사각 (`#A855F7`, 진짜 에셋 교체됨) | 85×85 | 3 | 220f/s | 30 |

> 크기·속도는 한 번 일괄 조정됨 — 진짜 캐릭터 에셋으로 교체된 시점(2주차 #4 직후)에 시각적 박력을 위해 30~40% 키우고 같은 비율로 속도 25~30% 감소시켜 회피 난이도 유지.
> 충돌 박스(`collisionRect`) 는 모든 IBoxCollidable 에 width·height × 0.8 inset 적용 (양쪽 10% 씩 안쪽). 캐릭터 PNG 의 투명 여백 보정. (§8.2)

EnemyGenerator: spawn 간격 `GEN_INTERVAL = 1.0f` (단순 1마리/1초). 8주차 밸런스 단계에서 wave 시스템 검토.

### 10.5 Layer enum (incremental — 그 시점 클래스가 있는 것만)

> framework 가 가르치는 원칙: **Layer enum 항목은 그 클래스를 도입하는 commit 에서 같이 추가** — 미래 layer 를 미리 박아두지 않는다 (§11#12 참조).

현재 (1주차 #6 완료 시점):
```kotlin
enum class Layer { BACKGROUND, PLAYER, BULLET, ENEMY, CONTROLLER, UI }
```

언제 무엇이 추가되는지:
| sub-task | 추가되는 layer | 이유 |
|---|---|---|
| 1주차 #2 | `BACKGROUND` | `VertScrollBackground` 도입 |
| 1주차 #3 | `PLAYER` | `Player` 클래스 도입 |
| 1주차 #4 | `BULLET` | `Bullet` 클래스 도입 |
| 1주차 #5 | `ENEMY`, `CONTROLLER` | `Enemy`, `EnemyGenerator`, `CollisionChecker` 도입 |
| 1주차 #6 | `UI` | `ScoreLabel`/`PlayerHpHud` HUD 객체 도입 |
| 2주차 #1 | `STARS` | 별 parallax `VertScrollBackground` 도입 (ENEMY 뒤·CONTROLLER 앞 — 캐릭터 위로 별빛이 흐름. DragonFlight 4/13 의 `CLOUD` 자리, §8.2) |

DragonFlight 4/9 의 최종 enum 순서 `BACKGROUND, PLAYER, BULLET, ENEMY, CONTROLLER, UI` 를 누적적으로 도달하는 흐름과 일치.

---

## 11. 자주 빠지는 함정

1. **§2 cutoff 어기기** — `clipsRect`, 구름 배경, `screenOrientation` 등을 1주차에 쓰면 안 됨. 2주차 작업.
2. **placeholder 인데 그림 리소스 찾으려 함** — 1~6주차에는 도형/단색만. 그림은 7주차.
3. **자동 진입 다시 추가** — DragonFlight `MainActivity` 를 그대로 베끼면 자동 진입 들어옴. Sky Blaster 는 의도적으로 빼 둠.
4. **Bullet/Enemy 만들 때 풀 안 거침** — 항상 `world.obtain(X::class.java) ?: X(gctx)` 패턴. 안 그러면 GC 부담.
5. **자기 자신 remove 시 forward iteration 사용** — `World.update` 가 reverse 순회로 안전하게 만들었으니 평소대로 `world.remove(this, layer)` 호출하면 됨.
6. **새 layer 추가 시 enum 순서 바꿈** — 그리는 순서가 바뀌므로 의도적으로만. CLOUD 가 PLAYER 위인지 아래인지 등.
7. **`gctx.frameTime` 안 쓰고 상수 속도** — 프레임 변동 시 게임 속도 흔들림. 항상 `value * gctx.frameTime`.
8. **JAVA_HOME 미설정 시 빌드 실패** — `$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"` 먼저.
9. **§9 갱신 빠뜨림** — 작업 끝낸 뒤 체크박스/다음 작업 안 바꾸면 다음 세션이 무엇이 됐는지 모름. 항상 갱신.
10. **`a2dg` 와 framework 어긋남** — framework 가 a2dg 에 새 파일 추가하면 (예: 2주차 `Scene.clipRect` 지원) E:/test/a2dg 에도 같이 옮겨야 함.
11. **드래그가 화면 edge 로 빠지면 시스템 back gesture 트리거** — Android 10+ gesture nav 에뮬레이터에서 발생 가능. 환경 이슈일 가능성 큼 (framework 4/10 에도 별도 처리 없음). 같은 에뮬레이터에서 professor 의 4/10 체크아웃과 비교해 보면 환경 vs 코드 문제를 가릴 수 있음. 코드로 막으려면 `GameView.onSizeChanged` 에서 `setSystemGestureExclusionRects(listOf(Rect(0,0,w,h)))` (API 29+) 추가 — 단, framework 도 안 한 작업이라 §8.2 사유 명시 필요.
12. **Layer enum 을 미리 다 채워두기** — framework 패턴은 **클래스 도입 commit 에서 layer 도 같이 추가**하는 incremental. 미래 layer 를 미리 적어두면 debug 의 `[1,1,3,0,0,0]` 처럼 빈 자리가 보이고, 클래스 없이 layer 만 있는 어색한 중간 상태가 됨 (§10.5 표 참조). #1 에서 6개 enum 한꺼번에 만든 실수를 #4 직후 incremental 로 정정함.
13. **placeholder 배경 비트맵이 tileable 하지 않음** — `VertScrollBackground` 는 비트맵을 세로로 반복 타일링하므로 **비트맵 자체가 위/아래 edge 가 이어져야** 이음새가 안 보임. DragonFlight 의 `df_bg.png` (384×512) 는 손으로 그려 자연스럽게 이어지게 디자인. PowerShell 로 random-stars 를 뿌릴 때는 **상하 edge 부근(예: y < 60, y > tileHeight-60) 에는 별을 찍지 않음** → 이음새 영역이 순수 배경색이라 seamless. 처음 #2 에서 0~1600 전구간에 별을 찍어 이음새가 보였던 문제를 #4 직후 60px buffer 로 정정함.
14. **`Gauge.thickness` 를 픽셀 단위로 줌** — `Gauge.draw(canvas, x, y, scale, progress)` 는 내부에서 `canvas.scale(scale, scale)` 후 1.0 단위 좌표계에 선을 그린다. 따라서 **`thickness` 도 1.0 단위**다. 실제 화면 두께 ≈ `thickness × scale`. 14f 같은 픽셀값을 넘기면 (예: scale=540 일 때) 7560 픽셀 두께 선이 그려져 **화면 전체가 단색으로 덮임**. DragonFlight 의 `enemy_gauge` / `player_gauge` 가 모두 0.1f 인 것이 단서. PlayerHpHud 첫 구현에서 14f 로 줬다가 화면 전체가 빨강으로 변해 1주차 #6 직후 0.025f 로 정정.
15. **`VertScrollBackground` 비트맵의 위/아래 buffer 부족으로 tile 이음새가 보임** — `VertScrollBackground` 가 비트맵을 단순 반복 타일링하므로 **위/아래 edge 부근에 시각 요소가 있으면 두 tile 의 경계가 "줄"처럼 보인다**. 2_Project (2주차 스냅샷) 의 `clouds.png` (900×600) 를 분석해 보면 **상단 80px 완전 투명, 80~200px fade in (알파 12→95), 200~500px full, 500~600px fade out** — 즉 **전체 50% 가 buffer/fade 영역**이다. seamless 효과의 정체는 `VertScrollBackground` 코드가 아니라 비트맵 디자인. Sky Blaster 의 `sky_star.png` 도 같은 패턴(위/아래 150px 투명 + 150~300 / 900~1050 fade + 300~900 full) 으로 재생성하여 2주차 #2 완료. 처음에는 buffer 100px + hard cutoff 였는데 사용자가 시각적으로 끊기는 느낌을 지적해 정정.

---

## 12. 이 문서 갱신 규칙

- 새 framework cutoff 진입 시 §6 (해당 주차 항목) 채우기
- sub-task 완료 시 §7 체크박스 `[x]`, §9.1 "다음" 항목 갱신, §9 "마지막 갱신" 날짜 변경
- DragonFlight 와 다른 새 패턴 도입 시 §8.2 표에 추가
- 게임 디자인 파라미터 변경 시 §10 갱신
- 새 함정 발견 시 §11 추가
