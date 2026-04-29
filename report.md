# Sky Blaster — 개발 진행 보고서

> 2026-04-29 기준 — 4주차 후반까지의 구현 보고
>
> 게임 컨셉, 기획, 일정은 [README.md](README.md) 참고.
> 다음 단계 작업 가이드는 [process.md](process.md) 참고.

---

## 1. 현재까지의 진행 상황

8주 일정 기준 누계 진행도는 약 **52%**.
4주차 후반(레벨업 보상 카드 UI + 무기 3종)까지 도달했고, 5주차의 보스 타이머까지 함께 들어가 있다.

> **시각 표현은 placeholder.** 이미지 리소스가 아직 없어서 모든 게임 오브젝트는
> "원 + 안에 한자 라벨" 형태로 그려진다 (Player="내", Kamikaze="폭", Ranged="원", Splitter="분").
> 이미지 추가 시 `process.md` 6번 표를 따라 `Sprite` / `AnimSprite` 로 교체하면 된다.

### 1-1. 주차별 진행도

| 주차 | 항목 | 진행도 | 비고 |
|------|------|:-----:|------|
| 1주차 | 프로젝트 셋업, GameView, Scene 전환 구조 | **100%** | `:a2dg` 모듈 분리 완료, BaseGameActivity / SceneStack push·pop·change 모두 사용 |
| 2주차 | Player 터치 드래그 이동, 자동 발사, 스크롤 배경 | **100%** | 별 + 구름 두 layer parallax. cooltime gauge / spark / damage flash 적용 |
| 3주차 | 적 3종, EnemyGenerator, ObjectPool, 충돌 처리 | **100%** | wave 시스템 추가. 모든 적과 EXP/총알이 Recycle pattern |
| 4주차 | EXP 구슬 드롭/흡수, 레벨업 시스템, 보상 카드 UI, 무기 3종 | **80%** | 레벨업 시 LevelUpScene push, 무기 3종(DUAL/TRIPLE/SPREAD) + 능력치 2종 적용 완료. 스킬은 5주차로 이월 |
| 5주차 | 스킬 3종, 능력치 증가, 보스 진입 타이머·선택 UI | **30%** | 보스 카운트다운 HUD + 능력치 2종 적용. 스킬과 보스 진입 패널은 미구현 |
| 6주차 | 보스 Bezier 이동, 패턴 공격, 보스 HP 바, 보스 Scene | **0%** | |
| 7주차 | 이펙트 10종+, BGM 2종, 타이틀·결과·Pause | **5%** | GAME OVER 오버레이 + 탭 재시작만 미니 결과화면 격으로 들어감 |
| 8주차 | 버그 검토, 마감 점검 | **0%** | |

### 1-2. 시스템별 진행도

| 시스템 | 진행도 |
|--------|:-----:|
| `a2dg` 프레임워크 모듈화 | 100% |
| Scene/World/Layer 구조 | 100% |
| SceneStack push / pop / change 사용 | 100% (LevelUpScene push, 게임오버 후 change) |
| Recycle pattern (Object Pool) | 100% (Bullet, EnemyBullet, Enemy 3종, ExpOrb 적용) |
| Collision (IBoxCollidable) | 100% (Player·Enemy / Bullet·Enemy / EnemyBullet·Player) |
| 배경 스크롤 (parallax) | 75% (별필드 + 구름 두 layer. PNG 두 장으로 교체만 남음) |
| Player (이동/HP/자동발사) | 95% (cooltime gauge / spark / damage flash 모두 적용) |
| 적 3종 동작 | 100% |
| Wave 기반 적 생성 / 속도 스케일링 | 100% |
| EXP / 레벨 시스템 | 100% (수치 + LevelUpScene + Reward 적용) |
| 무기 system | 70% (SINGLE / DUAL / TRIPLE / SPREAD. LASER / 스킬 미구현) |
| 능력치 system (스택) | 60% (DAMAGE_UP / ATTACK_SPEED. 치명타 미구현) |
| HUD (Score / Lv / EXP / Wave / Boss timer / GameOver) | 95% (텍스트 기반. ImageNumber 미적용) |
| 스킬 system | 0% |
| 보스 / 보스 패턴 | 5% (HUD에 카운트다운만) |
| 이펙트 (Frame Anim) | 0% |
| 사운드 (BGM/SFX) | 0% |
| Title / Pause / Result Scene | 5% (게임오버 오버레이만) |

