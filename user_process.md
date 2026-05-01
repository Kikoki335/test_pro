# Sky Blaster — 4주간 작업 요약 (다음 프로젝트용 가이드)

이 문서는 다른 프로젝트에서 비슷한 종스크롤 슈팅 × 로그라이크를 만들 때 참고할 **commit 순서 + 핵심 결정 사항** 요약. 자세한 사양은 `WORK_PLAN.md` 참조.

핵심 원칙: **한 commit 에 둘 이상의 시스템 섞지 말 것**. 검증 단계 (적/무기 종류별 단독 spawn) 도 별도 commit 으로 분리하지 않고 그 sub-task 의 일부.

---

## 1주차 — 5 commits (BIG framework: VertScrollBackground, IRecyclable, IBoxCollidable, EnemyGenerator)

| # | 내용 | 핵심 |
|---|---|---|
| 1 | 타이틀 화면 → 빈 게임 화면 | XML MainActivity + GAME START 버튼 → MainScene change. 자동 진입 X |
| 2 | VertScrollBackground 종스크롤 배경 | 배경 1장 + scroll speed. tileable 한 비트맵 (위/아래 edge 가 이어져야) |
| 3 | Player + 터치 드래그 이동 | targetX/Y + lerp 패턴. X+Y 자유 이동. 화면 경계 clamp. **사이즈 200×200, MAX_LIFE 10 처음부터** |
| 4 | Player 자동 발사 + Bullet (ObjectPool) | `Bullet.get()` private constructor + IRecyclable. FIRE_INTERVAL 0.3s. **사이즈 56×112** |
| 5 | 시간 경과 시 BossScene placeholder 전환 | `MainScene.elapsedSec` 누적 + `BOSS_ENTER_TIME = 60f` (실 사양). `BossScene = MainScene wrapper`, `MainScene.isBossStage: Boolean` (val) **처음부터** — `if (!isBossStage) add(enemyGenerator)` + `BossTimerHud` 가 isBossStage 시 "BOSS STAGE" 텍스트 |

**주의**: 1주차 cutoff 이전에 `clipsRect`, `screenOrientation`, 두 번째 parallax 등은 사용 X (2주차 framework).

---

## 2주차 — 7 commits (SMALL: clipRect, borderRect, GameView clip / 별 parallax)

| # | 내용 | 핵심 |
|---|---|---|
| 1 | 별 parallax 레이어 | `STARS` layer (ENEMY 뒤·CONTROLLER 앞) + `VertScrollBackground` 두 번째 인스턴스. parallax 비트맵은 위/아래 buffer (투명 ~150px) 로 이음새 숨김 |
| 2 | a2dg framework 갱신 | `Scene.clipsRect` + `GameMetrics.borderRect` + `GameView clip`. MainScene `clipsRect = true` |
| 3 | 캐릭터/적/배경 진짜 PNG 도입 | placeholder 도형 → 실제 sprite. tileable 보장. `weapon_*`, `enemy_*` 등 |
| 4 | AndroidManifest 정리 + HUD 자리 | `appCategory="game"` + `screenOrientation="nosensor"`. HP/Score/Timer HUD 자리만 잡음 |
| 5 | Enemy 3종 + EnemyGenerator + Bullet↔Enemy + Enemy HP gauge | Type enum (resId, w, h, hp, speed, score, **hitDamage**). 정착값 처음부터 (사이즈 130/155/120/70, hp 1/2/3/1, hitDamage 2/1/1/1). 정적 공유 Gauge. CollisionChecker 분리. |
| 6 | Player↔Enemy 충돌 + 점수 가산 | Player IBoxCollidable, MAX_LIFE 10, ScoreLabel displayScore lerp 처음부터, PlayerHpHud 좌하단/초록 |
| 7 | BossScene = 노말맵 + 배경만 다르게 | MainScene `open` + (`backgroundResId`, `isBossStage`) 두 파라미터. BossScene 한 줄 wrapper. 모든 IBoxCollidable inset 0.8 처음부터 |

---

## 3주차 — 5 commits (NONE: framework 신규 0건, 1·2주차 도구로 Enemy 공격 행동 + VFX)

**commit 분할 단위 (한 commit 에 두 type 섞지 말 것)**:

