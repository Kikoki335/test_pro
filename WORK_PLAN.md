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

8주, 2026-04-06 시작 ~ 2026-05-31 종료. 현재 3주차 진입 직전 (1주차 #1~#5 + 2주차 #1~#7 완료, 2026-05-01 기준. plan 재배치로 monster/충돌+HUD 가 2주차로, BossScene 전환이 1주차로 옮겨감).

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

### 1주차 (~4/10) — 5 commits *(원래 6 commits 였으나 Enemy + Player 충돌/HUD 가 2주차로 옮겨가고, 시간 경과 시 보스 placeholder Scene 전환을 6주차에서 1주차로 당겨와 5 commits 가 됨, §8.1)*

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
  - `app/.../Player.kt` — `Sprite(R.mipmap.player_placeholder)` 상속. **정착값(§10.2) 처음부터 사용**: `PLAYER_WIDTH/HEIGHT = 140`, 시작 `(metrics.width/2, metrics.height - PLAYER_HEIGHT * 1.5f)`, `SPEED = 1100f`. `targetX/Y` + `hypot` 거리 기반 보간. `onTouchEvent(ACTION_DOWN/MOVE)` 에서 `metrics.fromScreen` → `targetX/Y` 갱신, 화면 경계 clamp. (이전 placeholder 시점에는 100×100 / SPEED 1500f 였으나 진짜 캐릭터 PNG 도입 후 일괄 조정됨 — 다음 세션은 정착값으로 시작)
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

#### [x] #5 — 시간 경과 시 보스 placeholder Scene 전환 *(완료, 2026-05-01. 원래 6주차 #2 였던 작업을 1주차로 당김 — §8.1)*

- **활용 framework**:
  - `Scene` 추상 클래스 + `Scene.change()` (= `gctx.sceneStack.change(this)`) — 1주차 framework
- **만들 것 (다음 세션이 따라갈 가이드)**:
  - `BossScene.kt` 신규 — 단순한 placeholder Scene. world 없이, draw 만 override 해서 임시 단색 / 텍스트만. 실제 보스 컨텐츠는 6주차 sub-task 에서 채움. (현 working tree 의 BossScene 은 2주차 #7 에서 노말맵화되므로 1주차 #5 시점의 BossScene 은 그보다 단순한 placeholder)
  - `MainScene` 에 `var elapsedSec = 0f` (private set, public read), `private var bossEntered`, `BOSS_ENTER_TIME = 10f` (테스트용 임시값, 실 사양은 60f).
  - `MainScene.update()` override: `super.update(gctx)` 후 elapsedSec 누적, 임계 도달 시 `BossScene(gctx).change()` 한 번만 호출.
- **기준 동작**: 게임 시작 후 elapsedSec 이 10초 도달하면 화면이 placeholder BossScene 으로 즉시 교체.
- **주의**: 1주차 #5 시점에는 monster / 점수 / HP 가 아직 없는 상태이므로 BossScene 전환만 동작 검증되는 단순 demo 단계. monster + 점수 + HP 는 2주차 #5/#6 에서 합류.

---

> **이하 §7 1주차 #5/#6 기존 항목 (Enemy 소환 + Player 충돌+HUD) 은 §7 2주차 #5/#6 으로 이동했음. 아래는 자료 보존을 위해 그대로 둠 — 실제 sub-task 분류는 2주차 #5/#6 으로 본다.**

#### [x] #5(구) — Enemy 3종 + EnemyGenerator + Bullet↔Enemy 충돌 + Enemy HP Gauge (`CONTROLLER` layer) → **2주차 #5 로 이동** *(완료, 2026-05-01)*

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

#### [x] #6(구) — Player↔Enemy 충돌 + HUD (Player HP Gauge + Score) → **2주차 #6 으로 이동** *(완료, 2026-05-01)*

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

### 2주차 (~4/17, 누적) — 7 commits *(원래 5 commits — 1주차 cutoff (4/10) 이후의 커밋이라 framework 측면에서 자연스러운 2주차 작업인 Enemy 소환과 Player 충돌+HUD 를 1주차에서 옮겨오고, BossScene 노말맵화 작업이 추가됨, §8.1)*

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

#### [x] #5(구) — MainScene → BossScene 전환 흐름 (placeholder) → **1주차 #5 로 이동** *(완료, 2026-05-01)*

> 새 plan 에서는 이 작업이 1주차 #5 자리. 자료 보존을 위해 본문은 그대로 유지.

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

#### [x] #5 — Enemy 3종 + EnemyGenerator + Bullet↔Enemy 충돌 + Enemy HP Gauge (`CONTROLLER` layer) *(완료, 2026-05-01. 원래 1주차 #5 였지만 framework cutoff 4/10 이후의 commit `bd9c1a7 몬스터 제작` (Apr 11) 시점이라 2주차로 분류, §8.1)*

- **활용 framework**: §7 의 (구 1주차 #5) 본문 그대로. 1주차 #5(구) 의 모든 활용 framework / 만들 것 / 기준 동작 / DragonFlight 와 다른 점이 그대로 유효.
- **만들 것 (다음 세션이 따라갈 가이드, 정착값 명시)**:
  - placeholder PNG 3장 (`enemy_suicide.png` 등) — 이미 진짜 캐릭터 에셋으로 교체됨.
  - `Enemy.kt`의 `Type` enum 에 **정착값 처음부터 사용**: `SUICIDE(95×95, hp=1, speed=280f, score=10)`, `RANGED(110×110, hp=2, speed=150f, score=20)`, `SPLIT(85×85, hp=3, speed=220f, score=30)`. (이전 placeholder 시점 70/80/60 + 400/200/300 은 진짜 PNG 도입 후 일괄 조정됨)
  - `EnemyGenerator.kt`: `GEN_INTERVAL = 1.0f`.
  - `Bullet.kt`/`Enemy.kt` 모두 `IBoxCollidable`. **collisionRect inset 0.8 처음부터 적용** — `width × COLLISION_INSET_RATIO / 2` 로 양쪽 10% 씩 안쪽. (§8.2, §10.4)
  - `Enemy.kt` 의 `companion object` 에 정적 `Gauge` (모든 Enemy 공유, lazy init), draw 에서 머리 위에 HP 비율 표시.
  - `CollisionChecker.kt` (CONTROLLER layer): BULLET↔ENEMY 만 (Player↔Enemy 는 #6).

#### [x] #6 — Player↔Enemy 충돌 + 점수 가산 *(완료, 2026-05-01. 원래 1주차 #6 였지만 #5 와 같은 이유로 2주차로, §8.1)*

- **활용 framework**: §7 의 (구 1주차 #6) 본문 그대로.
- **만들 것 (다음 세션이 따라갈 가이드, 정착값 명시)**:
  - `Player.kt` 가 `IBoxCollidable` — **collisionRect inset 0.8 처음부터 적용**.
  - `Player.kt`: `MAX_LIFE = 5`, `decreaseLife()`, `dead` getter.
  - `Enemy.Type` 의 `score` 컬럼 SUICIDE 10 / RANGED 20 / SPLIT 30 (#5 에서 이미 정착값으로 두면 이 작업에선 사용만).
  - `MainScene` 에 `var score = 0` + `addScore(amount)`.
  - `ScoreLabel.kt` (UI layer) — **`displayScore` 분리 + lerp 처음부터** (a2dg `ImageNumber.update` 의 lerp 패턴: `diff in -9..-1 -> -1`, `1..9 -> 1`, else `diff/10`). 텍스트는 `LabelUtil`. 7주차 #5 에서 `ImageNumber` + 폰트 시트로 교체하되 displayValue 패턴은 유지.
  - `PlayerHpHud.kt` (UI layer) — **시안(02_normal_stage.png) 에 맞게 처음부터 좌하단 + 초록 색**, `Gauge.thickness` 는 1.0 단위(예: 0.04f) 주의 (§11#14).
  - `CollisionChecker` 에 PLAYER↔ENEMY 추가, `player.dead` 시 `Activity.finish()` (3주차 #4 에서 ResultScene 으로 교체).

#### [x] #7 — BossScene = 노말맵 + 배경만 다르게 + 시작화면 박스 제거 *(완료, 2026-05-01)*

- **활용 framework**: 1주차 framework 만 (Scene 상속 + 생성자 파라미터).
- **만들 것 (다음 세션이 따라갈 가이드)**:
  - `MainScene` 을 **`open class`** 로 + 생성자 파라미터 두 개 추가 — `backgroundResId: Int = R.mipmap.sky_bg`, `isBossStage: Boolean = false`. `bossEntered` 초기값 = `isBossStage` 로 두면 보스 인스턴스는 시작부터 트리거 막힘.
  - `BossScene.kt` = **MainScene 을 상속한 한 줄 wrapper**: `class BossScene(gctx) : MainScene(gctx, R.mipmap.boss_bg, isBossStage = true)`. 게임 로직(Player/Bullet/Enemy/충돌/HUD/타이머) 다 그대로 쓰고 배경만 `boss_bg` 로 다름.
  - `activity_main.xml` — GAME START 버튼 감싸던 LinearLayout (반투명 박스) 제거하고 Button 만 RelativeLayout 직속에.
  - 배경 리소스 매핑: `sky_bg` = 노말맵, `boss_bg` = 보스맵, `title_bg` = 시작화면. (사용자가 받은 진짜 에셋 PNG 들이 이 자리에 들어감)
  - 6주차 sub-task 는 BossScene 에 보스 캐릭터/패턴/HP gauge/진입 연출 등을 override 로 추가.

### 3주차 (~4/24, 누적) — 5 commits *(Enemy 공격 행동 + VFX — 원래 들어 있던 화면 작업은 7~8주차로 분산 이동, §8.1. VFX 도 7주차에서 당겨옴 — 사용자가 에셋 미리 제공)*

> **commit 분할 단위 (다음 세션이 처음부터 구현 시 따를 것)**: ① SUICIDE (근거리 자폭) → commit, ② RANGED (원거리) → commit, ③ SPLIT (분열 + minion) → commit, ④ VFX (hit + die) → commit, ⑤ EnemyGenerator random 복원 + 밸런스 → commit. 사용자가 명시적으로 결정한 4(+1) commit 구조. 한 commit 안에 두 type 섞지 말 것 — 검증/되돌리기/리뷰가 어려워짐.

framework 추가 0건이므로 새 시스템 도입 X. 1·2주차 누적 도구 (`when type` 분기, `IRecyclable` ObjectPool, `CollisionChecker`, `Layer` enum 확장) 만으로 구현.

SUICIDE → RANGED → SPLIT 순서로 한 종류씩 도입하며 검증. 자폭/원거리/분열 동작이 미완 상태에서 다른 종류가 같이 spawn 되면 무엇이 어디서 깨지는지 분리하기 어렵기 때문에, 각 단계에서 `EnemyGenerator` 는 그 sub-task 에서 작업 중인 종류만 spawn 하도록 임시 수정하고, 마지막 sub-task 에서 random 복원.

#### [ ] #1 — SUICIDE 자폭

- `Enemy.update` 를 type 별 `when` 분기 베이스로 교체. RANGED/SPLIT 은 일단 기존 직선 하강 그대로 두고 SUICIDE 만 새 동작.
- SUICIDE 동작: 화면 위에서 직선 하강 → y 가 화면 높이 40% 지점 (`SUICIDE_LOCK_RATIO`) 도달 시 그 시점 Player 위치를 향해 lock-on → 그 방향으로 `SUICIDE_DIVE_MUL = 1.6f` 속도로 직진. **lock 시점 한 번만 읽어 방향 고정 — 이후 추적 X.** 자폭병의 "결심하면 끝까지 직진" 느낌. 빗나가면 화면 밖으로 빠져나가게 둠.
- `Enemy.Type.hitDamage` 필드 추가 — `SUICIDE = 2`, `RANGED = 1`, `SPLIT = 1`. `CollisionChecker.PLAYER_HIT_DAMAGE` 상수 제거하고 `enemy.hitDamage` 로 교체.
- 화면 밖 검사 확장 — dive 가 사선이라 좌·우로도 벗어날 수 있으므로 X 방향 검사 추가.
- recycle 시 dive 상태 reset — `init()` 에서 `diving = false`, dive 속도 0 으로.
- `EnemyGenerator` 임시 수정: `Type.SUICIDE` 만 spawn. 주석에 "RANGED 는 #2, SPLIT 은 #3, random 복원은 #4" 명시.

#### [x] #2 — RANGED 원거리 공격 + EnemyBullet *(검증 완료, 2026-05-01)*

- `EnemyBullet` 클래스 신규 — Bullet 의 ObjectPool 패턴 (`private constructor` + `IRecyclable` + `get(...)`) 그대로 복제. **자유 방향 (`vx, vy`) 인자 처음부터** (default = 직하 fallback). 사이즈 56×84, speed 700f/s, damage 1. 화면 밖 검사는 사방 (aimed 탄이 좌·우·위로도 빠질 수 있음).
- `MainScene.Layer` 에 `ENEMY_BULLET` 추가 (`ENEMY` 와 `STARS` 사이 — 적 위에 그려지되 별·UI 뒤).
- RANGED 동작 (정착): `APPROACHING → ATTACKING` 2단계만. 화면 높이 [`RANGED_STOP_RATIO_MIN = 0.22`, `MAX = 0.35`] 사이 spawn 시점 random 추출한 라인에 정지 (매 RANGED 가 다른 Y 라인). 정지 후 `RANGED_FIRE_INTERVAL = 1.2f` 간격으로 **Player 방향 단위벡터 aimed 발사** — Bullet 에 처치될 때까지 계속 (RETREATING 단계 / `RANGED_STAY_TIME` 없음).
- CollisionChecker 에 `ENEMY_BULLET ↔ PLAYER` 검사 추가 (ENEMY 루프와 분리해서 별도 forEach — 책임 분리).
- `EnemyGenerator` 임시: `Type.RANGED` 만 spawn 으로 검증 후 #5 에서 random 복원.

#### [x] #3 — SPLIT 분열 + SPLIT_MINION 자폭 *(검증 완료, 2026-05-01)*

- **SPLIT 본체 동작 = SUICIDE 와 동일** (`updateSuicide` 공유). 차이는 HP 3 (Bullet 3발) 으로 더 오래 살고, `startDying` 안에서 minion 2마리 분열 추가까지.
- `Enemy.Type.SPLIT_MINION` 추가 — `R.mipmap.enemy_split_minion`, 70×70, HP 1, speed 350, score 5, hitDamage 1. EnemyGenerator 의 random 풀에서는 제외 (#5 에서 `entries.filter { it != SPLIT_MINION }`).
- 분열은 `Enemy.startDying(scene)` 안에서 — SPLIT 인 경우 좌·우 30° 두 방향으로 SPLIT_MINION spawn (`MINION_ANGLES = listOf(-30f, 30f)`). startDying 은 dying 상태 진입 + die vfx 그리는 책임도 같이 가짐 (#4 참조).
- SPLIT_MINION 의 사선 이동은 SUICIDE 의 dive 필드 (`diveVx, diveVy, diving`) 재사용 — init 시점에 angle 받아 즉시 dive 모드.
- **SPLIT_MINION 공격 = lock-on 자폭** (SUICIDE 와 같은 패턴). 분열 직후 `MINION_LOCK_DELAY = 0.3f` 동안 사선 비행 → 그 시점 Player 위치로 lock-on (`lockDiveTarget` 재사용 — diveVx/Vy 갱신) → 이후 같은 dive 코드로 직진. 시작 위치 / 크기 / 속도 / 숫자 (70×70, 350f/s, 동시 2마리) 가 달라 회피 패턴이 SUICIDE 와 다름.
- `EnemyGenerator` 임시: `Type.SPLIT` 만 spawn 으로 검증.

#### [x] #4 — VFX 도입 (hit + die, "주체가 자기 draw 에서 직접 그림" 일관 패턴) *(검증 완료, 2026-05-01)*

- 자산 6종 — 처음부터 lowercase + underscore 이름으로 import (Android 리소스 규칙):
  `vfx_player_hit` / `vfx_enemy_hit` / `vfx_suicide_die` / `vfx_ranged_die` / `vfx_split_burst` / `vfx_minion_die`.
- **공통 원칙** = framework `laser_spark` (DragonFlight `Player.kt:38, 117~125`) 의 "발사 주체가 자기 draw 안에서 짧은 시간 직접 그리고 사라진다" 를 hit / die 양쪽에 일관 적용. **별도 Effect 클래스 / EFFECT layer / muzzle flash 단계 모두 채택 안 함** (시행착오 끝에 정착, §8.2).
- **hit vfx** — Bullet/EnemyBullet 에 `hitting` 상태 (`hitting`, `hitTime`, `hitRect`, `sharedHitBitmap` 캐시):
  - 명중 시 즉시 `world.remove` 하지 않고 `bullet.startHitting()` 호출 → `HIT_DURATION = 0.1f` 동안 본체 sprite 안 그리고 hit vfx 만 자기 위치에 그리다 self-remove.
  - `collisionRect` getter 의 hitting 분기 → 빈 사각형 반환 → 다른 enemy 와 또 부딪히지 않음.
  - 사이즈: Bullet 110, EnemyBullet 90.
  - CollisionChecker 의 명중 분기는 `bullet.startHitting()` 한 줄 (`world.remove` 호출 X).
- **die vfx** — Enemy 에 `dying` 상태 (`dying`, `dyingTime`, `dieRect`, `sharedDieBitmaps[type]` 캐시):
  - `enemy.life <= 0` 시 즉시 `world.remove` 하지 않고 `enemy.startDying(scene)` 호출 → dying 진입 + `collisionRect.setEmpty()` + (SPLIT 일 경우) minion 분열까지 한 곳에서.
  - dying 동안 `update` 는 dyingTime 카운트만 (이동/공격 skip), 0 이하 되면 self-remove.
  - dying 동안 `draw` 는 본체 sprite / HP gauge 안 그리고 die vfx 만 자기 위치에 `width × DIE_SIZE_MUL = 1.5` 로 그림.
  - `DIE_DURATION = 0.1f` (hit 와 같은 길이 — 깜빡 보이고 사라지는 정도. 본격 vfx 폴리싱은 7주차).
  - type 별 die 자산: SUICIDE → vfx_suicide_die, RANGED → vfx_ranged_die, SPLIT → vfx_split_burst (분열 vfx 가 die 대용 — 별도 die PNG 없음), SPLIT_MINION → vfx_minion_die.
- CollisionChecker 의 모든 enemy/bullet 사망 분기에서 `world.remove(...)` 호출 제거 — layer 제거 책임이 enemy/bullet self-remove 로 이동.

#### [ ] #5 — EnemyGenerator random 복원 + 밸런스

- `EnemyGenerator` 의 임시 single-type 로직 제거 → 다시 `Enemy.Type.entries.random()` (단, SPLIT_MINION 은 spawn 후보에서 제외 — `entries.filter { it != SPLIT_MINION }.random()`).
- 3종 동시 등장 시 spawn 빈도, HP, Player 데미지 시각적 검증하면서 1차 밸런싱 (수치 조정만, 동작 추가 X).
- 3주차 종료 빌드/플레이 확인.

### 4주차 (~5/1, 누적) — 6 commits *(5주차 일부 당김. 능력치 증가 sub-task 는 #3 보상 카드에 흡수, #5 무기 보상 + #6 풀 룰 보강 분리)*

> **commit 분할 단위 (사용자 결정)**: ① EXP 시스템 (ExpOrb + ExpLabel), ② 레벨업 구조 (LevelUpScene + push/pop, placeholder 카드 3장 모두 동일 텍스트, levelUp 만), ③ 보상 카드 (공격력 / 공속 / **치명타 확률** 3종 stat 카드 — 탄환수 stat 은 무기 시스템으로 이동, + 좌하단 디버그 HUD stat 수치 표시), ④ Weapon Registry + 4종 + 등급 (Default / Shotgun / Laser / Missile × RARE/EPIC), ⑤ 무기 보상 카드 (RewardCard + CardPool + LevelUpScene 통합, sprite + 등급 색), ⑥ 풀 룰 보강 (영웅 무기 받으면 같은 무기 희귀도 풀 제외). 한 commit 안에 둘 이상의 시스템 섞지 말 것.

#### [x] #1 — EXP 시스템 (ExpOrb + ExpLabel) *(코드 완료, 2026-05-01. 1 commit 단위)*

ExpOrb (구슬):
- **선택**: 단순 `IGameObject + IBoxCollidable + IRecyclable` (Sprite 도 상속 X) — 본격 부모 추출은 4주차 다른 sub-task 들이 다 깔린 뒤 같은 패턴이 여러 군데 보일 때 검토.
- placeholder: cyan 원 (`Color.rgb(34, 211, 238)`) + 밝은 청록 테두리. RADIUS = 18f. (7주차 그래픽 폴리싱에서 PNG 교체)
- 동작: **drop 즉시부터 매 프레임 Player 방향 추적** (`ATTRACT_SPEED = 800f/s`, 거리 제한 없음 — 탄환처럼 spawn 후 Player 향해 이동, Player 가 움직이면 방향 갱신되는 homing) → `collidesWith(player)` 시 `player.gainExp(VALUE = 1)` + self-remove.
- drop 시점: `Enemy.startDying(scene)` 안에서 SUICIDE / RANGED / **SPLIT_MINION** 처치 시 그 자리에 1개 spawn. **SPLIT 본체는 제외** — 본체는 분열만 시키고 EXP 책임은 분열된 minion 들에게 넘김 (사용자 결정 — 분열 몬스터의 보상은 자식을 처치해야 받는 구조).
- `MainScene.Layer.EXP_ORB` 추가 (ENEMY_BULLET 위, STARS 아래 — 적/탄과 함께 떠 보이게).

ExpLabel (HUD):
- 좌하단 PlayerHpHud 게이지 오른쪽에 `"EXP: N"` 텍스트. cyan 색 (orb 와 매칭) — 시각적 연결.
- ScoreLabel 패턴 (LabelUtil + 매 draw 마다 `scene.player.exp` 읽어 표시).
- 위치: HP 게이지 (`x=30 + width×0.35`) 끝에서 25px 띄움, baseline 은 게이지 baseline 과 동일.
- 4주차 #2 레벨업 시스템에서 maxExp 도달 시 `LevelUpScene.push()` 트리거 + level 표시 추가 예정. 이번 commit 에서는 누적 값까지만.

`Player` 에 `var exp: Int` + `gainExp(amount)` 추가.

#### [x] #2 — 레벨업 구조 (LevelUpScene + push/pop, placeholder 카드) *(코드 완료, 2026-05-01)*

- `Player` 에 `level: Int = 1`, `maxExp: Int = INITIAL_MAX_EXP`, `levelUp()` 함수 추가. `INITIAL_MAX_EXP = 5`, `MAX_EXP_GROWTH = 1.5f` (다음 단계 = floor(maxExp × 1.5), 단 최소 +1 보장). levelUp 은 exp -= maxExp, level += 1, maxExp 갱신.
- `ExpLabel` 텍스트 → `"Lv.N  EXP e/m"` 형식.
- `LevelUpScene : Scene(gctx)` — `MainScene` 참조를 받아 그 player 에 직접 보상 적용. 반투명 검정 overlay + "Level Up!" 타이틀 + 카드 3장 (가운데 가로 정렬, 230×320, 24px corner, cyan 테두리). **placeholder 단계 — 카드 3장 모두 같은 `"+ Lv N+1"` 텍스트, 어느 카드 클릭해도 `player.levelUp()` + `pop()` 만**. 카드별 보상 분기는 #3 에서.
- `MainScene.update()` 에서 `if (player.exp >= player.maxExp) { LevelUpScene(gctx, this).push(); return }` — SceneStack 이 stack top 만 update 하므로 push 즉시 게임 정지. 카드 선택 후 pop 되어 MainScene 으로 복귀.
- onTouchEvent: `ACTION_UP` + `metrics.fromScreen` 으로 카드 hit-test, 카드 밖 터치는 무시 (사용자가 카드 선택해야 게임 재개).
- `BOSS_ENTER_TIME` 10f → **60f** (README §1 사양 정착) 도 같이 처리.

#### [x] #3 — 보상 카드 (공격력 / 공격속도 / 탄환 개수 3종 stat) + 디버그 화면 stat 표시 *(코드 완료, 2026-05-01)*

- `Player` 에 stat 3종 추가:
  - `var attackMul: Float = 1f` — Bullet 데미지 = `(Bullet.DAMAGE × attackMul).toInt().coerceAtLeast(1)`
  - `var fireRateMul: Float = 1f` — 발사 간격 = `FIRE_INTERVAL / fireRateMul`
  - `var bulletCount: Int = 1` — `fireBullet` 에서 가로 spread spawn (BULLET_SPREAD = 50, 좌·우 대칭)
- `Bullet` 에 인스턴스 필드 `var power: Int` 추가 (DragonFlight Player.kt:181 의 power 패턴). `Bullet.get(...)` 에 `power: Int = DAMAGE` default 인자. CollisionChecker 가 `enemy.decreaseLife(bullet.power)` 사용. `Bullet.DAMAGE` 는 default 상수로 유지.
- `LevelUpScene` 카드 3장 분기 (사용자 결정 — 한 번에 체감되는 큰 폭):
  - 0: "공격력 x2" → `player.attackMul *= ATK_BOOST = 2.0f`
  - 1: "공속 +30%" → `player.fireRateMul *= RATE_BOOST = 1.3f`
  - 2: "탄환 +1" → `player.bulletCount += 1`
  - 모든 카드에서 `player.levelUp()` 같이.
- `DebugStatLabel` 신규 — framework `GameView.drawDebugInfo` 와 같은 textSize 40 / MONOSPACE / 흰색. **위치는 화면 하단** (좌측 x=30, y=height-14, PlayerHpHud / ExpLabel 줄 아래 한 줄) — 좌상단 fps/grid 영역과 분리되어 보상 정보임을 명확히. + a2dg `GameView.debugPaint` color BLUE → WHITE 1줄 수정 (§8.2) — 검정+파랑 배경 가시성.

#### [x] #4 — Weapon Registry + 4종 (Default / Shotgun / Laser / Missile) + 등급 시스템 *(코드 완료, 2026-05-01)*

- **활용**: CookieRun 4/29 `8017a48` MapObject Registry 패턴 응용 — `sealed class Weapon` + 4 object + `WeaponRegistry`.
- `enum class WeaponGrade { RARE, EPIC }` — 2단계만 (전설은 5주차 스킬 자리, 사용자 결정).
- `Weapon` 추상 멤버: `displayName`, `fireInterval`, `fire(player, scene, gctx, grade)`.
- 4종 동작 (등급 차이 정리 — **샷건/호밍 = 발수, 레이저 = 굵기**):
  - `DefaultWeapon` (직진, fireInterval 0.3) — 단순 직진 한 발. 카드 풀에 안 나옴.
  - `ShotgunWeapon` (부채꼴, 0.6) — **희귀 3발 ±15° / 영웅 5발 ±20°**. Bullet vx/vy 사선.
  - `LaserWeapon` (직선 관통, 1.2) — `LaserBeam` 별도. **희귀 폭 120 (반경 60) / 영웅 폭 200 (반경 100, 화면 22%)**. 둘 다 `LASER_LIFETIME = 1.0f` 통일, `LASER_TICK_INTERVAL = 0.1f`. 빔이 매 프레임 Player x 따라감. PNG (`weapon_laser`) 가 가로 stretch 되어 폭이 굵어져도 코어/글로우 라인은 자동 늘어남.
  - `MissileWeapon` (추적, 0.8) — `HomingMissile` 별도. **희귀 1발 / 영웅 2발 (좌·우 ±30 동시)**. vx/vy lerp 추적 (TURN_RATE = 6f). sprite 가 진행 방향으로 회전 (collisionRect 는 axis-aligned 유지 — 사용자 결정).
- `Player.currentWeapon: Weapon` + `weaponGrade: WeaponGrade`. 시작값 = `ShotgunWeapon` + `RARE` (사용자 결정 — 검증 단계 한 줄 변경으로 무기 교체).
- `Player.fireBullet` → `currentWeapon.fire(this, scene, gctx, weaponGrade)` 한 줄 위임.
- `Player.calculatePower(): Int` 추가 — `attackMul + critRate` 적용한 데미지 계산을 모든 Weapon 이 공유.
- `Bullet` 에 `vx, vy` 인자 추가 (default = 위쪽 직진), 화면 밖 검사를 사방으로 확장 — Shotgun 의 사선 발사 지원.
- `MainScene.Layer` 에 `LASER`, `MISSILE` 추가 (BULLET 위, ENEMY 아래).
- `CollisionChecker` — MISSILE ↔ ENEMY 검사 추가 (Bullet ↔ Enemy 와 같은 패턴, 충돌 시 `world.remove(missile)`). LASER 는 자체 처리.

#### [x] #5 — 무기 보상 카드 시스템 (RewardCard + CardPool + LevelUpScene 통합) *(코드 완료, 2026-05-01)*

- `RewardCard` sealed class — `AttackStatCard` / `FireRateStatCard` / `CritRateStatCard` (object 3종, stat 카드) + `WeaponCard(weapon, grade)` (class). `apply(player)` 가 보상 적용.
- `CardPool` — `statCards` 3종 (영구) + `weaponCards` 6종 (3 무기 × 2 등급). `pickThree()` 가 합친 풀에서 무작위 3장. `consume(card)` 는 WeaponCard 만 풀에서 제거 (stat 카드는 항상 풀 그대로).
- `Weapon.cardSpriteResId` 추가 (각 무기의 카드 sprite). `WeaponGrade.cardColor` 추가 (희귀 파랑 (96,165,250) / 영웅 보라 (168,85,247)).
- `LevelUpScene` — `cards: List<RewardCard>` 인자. WeaponCard 는 위쪽 60% sprite + 아래쪽 40% 텍스트 ("희귀 샷건" / "장착"), stat 카드는 텍스트 두 줄. stroke 색은 카드 종류별. onCardSelected → `card.apply(player)` + `cardPool.consume(card)` + `levelUp()` + pop.
- `MainScene.cardPool` 인스턴스 멤버. `update()` 의 LevelUpScene push 시 `cardPool.pickThree()` 결과 전달.
- 카드 풀 크기: 시작 9 (stat 3 + 무기 6) → 무기 다 받으면 3 (stat 만). 항상 ≥ 3 보장.

#### [ ] #6 — 무기 카드 풀 룰 보강 (영웅 받으면 같은 무기 희귀도 제외)

- 현재 `CardPool.consume(card)` 는 받은 (무기, 등급) 조합만 제거. **사용자 결정 — 영웅 무기 받으면 같은 무기의 희귀 등급도 같이 제거** (다운그레이드 의미 없음). 희귀 받으면 영웅은 그대로 (업그레이드 가능).
- 변경:
  ```kotlin
  fun consume(card: RewardCard) {
      if (card is WeaponCard) {
          weaponCards.remove(card)
          if (card.grade == WeaponGrade.EPIC) {
              weaponCards.removeAll { it.weapon == card.weapon && it.grade == WeaponGrade.RARE }
          }
      }
  }
  ```
- 풀 크기 변동: 영웅 받을 때마다 -2 (영웅 + 같은 무기 희귀). 희귀 받을 때마다 -1.

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

**총 43 commits** — 1주차 5 + 2주차 7 + 3주차 4 + 4주차 6 + 5주차 4 + 6주차 7 + 7주차 7 + 8주차 3. (plan 재배치 — §8.1: monster/충돌+HUD 1→2주차 이동, BossScene 전환 6→1주차 당김, BossScene 노말맵화 신규, +1) (Scene 작업과 Character 작업 분리 원칙 §2.2#5 적용)

---

## 8. 당김/미룸 항목 + 이유

### 8.1 작업 시점 이동

| 항목 | 원래 (README §4) | 옮긴 곳 | 이유 |
|---|---|---|---|
| 리소스 수집 (그래픽/사운드) | 1주차 | 7주차 | 1주차 framework 가 그림 없이 만들 만큼 풍부. placeholder 도형으로 동작 먼저 검증한 뒤 7주차에 이펙트·BGM 과 함께 폴리싱 묶기 |
| Player + 배경 + 자동 발사 | 2주차 | 1주차 | 1주차 framework 커밋이 모두 1주차 cutoff 안에 있음 (`VertScrollBackground` 4/9, Player 직접 터치 처리 4/2). 미루면 1주차 backlog 비어 framework 진도와 어긋남 |
| 적 3종 + 충돌 + Recycle | 3주차 | 1주차 | 위와 동일. `IRecyclable` 4/4, `IBoxCollidable` 4/3, EnemyGenerator 4/2 모두 4/10 cutoff 안 |
| 타이틀/Pause/Result 화면 | 7주차 → 3주차 (1차 시도) | **7~8주차로 환원** | 3주차 시작 시점 (2026-05-01) 에 사용자가 "framework 진도(원래 3주차에 몬스터 다양화) 따라 Enemy 공격 행동 먼저" 로 결정. Result Scene 은 6주차 #7 (보스 클리어 → Result) 에 자연 흡수, Pause/Title 다듬기는 7주차 폴리싱 단계로 묶음 |
| Enemy 공격 행동 (자폭/원거리/분열) | 5주차 | **3주차** | 3주차 framework 신규 0건이지만 1·2주차 도구 (`when type` 분기, ObjectPool, CollisionChecker, Layer 추가) 만으로 구현 가능. DragonFlight 도 4/9 시점에 Enemy 종 분기 패턴 도입. framework 진도와 plan 진도 모두 자연스러움 |
| VFX (muzzle flash + die effect) 도입 | 7주차 | **3주차 #4** | 사용자가 vfx 에셋(6종) 을 미리 만들어 와서 적용 요청. framework 의 laser_spark 패턴 (Player.kt 가 자기 draw 에서 발사 직후 SPARK_DURATION 0.1초만 직접 그림) 을 muzzle flash 뿐 아니라 die effect 에도 동일 적용 — Enemy 가 죽어도 즉시 layer 에서 빠지지 않고 dying 상태로 DIE_DURATION (0.4초) 동안 살아남아 자기 draw 에서 die vfx 만 그리다 self-remove. 별도 Effect 클래스/EFFECT layer 없이 framework 원칙 "주체가 자기 draw 에서 직접 그림" 일관 유지 |
| 능력치 증가 (데미지/공속/치명) | 5주차 | 4주차 | 단순 변수 곱셈. 4주차 Registry 패턴으로 보상 카드 종류만 추가하면 같은 framework 사용 |
| 보스 진입 타이머 UI 자리 | 5주차 | 4주차(자리), 5주차(동작), 6주차(실제 진입) | UI 자리는 4주차 HUD 작업과 같이, 진입 선택 동작은 5주차, 보스 자체는 6주차 |
| 보스 Scene 진입 흐름 (placeholder Scene 으로 전환) | 6주차 #2 | **1주차 #5** | 사용자가 보스 타일맵 에셋 받아오는 동안 화면 전환 흐름만 미리 만들어 두고 싶다는 결정. `Scene.change` 호출 패턴 자체는 1주차 framework 로 가능 (새 도구 도입 X). 보스 본체(타일맵/패턴/HP) 는 6주차에 그대로 남음 — 그때 `BossScene` placeholder 만 채우면 됨 |
| Enemy 3종 + EnemyGenerator + Bullet↔Enemy 충돌 + Enemy HP gauge | 1주차 #5 | **2주차 #5** | commit `bd9c1a7 몬스터 제작` (Apr 11) 이 framework 1주차 cutoff (4/10) 이후의 시점이라 framework 진도 측면에서 2주차 작업으로 분류하는 게 더 정확 |
| Player↔Enemy 충돌 + 점수 가산 + HP HUD | 1주차 #6 | **2주차 #6** | commit `15ce5ab 플레이어 체력, 충돌시 데미지` (Apr 11) — 위와 동일 사유 |
| BossScene = 노말맵 동일 + 배경만 다르게 (개념적 통합) | (원래 plan 에 없던 작업) | **2주차 #7** | 사용자가 보스 맵에서도 같은 게임 로직을 진행하고 싶다는 결정. MainScene 을 open + 파라미터화 하는 것으로 commit 1개 분량 |

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
| 모든 객체 사이즈 일괄 1.4배 (3주차 #4) | DragonFlight 기준 (Player 160, Enemy 작음) | Player 200, Enemy 130/155/120/70, Bullet 56×112, EnemyBullet 56×84 | 진짜 캐릭터/적 PNG 도입 후 시각 밀도 높아져 placeholder 사이즈로는 묻혀 보임. 사용자 가시성 요구 → 모두 같은 비율로 키워 상대 크기 / 회피 난이도 / 충돌 박스 (inset 0.8) 보존 |
| Player HP 다단계 (2주차 #6) | 1격사 (HP 없음) | `MAX_LIFE = 10` + `enemy.hitDamage` 별 차감 (SUICIDE 2, 나머지 1) | Sky Blaster 사양상 자폭병/원거리 탄/분열 minion 등 다양한 위협을 한 게임에서 부딪히게 하려면 다단계 HP 가 필수. 5 → 10 으로 올려 RANGED EnemyBullet 한 방의 비중을 10% 로 약화 |
| RANGED 정지 위치 Y 랜덤화 (3주차 #2) | (해당 없음 — DragonFlight 에 RANGED 패턴 없음) | spawn 시점 `[0.22, 0.35]` 범위 random 추출 | 매 RANGED 가 같은 Y 라인에 정지하면 화면 위쪽이 한 줄로 뭉쳐 시각적/회피 단조로움. 인스턴스별 random 으로 위·아래 흩어짐 |
| RANGED 의 EnemyBullet aimed 발사 (3주차 #2) | (해당 없음) | 발사 시점 Player 위치 단위벡터 × `EnemyBullet.SPEED` | Player 의 위치 회피가 의미 있는 패턴이 되도록 — 직하 발사면 수직 이동만 회피, aimed 면 좌우 이동도 회피로 카운트 |
| die effect 패턴 (3주차 #4) | 적이 죽으면 즉시 사라짐 (die effect 없음) | Enemy 에 `dying` 상태 — life≤0 시 즉시 `world.remove` 안 하고 DIE_DURATION 동안 살아남아 자기 draw 에서 die vfx 만 그리다 self-remove | 사용자가 die vfx 자산을 만들어 적용 요청. **별도 Effect 클래스 / EFFECT layer 시도 (option A)** 했다가 폐기 → laser_spark "주체가 자기 draw" 원칙을 die 에도 확장 (option B) 가 framework 와 일관. CollisionChecker 의 `world.remove(enemy)` 모두 제거, 자기 정리 책임이 dying enemy 본인 |
| hit vfx 패턴 (3주차 #4) | (해당 없음 — laser_spark 는 muzzle flash 였음) | Bullet/EnemyBullet 에 `hitting` 상태 — 명중 시 즉시 `world.remove` 안 하고 HIT_DURATION 동안 살아남아 hit vfx 만 자기 draw | 사용자 결정으로 muzzle flash 자산을 hit vfx 로 재배치 (option A) — 사격 게임 피드백은 명중 지점이 더 중요. dying enemy 와 정확히 같은 패턴이라 일관성 ↑ |
| BossScene placeholder 동작 (1주차 #5 + 3주차 #4) | (해당 없음 — DragonFlight 에 보스 X) | `MainScene.isBossStage: Boolean` (val). BossScene 진입 시 `if (!isBossStage) add(enemyGenerator)` 로 일반 적 spawn 정지 + BossTimerHud 가 mm:ss 대신 "BOSS STAGE" 텍스트 (LabelUtil 패턴 — HUD 가 매 draw 시점 scene 상태 보고 텍스트 결정) | 6주차 보스 본체는 미정 — 일단 placeholder Scene 전환만 완료. 보스 스테이지의 시각적 신호(라벨)와 행동 분기(spawn 정지) 만 도입 |
| `GameView.debugPaint` 색 (4주차 #3) | `Color.BLUE` | `Color.WHITE` | Sky Blaster 배경이 검정 + 파랑이라 BLUE 가 묻혀 잘 안 보임. framework a2dg 모듈을 직접 1줄 수정 — 단순 색 조정이라 framework 도구 도입 위반 X, 단 §11#10 (a2dg sync) 측면에서 reference 와 어긋나는 부분이라 의도적 차이로 기록 |

---

## 9. 현재 진척

> **마지막 갱신**: 2026-05-01 (4주차 #1~#5 검증 완료. EXP 시스템 / 레벨업 구조 / 보상 카드 (공격력 x2 / 공속 +30% / **치명타 +50%**, 탄환수 stat 은 무기 시스템으로 이동) + 디버그 HUD / Weapon Registry + 4종 + 등급 (레이저 = **굵기 차별** 희귀 60 / 영웅 120, 샷건/호밍 = 발수) / 무기 보상 카드 (CardPool, RewardCard sealed, 받은 (무기, 등급) 조합만 풀 제외). 시작 무기 = 영웅 레이저로 굵은 빔 검증 단계. 다음 #6 = CardPool.consume 의 EPIC 분기 추가 (영웅 받으면 같은 무기 희귀도 풀 제외). 코드 working tree 모두 unstaged.)

### 9.1 완료 / 진행 / 다음

| 상태 | sub-task | 비고 |
|---|---|---|
| ✅ 완료 | 1주차 #1 — 타이틀 → 빈 게임 화면 | 빌드 확인됨, GAME START 클릭으로만 진입 |
| ✅ 완료 | 1주차 #2 — VertScrollBackground 종스크롤 배경 | placeholder `sky_bg.png` (900×1600) |
| ✅ 완료 | 1주차 #3 — Player 클래스 + 터치 드래그 이동 | placeholder `player_placeholder.png`, X+Y 드래그 follow |
| ✅ 완료 | 1주차 #4 — Player 자동 발사 + Bullet (Recyclable + ObjectPool) | placeholder `bullet_placeholder.png`, `Bullet.get()` 풀 패턴, `FIRE_INTERVAL = 0.3f` |
| ✅ 완료 | 1주차 #5 — 시간 경과 시 보스 placeholder Scene 전환 (6주차 #2 에서 당겨옴) | `BossScene` placeholder (MainScene 한 줄 wrapper), `MainScene.elapsedSec` + 임계 도달 시 `Scene.change()`, `BOSS_ENTER_TIME = 10f` 테스트값. **`MainScene.isBossStage: Boolean` 을 val 로 노출 — BossScene 진입 시 (1) `if (!isBossStage) add(enemyGenerator, ...)` 로 일반 적 spawn 정지, (2) BossTimerHud 가 mm:ss 대신 "BOSS STAGE" 라벨 표시 (3주차 #4 후반에서 보강한 결정 — 다음 세션에서는 처음부터 함께 도입)** |
| ✅ 완료 | 2주차 #1 — 별 parallax 레이어 (구름 대신 별, §8.2) | `sky_bg.png` 별 빼고 단색 + `sky_star.png` 신규, `STARS` layer (ENEMY 뒤·CONTROLLER 앞), `STARS_SPEED = 100f` |
| ✅ 완료 | 2주차 #2 — a2dg framework 갱신 (Scene.clipRect + GameMetrics.borderRect + GameView clip) | 2_Project 4/13 `3300abc`+`10492d7` 의 변경분 그대로 가져옴, MainScene `clipsRect = true` |
| ✅ 완료 | 2주차 #3 — 캐릭터/별/배경 에셋 교체 (구 sky_star seamless) | sky_star/sky_bg/title_bg/boss_bg + Player/Bullet/Enemy/Enemy_split_minion/enemy_bullet PNG 들. clouds 패턴(seamless) 은 placeholder 단계에서 검토했고 사용자가 진짜 에셋 가져오면서 교체로 마무리 |
| ✅ 완료 | 2주차 #4 — AndroidManifest 정리 + HUD 자리 잡기 | `appCategory="game"` + `screenOrientation="nosensor"`, `PlayerHpHud` 좌하단·초록 + HP 라벨, `BossTimerHud` 신규 (Scene.elapsedSec 읽기), code reference `sky_stars` → `sky_star` |
| ✅ 완료 | 2주차 #5 — Enemy 3종 + EnemyGenerator + Bullet↔Enemy 충돌 + Enemy HP gauge (1주차 #5 였음, §8.1) | **정착값 (130/155/120 사이즈, 280/150/220 속도, 1/2/3 HP, 10/20/30 점수, 2/1/1 hitDamage) 처음부터**. Type enum + 정적 공유 Gauge, CollisionChecker 가 BULLET↔ENEMY |
| ✅ 완료 | 2주차 #6 — Player↔Enemy 충돌 + 점수 가산 (1주차 #6 였음, §8.1) | Player IBoxCollidable + **MAX_LIFE = 10 처음부터** (5 → 10 시행착오 생략, §8.2). ScoreLabel 의 displayScore lerp 처음부터, PlayerHpHud 좌하단/초록 처음부터, CollisionChecker 에 PLAYER↔ENEMY + Activity.finish |
| ✅ 완료 | 2주차 #7 — BossScene = 노말맵 + 배경만 다르게 + 시작화면 박스 제거 + collisionRect inset 0.8 | MainScene 을 open + 두 파라미터, BossScene 한 줄 wrapper, activity_main.xml 박스 제거, Player/Bullet/Enemy 모두 inset 0.8. (3주차 #4 후반 보강) BossScene 진입 시 EnemyGenerator 가 world 에 안 추가되어 일반 적 spawn 정지 + BossTimerHud 가 mm:ss 대신 "BOSS STAGE" 텍스트 표시 — `MainScene.isBossStage: Boolean` 을 val 로 노출, BossTimerHud 가 매 draw 마다 분기. framework `LabelUtil` 패턴 (HUD 가 매 draw 시점 scene 상태 보고 텍스트 결정) 그대로 |
| ✅ 디바이스 검증 OK | **3주차 #1 SUICIDE 자폭** | 자폭 lock-on + die vfx + Bullet hit vfx + 사이즈/HP buff 까지 통합 검증 |
| ✅ 디바이스 검증 OK | **3주차 #2 RANGED + EnemyBullet** | aimed 발사 + Y 랜덤화 + hit vfx 까지 모두 검증 |
| ✅ 디바이스 검증 OK | **3주차 #3 SPLIT 분열 + SPLIT_MINION 자폭** | SPLIT 본체 = SUICIDE 와 같은 lock-on 자폭 (코드 공유) + 죽을 때 minion 분열, minion 도 lock-on 자폭 |
| ✅ 디바이스 검증 OK | **3주차 #4 VFX (hit + die)** | dying / hitting 상태 패턴 일관 적용. 별도 Effect 클래스 / EFFECT layer / muzzle flash 단계 모두 없음 |
| ✅ 보강 완료 | **(1주차 #5) BossScene 진입 시 spawn 정지 + "BOSS STAGE" 라벨** | `isBossStage` val + 조건부 add + BossTimerHud 텍스트 분기 |
| ✅ random 복원 완료 (밸런싱 후순위) | **3주차 #5 EnemyGenerator random 복원** | `SPAWNABLE_TYPES = Enemy.Type.entries.filter { it != SPLIT_MINION }` 캐시 + `.random()`. 수치 밸런싱은 7~8주차 polish 단계로 미룸 |
| ✅ 디바이스 검증 OK | **4주차 #1 EXP 시스템 (ExpOrb + ExpLabel)** | ExpOrb (cyan 원, drop 즉시 Player homing 800f/s) + ExpLabel (좌하단 HP 게이지 옆 cyan "Lv.N EXP e/m") |
| ✅ 디바이스 검증 OK | **4주차 #2 레벨업 구조 (LevelUpScene + placeholder 카드)** | level/maxExp/levelUp 구조. BOSS_ENTER_TIME 60f. |
| ✅ 검증 OK | **4주차 #3 보상 카드 (공격력 / 공속 / 치명타) + 디버그 HUD stat 표시** | bulletCount → critRate (탄환수는 무기 등급 영역). ATK x2 / RATE +30% / CRIT +50%, CRIT_MUL=3 |
| ✅ 검증 OK | **4주차 #4 Weapon Registry + 4종 + 등급** | Shotgun/Laser/Missile 각각 검증, sprite + hit vfx 적용. 레이저 등급 차이 = 굵기 (희귀 60 / 영웅 120) 로 정착 |
| ✅ 검증 OK | **4주차 #5 무기 보상 카드 (RewardCard + CardPool + LevelUpScene 통합)** | stat 3 영구 + 무기 6 (3 × 2 등급) 받으면 그 조합만 풀 제외. 카드는 sprite + 등급 색 |
| ▶ **다음** | **4주차 #6 풀 룰 보강 — 영웅 받으면 같은 무기 희귀도 제외** | CardPool.consume 의 EPIC 분기 추가 (한 줄). + 시작 무기 = 영웅 레이저 (굵은 빔 검증 단계) |
| ⏸ 대기 | 4~8주차 전부 | |

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

- 크기: **200×200** (placeholder 80→100→140→**200** 단계로 키움. 마지막 200 은 3주차 #4 — 사용자가 모든 게임 객체 가시성 일괄 1.4배 결정. 화면 폭 900 의 22% 로 모바일 슈팅 기준 큰 편이지만 가시성 우선. `Player.PLAYER_WIDTH/HEIGHT`)
- 시작 위치: `(metrics.width / 2, metrics.height - PLAYER_HEIGHT * 1.5f)` = `(450, 1390)` (화면 하단 중앙)
- 시작 HP: **10** (5 → 10 으로 3주차 #4 시점에 buff. EnemyBullet/접촉 데미지의 비중을 절반으로 약화)
- 이동: framework lerp 패턴 (`targetX/Y`, `SPEED = 1100f` per second. 1500→1100 으로 줄임 — 캐릭터가 커진 만큼 회피 난이도 보정). 화면 경계 안에서 clamp.
- 발사 간격: 0.3초 (FIRE_INTERVAL) — 3번 sub-task 에서 도입
- Bullet 데미지: 1 (4주차 능력치 곱셈 적용)

### 10.3 Bullet

- 크기: **56×112** (`Bullet.BULLET_WIDTH/HEIGHT`, 12×24 → 24×48 → 40×80 → **56×112** 로 세 번 키움. 마지막은 3주차 #4 — 사용자가 Player/Enemy/Bullet/EnemyBullet 모두 일괄 1.4배)
- 속도: 1500f/s (위 방향, `Bullet.SPEED`)
- 발사 위치: Player 머리 위 (`y - PLAYER_HEIGHT/2 - BULLET_OFFSET`, `BULLET_OFFSET = 8f`)
- 발사 간격: 0.3초 (`Player.FIRE_INTERVAL`)
- 화면 위로 벗어나면 (`y + height/2 < 0`) `world.remove(this, BULLET)` → recycle bin 으로
- 풀 패턴: `Bullet.get(gctx, x, y)` — `private constructor`, 외부에서 `Bullet(gctx)` 직접 생성 금지

### 10.4 Enemy 종류별

| 종류 | placeholder | width×height | HP | 속도 (아래 방향) | 점수 | hitDamage (Player 충돌 시) |
|---|---|---|---|---|---|---|
| `Type.SUICIDE` | 빨간 원 (`#EF4444`, 진짜 에셋 교체됨) | 130×130 | 1 | 280f/s | 10 | **2** (자폭, 3주차 #1) |
| `Type.RANGED` | 주황 삼각 (`#F97316`, 진짜 에셋 교체됨) | 155×155 | 2 | 150f/s | 20 | 1 |
| `Type.SPLIT` | 보라 사각 (`#A855F7`, 진짜 에셋 교체됨) | 120×120 | 3 | 220f/s | 30 | 1 |
| `Type.SPLIT_MINION` | SPLIT 본체 축소판 (진짜 에셋) | 70×70 | 1 | 350f/s | 5 | 1 |

> 사이즈 history: 70 → 95/110/85/50 (진짜 PNG 도입 시점, 2주차 #4) → **130/155/120/70** (3주차 #4 일괄 1.4배). SPLIT_MINION 은 SPLIT 본체가 죽었을 때 좌·우 30° 사선으로 2마리만 분열 spawn (`Enemy.MINION_ANGLES = [-30°, +30°]`). 분열 후 `MINION_LOCK_DELAY = 0.3f` 동안 사선 비행을 유지하고 그 시점 Player 위치로 lock-on → SUICIDE 와 같은 dive 직진 (자폭). EnemyGenerator 의 일반 random 풀에는 포함하지 않으며, 3주차 #5 random 복원 단계에서 `entries.filter { it != SPLIT_MINION }.random()` 로 명시적 제외.

EnemyBullet (RANGED 가 발사): `width=56, height=84, speed=700f/s, damage=1`. 24×36 → 40×60 → **56×84** 로 두 번 키움 (가시성, 마지막은 일괄 1.4배). RANGED 는 발사 시점 Player 위치로 단위벡터 정규화한 (vx, vy) 로 **aimed 발사** — Player 가 좌우로 빠르게 움직이면 매 1.2초마다 다른 방향. Player Bullet 의 ObjectPool 패턴 그대로, layer=`ENEMY_BULLET`. aimed 탄이 좌·우·위로도 빠질 수 있어 화면 밖 검사는 사방.

> 크기·속도는 한 번 일괄 조정됨 — 진짜 캐릭터 에셋으로 교체된 시점(2주차 #4 직후)에 시각적 박력을 위해 30~40% 키우고 같은 비율로 속도 25~30% 감소시켜 회피 난이도 유지.
> 충돌 박스(`collisionRect`) 는 모든 IBoxCollidable 에 width·height × 0.8 inset 적용 (양쪽 10% 씩 안쪽). 캐릭터 PNG 의 투명 여백 보정. (§8.2)

EnemyGenerator: spawn 간격 `GEN_INTERVAL = 1.0f` (단순 1마리/1초). 3주차 #1~#3 동안에는 한 종류만 spawn 하도록 임시 수정해서 검증, #4 에서 random 복원. 8주차 밸런스 단계에서 wave 시스템 검토.

RANGED 행동 파라미터: `RANGED_STOP_RATIO_MIN/MAX = 0.22f / 0.35f` (spawn 시점에 그 사이 random 추출, 매 인스턴스가 다른 Y 라인에 정지), `RANGED_FIRE_INTERVAL = 1.2f`. 정지 후엔 Bullet 으로 처치될 때까지 계속 발사 (재하강 X). SUICIDE/SPLIT: `SUICIDE_LOCK_RATIO = 0.4f`, `SUICIDE_DIVE_MUL = 1.6f` (최종 dive 속도 = type.speed × 1.6 → SUICIDE 448f/s, SPLIT 352f/s). SPLIT 은 본체 동작이 SUICIDE 와 동일하고 죽을 때만 분열 추가.

VFX 파라미터: `Bullet.HIT_DURATION = EnemyBullet.HIT_DURATION = Enemy.DIE_DURATION = 0.1f` 일관 — 모든 vfx 가 깜빡 보이고 사라지는 정도. muzzle flash 는 사용자 결정 (3주차 #4 후반) 으로 폐기 → 같은 자산을 hit vfx 로 재배치 (사격 게임 피드백은 명중 지점이 더 중요). 본격 vfx 폴리싱 (alpha/scale 변화) 은 7주차.

### 10.5 Layer enum (incremental — 그 시점 클래스가 있는 것만)

> framework 가 가르치는 원칙: **Layer enum 항목은 그 클래스를 도입하는 commit 에서 같이 추가** — 미래 layer 를 미리 박아두지 않는다 (§11#12 참조).

현재 (3주차 #4 VFX 도입 시점 — die effect 가 dying enemy 자기 draw 라 별도 layer 없음):
```kotlin
enum class Layer { BACKGROUND, PLAYER, BULLET, ENEMY, ENEMY_BULLET, STARS, CONTROLLER, UI }
```

언제 무엇이 추가되는지:
| sub-task | 추가되는 layer | 이유 |
|---|---|---|
| 1주차 #2 | `BACKGROUND` | `VertScrollBackground` 도입 |
| 1주차 #3 | `PLAYER` | `Player` 클래스 도입 |
| 1주차 #4 | `BULLET` | `Bullet` 클래스 도입 |
| 1주차 #5 | (Layer 추가 없음) | BossScene placeholder 전환 — Scene/SceneStack 만 사용 |
| 2주차 #1 | `STARS` | 별 parallax `VertScrollBackground` 도입 (ENEMY 뒤·CONTROLLER 앞 — 캐릭터 위로 별빛이 흐름. DragonFlight 4/13 의 `CLOUD` 자리, §8.2) |
| 2주차 #5 | `ENEMY`, `CONTROLLER` | `Enemy`, `EnemyGenerator`, `CollisionChecker` 도입 (재배치 후 — §8.1) |
| 2주차 #6 | `UI` | `ScoreLabel`/`PlayerHpHud`/`BossTimerHud` HUD 객체 도입 (재배치 후 — §8.1) |
| 3주차 #2 | `ENEMY_BULLET` | `EnemyBullet` 클래스 도입. ENEMY 위·STARS 아래 — 적이 자기 탄을 가리지 않도록 ENEMY 위에 두되, 별 parallax 보다는 아래 |

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
15. **`VertScrollBackground` 비트맵의 위/아래 buffer 부족으로 tile 이음새가 보임** — `VertScrollBackground` 가 비트맵을 단순 반복 타일링하므로 **위/아래 edge 부근에 시각 요소가 있으면 두 tile 의 경계가 "줄"처럼 보인다**. 2_Project (2주차 스냅샷) 의 `clouds.png` (900×600) 를 분석해 보면 **상단 80px 완전 투명, 80~200px fade in (알파 12→95), 200~500px full, 500~600px fade out** — 즉 **전체 50% 가 buffer/fade 영역**이다. seamless 효과의 정체는 `VertScrollBackground` 코드가 아니라 비트맵 디자인. Sky Blaster 의 별 비트맵도 같은 패턴(위/아래 150px 투명 + 150~300 / 900~1050 fade + 300~900 full) 으로 재생성. 이후 사용자가 직접 만든 별 에셋(투명 배경 처리됨) 으로 교체.
16. **다음 세션이 plan 을 처음부터 따라갈 때, 옛 값을 사용하지 말 것** — 진행 중에 사용자 결정으로 일괄 조정된 정착값들이 있다. **§10 정착값 + sub-task "만들 것" 의 정착값 메모 + §8.2 의도적 차이표 를 그대로 사용**한다.
    - **Player**: PLAYER_WIDTH/HEIGHT = **200**, SPEED = **1100f**, MAX_LIFE = **10** (1주차 #3 + 2주차 #6 부터 처음부터). 옛 100/140/5 사용 X.
    - **Bullet**: BULLET_WIDTH/HEIGHT = **56×112**, SPEED = 1500f. 옛 12×24 / 24×48 / 40×80 사용 X.
    - **EnemyBullet**: ENEMY_BULLET_WIDTH/HEIGHT = **56×84**, SPEED = 700f, **vx/vy 인자 처음부터** (RANGED 가 aimed 발사). 화면 밖 검사는 사방. 옛 24×36 / 40×60 + 직하 전용 사용 X.
    - **Enemy.Type**: **SUICIDE 130×130/280f, RANGED 155×155/150f, SPLIT 120×120/220f, SPLIT_MINION 70×70/350f**. hitDamage **2/1/1/1**. 옛 95/110/85/50 사용 X.
    - **Enemy 동작**: **SUICIDE/SPLIT 둘 다 `updateSuicide` 공유** (lock-on 자폭). RANGED 는 `[0.22, 0.35]` Y random 정지 + Player 방향 aimed 발사 (RETREATING/STAY 단계 없음, 죽을 때까지 발사). SPLIT_MINION 은 init 시 dive 모드 + 0.3초 후 lock-on 자폭. SPLIT 은 startDying 시 minion 2마리 분열 (좌·우 30°).
    - **collisionRect inset**: 모든 IBoxCollidable 에 **width × 0.8 비율 inset**. 2주차 #5/#6 부터 처음부터.
    - **VFX 패턴 (3주차 #4 부터)**: hit / die 모두 "주체가 자기 draw" — 별도 Effect 클래스 / EFFECT layer / muzzle flash 단계 시도 X. **Bullet/EnemyBullet 에 hitting 상태**, **Enemy 에 dying 상태**, 모두 명중/사망 시 즉시 `world.remove` 안 하고 `startHitting()` / `startDying(scene)` 호출 → `HIT_DURATION = DIE_DURATION = 0.1f` 동안 자기 draw 에서 vfx 만 그리다 self-remove. `collisionRect.setEmpty()` 로 추가 충돌 자동 skip. CollisionChecker 의 모든 `world.remove(...)` 제거.
    - **VFX 자산 6종**: `vfx_player_hit` (Bullet 명중), `vfx_enemy_hit` (EnemyBullet 명중), `vfx_suicide_die` / `vfx_ranged_die` / `vfx_split_burst` (SPLIT die 대용) / `vfx_minion_die` (Enemy 사망). 사용자가 mipmap 에 lowercase + underscore 이름으로 import.
    - **PlayerHpHud**: 시안(02_normal_stage.png) 에 맞춰 **좌하단 + 초록색** 처음부터. `Gauge.thickness` 는 1.0 단위 (예: 0.04f) — 픽셀 단위 X (§11#14).
    - **ScoreLabel**: `displayScore` 분리 + lerp 패턴 처음부터.
    - **BOSS_ENTER_TIME**: README §1 사양 그대로 **`60f`** (1주차 #5 시점 테스트값 10f → 4주차 #2 에서 60f 로 정착).
    - **MainScene 은 open class** + 세 생성자 파라미터 (`backgroundResId`, **`val isBossStage`**) 처음부터. **BossScene 진입 시 `if (!isBossStage) add(enemyGenerator, ...)` 로 spawn 정지** + **BossTimerHud 가 isBossStage 시 "BOSS STAGE" 라벨** (mm:ss 대신). BossScene = 한 줄 wrapper.
    - **에셋 매핑** (mipmap-xxxhdpi): `sky_bg`, `boss_bg`, `title_bg`, `sky_star`, `player_placeholder`, `bullet_placeholder`, `enemy_suicide`, `enemy_ranged`, `enemy_split`, `enemy_split_minion`, `enemy_bullet`, `vfx_player_hit`, `vfx_enemy_hit`, `vfx_suicide_die`, `vfx_ranged_die`, `vfx_split_burst`, `vfx_minion_die`, `weapon_shotgun`, `weapon_laser`, `weapon_homing`, `vfx_shotgun_hit`, `vfx_laser_hit` (현재 미사용), `vfx_homing_hit`.
    - **EXP / 레벨 (4주차 #1~#2)**: `Player.exp`, `Player.level = 1`, `Player.maxExp = INITIAL_MAX_EXP = 5`, `MAX_EXP_GROWTH = 1.5f`. `gainExp(amount)` 는 누적만, `levelUp()` 은 외부 (LevelUpScene) 가 호출. ExpOrb (cyan 원, drop 즉시 Player homing 800f/s, value 1) → ExpLabel (좌하단 HP 게이지 옆 cyan, `"Lv.N  EXP e/m"`).
    - **LevelUpScene (4주차 #2)**: MainScene 위 push, 반투명 검정 + 카드 3장 (230×320, cyan 테두리). #2 단계는 placeholder (3장 모두 동일 `"+ Lv N+1"` + `levelUp()` 만), #3 부터 카드별 보상 분기 (공격력 ×1.2 / 공속 ×1.15 / 탄환 +1).
    - **Player stat 보상 (4주차 #3)**: `attackMul=1f`, `fireRateMul=1f`, `bulletCount=1`. Bullet 에 `var power: Int = DAMAGE`. fireBullet 에서 `fireCooldown = FIRE_INTERVAL / fireRateMul`, power = `(DAMAGE × attackMul).toInt().coerceAtLeast(1)`, bulletCount > 1 이면 `BULLET_SPREAD = 50f` 가로 spread.
    - **카드 보상 배수 (4주차 #3 + #5)**: stat 카드 = `ATK_BOOST = 2.0f` / `RATE_BOOST = 1.3f` / `CRIT_BOOST = 0.5f` (1.0 캡), `CRIT_MUL = 3` (Player 의 calculatePower 가 매 발사마다 critRate 굴림). 무기 카드는 `WeaponCard(weapon, grade)` — apply 시 currentWeapon + weaponGrade 교체.
    - **무기 등급 차이 (4주차 #4)**: 샷건 = 발수 (희귀 3 / 영웅 5), 호밍 = 발수 (희귀 1 / 영웅 2), **레이저 = 굵기** (희귀 beamHalf 60 / 영웅 150, 폭 120 / 300, lifetime 통일 1.0f). lifetime / fireInterval 은 등급 무관. **LaserBeam 충돌 폭 = 시각 폭 × 0.15** (영웅 시각 폭 300 → 충돌 45, 코어 라인만 데미지 — "충돌박스는 액터보다 작아야 자연스럽다" 룰, 다른 IBoxCollidable 의 inset 0.8 보다 훨씬 좁은 이유는 빔 sprite 가 화면을 가로지르는 큰 시각 효과라 글로우 영역까지 잡으면 시각/물리 어긋남).
    - **무기 카드 풀 룰 (4주차 #5 + #6)**: stat 3 카드는 영구 풀, 무기 6 카드 (3 무기 × 2 등급) 는 받으면 그 조합 제외. **#6 보강 — 영웅 받으면 같은 무기 희귀도 함께 제외** (다운그레이드 무의미). 희귀 받으면 영웅은 그대로 (업그레이드 가능).
    - **EXP drop 룰 (4주차 #1)**: `Enemy.startDying` 안에서 `if (type != Type.SPLIT) ExpOrb spawn` — SPLIT 본체는 분열만 시키고 EXP X, SPLIT_MINION 처치해야 보상.
    - **DebugStatLabel (4주차 #3)**: textSize 40, `Typeface.MONOSPACE`, `Color.WHITE`. **위치는 화면 하단 좌측** (x=30, y=metrics.height-14), format `"ATK x%.2f RATE x%.2f COUNT %d"`. + framework `GameView.debugPaint` color BLUE → WHITE (§8.2 — Sky Blaster 검정+파랑 배경 가시성).

17. **die / hit vfx 시 "별도 GameObject 만들기" 함정** — Effect 클래스 / EFFECT layer 를 만들어 spawn 하는 방식은 시행착오 끝에 폐기됨. framework laser_spark 의 "주체가 자기 draw" 원칙을 **dying enemy 와 hitting bullet 까지 일관 확장**하는 게 정답. 즉 죽거나 명중한 객체를 즉시 layer 에서 빼지 않고 짧은 시간 살려두며 자기가 vfx 그리다 self-remove. 자세한 패턴은 §11#16, sub-task 3주차 #4 참조.

18. **BossScene 에 EnemyGenerator 무지성으로 추가하기** — MainScene 의 `init { world.apply { add(...) } }` 블록을 그대로 가져가면 보스 스테이지에서도 일반 적이 spawn 된다. **`if (!isBossStage) add(enemyGenerator, Layer.CONTROLLER)` 로 분기 필수** (1주차 #5). 같은 이유로 BossTimerHud 도 `scene.isBossStage` 보고 텍스트 결정 (LabelUtil 패턴 — HUD 가 매 draw 시점 scene 상태 보고 그릴 문자열 결정).

---

## 12. 이 문서 갱신 규칙

- 새 framework cutoff 진입 시 §6 (해당 주차 항목) 채우기
- sub-task 완료 시 §7 체크박스 `[x]`, §9.1 "다음" 항목 갱신, §9 "마지막 갱신" 날짜 변경
- DragonFlight 와 다른 새 패턴 도입 시 §8.2 표에 추가
- 게임 디자인 파라미터 변경 시 §10 갱신
- 새 함정 발견 시 §11 추가
