# Sky Blaster — process.md

> 진행 상황과 이어서 해야 할 일을 정리한 문서

## 1. 현재까지 진행 상태 (2026-04-29)

README 일정 기준 **1주차 ~ 5주차 일부**까지 코드를 채워 두었음 (누계 약 52%).
교수님이 DragonFlight 수업에서 다룬 패턴(`a2dg`, Recycle, World layer enum, IBoxCollidable, Gauge / LabelUtil, wave 시스템, cooltime gauge, spark, 구름 layer)을 모두 사용하고 있음.

- [x] 1주차 — 프로젝트 셋업 (`:a2dg` 모듈, BaseGameActivity, GameView, Scene/World)
- [x] 2주차 — Player 터치 드래그 + 자동 발사 + 별/구름 두 layer parallax + cooltime gauge / spark / damage flash
- [x] 3주차 — Enemy 3종 + EnemyGenerator + CollisionChecker + Recycle pattern + **wave 기반 속도/생성속도 스케일링**
- [x] 4주차 — EXP 구슬 드롭/흡수, 레벨업 시스템, **`LevelUpScene` 카드 3장**, **무기 3종 (DUAL/TRIPLE/SPREAD)**, **능력치 2종 (DAMAGE_UP / ATTACK_SPEED 누적)**
- [△] 5주차 — 보스 진입 카운트다운 HUD 까지. 스킬과 보스 진입 선택 UI 미구현
- [ ] 6주차 — 보스 Scene + Bezier 패턴
- [ ] 7주차 — 이펙트 / BGM / 타이틀 / Pause / Result Scene (게임오버 오버레이만 임시 구현)
- [ ] 8주차 — 마감 점검

이미지 파일 없이 모든 캐릭터·배경을 Canvas 도형으로 그려 둠.
**현재 placeholder 규칙: 모든 게임 오브젝트는 "원 + 한자 라벨"로 표시** —
Player "내" / Kamikaze "폭" / Ranged "원" / Splitter "분" / Bullet·EnemyBullet·ExpOrb 는 라벨 없는 작은 원.
이미지로 교체할 때 어디를 수정하면 되는지는 6번 섹션에 표로 정리해 두었음.

## 2. 모듈 구조

```
SmartphoneTermProject/
├── a2dg/                                      # 수업에서 만들어 가는 프레임워크
│   └── kr.ac.tukorea.ge.spgp2026.a2dg/
│       ├── activity/BaseGameActivity
│       ├── view/{GameView, GameContext, GameMetrics}
│       ├── scene/{Scene, SceneStack, World}
│       ├── objects/{IGameObject, IBoxCollidable, IRecyclable,
│       │            Sprite, AnimSprite,
│       │            VertScrollBackground, HorzScrollBackground, ImageNumber}
│       ├── res/{BitmapPool, GameResources}
│       └── util/{Gauge, LabelUtil}
└── app/
    └── kr.ac.tukorea.ge.spgp2026.skyblaster/
        ├── SkyBlasterActivity                 # 진입점
        ├── MainScene                          # 일반 스테이지 Scene (gameOver, bossTimer, pendingLevelUps)
        ├── LevelUpScene                       # 카드 3장 transparent push
        ├── Reward + RewardKind                # 보상 enum (WEAPON / STAT)
        ├── StarfieldBackground                # 별 두 레이어
        ├── CloudLayer                         # 구름 (parallax 2번째 layer)
        ├── Player                             # 무기 / 능력치 / cooltime gauge / spark / damage flash
        ├── Bullet                             # vx,vy 방향 지원 + Recycle
        ├── Enemy / KamikazeEnemy / RangedEnemy / SplitterEnemy
        ├── EnemyBullet                        # 원거리 적이 쏘는 탄환
        ├── EnemyGenerator                     # wave 기반 속도/생성속도 스케일링
        ├── CollisionChecker                   # 게임오버 시 skip
        ├── ExpOrb                             # EXP 구슬 + 자석 흡수
        └── Hud                                # 점수/레벨/EXP/Wave/BossTimer/GameOver
```

## 3. 구현된 기능 자세히