| # | 내용 | 핵심 |
|---|---|---|
| 1 | SUICIDE 자폭 | `Enemy.update` 를 `when (type)` 분기 베이스. SUICIDE = 화면 높이 40% 도달 시 Player 위치 lock-on → 1.6× 속도 직진 (한 번만 lock). `hitDamage` Type enum 에 포함 (SUICIDE = 2, 나머지 1). EnemyGenerator 임시 `Type.SUICIDE` only |
| 2 | RANGED + EnemyBullet | `EnemyBullet` 신규 (vx/vy 인자 처음부터, default = 직하). **Y 랜덤화** (`RANGED_STOP_RATIO_MIN/MAX = 0.22/0.35`) — 매 RANGED 다른 라인 정지. 정지 후 1.2초 간격으로 **Player 방향 aimed 발사** (단위벡터). 죽을 때까지 발사 (RETREATING 단계 없음). `ENEMY_BULLET` layer |
| 3 | SPLIT 분열 + minion 자폭 | SPLIT 본체 = SUICIDE 와 같은 lock-on 자폭 (`updateSuicide` 공유). 죽으면 `startDying` 안에서 SPLIT_MINION 2마리 좌·우 30° 사선 spawn. minion 은 0.3초 사선 비행 후 lock-on 자폭 (`lockDiveTarget` 재사용) |
| 4 | VFX (hit + die, 모두 "주체가 자기 draw") | framework `laser_spark` 패턴 (DragonFlight `Player.kt:38, 117~125`) 의 "주체가 자기 draw 에서 직접 그리고 짧게 사라진다" 를 hit/die 양쪽 일관 적용. **별도 Effect 클래스 / EFFECT layer / muzzle flash 단계 모두 채택 안 함**. Bullet/EnemyBullet 에 `hitting` 상태, Enemy 에 `dying` 상태. `HIT_DURATION = DIE_DURATION = 0.1f`. `collisionRect.setEmpty()` 로 추가 충돌 자동 skip |
| 5 | EnemyGenerator random 복원 | `SPAWNABLE_TYPES = Enemy.Type.entries.filter { it != SPLIT_MINION }` 캐시 + `.random()` |

**주의**:
- 검증 단계는 EnemyGenerator 의 type 한 줄을 `Type.SUICIDE` → `Type.RANGED` → `Type.SPLIT` 순으로 바꿔가며 단독 검증. 사용자가 디바이스에서 OK 하면 다음 commit.
- VFX die effect 도입 시 처음에 별도 `Effect` 클래스 + `EFFECT` layer 시도하다가 framework 패턴 어긋나서 폐기. 처음부터 `dying` 상태로 가는 게 정답.

---

## 4주차 — 6 commits (MEDIUM: CookieRun, MapObject Registry / SheetSprite / AnimSprite 보강)