---

## 2. Activity 구성

이 프로젝트의 Activity 는 **단 하나**이다.

### `SkyBlasterActivity` (`a2dg` 의 `BaseGameActivity` 상속)

- 진입점이며 `AndroidManifest.xml` 의 launcher activity.
- `screenOrientation="portrait"` 로 세로 고정.
- `onCreate()` 가 `BaseGameActivity` 에서 처리되며, 다음을 자동 수행:
  1. `GameView` 인스턴스 생성 → `setContentView(gameView)`
  2. `setRootScene { gctx -> createRootScene(gctx) }` 으로 첫 Scene 등록
  3. fullscreen + system bar 숨김
  4. `OnBackPressedCallback` 등록 → 뒤로가기는 현재 Scene 이 먼저 처리
- `onPause()` / `onResume()` 에서 `gameView.pauseGame()` / `gameView.resumeGame()` 호출 →
  Choreographer frame callback 을 끊었다가 잇고, 직전 nanos 를 0 으로 끊어 누적 frameTime 을 끊는다.
- `createRootScene()` 만 override 해서 `MainScene` 을 돌려준다.

```
┌─────────────────────────┐        ┌─────────────────────┐
│ SkyBlasterActivity      │ owns → │ a2dg.GameView       │
│  (BaseGameActivity)     │        │   (Choreographer)   │
└─────────────────────────┘        └─────────────────────┘
                                        │
                                        ▼
                                   ┌──────────────┐
                                   │ GameContext  │
                                   │  - metrics   │
                                   │  - sceneStack│
                                   │  - res       │
                                   └──────────────┘
```

---

## 3. Scene 구성 및 전환 관계

### 3-1. 현재 구현된 Scene

지금 단계에서 구현된 Scene 은 **`MainScene`** + **`LevelUpScene`** 두 개.
앱 실행 시 `SceneStack` 에 `MainScene` 만 push 된 상태로 시작하고,
플레이어가 레벨업할 때마다 `LevelUpScene` 이 그 위로 push 된다.

```
[ 평소 ]                           [ 레벨업 직후 ]

┌──────────────────────────┐       ┌──────────────────────────┐
│  SceneStack (top→bottom) │       │  SceneStack (top→bottom) │
│ ┌──────────────────────┐ │       │ ┌──────────────────────┐ │
│ │  MainScene           │ │       │ │  LevelUpScene        │ │  ← push
│ │  (clipsRect = true)  │ │       │ │  (clipsRect = true)  │ │
│ └──────────────────────┘ │       │ ├──────────────────────┤ │
└──────────────────────────┘       │ │  MainScene           │ │  ← onPause
                                   │ │  (still drawn under) │ │
                                   │ └──────────────────────┘ │
                                   └──────────────────────────┘
```

`LevelUpScene.draw()` 는 자기 자신만 그리지 않고 먼저 `mainScene.draw(canvas)` 를 호출해서
이전 화면을 그대로 띄운 뒤 그 위에 반투명 검정 + 카드 3장을 덧그린다.
이렇게 해서 "게임은 잠깐 멈춰 있고, 그 위에 카드 UI 가 뜨는" 느낌을 준다.

### 3-2. 게임 오버 시 Scene 전환

플레이어 HP 가 0 이 되면 `MainScene.gameOver = true` 로 들어가고,
이후의 모든 `update()` 가 즉시 return — 적, 총알, EnemyGenerator 가 전부 멈춘다.
HUD 가 화면 위에 반투명 검정 + `GAME OVER` + `TAP TO RESTART` 를 그린다.
화면 어디든 `ACTION_DOWN` 을 받으면 다음을 호출:

```kotlin
gctx.sceneStack.change(MainScene(gctx))
```

`SceneStack.change()` 는 stack 의 맨 위(현재 게임오버된 MainScene)만 새 MainScene 으로 교체하므로,
게임이 새 상태로 다시 시작된다 (Recycle bin / 레벨 / 무기 모두 초기화).

### 3-3. 향후 추가될 Scene (5주차 이후)

