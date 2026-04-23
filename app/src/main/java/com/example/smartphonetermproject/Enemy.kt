package com.example.smartphonetermproject

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.util.Gauge
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.random.Random

class Enemy private constructor(
    private val gctx: GameContext,
) : Sprite(gctx, Type.SUICIDE.resId), IBoxCollidable, IRecyclable {

    enum class Type(
        val resId: Int,
        val width: Float,
        val height: Float,
        val hp: Int,
        val speed: Float,
        val score: Int,
        val hitDamage: Int,
    ) {
        // hitDamage = Player 와 충돌했을 때 Player.life 가 깎이는 양.
        // SUICIDE 만 자폭형이라 2, RANGED/SPLIT 은 단순 접촉 데미지 1.
        // SPLIT_MINION 은 SPLIT 본체가 죽었을 때 분열로만 등장하므로 EnemyGenerator 의
        // 일반 spawn 풀에서는 제외한다 (#4 random 복원 단계에서 entries 필터링).
        // 95/110/85/50 → 130/155/120/70. 3주차 #4 시점에 사용자가 모든 객체 일괄 1.4배 결정.
        SUICIDE(R.mipmap.enemy_suicide, 130f, 130f, 1, 280f, 10, 2),
        RANGED(R.mipmap.enemy_ranged, 155f, 155f, 2, 150f, 20, 1),
        SPLIT(R.mipmap.enemy_split, 120f, 120f, 3, 220f, 30, 1),
        SPLIT_MINION(R.mipmap.enemy_split_minion, 70f, 70f, 1, 350f, 5, 1),
    }

    val score: Int
        get() = type.score
    val hitDamage: Int
        get() = type.hitDamage

    private lateinit var type: Type
    var life = 0
        private set
    var maxLife = 0
        private set
    private var speed = 0f

    // SUICIDE 의 lock 후 / SPLIT_MINION 의 init 직후 모두 같은 "고정 방향 직진" 패턴이라
    // diveVx, diveVy 한 쌍을 공유한다. diving=true 이면 update 가 직선 하강이 아니라 dive 속도로 움직인다.
    private var diving = false
    private var diveVx = 0f
    private var diveVy = 0f

    // RANGED 전용 상태 머신. APPROACHING → ATTACKING 두 단계만 — 정지 후엔 죽을 때까지 발사.
    private enum class RangedPhase { APPROACHING, ATTACKING }
    private var rangedPhase = RangedPhase.APPROACHING
    private var rangedFireCooldown = 0f
    // 정지하는 화면 높이 비율. spawn 시점에 RANGED_STOP_RATIO_MIN~MAX 사이에서 random 으로 결정 —
    // 매 RANGED 가 같은 라인에 정지하지 않고 위·아래로 흩어지게 해 시각적/회피 패턴 다양성 확보.
    private var rangedStopRatio = 0f

    // SPLIT_MINION 전용 — 분열 직후 짧은 지연 동안 사선 비행 → 그 시점 Player 위치로 lock-on 후 직진.
    private var minionLockDelay = 0f
    private var minionLocked = false

    // dying 단계. life 가 0 이 되면 즉시 ENEMY layer 에서 빠지지 않고, dyingTime 동안 본체 sprite 와
    // HP gauge 는 멈춘 채 die vfx 만 자기 draw 에서 그리다가 self-remove 한다 (laser_spark 와
    // 같은 "주체가 자기 draw 에서 직접 그림" 원칙). 이 동안 collisionRect 는 빈 사각형이라 어떤
    // 충돌 검사에도 잡히지 않는다.
    private var dying = false
    private var dyingTime = 0f
    private val dieRect = RectF()

    val dead: Boolean
        get() = life <= 0

    override var width = 0f
    override var height = 0f
    override var x = 0f
    override var y = 0f

    // Enemy 충돌 박스는 placeholder 단계에서는 dstRect 와 같게 둔다.
    // (DragonFlight 4/9 는 11f inset 으로 그림보다 안쪽에서 충돌 — Sky Blaster 는 7주차 그래픽 교체 시 재검토)
    // collisionRect 를 별도 RectF 로 두는 이유는, 추후 inset 도입 시 dstRect 와 분리해 관리하기 위함.
    override val collisionRect = RectF()

    init {
        // 모든 Enemy 인스턴스가 정적 Gauge 하나를 공유한다 (DragonFlight 패턴, 4/3 `be491bf`).
        // Gauge 는 progress 0~1 만 받는 stateless 그리기 도구이므로 매 Enemy 마다 새로 만들 필요가 없다.
        if (sharedGauge == null) {
            sharedGauge = Gauge(GAUGE_THICKNESS, GAUGE_FG_COLOR, GAUGE_BG_COLOR)
        }
        // type 별 die vfx 도 sharedGauge 와 같은 방식으로 공유한다 — 풀에서 재사용되는 enemy
        // 인스턴스가 매번 bitmap 을 새로 로드하지 않도록.
        if (sharedDieBitmaps.isEmpty()) {
            for (t in Type.entries) {
                sharedDieBitmaps[t] = gctx.res.getBitmap(dieResIdFor(t))
            }
        }
    }

    fun init(
        x: Float,
        type: Type,
        // null 이면 화면 위쪽 (보이지 않는 곳) 에서 시작. SPLIT 분열 시에는 부모 위치를 그대로 넘긴다.
        startY: Float? = null,
        // SPLIT_MINION 이 사선으로 튀어나갈 때 쓰는 각도. "수직 아래 = 0, 우측 = +" 기준.
        angleFromVerticalDeg: Float = 0f,
    ): Enemy {
        this.type = type
        this.bitmap = gctx.res.getBitmap(type.resId)
        this.width = type.width
        this.height = type.height
        this.life = type.hp
        this.maxLife = type.hp
        this.speed = type.speed
        this.x = x
        this.y = startY ?: -type.height / 2f

        // ObjectPool 에서 재사용될 때 이전 인스턴스의 dive/RANGED/MINION/dying 상태가 남아 있으면 안 된다.
        diving = false
        diveVx = 0f
        diveVy = 0f
        rangedPhase = RangedPhase.APPROACHING
        rangedFireCooldown = 0f
        rangedStopRatio = RANGED_STOP_RATIO_MIN +
            Random.nextFloat() * (RANGED_STOP_RATIO_MAX - RANGED_STOP_RATIO_MIN)
        minionLockDelay = 0f
        minionLocked = false
        dying = false
        dyingTime = 0f

        // SPLIT_MINION 만 spawn 시점에 즉시 dive 모드 (사선 방향) + lock 예약.
        if (type == Type.SPLIT_MINION) {
            val rad = Math.toRadians(angleFromVerticalDeg.toDouble())
            diveVx = sin(rad).toFloat() * speed
            diveVy = cos(rad).toFloat() * speed
            diving = true
            minionLockDelay = MINION_LOCK_DELAY
        }

        syncDstRect()
        updateCollisionRect()
        return this
    }

    override fun update(gctx: GameContext) {
        // dying 동안에는 이동/공격을 멈추고 die vfx 만 자기 draw 에서 표시되도록 시간만 카운트.
        // dyingTime 이 다 되면 self-remove — CollisionChecker 가 직접 layer 에서 빼지 않고
        // 죽은 주체가 스스로 마지막을 정리하는 책임을 가진다.
        if (dying) {
            dyingTime -= gctx.frameTime
            if (dyingTime <= 0f) {
                val scene = gctx.scene as? MainScene ?: return
                scene.world.remove(this, MainScene.Layer.ENEMY)
            }
            return
        }

        when (type) {
            // SPLIT 본체도 SUICIDE 와 동일한 lock-on 자폭 — 다른 점은 HP 가 3 (Bullet 3발) 이라
            // 더 오래 살고, 죽었을 때 startDying 안에서 minion 2마리로 분열한다는 것뿐.
            Type.SUICIDE, Type.SPLIT -> updateSuicide(gctx)
            Type.RANGED -> updateRanged(gctx)
            Type.SPLIT_MINION -> {
                moveByDiveVelocity(gctx)
                updateMinionLock(gctx)
            }
        }
        syncDstRect()
        updateCollisionRect()

        // dive 가 사선 방향이라 좌·우로도 화면 밖으로 빠져 나갈 수 있다.
        // 어느 한 방향이라도 완전히 화면 밖이면 ENEMY layer 에서 제거 → 풀로 환수.
        val outBottom = y - height / 2f > gctx.metrics.height
        val outRight = x - width / 2f > gctx.metrics.width
        val outLeft = x + width / 2f < 0f
        if (outBottom || outRight || outLeft) {
            val scene = gctx.scene as? MainScene ?: return
            scene.world.remove(this, MainScene.Layer.ENEMY)
        }
    }

    private fun moveByDiveVelocity(gctx: GameContext) {
        x += diveVx * gctx.frameTime
        y += diveVy * gctx.frameTime
    }

    // SUICIDE 동작:
    //   1) 화면 위에서 아래로 직선 하강 (다른 종류와 동일).
    //   2) y 가 화면 높이의 SUICIDE_LOCK_RATIO (현재 0.4) 지점을 넘으면 그 시점 Player 위치를
    //      한 번만 읽어 방향을 정하고, 속도 (speed × SUICIDE_DIVE_MUL) 로 그 방향으로 직진.
    //   3) 이후로는 추적하지 않는다 — 자폭병이 "결심하면 끝까지 직진" 하는 느낌.
    //      Player 가 옆으로 빠지면 그대로 빗나가 화면 밖으로 사라진다.
    private fun updateSuicide(gctx: GameContext) {
        if (!diving) {
            y += speed * gctx.frameTime
            if (y >= gctx.metrics.height * SUICIDE_LOCK_RATIO) {
                lockDiveTarget(gctx)
            }
        } else {
            moveByDiveVelocity(gctx)
        }
    }

    private fun lockDiveTarget(gctx: GameContext) {
        val player = (gctx.scene as? MainScene)?.player ?: return
        val dx = player.x - x
        val dy = player.y - y
        val len = hypot(dx, dy)
        // 거의 같은 위치면 방향이 불안정하므로 그냥 직선 하강 유지.
        if (len < 1f) return
        val diveSpeed = speed * SUICIDE_DIVE_MUL
        diveVx = dx / len * diveSpeed
        diveVy = dy / len * diveSpeed
        diving = true
    }

    // RANGED 동작:
    //   APPROACHING : 직선 하강하다 화면 높이의 RANGED_STOP_RATIO (현재 0.27) 도달 시 정지.
    //   ATTACKING   : 그 자리에서 RANGED_FIRE_INTERVAL 간격으로 EnemyBullet 발사 — Bullet 으로 처치될 때까지.
    //                  (도착 직후 발사 X — fireCooldown 을 한 주기 채워 둠)
    private fun updateRanged(gctx: GameContext) {
        when (rangedPhase) {
            RangedPhase.APPROACHING -> {
                y += speed * gctx.frameTime
                if (y >= gctx.metrics.height * rangedStopRatio) {
                    rangedPhase = RangedPhase.ATTACKING
                    rangedFireCooldown = RANGED_FIRE_INTERVAL
                }
            }
            RangedPhase.ATTACKING -> {
                rangedFireCooldown -= gctx.frameTime
                if (rangedFireCooldown <= 0f) {
                    rangedFireCooldown = RANGED_FIRE_INTERVAL
                    fireRangedBullet(gctx)
                }
            }
        }
    }

    private fun fireRangedBullet(gctx: GameContext) {
        val scene = gctx.scene as? MainScene ?: return
        val muzzleX = x
        val muzzleY = y + height / 2f + ENEMY_BULLET_OFFSET
        // 발사 시점 Player 위치를 한 번 읽어 그쪽으로 EnemyBullet.SPEED 단위벡터 발사.
        // Player 가 좌우로 빠르게 움직이면 1.2초 (RANGED_FIRE_INTERVAL) 마다 다른 방향으로 쏘므로
        // "위치 회피" 가 의미 있는 회피 패턴이 된다.
        val player = scene.player
        val dx = player.x - muzzleX
        val dy = player.y - muzzleY
        val len = hypot(dx, dy)
        val (vx, vy) = if (len < 1f) {
            // RANGED 가 Player 와 거의 같은 위치인 극단적 케이스 — 방향 불안정해서 직하로 fallback.
            0f to EnemyBullet.SPEED
        } else {
            (dx / len * EnemyBullet.SPEED) to (dy / len * EnemyBullet.SPEED)
        }
        val bullet = EnemyBullet.get(gctx, muzzleX, muzzleY, vx, vy)
        scene.world.add(bullet, MainScene.Layer.ENEMY_BULLET)
    }

    // SPLIT_MINION 동작:
    //   분열 직후 MINION_LOCK_DELAY 동안은 그대로 사선 비행 (init 에서 정한 좌·우 30° 방향).
    //   delay 가 끝나면 그 시점 Player 위치를 향해 lock-on (lockDiveTarget 재사용 — diveVx/Vy 갱신).
    //   이후로는 이미 diving=true 이므로 같은 dive 코드가 새 방향으로 직진 → Player 에게 자폭.
    //   SUICIDE 와 같은 lock-on 자폭이지만, 시작 위치가 분열 지점 (화면 어디든) 이고 사이즈/속도/숫자
    //   (작고 빠르고 2마리) 가 달라 회피 패턴이 달라진다.
    private fun updateMinionLock(gctx: GameContext) {
        if (minionLocked) return
        minionLockDelay -= gctx.frameTime
        if (minionLockDelay <= 0f) {
            lockDiveTarget(gctx)
            minionLocked = true
        }
    }

    // CollisionChecker 가 enemy.dead 를 확인한 직후 호출한다.
    // 즉시 layer 에서 빼지 않고 dying 상태로 전환 — DIE_DURATION 동안 본체는 멈춘 채 die vfx 만
    // 자기 draw 에서 그리다가 self-remove. SPLIT 본체에 한해 같은 시점에 SPLIT_MINION 두 마리 분열 spawn.
    // Bullet 처치 / Player 충돌 자폭 둘 다 같은 startDying 경로를 탄다.
    fun startDying(scene: MainScene) {
        if (dying) return  // 이미 dying 이면 중복 호출 무시 (idempotent)
        dying = true
        dyingTime = DIE_DURATION
        // dying 즉시 충돌 박스를 비워서 같은 프레임에 도는 다른 충돌 검사도 이 enemy 를 통과하게.
        collisionRect.setEmpty()

        if (type == Type.SPLIT) {
            for (angleDeg in MINION_ANGLES) {
                val minion = get(
                    gctx,
                    x = x,
                    type = Type.SPLIT_MINION,
                    startY = y,
                    angleFromVerticalDeg = angleDeg,
                )
                scene.world.add(minion, MainScene.Layer.ENEMY)
            }
        }
    }

    override fun draw(canvas: Canvas) {
        // dying 동안에는 본체 sprite / HP gauge / muzzle flash 모두 그리지 않고 die vfx 만
        // 자기 위치에 표시. DragonFlight 의 laser_spark 와 같은 "주체가 자기 draw 에서 직접 그림"
        // 원칙을 die 효과에도 동일하게 적용한 형태.
        if (dying) {
            val dieBitmap = sharedDieBitmaps[type] ?: return
            val size = width * DIE_SIZE_MUL
            dieRect.set(
                x - size / 2f,
                y - size / 2f,
                x + size / 2f,
                y + size / 2f,
            )
            canvas.drawBitmap(dieBitmap, null, dieRect, null)
            return
        }

        super.draw(canvas)

        // HP gauge 는 Enemy 머리 위에 적의 폭의 70% 길이로 그린다.
        // life / maxLife 비율을 progress 로 넘기면 Gauge 가 알아서 잘라 그려 준다.
        val gauge = sharedGauge ?: return
        val gaugeWidth = width * 0.7f
        val gaugeX = x - gaugeWidth / 2f
        val gaugeY = y - height / 2f - GAUGE_OFFSET_FROM_TOP
        gauge.draw(canvas, gaugeX, gaugeY, gaugeWidth, life.toFloat() / maxLife)
    }

    fun decreaseLife(damage: Int) {
        life -= damage
    }

    private fun updateCollisionRect() {
        // 충돌 박스는 그림 영역의 80% (양쪽 10% 씩 안쪽으로) — 캐릭터 PNG 의 투명 여백 보정.
        val halfW = width * COLLISION_INSET_RATIO / 2f
        val halfH = height * COLLISION_INSET_RATIO / 2f
        collisionRect.set(x - halfW, y - halfH, x + halfW, y + halfH)
    }

    override fun onRecycle() {
    }

    companion object {
        // gauge 와 type 별 die bitmap 은 모든 Enemy 인스턴스가 공유 (sharedGauge 패턴).
        private var sharedGauge: Gauge? = null
        private val sharedDieBitmaps = mutableMapOf<Type, Bitmap>()
        private const val GAUGE_THICKNESS = 0.12f
        private val GAUGE_FG_COLOR = Color.GREEN
        private val GAUGE_BG_COLOR = Color.argb(180, 0, 0, 0)
        private const val GAUGE_OFFSET_FROM_TOP = 8f
        private const val COLLISION_INSET_RATIO = 0.8f

        // dying 단계 지속 시간. 사용자 결정 — 일단 muzzle flash 와 같은 0.1 초로 두어 "깜빡 보이고
        // 사라진다" 정도로 가볍게. 더 임팩트 있는 vfx (alpha/scale 변화 등) 는 7주차 폴리싱에서 검토.
        private const val DIE_DURATION = 0.1f
        // die vfx 는 적 폭의 1.5 배. 본체 사이즈에 비례시켜 작은 minion 은 작게, 큰 RANGED 는 크게.
        private const val DIE_SIZE_MUL = 1.5f

        // SPLIT 만 분열 vfx 가 die 효과 대용 — 별도 die PNG 가 없음.
        private fun dieResIdFor(type: Type) = when (type) {
            Type.SUICIDE -> R.mipmap.vfx_suicide_die
            Type.RANGED -> R.mipmap.vfx_ranged_die
            Type.SPLIT -> R.mipmap.vfx_split_burst
            Type.SPLIT_MINION -> R.mipmap.vfx_minion_die
        }

        // SUICIDE: 화면 높이의 40% 지점에 도달하면 Player 방향으로 lock-on,
        // 그 시점 speed 의 1.6 배로 직진 (=280 × 1.6 = 448f/s).
        private const val SUICIDE_LOCK_RATIO = 0.4f
        private const val SUICIDE_DIVE_MUL = 1.6f

        // RANGED: spawn 시점에 화면 위쪽 [22%, 35%] 사이 한 점을 random 추출해 그 라인에서 정지,
        // 그 후 1.2초 간격으로 Bullet 에 처치될 때까지 계속 발사. 매 인스턴스가 다른 Y 라인에
        // 멈추므로 화면 위쪽이 한 줄로 뭉치지 않고 위·아래로 흩어진다.
        // 누적 밸런스는 3주차 #5 random 복원 단계에서 spawn 빈도/wave 로 조정.
        private const val RANGED_STOP_RATIO_MIN = 0.22f
        private const val RANGED_STOP_RATIO_MAX = 0.35f
        private const val RANGED_FIRE_INTERVAL = 1.2f
        // RANGED 의 발 아래 (sprite 바닥에서 살짝 떨어진 곳) 에서 EnemyBullet 발사.
        private const val ENEMY_BULLET_OFFSET = 8f

        // SPLIT 분열 시 minion 이 튀어나가는 각도 (수직선 기준, 좌·우 30도).
        // 가운데 직하 각도는 SPLIT 본체와 거의 같은 궤도라 시각적 분열감이 약해 제외.
        private val MINION_ANGLES = listOf(-30f, 30f)
        // SPLIT_MINION 이 분열 직후 사선 비행을 유지하다 Player 방향으로 lock 하기까지의 지연.
        // 너무 짧으면 본체와 거의 같은 위치에서 lock 해 SUICIDE 와 구분이 안 가고, 너무 길면
        // minion 이 화면 밖으로 빠져나간 뒤 lock 되어 의미가 없어진다. 0.3 정도가 적당.
        private const val MINION_LOCK_DELAY = 0.3f

        fun get(
            gctx: GameContext,
            x: Float,
            type: Type,
            startY: Float? = null,
            angleFromVerticalDeg: Float = 0f,
        ): Enemy {
            val scene = gctx.scene as? MainScene
                ?: return Enemy(gctx).init(x, type, startY, angleFromVerticalDeg)
            val enemy = scene.world.obtain(Enemy::class.java) ?: Enemy(gctx)
            return enemy.init(x, type, startY, angleFromVerticalDeg)
        }
    }
}