| # | 내용 | 핵심 |
|---|---|---|
| 1 | EXP 시스템 (ExpOrb + ExpLabel) | `ExpOrb` = `IGameObject + IBoxCollidable + IRecyclable` (Sprite 상속 X, 가벼운 형태). cyan 원 placeholder. **drop 즉시 Player homing** (거리 제한 없음, ATTRACT_SPEED 800f/s). 흡수 시 `player.gainExp(1)`. `Enemy.startDying` 에서 SUICIDE/RANGED/**SPLIT_MINION** drop, **SPLIT 본체 X** (분열 책임 minion 으로). `EXP_ORB` layer. `ExpLabel` = 좌하단 HP 게이지 옆 cyan "Lv.N EXP e/m" |
| 2 | 레벨업 구조 (LevelUpScene + placeholder 카드) | Player 에 `level / maxExp / levelUp()` 추가. `INITIAL_MAX_EXP = 5`, `MAX_EXP_GROWTH = 1.5f`. `LevelUpScene : Scene` — MainScene 위 `push()` 로 게임 정지 (SceneStack 이 stack top 만 update). 카드 3장 placeholder (모두 같은 텍스트, 어느 거나 levelUp + pop). `BOSS_ENTER_TIME` 10f → **60f** 정착 |
| 3 | 보상 카드 (공격력 / 공속 / 치명타) + 디버그 HUD | Player 에 `attackMul / fireRateMul / critRate` (탄환수 stat 은 무기 시스템으로 이동). `Bullet.power` 인스턴스 필드 (DragonFlight Player.kt:181 패턴). `LevelUpScene` 카드 3장 분기 — ATK x2 / RATE +30% / CRIT +50%, `CRIT_MUL = 3`. `DebugStatLabel` 좌하단 (framework debug 와 같은 textSize 40 MONOSPACE WHITE), framework `GameView.debugPaint` 색 BLUE → WHITE |
| 4 | Weapon Registry + 4종 + 등급 | `sealed class Weapon` + `enum WeaponGrade { RARE, EPIC }` (전설은 5주차 스킬). 4종 object: `DefaultWeapon` / `ShotgunWeapon` / `LaserWeapon` / `MissileWeapon`. **등급 차이**: 샷건/호밍 = 발수, 레이저 = **굵기** (희귀 폭 120 / 영웅 폭 300, lifetime 통일 1.0f). `LaserBeam` / `HomingMissile` 별도 클래스. `Bullet` 에 vx/vy + spriteResId/hitVfxResId 인자. HomingMissile 은 sprite 회전 (`canvas.rotate`), collisionRect 는 axis-aligned. `LaserBeam` 충돌 폭 = `beamHalf × 0.15` (코어만, 글로우 제외) |
| 5 | 무기 보상 카드 (RewardCard + CardPool + LevelUpScene 통합) | `sealed class RewardCard` — stat 카드 3 (object) + `WeaponCard(weapon, grade)` (class). `CardPool` — statCards 3 (영구) + weaponCards 6 (3 × 2 등급). `pickThree()` 무작위 추출, `consume()` 은 WeaponCard 만 풀에서 제거. `LevelUpScene` 카드는 sprite + 등급 색 (희귀 파랑 96/165/250 / 영웅 보라 168/85/247). `Weapon.cardSpriteResId` 추가. `WeaponGrade.cardColor` 추가 |
| 6 | 풀 룰 보강 (영웅 받으면 같은 무기 희귀도 제외) | `CardPool.consume` 의 EPIC 분기 — `if (card.grade == EPIC) weaponCards.removeAll { it.weapon == card.weapon && it.grade == RARE }`. 영웅 받으면 다운그레이드 카드 안 보이게. 희귀 받으면 영웅은 그대로 (업그레이드 가능) |

**주의**:
- 4주차 #1 EXP HUD 위치는 좌하단 HP 게이지 끝에서 25px 띄움 + 같은 baseline.
- 4주차 #3 의 `Bullet.power` 인스턴스화는 4주차 #4 무기 시스템에서도 그대로 사용 (각 Weapon 의 fire 가 power 계산 후 전달).
- 4주차 #4 의 검증 단계는 `Player.currentWeapon` 한 줄을 무기 종류 + 등급 (ShotgunWeapon RARE → LaserWeapon EPIC 등) 으로 바꿔가며 단독 시각 확인.

---

## 다음 프로젝트 시작 시 따라갈 순서 — 한 줄 요약

1. **1주차**: 타이틀 → 배경 → Player → Bullet → BossScene placeholder
2. **2주차**: 별 parallax → framework clipRect → 진짜 sprite → HUD 자리 → Enemy 3종 → Player 충돌 → BossScene 노말맵
3. **3주차**: SUICIDE → RANGED → SPLIT → VFX (hitting/dying 상태) → random 복원
4. **4주차**: EXP → 레벨업 구조 → stat 보상 카드 → Weapon Registry + 4종 → 무기 보상 카드 → 풀 룰 보강

각 주차 cutoff (4/10, 4/17, 4/24, 5/1) 이후 framework 신규 도구 사용 X. 시각/사이즈 정착값은 **§11#16 정착값 가이드** 그대로 처음부터 적용 (placeholder → 진짜 사이즈 단계 거치지 말 것).

---

## 시행착오로 배운 것 (다음에는 시도 X)

1. **die effect 를 별도 GameObject** (`Effect.kt + EFFECT layer`) 만들기 시도 → 폐기. **dying 상태로 enemy 안에 두는 게 정답** (laser_spark 패턴 확장).
2. **muzzle flash** 자산을 발사 시 표시 → 폐기. **같은 자산을 hit vfx 로 재배치**가 사격 게임 피드백에 더 자연.
3. **bulletCount stat** (탄환수 +1) → 폐기. 무기 등급 영역으로 이동, **critRate** stat 으로 교체.
4. **LaserBeam 의 lifetime 등급별 차이** → 폐기. **굵기 차이로 통일** (lifetime 1.0f 통일).
5. **HomingMissile 의 collisionRect 회전 (AABB)** 시도 → 사용자 요청으로 axis-aligned 원복.
6. **PNG 파일 이름** 대문자/하이픈/공백/한글 → Android resource 규칙 위반. 처음부터 lowercase + underscore 만.
7. **Enemy.startDying 의 ExpOrb drop 룰** — 처음에 SPLIT 본체가 drop, MINION 제외였다가 → 사용자 결정으로 **SPLIT 본체 제외, MINION 처치 시 drop** (분열 보상 구조).
