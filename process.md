# Sky Blaster — 진행 README (1주차)

> 게임 컨셉 / 8주 일정 / 정량 개발 범위는 [README.md](README.md) 참고.
>
> 본 문서는 **개발 진행 로그**로, 매 주차 끝에 갱신한다.

---

## 0. 기준선

- **계획서 작성일**: 2026-04-06
- **주차 분기**: 1주차 4/6~4/12, 2주차 4/13~4/19, 3주차 4/20~4/26, 4주차 4/27~5/3
- **참고 프레임워크**: 교수님 `spgp_2026` 저장소의 **2026-04-10 시점**(commit `9e232ab` 까지) 의 `:a2dg` 모듈을 그대로 가져온다.
  - 4/13 의 `Scene.clipsRect`, `GameMetrics.borderRect`, `Activity nosensor`, `appCategory="game"`, `HorzScrollBackground` 는 **포함하지 않는다**.

---

## 1. 1주차 진행 현황

계획서의 1주차 항목은 **"프로젝트 셋업 구현 — CustomView 기반 GameView, Scene 전환 구조, 리소스 수집"** 이다. 1주차 안에 끝낼 부분과, 후순위로 미루거나 2주차 이후로 당겨도 되는 부분을 아래처럼 나눈다.

### 1-1. 1주차 작업 분할

| # | 작업 | 상태 | 비고 |
|:-:|------|:----:|------|
| **W1-1** | `:a2dg` 프레임워크를 4/10 시점 그대로 가져오기 | ✅ | 본 세션에서 완료. 아래 §2 참고 |
| **W1-2** | `:app` 모듈 골격 — `SkyBlasterActivity` + `TitleScene` + `MainScene` placeholder, Scene 전환 데모 | ✅ | 본 세션에서 완료. 아래 §3 참고 |
| **W1-3** | 디버그 표시 확인 — 가상 좌표계 격자 / FPS / collision box debug | ⬜ | `BuildConfig.DEBUG` 빌드로 실기기에서 한 번 띄워서 확인만 하면 끝 |
| **W1-4** | (선반영) Player 스켈레톤 — 드래그 이동, Canvas 도형 placeholder | ⬜ | 계획서 2주차이지만 리소스 없이 가능. §4 참고 |
| **W1-5** | (선반영) Bullet 자동 발사 + `IRecyclable` Recycle 패턴 | ⬜ | 계획서 2주차이지만 리소스 없이 가능 |
| **W1-6** | (선반영) `IBoxCollidable` + `CollisionChecker` 골격 | ⬜ | 계획서 3주차 패턴이지만 빈 hook 만이라도 미리 잡아 둘 수 있음 |
| **W1-7** | (후순위) **리소스 수집** — `space_bg.png`, `player.png`, enemy strip 등 | ⏸ | 의도적으로 후순위. 2주차 시작 시 한꺼번에 모음 |

`✅` 완료 / `⬜` 1주차 안에 진행 / `⏸` 후순위

### 1-2. 리소스 없이 미리 할 수 있는 것 (4/10 기준)

교수님이 4/10 시점에 가지고 있던 게임 코드는 다음과 같다.

| 분류 | 4/10 교수님 게임 코드 (DragonFlight) | Sky Blaster 적용 가능성 |
|------|------|------|
| Player | `Sprite(R.mipmap.fighters)` + `IBoxCollidable`, 드래그 이동, `cooltime gauge`, sprite sheet roll | 드래그 이동만 도형 placeholder 로 옮기면 **리소스 없이 가능** |
| Bullet | `Sprite(R.mipmap.laser_1)` + `IBoxCollidable` + `IRecyclable`, recycle pattern | 노란 사각형 도형 + `IRecyclable` 패턴 그대로 → **가능** |
| Enemy | `AnimSprite(R.mipmap.enemy_01, 20fps, 20)` + `IBoxCollidable` + `IRecyclable`, life gauge | 도형 placeholder + 같은 패턴 → **가능** (단, AnimSprite 는 strip PNG 가 있어야 의미 있음. 도형으로는 정적이 됨) |
| EnemyGenerator | 주기적 spawn, wave 별 속도 스케일링 | **가능** (Enemy placeholder 있으면) |
| CollisionChecker | Bullet/Enemy/Player 충돌 처리, `forEachReversedAt`, smart cast | **가능** |
| ScoreNumber | `ImageNumber(R.mipmap.number_24x32)` | 리소스 필요 → 후순위 |
| Background | `Sprite(R.mipmap.df_bg)` → `VertScrollBackground` | 리소스 필요 → 후순위 |

요약: **Player / Bullet / Enemy(정적) / Generator / CollisionChecker** 까지는 placeholder 도형으로 1주차에 미리 만들어 둘 수 있다. **배경 스크롤 / 점수 ImageNumber / Enemy 애니메이션** 은 PNG 가 있어야 의미가 있으므로 2주차로 미룬다.

---

## 2. `:a2dg` 모듈 — 4/10 기준

가져온 파일 (총 19 + manifest):