```
                    [앱 실행]
                       │
                       ▼  push
                ┌──────────────┐
                │ TitleScene   │   ← 7주차 추가 예정
                └──────────────┘
                       │
                  [GAME START 터치]
                       │
                       ▼  change
                ┌──────────────┐
       ┌────────┤  MainScene   ├────────┐
       │        └──────────────┘        │
       │  push      │           push    │
       ▼            │             ▼ (1분 타이머 — 이미 HUD에 카운트다운 표시 중)
  ┌──────────┐      │       ┌──────────────┐
  │ Pause    │      │       │  보스 진입   │
  │  Scene   │      │       │  선택 패널   │
  └──────────┘      │       └──────────────┘
       │            │             │ 진입 선택
       │ pop        │ push        │ change
       ▼            ▼             ▼
   (재개)      ┌──────────────┐  ┌──────────────┐
               │ LevelUpScene │  │  BossScene   │
               │ (transparent)│← │              │
               │    구현됨    │  └──────────────┘
               └──────────────┘          │ 보스 처치 / 사망
                                          │ change
                                          ▼
                                  ┌──────────────┐
                                  │ ResultScene  │
                                  └──────────────┘
```

### 3-4. Scene 전환에 사용되는 `a2dg.SceneStack` API

| 메서드 | 용도 | 본 프로젝트 사용 위치 |
|--------|------|------------------|
| `push(scene)` | 위에 쌓고, 아래 Scene 은 `onPause()` | `MainScene.update()` 의 post-step 에서 `LevelUpScene` 을 push |
| `pop()` | 맨 위 Scene 제거, 아래 Scene `onResume()` | `LevelUpScene.onTouchEvent` 에서 카드 선택 후 pop |
| `change(scene)` | 맨 위만 교체, 나머지 stack 유지 | `MainScene.onTouchEvent` 에서 게임오버 탭 시 새 MainScene 으로 change |

---

## 4. MainScene 의 game object 구성

MainScene 은 `World<MainScene.Layer>` 한 개를 들고 있고,
`Layer` 는 그리기/업데이트 순서를 정하는 9개의 enum 으로 구성된다.

```
Layer 순서 (먼저 적힌 것이 먼저 그려짐 → 화면 아래쪽)
  BACKGROUND → CLOUD → EXP → ENEMY_BULLET → ENEMY → BULLET → PLAYER → CONTROLLER → UI
```

`CONTROLLER` 레이어는 화면에 직접 보이지 않지만 `update()` 만 받고 일하는 객체들의 자리이다 (`EnemyGenerator`, `CollisionChecker`).

```
                  MainScene.world
                         │
       ┌──────────┬──────┼──────┬──────────────┬────────┬────────┐
       ▼          ▼      ▼      ▼              ▼        ▼        ▼
 Background  Cloud   ExpOrb*  EnemyBullet*  Enemy*   Bullet*  Player
                                              │
                                              ├ KamikazeEnemy
                                              ├ RangedEnemy
                                              └ SplitterEnemy
       ┌─────────┐
       │ Hud     │   (UI layer)
       └─────────┘
       ┌──────────────────┬──────────────────┐
       │ EnemyGenerator   │ CollisionChecker │   (CONTROLLER layer)
       └──────────────────┴──────────────────┘

(*) 표시는 다수 인스턴스 + Recycle pattern 적용
```

아래는 `MainScene` 안에 등장하는 각 game object 별 상세 설명이다.

---

### 4-1. `StarfieldBackground` — 배경 1 (별)

#### class 구성
- `IGameObject` 직접 구현. `Sprite` 를 상속하지 않는다 (이미지 없이 도형으로만 그리므로).
- 가까운 별 40개, 먼 별 60개의 위치를 `FloatArray` 로 보관.
- 가까운 별은 흰색, 먼 별은 옅은 회색-파랑. 가까운 별이 빠르게, 먼 별은 `FAR_SPEED_RATIO = 0.4f` 배 속도로 흐른다 (parallax).

#### 동작 / 상호작용
- `update()` 마다 모든 별 y 를 `speed * frameTime` 만큼 증가. 화면 아래로 나가면 위로 보내고 x 재배치.
- 다른 객체와 직접적인 상호작용은 없음. 시각적인 속도감만 담당.

---

### 4-2. `CloudLayer` — 배경 2 (구름, 교수님 5a2126b 패턴)

#### class 구성
- `IGameObject` 직접 구현. 7개의 옅은 흰파랑 구름을 `xs/ys/rs` 세 FloatArray 로 관리.
- 한 구름은 세 개의 원을 살짝 겹쳐 그려서 더 구름 같은 실루엣을 낸다.