### Activity / Scene
- `SkyBlasterActivity` 는 `BaseGameActivity` 상속, fullscreen + portrait 잠금.
- `MainScene` 의 layer 9개(BACKGROUND/CLOUD/EXP/ENEMY_BULLET/ENEMY/BULLET/PLAYER/CONTROLLER/UI), `clipsRect = true`.
- `LevelUpScene` 은 transparent overlay — `mainScene.draw(canvas)` 후 반투명 + 카드.

### 배경 (parallax 2 layer, 교수님 5a2126b 패턴)
- `StarfieldBackground` — 별 두 레이어. `CloudLayer` — 더 빠른 흰파랑 구름.
- 나중에 PNG 두 장이 들어오면 `a2dg.VertScrollBackground` 두 개로 그대로 교체 가능.

### Player (cooltime / spark / damage flash / weapon / stat)
- 화면 어디든 잡고 끌면 그만큼 비행기가 따라온다 (텔레포트 없음).
- HP gauge + cooltime gauge 두 개를 비행기 아래에 그린다 (교수님 4ccd8a7 패턴).
- 발사 직후 spark 가 잠깐 머즐에 표시된다 (교수님 e5fab46 패턴).
- 피격 시 damage flash — 비행기 위에 빨간색이 0.25초 깜빡임.
- weapon: `SINGLE / DUAL / TRIPLE / SPREAD` 4종.
- stat: `damageBonus` (DAMAGE_UP 누적), `fireIntervalScale` (ATTACK_SPEED 누적).
- `applyReward(reward)` 에서 보상 종류에 따라 weapon 교체 또는 stat 누적.

### Enemy 3종 + Wave (교수님 8ea2c51 패턴)
- 모두 `init(...)` 에서 `speedMultiplier` 를 받아 현재 wave 에 맞춰 빨라진다.
- KamikazeEnemy / RangedEnemy / SplitterEnemy 모두 Recycle pattern.
- EnemyGenerator 가 12초마다 wave++ 하고 생성간격을 0.92 배씩 줄임 (`MIN_INTERVAL` 까지).

### EXP / 레벨업
- 적이 총알에 죽을 때만 ExpOrb 드롭. 자석 흡수.
- `MainScene.gainExp()` 가 EXP 누적 + 레벨업 → `pendingLevelUps++`.
- 매 update 마지막에 `pendingLevelUps > 0` 이면 한 개씩 pop → `LevelUpScene` push.
- 레벨이 한 번에 여러 단계 오르면 카드 화면이 연속해서 뜬다.

### Reward / LevelUpScene
- `Reward.roll(3)` 으로 5종 중 3개 셔플.
  - `DUAL_SHOT`, `TRIPLE_SHOT`, `SPREAD_SHOT` (WEAPON)
  - `DAMAGE_UP`, `ATTACK_SPEED` (STAT)
- 카드 한 장을 터치하면 `player.applyReward(reward)` + `pop()`.
- 뒤로가기로 우회 불가 (`onBackPressed()` 가 true).

### 게임 오버
- `player.hp <= 0` 되면 `MainScene.gameOver = true`.
- 모든 update가 즉시 return — 적/총알/EnemyGenerator 가 멈춤.
- HUD 가 반투명 검정 + `GAME OVER` + `TAP TO RESTART` 표시.
- 화면 아무 곳이나 터치하면 `gctx.sceneStack.change(MainScene(gctx))` 로 새 게임 시작.

### Recycle pattern
- `Bullet`, `EnemyBullet`, `KamikazeEnemy`, `RangedEnemy`, `SplitterEnemy`, `ExpOrb`
  모두 `IRecyclable` + private 생성자 + `companion object get(...)` 패턴.

## 4. 이어서 해야 할 일 (주차별 다음 작업)

### 5주차 마무리 — 스킬 + 보스 진입 패널
1. `Skill` 인터페이스 (`onTouch`, `update`) 만들고 Player 가 슬롯 1~2개 보관.
2. 화면 아래 빈 영역에 스킬 버튼 영역을 두고, 그 영역의 터치는 `Player.onTouchEvent` 보다 먼저 가로채기.
3. `Reward` 에 SKILL 종류 3개 추가하고 `LevelUpScene` 에 자연스럽게 끼워 넣기.
4. `bossTimer == 0` 일 때 작은 패널을 띄워 "지금 보스로 진입할지" / "조금 더 파밍할지" 선택.

