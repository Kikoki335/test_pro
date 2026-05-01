package com.example.smartphonetermproject

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.collidesWith
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// LaserWeapon 이 발사하는 직선 관통 빔. spawn 시점부터 lifetime 동안 살아남으며 매 frame Player 의
// 머리 위 ~ 화면 위 끝까지 세로 사각형으로 그려진다 — Player 가 좌우로 움직이면 빔도 따라간다.
//
// 데미지는 LASER_TICK_INTERVAL 마다 한 번씩 ENEMY layer 의 모든 enemy 와 collidesWith 검사. 즉
// 한 enemy 가 빔 안에 머무는 동안 일정 간격으로 반복 데미지. CollisionChecker 에 별도 분기를 넣지
// 않고 이 클래스가 자기 update 안에서 직접 처리 (BULLET ↔ ENEMY 와는 책임이 달라 분리).
//
// 그림은 weapon_laser PNG 를 세로로 stretch — 빔 영역 (top=0 ~ bottom=Player 머리) 에 맞춤.
class LaserBeam private constructor(
    private val gctx: GameContext,
) : IGameObject, IBoxCollidable, IRecyclable {
    private var x = 0f
    // top: 화면 위 끝 (0). bottom: spawn 시 Player 머리 위 (init 인자로 받음, update 에서 갱신).
    private var beamBottom = 0f

    private var lifetime = 0f
    private var elapsed = 0f
    private var tickCooldown = 0f
    private var power = 0
    // 빔 시각/충돌 폭의 절반. 등급별 다름 (희귀 = 가는 빔, 영웅 = 굵은 빔).
    private var beamHalf = 0f

    private val beamRect = RectF()
    override val collisionRect = RectF()

    init {
        if (sharedBitmap == null) {
            sharedBitmap = gctx.res.getBitmap(R.mipmap.weapon_laser)
        }
    }

    fun init(startBottom: Float, lifetime: Float, power: Int, beamHalf: Float): LaserBeam {
        this.beamBottom = startBottom
        this.lifetime = lifetime
        this.elapsed = 0f
        this.tickCooldown = 0f
        this.power = power
        this.beamHalf = beamHalf
        // x 는 update 에서 매 프레임 Player 따라 갱신.
        return this
    }

    override fun update(gctx: GameContext) {
        val scene = gctx.scene as? MainScene ?: return

        elapsed += gctx.frameTime
        if (elapsed >= lifetime) {
            scene.world.remove(this, MainScene.Layer.LASER)
            return
        }

        // Player 따라 x / 빔 끝 위치 갱신 (Player 가 움직이면 빔도 같이).
        val player = scene.player
        x = player.x
        beamBottom = player.y - Player.PLAYER_HEIGHT / 2f - Player.BULLET_OFFSET
        updateCollisionRect()

        // 매 LASER_TICK_INTERVAL 마다 빔 안에 들어와 있는 모든 (살아있는) enemy 에 데미지.
        tickCooldown -= gctx.frameTime
        if (tickCooldown <= 0f) {
            tickCooldown = LASER_TICK_INTERVAL
            scene.world.forEachReversedAt(MainScene.Layer.ENEMY) { enemyObj ->
                val enemy = enemyObj as? Enemy ?: return@forEachReversedAt
                if (collidesWith(enemy)) {
                    enemy.decreaseLife(power)
                    if (enemy.dead) {
                        enemy.startDying(scene)
                        scene.addScore(enemy.score)
                    }
                }
            }
        }
    }

    override fun draw(canvas: Canvas) {
        val bmp = sharedBitmap ?: return
        // weapon_laser PNG 를 빔 영역 (가로 beamHalf*2, 세로 0~beamBottom) 에 stretch 그리기.
        beamRect.set(x - beamHalf, 0f, x + beamHalf, beamBottom)
        canvas.drawBitmap(bmp, null, beamRect, null)
    }

    private fun updateCollisionRect() {
        // 충돌 폭은 시각 빔의 30% — "충돌박스는 실제 액터보다 작아야 자연스럽다" 룰 (사용자 결정,
        // framework 의 inset 0.8 보다 더 좁은 이유는 영웅 빔 시각 폭이 300px 까지 커지기 때문 —
        // 비율을 같이 키우면 빔 옆 글로우 영역까지 충돌해 시각/물리 어긋남).
        val half = beamHalf * COLLISION_INSET_RATIO
        collisionRect.set(x - half, 0f, x + half, beamBottom)
    }

    override fun onRecycle() {
    }

    companion object {
        // 매 0.1 초마다 데미지 — 1초 lifetime 이면 10 틱.
        private const val LASER_TICK_INTERVAL = 0.1f
        // 충돌 폭이 시각 폭에서 차지하는 비율 — 빔 코어 영역만 데미지, 글로우 영역은 시각 효과만.
        private const val COLLISION_INSET_RATIO = 0.15f

        // sharedGauge 패턴 — 모든 LaserBeam 인스턴스 공유.
        private var sharedBitmap: Bitmap? = null

        fun get(
            gctx: GameContext,
            startBottom: Float,
            lifetime: Float,
            power: Int,
            beamHalf: Float,
        ): LaserBeam {
            val scene = gctx.scene as? MainScene
                ?: return LaserBeam(gctx).init(startBottom, lifetime, power, beamHalf)
            val laser = scene.world.obtain(LaserBeam::class.java) ?: LaserBeam(gctx)
            return laser.init(startBottom, lifetime, power, beamHalf)
        }
    }
}