#### 동작 / 상호작용
- 별보다 빠른 속도(`CLOUD_SPEED = 90f`)로 아래로 흐른다. parallax 두 번째 층.
- 화면 아래로 나가면 x 위치와 반지름을 새로 굴려 위에서 다시 등장.

---

### 4-3. `Player` — 플레이어

#### class 구성
- `IGameObject` + `IBoxCollidable`.
- 그림: 파란 원 + 가운데 한자 라벨 "내" (테스트용 placeholder. 이미지가 들어오면 Sprite 로 교체).
- HP 게이지(`a2dg.util.Gauge`) 와 **쿨타임 게이지** (교수님 4ccd8a7 패턴) 두 개를 인스턴스 멤버로 보유.
- `damageFlash` 타이머 — 피격 시 잠깐 빨간색이 비행기 위에 깜빡임.
- `sparkTime` 타이머 — 발사 직후 머즐(총구)에 노란 원이 잠깐 표시 (교수님 e5fab46 패턴).
- `weapon: Weapon` enum (`SINGLE`, `DUAL`, `TRIPLE`, `SPREAD`).
- `damageBonus` (스택), `fireIntervalScale` (스택) — 능력치 보상이 누적된다.
- `collisionRect` 는 비행기 그림의 절반 폭/절반 높이만 사용해 너그럽게 잡음.

#### 동작 구성
- 자동 발사: `fireCoolTime` 카운터를 `frameTime` 만큼 감소, 0 이하가 되면 한 번 `fireBullet()` 후 `currentInterval()` 로 리셋.
- 이동: 현재 → 목표를 `SPEED * frameTime` 으로 보정 이동. 화면 경계 클램프.
- HP: `damage(amount)` 호출 시 hp 감소 + `damageFlash` 시작.

#### 상호작용 (UX)
- **터치 다운**: 그 순간의 `(터치 좌표)` 와 `(현재 플레이어 좌표)` 의 차이를 `dragOffset` 으로 기억 → 화면 어디를 눌러도 비행기가 텔레포트하지 않음.
- **드래그**: 손가락 이동량만큼 `targetX/Y` 가 움직이고, 플레이어가 부드럽게 따라감.
- **CollisionChecker** 가 적/적 총알과 충돌 시 `damage()` 호출.
- **MainScene** 에서 `playerLevel` 을 읽어 총알 power 에 +1 씩 반영.
- **LevelUpScene** 이 카드 선택 후 `applyReward(reward)` 를 호출 — 무기 또는 능력치 갱신.

#### 핵심 코드 요약
```kotlin
// 무기에 따라 발사 패턴이 달라진다
when (weapon) {
    Weapon.SINGLE -> addBullet(scene, x, muzzleY, 0f, -Bullet.SPEED, basePower)
    Weapon.DUAL   -> { addBullet(scene, x - 18f, ...); addBullet(scene, x + 18f, ...) }
    Weapon.TRIPLE -> { ... 3발 나란히 ... }
    Weapon.SPREAD -> {
        addBullet(scene, x, muzzleY, 0f, -Bullet.SPEED, basePower)
        val rad = SPREAD_ANGLE_DEG * Math.PI.toFloat() / 180f
        addBullet(scene, x, muzzleY, -sin(rad)*Bullet.SPEED, -cos(rad)*Bullet.SPEED, basePower)
        addBullet(scene, x, muzzleY,  sin(rad)*Bullet.SPEED, -cos(rad)*Bullet.SPEED, basePower)
    }
}

// 능력치 보상도 같은 함수에서 처리
fun applyReward(reward: Reward) {
    when (reward) {
        Reward.DUAL_SHOT     -> weapon = Weapon.DUAL
        Reward.TRIPLE_SHOT   -> weapon = Weapon.TRIPLE
        Reward.SPREAD_SHOT   -> weapon = Weapon.SPREAD
        Reward.DAMAGE_UP     -> damageBonus += DAMAGE_UP_STEP
        Reward.ATTACK_SPEED  -> fireIntervalScale = (fireIntervalScale * 0.85f).coerceAtLeast(0.25f)
    }
}
```

---

### 4-4. `Bullet` — 플레이어 총알 (방향 지원)

#### class 구성
- `IGameObject` + `IBoxCollidable` + `IRecyclable`.
- `private constructor` + `companion object get(...)` 로 풀(pool)에서 꺼내 쓰는 패턴.
- `(vx, vy)` 를 보관 → SPREAD 처럼 비스듬히 나가는 총알도 같은 클래스로 처리.
- 작은 노란 원(`drawCircle`).