```
a2dg/src/main/
├── AndroidManifest.xml
└── java/kr/ac/tukorea/ge/spgp2026/a2dg/
    ├── activity/BaseGameActivity.kt
    ├── objects/
    │   ├── AnimSprite.kt
    │   ├── IBoxCollidable.kt
    │   ├── IGameObject.kt
    │   ├── IRecyclable.kt
    │   ├── ImageNumber.kt
    │   ├── JoyStick.kt              ← 4/9 시점부터 존재
    │   ├── Sprite.kt                ← 4/9 의 setCenterProportionalWidth/Height helper 포함
    │   └── VertScrollBackground.kt  ← 4/9 추가
    ├── res/{BitmapPool, GameResources}.kt
    ├── scene/{Scene, SceneStack, World}.kt
    ├── util/{Gauge, LabelUtil}.kt
    └── view/{GameContext, GameMetrics, GameView}.kt
```

### 4/13 변경분 중 **포함하지 않은** 것

| 4/13 commit | 내용 | 1주차에 미포함 이유 |
|-------------|------|--------------------|
| `7867d49` | Activity `screenOrientation="nosensor"` + `appCategory="game"`, `minSdk` 24 → 26 | 4/10 기준이므로 그대로 두고, Sky Blaster 는 manifest 의 `portrait` 만 유지 |
| `3300abc` | `Scene.clipsRect`, `GameMetrics.borderRect`, `GameView` 의 `clipRect` 처리 | 4/10 기준 미포함. clip 이 필요해지면 그때 가져오기로 |
| `5a2126b` | `clouds.png` 리소스 + 구름 background layer | PNG 리소스 — 후순위 |
| (homework branch) | `HorzScrollBackground` | 가로 스크롤은 본 게임에 불필요 |

---

## 3. `:app` 모듈 — 1주차 골격

```
app/src/main/java/kr/ac/tukorea/ge/spgp2026/skyblaster/
├── SkyBlasterActivity.kt   # BaseGameActivity 상속, createRootScene → TitleScene
├── TitleScene.kt           # "SKY BLASTER" + "TAP TO START", touch → MainScene.push()
└── MainScene.kt            # Layer enum 9종 reserve, 빈 World, "MAIN SCENE" placeholder text
```

`MainScene` 안의 `Layer` enum 은 향후 주차에서 채울 자리만 미리 잡아 둔다:

```
BACKGROUND → CLOUD → EXP → ENEMY_BULLET → ENEMY → BULLET → PLAYER → CONTROLLER → UI
```

### Scene 전환 흐름

```
[앱 실행]
   │ SkyBlasterActivity.createRootScene
   ▼
TitleScene  ──ACTION_DOWN──▶  MainScene  ──BACK──▶ TitleScene
                  push                       pop
```

### 빌드 / Manifest 정리

- `app/AndroidManifest.xml` — `portrait` 유지, `appCategory="game"` 제거
- `app/build.gradle.kts` — `minSdk = 24` 로 환원

---

## 4. 다음 (1주차 마무리 → 2주차 시작) 작업 메모

### W1-3 디버그 확인 체크리스트
- 빌드 후 실기기에서 빨간 테두리 + 회색 100단위 격자
- 좌상단 FPS / 객체 수 / 레이어별 카운트
- 하단 frame time 그래프

### W1-4 Player 스켈레톤 (선반영 시)
- `Player(gctx) : IGameObject, IBoxCollidable` (Sprite 상속 X — PNG 없으므로)
- `width=120f`, `height=120f`, 중심 (450, 1400)
- 드래그 이동: ACTION_DOWN 에서 `dragOffset` 기억, ACTION_MOVE 에서 `targetX/Y` 갱신, `update()` 에서 `SPEED * frameTime` 만큼 보정 이동
- `draw()` 는 파란 원 + 가운데 "내" 한자 라벨 (placeholder)
- `collisionRect` 는 dstRect 의 절반 폭 inset

### W1-5 Bullet 자동 발사 (선반영 시)
- `Bullet : IGameObject, IBoxCollidable, IRecyclable`
- `private constructor` + `companion object get(world, x, y, vx, vy, power)`
- `World.obtain(Bullet::class.java) ?: Bullet(gctx)` 패턴
- `Player.update()` 가 `fireCoolTime` 카운터로 자동 발사 호출

### W1-7 → 2주차로 미루는 리소스 후보
- `space_bg.png` (어두운 우주, 720x2560 권장)
- `clouds.png` 또는 `nebula.png` (parallax 두 번째 layer)
- `player.png` 또는 fighters strip
- `bullet.png` (28x40 노란 레이저)
- `enemy_*.png` 3종 (180x180)
- `enemy_bullet.png`, `exp_orb.png`
- `number_24x32.png` (HUD 점수용)

---

## 5. 현재 디렉터리 트리

```
SmartphoneTermProject/
├── README.md                        # 게임 컨셉 / 8주 일정 (계획서)
├── process.md                       # ← 본 문서
├── 01_title.png ~ 06_result.png     # 계획서 mockup
├── settings.gradle.kts              # include(":app", ":a2dg")
├── a2dg/                            # §2 참고 (4/10 기준)
└── app/
    └── src/main/
        ├── AndroidManifest.xml      # portrait, appCategory 없음
        ├── java/kr/ac/tukorea/ge/spgp2026/skyblaster/
        │   ├── SkyBlasterActivity.kt
        │   ├── TitleScene.kt
        │   └── MainScene.kt
        └── res/                     # ic_launcher 만, 게임 리소스는 후순위
```