### 6주차 — 보스
- `Boss` (Bezier 경로 따라 이동, 패턴 공격, HP 게이지 위에 표시).
- `BossScene` 으로 분기하거나 MainScene 안에서 wave 형식으로 처리 (선호도에 따라).

### 7주차 — UI / 이펙트 / 사운드
- `TitleScene`, `PauseScene`(transparent), `ResultScene`.
- `AnimSprite` 로 폭발/레벨업/피격 strip 재생.
- BGM 두 종 (`MediaPlayer`).

### 8주차 — 버그 / 마감

## 5. 빌드 / 실행

1. Android Studio 에서 이 프로젝트 폴더를 열고 Gradle sync.
   - `:a2dg` 모듈이 잡혀야 함. 안 보이면 `settings.gradle.kts` 의 `include(":a2dg")` 와
     `app/build.gradle.kts` 의 `implementation(project(":a2dg"))` 확인.
2. Run 'app'. 기본 동작:
   - 화면을 끌면 비행기 이동.
   - 자동 발사. 적이 위에서 내려옴.
   - 적 처치 → 녹색 EXP 구슬 → 흡수 → EXP 가득 차면 카드 3장 화면.
   - 카드 한 장 터치 → 즉시 게임 재개.
   - HP 0 이면 `GAME OVER` → 화면 터치하면 다시 시작.
3. 디버그 빌드에서는 collision box / 격자 / FPS 표시.
   거슬리면 `SkyBlasterActivity.kt` 의 debug flag 들을 false 로.

## 6. 이미지를 넣을 때 교체할 위치

지금은 PNG 가 없어도 바로 돌도록 모든 캐릭터/배경을 Canvas 도형으로 그렸음.
README 구상에 맞는 진짜 그림을 넣고 싶다면 아래만 바꾸면 됨.

| 파일 | 현재 그림 | 이미지로 교체할 때 |
|------|----------|------------------|
| `StarfieldBackground.kt` | 별 두 레이어 | `a2dg.VertScrollBackground(R.mipmap.space_bg, ...)` 로 교체 |
| `CloudLayer.kt` | 흰파랑 원 7개 | `a2dg.VertScrollBackground(R.mipmap.nebula, ...)` 로 교체 |
| `Player.kt` | 파란 비행기 Path | `Sprite` 상속, `R.mipmap.player`. fighters strip 이 있으면 srcRect 로 roll 처리 |
| `Bullet.kt` / `EnemyBullet.kt` | RoundRect / 원 | laser png 한 장이면 충분 |
| `KamikazeEnemy` / `RangedEnemy` / `SplitterEnemy` | 도형 | 각각 `AnimSprite(R.mipmap.enemy_*, fps, frameCount)` 로 교체 |
| `ExpOrb.kt` | 녹색 원 | exp_orb.png |
| `Hud.kt` 점수 | LabelUtil 텍스트 | `ImageNumber` + `number_24x32.png` |

## 7. 필요한 이미지 (이미지 추가 권장 목록)

지금 당장은 필요 없음. 위 표대로 교체할 때를 위해 모아 둘 후보:

- `space_bg.png` — 어두운 배경, 720x2560.
- `nebula_bg.png` — 옅은 색의 위쪽 layer (투명 배경 PNG), 720x2560.
- `player.png` 또는 fighters strip (roll 11frame, 80x80 한 frame).
- `bullet.png` — 28x40 정도 노란 레이저.
- `enemy_kamikaze.png`, `enemy_ranged.png`, `enemy_splitter.png` — 각 180x180.
- `enemy_bullet.png` — 작고 둥근 빨간 탄, 32x32.
- `exp_orb.png` — 24x24 녹색 보석.
- `number_24x32.png` — 0~9 가로 시트.
- (나중에) `explosion.png`, `levelup.png` 같은 strip.