#### 동작 / 상호작용
- `update()` 마다 `(x, y) += (vx, vy) * frameTime`. 화면 어느 방향이든 멀리 벗어나면 `world.remove()` → recycle bin.
- **CollisionChecker** 가 `Enemy.collisionRect` 와 겹치면 `bullet.power` 만큼 적의 life 감소, 자기 자신은 즉시 remove.

---

### 4-5. `Enemy` (base) + 3종 자식 클래스

`Enemy` 는 `abstract`. 공통 필드(`x`, `y`, `width`, `height`, `life`, `maxLife`, `collisionRect`),
공통 동작(life 감소, 화면 밖 판정, 공유 `Gauge` 로 life 바 그리기) 을 담는다.
`expReward`, `scoreReward` 는 자식이 정의한다. `init(...)` 마다 `speedMultiplier`(현재 wave 기준)를 받아 `speed = SPEED * speedMultiplier` 로 빠르게 만든다 (교수님 8ea2c51 패턴).

```
Enemy (abstract)
 ├─ KamikazeEnemy   : 빨간 원, 추적 자폭
 ├─ RangedEnemy     : 주황 삼각, 정지 후 사격
 └─ SplitterEnemy   : 보라 사각, 분열
```

#### 4-5-a. `KamikazeEnemy` — 자폭병

- 빨간 원 + 어두운 빨강 외곽 + 가운데 흰색 "폭" 한자 라벨.
- `dx`, `dy` 단위 벡터로 이동 방향 보관. `y < LOCK_Y` 동안에는 매 프레임 플레이어 방향으로 갱신, 넘기면 잠겨 직진.
- `SPEED * speedMultiplier`. 화면 밖 → remove.
- 충돌: 플레이어와 닿으면 `damage()` 후 즉시 제거 (EXP 안 떨어짐). 총알에 죽으면 EXP 드롭, 점수 +50.

#### 4-5-b. `RangedEnemy` — 원거리 몬스터

- 주황 원 + 가운데 "원" 라벨.
- y 가 `stopY` 보다 작을 동안에만 내려옴, 도달 후에는 정지하고 `FIRE_INTERVAL = 1.6s` 마다 플레이어 쪽으로 `EnemyBullet.spawnTowards()`.

#### 4-5-c. `SplitterEnemy` — 분열형 몬스터

- 보라 원 + 가운데 "분" 라벨 (자식은 폰트 크기를 줄여 표시).
- `swayPhase` 로 sin 좌우 흔들림, y는 `speed * frameTime` 으로 내려옴.
- 처치되면 `spawnChildren()` 으로 자식 3마리(작고 약함)를 같은 위치에 추가. 부모는 같은 자식과 같은 `speedMultiplier` 를 물려준다.

---

### 4-6. `EnemyBullet` — 적 탄환

- `IGameObject` + `IBoxCollidable` + `IRecyclable`.
- `vx, vy` 자유 방향 속도. `spawnTowards(sx, sy, tx, ty)` 가 정규화 후 `SPEED = 520` 곱해 초기화.
- 플레이어 collision 시 `player.damage(damage)` 후 자기 자신 제거.

---

### 4-7. `EnemyGenerator` — 적 생성기 + Wave system

#### class 구성
- `IGameObject` 만 구현, `draw()` 는 빈 함수.
- `wave` 카운터와 `nextWaveAt` 로 12초마다 wave 증가. wave 가 오를수록 `speedMultiplier += 0.08`.
- 생성 간격은 `BASE_INTERVAL * 0.92^(wave-1)` (`MIN_INTERVAL = 0.35s` 까지).
- spawn 확률표: Kamikaze 55% / Ranged 30% / Splitter 15%.
- `scene.gameOver` 가 true 면 즉시 return — 게임오버 후엔 적이 더 안 나옴.

#### 핵심 코드 요약
```kotlin
override fun update(gctx: GameContext) {
    val scene = gctx.scene as? MainScene ?: return
    if (scene.gameOver) return

    elapsed += gctx.frameTime
    if (elapsed >= nextWaveAt) {
        wave++; nextWaveAt += WAVE_INTERVAL    // wave 진행
    }

    spawnTime -= gctx.frameTime
    if (spawnTime > 0f) return
    spawnTime = currentInterval()              // 점점 짧아짐
    spawn()
}
```

---

