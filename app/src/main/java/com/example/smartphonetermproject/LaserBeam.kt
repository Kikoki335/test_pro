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

    private val beamRect = RectF()
    override val collisionRect = RectF()

    init {
        if (sharedBitmap == null) {
            sharedBitmap = gctx.res.getBitmap(R.mipmap.weapon_laser)
        }
    }

    fun init(startBottom: Float, lifetime: Float, power: Int): LaserBeam {
        this.beamBottom = startBottom
        this.lifetime = lifetime
        this.elapsed = 0f
        this.tickCooldown = 0f
        this.power = power
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
        // weapon_laser PNG 를 빔 영역 (가로 BEAM_HALF*2, 세로 0~beamBottom) 에 stretch 그리기.
        beamRect.set(x - BEAM_HALF, 0f, x + BEAM_HALF, beamBottom)
        canvas.drawBitmap(bmp, null, beamRect, null)
    }

    private fun updateCollisionRect() {
        // 충돌 폭은 BEAM_HALF 의 절반 (실제 시각보다 좁게 — sprite 의 글로우 영역까지 충돌하면 너무 넓음).
        val half = BEAM_HALF * 0.5f
        collisionRect.set(x - half, 0f, x + half, beamBottom)
    }

    override fun onRecycle() {
    }

    companion object {
        // 빔 시각 폭 (PNG 를 stretch 할 가로 영역). PNG 의 가로/세로 비율과 무관하게 stretch.
        private const val BEAM_HALF = 50f
        // 매 0.1 초마다 데미지 — 1초 lifetime 이면 10 틱.
        private const val LASER_TICK_INTERVAL = 0.1f

        // sharedGauge 패턴 — 모든 LaserBeam 인스턴스 공유.
        private var sharedBitmap: Bitmap? = null

        fun get(gctx: GameContext, startBottom: Float, lifetime: Float, power: Int): LaserBeam {
            val scene = gctx.scene as? MainScene
                ?: return LaserBeam(gctx).init(startBottom, lifetime, power)
            val laser = scene.world.obtain(LaserBeam::class.java) ?: LaserBeam(gctx)
            return laser.init(startBottom, lifetime, power)
        }
    }
}