### 4-8. `CollisionChecker` — 충돌 처리

- `IGameObject` 만 구현, `draw()` 는 빈 함수.
- `scene.gameOver` 면 즉시 return.
- 매 프레임 3쌍 검사:
  1. `Enemy ↔ Player` → 플레이어 데미지 + 적 제거 (EXP X)
  2. `Bullet ↔ Enemy` → 적 life 감소. dead 면 EXP 드롭 + 점수 + (분열형이면) 자식 spawn.
  3. `EnemyBullet ↔ Player` → 플레이어 데미지 + 탄환 제거.
- `World.forEachReversedAt()` 로 뒤에서 앞으로 순회 → 자기 자신을 `remove()` 해도 안전.

---

### 4-9. `ExpOrb` — 경험치 구슬

- `IGameObject` + `IBoxCollidable` + `IRecyclable`. 녹색 원(+옅은 글로우).
- 적 사망 위치에서 무작위 각도로 `INITIAL_SPEED = 220` 으로 튀어나가고 마찰로 감속.
- 플레이어가 `ATTRACT_RADIUS = 240` 안에 들어오면 `ATTRACT_SPEED = 720` 으로 끌려옴.
- `PICKUP_RADIUS = 36` 안에서 `MainScene.gainExp(amount)` 호출 후 자기 제거.

---

### 4-10. `Hud` — 상단 UI + 게임오버 오버레이

#### class 구성
- `IGameObject` 만 구현. `update()` 빈 함수.
- `LabelUtil` 6종(점수 / 레벨 / EXP / Wave / Boss timer / GAME OVER 타이틀·힌트).
- `Gauge` 1개 (EXP 바).

#### 동작 / 상호작용
- `score`, `level`, `expRatio`, `wave`, `bossTimer`, `gameOver` 6 개의 public 필드를 들고 있고 `MainScene` 이 직접 채워 준다.
- `draw()`:
  - 좌상단: `Lv.`, EXP 게이지, "EXP"
  - 우상단: `SCORE`, `Wave n`
  - 상단 중앙: `BOSS in 42s` (또는 `BOSS READY`)
  - `gameOver` 면 화면 전체에 반투명 검정 + `GAME OVER` + `TAP TO RESTART`

---

### 4-11. `LevelUpScene` — 레벨업 보상 카드 (4주차 task)

#### class 구성
- `Scene` 직접 상속. `clipsRect = true`.
- `Reward.roll(3)` 으로 5종(`DUAL_SHOT`, `TRIPLE_SHOT`, `SPREAD_SHOT`, `DAMAGE_UP`, `ATTACK_SPEED`) 중 3개를 셔플해 받는다.
- 카드 3장의 `RectF` 를 미리 계산해 둔다.

#### 동작 / 상호작용
- `MainScene.update()` 가 끝난 직후 `pendingLevelUps > 0` 이면 push 됨.
- `draw()` 가 먼저 `mainScene.draw(canvas)` 를 호출해 게임 화면을 그리고, 그 위에 반투명 검정 오버레이 + "LEVEL UP!" 배너 + 카드 3장.
- `onTouchEvent`: 카드 안을 누르면 해당 reward 를 `mainScene.player.applyReward(reward)` 로 적용 후 `pop()`.
- `onBackPressed()` 는 `true` 만 반환 → 레벨업 카드는 뒤로가기로 우회 못함.

#### 핵심 코드 요약
```kotlin
override fun draw(canvas: Canvas) {
    mainScene.draw(canvas)                    // 아래 게임 화면 그대로
    canvas.drawRect(borderRect, overlayPaint) // 반투명 검정 위로
    drawText("LEVEL UP!", ...)                // 배너
    for (i in rewards.indices) {              // 카드 3장
        canvas.drawRoundRect(cardRects[i], 24f, 24f, cardBgPaint)
        // 제목, 설명, 종류 라벨...
    }
}

override fun onTouchEvent(event: MotionEvent): Boolean {
    if (event.actionMasked != ACTION_DOWN) return true
    val pt = gctx.metrics.fromScreen(event.x, event.y)
    for (i in rewards.indices) {
        if (cardRects[i].contains(pt.x, pt.y)) {
            mainScene.player.applyReward(rewards[i])
            pop()
            return true
        }
    }
    return true
}
```

---

## 5. UX 진행 방법 (사용자 입장에서의 실제 플레이 흐름)

### 5-1. 진입

1. 앱을 실행하면 곧바로 검은 우주 위에 별과 구름이 흐르고, 화면 하단 중앙에 비행기가 등장한다 (`MainScene` 부터 시작 — 타이틀은 7주차 예정).
2. 좌상단에 `Lv. 1`, EXP 게이지, "EXP". 우상단에 `SCORE  0`, `Wave 1`. 상단 중앙에 `BOSS in 60s`.
3. 비행기 머리에서 자동으로 노란 총알이 위로 발사되기 시작하고, 머즐에 노란 spark 가 짧게 깜빡인다.
4. 비행기 아래에 HP 게이지(파랑) 와 발사 쿨타임 게이지(노랑) 가 그려진다.

### 5-2. 이동

- 화면을 누르고 끌면 비행기가 손가락 이동량만큼 따라간다.
- 손가락이 떨어져도 비행기는 마지막 목표 위치를 유지.
- 화면 가장자리(가상 좌표 0~900, 0~1600) 안에서만 움직임.

### 5-3. 적 처치 / 성장

- 위에서 적 3종이 차례로 내려온다.
  - **빨간 원** 은 자폭 — 거리를 두고 처리.
  - **주황 삼각** 은 멈춰 사격 — 빠르게 처치하거나 옆으로 회피.
  - **보라 사각** 은 좌우로 흔들리며 천천히 → 처치 시 작은 사각 3마리.
- 시간이 지나면 우상단 `Wave` 가 올라가고 새 적이 더 빨리 등장.
- 적이 죽으면 녹색 EXP 구슬이 튀어나오고, 가까이 가면 자석처럼 흡수.
- EXP 가 차면 게임이 잠시 멈추고 **레벨업 카드 3장**이 화면 중앙에 뜬다.
  - 파란 카드(WEAPON): Dual Shot / Triple Shot / Spread Shot
  - 초록 카드(STAT): Damage + / Attack Speed +
  - 카드 한 장을 터치하면 즉시 적용되고 게임이 재개된다.
  - WEAPON 은 항상 현재 무기를 교체하고, STAT 은 누적된다.

### 5-4. 보스 진입 카운트다운

- 상단 중앙에 `BOSS in 42s` 식의 카운트다운이 진행. 0초가 되면 `BOSS READY` 로 바뀐다 (보스 Scene 자체는 6주차 예정).

### 5-5. 피격 / 게임오버

- 적 본체와 닿으면 데미지(`ENEMY_TOUCH_DAMAGE = 15`) + 비행기에 빨간 깜빡임 + 적도 사라짐.
- 적 탄환 맞으면 데미지(`EnemyBullet.DEFAULT_DAMAGE = 10`).
- HP 가 0 이 되면 모든 게임 오브젝트가 정지 + 화면 위에 반투명 검정 + **`GAME OVER` / `TAP TO RESTART`**.
- 화면을 한 번 터치하면 새 `MainScene` 으로 바뀌어 처음부터 다시 시작.

### 5-6. 디버그 표시 (Debug build only)

`SkyBlasterActivity` 에서 `BuildConfig.DEBUG` 일 때 다음이 표시된다:

- 가상 좌표계 격자 (회색 100단위)
- 모든 `IBoxCollidable` 객체의 collision box (빨간 사각 외곽선)
- 좌상단의 layer 별 오브젝트 수, FPS
- 하단의 frame time 그래프

빌드 시 거슬리면 `SkyBlasterActivity.kt` 의 `drawsDebugGrid` / `drawsDebugInfo` / `drawsFpsGraph` 를 false 로 둔다.

### 5-7. 뒤로가기

- 시스템 뒤로가기 → 현재 Scene 의 `onBackPressed()` 가 호출됨.
- `MainScene` 단독일 때는 false → Activity 종료.
- `LevelUpScene` 이 위에 있을 때는 true 만 반환해서 뒤로가기로 우회 불가능 (반드시 카드 한 장은 골라야 함).

---

## 6. 빌드 / 실행 요약

1. Android Studio 에서 프로젝트 폴더(`SmartphoneTermProject/`)를 연 뒤 Gradle Sync.
2. `:a2dg` 모듈이 인식되는지 확인 — `settings.gradle.kts` 에 `include(":a2dg")` 와 `app/build.gradle.kts` 의 `implementation(project(":a2dg"))` 필수.
3. **Run 'app'** 으로 디바이스 또는 에뮬레이터에서 실행.
