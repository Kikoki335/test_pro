package com.example.smartphonetermproject

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.atan2
import kotlin.math.hypot

// MissileWeapon 이 발사하는 추적 미사일. spawn 시 위 방향으로 시작해 매 프레임 가장 가까운 enemy
// 방향으로 vx/vy 를 lerp — 직선 추적이 아닌 부드러운 회전. 적과 충돌 시 hitting 상태로 진입,
// HIT_DURATION 동안 vfx_homing_hit 만 그리다 self-remove (Bullet hitting 패턴 그대로).
class HomingMissile private constructor(
    private val gctx: GameContext,
) : IGameObject, IBoxCollidable, IRecyclable {
    private var x = 0f
    private var y = 0f
    private var vx = 0f
    private var vy = -INITIAL_SPEED
    var power: Int = 0
        private set

    private val dstRect = RectF()
    private val hitRect = RectF()
    private val _collisionRect = RectF()
    override val collisionRect: RectF
        get() {
            if (hitting) {
                _collisionRect.setEmpty()
                return _collisionRect
            }
            // sprite 는 회전하지만 collisionRect 는 그대로 axis-aligned (사용자 결정 — 시각/물리
            // 약간 어긋나도 충돌 검사 단순함이 더 중요).
            val hw = HALF_W * COLLISION_INSET_RATIO
            val hh = HALF_H * COLLISION_INSET_RATIO
            _collisionRect.set(x - hw, y - hh, x + hw, y + hh)
            return _collisionRect
        }

    private var hitting = false
    private var hitTime = 0f

    init {
        if (sharedBitmap == null) {
            sharedBitmap = gctx.res.getBitmap(R.mipmap.weapon_homing)
        }
        if (sharedHitBitmap == null) {
            sharedHitBitmap = gctx.res.getBitmap(R.mipmap.vfx_homing_hit)
        }
    }

    fun init(startX: Float, startY: Float, power: Int): HomingMissile {
        this.x = startX
        this.y = startY
        this.vx = 0f
        this.vy = -INITIAL_SPEED
        this.power = power
        hitting = false
        hitTime = 0f
        return this
    }

    override fun update(gctx: GameContext) {
        val scene = gctx.scene as? MainScene ?: return

        if (hitting) {
            hitTime -= gctx.frameTime
            if (hitTime <= 0f) {
                scene.world.remove(this, MainScene.Layer.MISSILE)
            }
            return
        }

        // 가장 가까운 (살아있는) enemy 를 찾아 그 방향으로 vx/vy 를 lerp.
        val nearest = findNearestEnemy(scene)
        if (nearest != null) {
            val dx = nearest.x - x
            val dy = nearest.y - y
            val len = hypot(dx, dy)
            if (len > 1f) {
                val targetVx = dx / len * MISSILE_SPEED
                val targetVy = dy / len * MISSILE_SPEED
                val turn = TURN_RATE * gctx.frameTime
                vx += (targetVx - vx) * turn
                vy += (targetVy - vy) * turn
            }
        }

        x += vx * gctx.frameTime
        y += vy * gctx.frameTime

        // 화면 밖 (사방) 사라짐.
        val out = (y - HALF_H > gctx.metrics.height) ||
            (y + HALF_H < 0f) ||
            (x - HALF_W > gctx.metrics.width) ||
            (x + HALF_W < 0f)
        if (out) {
            scene.world.remove(this, MainScene.Layer.MISSILE)
        }
    }

    override fun draw(canvas: Canvas) {
        if (hitting) {
            val bmp = sharedHitBitmap ?: return
            hitRect.set(x - HIT_HALF, y - HIT_HALF, x + HIT_HALF, y + HIT_HALF)
            canvas.drawBitmap(bmp, null, hitRect, null)
            return
        }
        val bmp = sharedBitmap ?: return
        dstRect.set(x - HALF_W, y - HALF_H, x + HALF_W, y + HALF_H)

        // sprite 의 기본 "앞" 방향이 위쪽 (-y) 이므로, 진행 방향 (vx, vy) 에 맞추려면
        // atan2(vy, vx) (양의 x축 기준 라디안) → 도 단위로 환산 → +90 보정 (위 방향이 -90 이라).
        // vx == vy == 0 인 극단 케이스에는 회전 안 하고 sprite 기본 (위) 방향 유지.
        val rotationDeg = if (vx == 0f && vy == 0f) {
            0f
        } else {
            Math.toDegrees(atan2(vy, vx).toDouble()).toFloat() + 90f
        }
        canvas.save()
        canvas.rotate(rotationDeg, x, y)
        canvas.drawBitmap(bmp, null, dstRect, null)
        canvas.restore()
    }

    fun startHitting() {
        if (hitting) return
        hitting = true
        hitTime = HIT_DURATION
    }

    override fun onRecycle() {
    }

    private fun findNearestEnemy(scene: MainScene): Enemy? {
        var nearest: Enemy? = null
        var nearestDistSq = Float.MAX_VALUE
        scene.world.forEachReversedAt(MainScene.Layer.ENEMY) { enemyObj ->
            val enemy = enemyObj as? Enemy ?: return@forEachReversedAt
            // dying enemy 는 collisionRect 가 비어 있어 무시.
            if (enemy.collisionRect.isEmpty) return@forEachReversedAt
            val dx = enemy.x - x
            val dy = enemy.y - y
            val distSq = dx * dx + dy * dy
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq
                nearest = enemy
            }
        }
        return nearest
    }

    companion object {
        private const val HALF_W = 28f
        private const val HALF_H = 56f
        private const val INITIAL_SPEED = 600f
        private const val MISSILE_SPEED = 1100f
        // turn rate — 수치 클수록 즉시 추적, 작을수록 느린 회전.
        private const val TURN_RATE = 6f

        private const val HIT_DURATION = 0.1f
        private const val HIT_HALF = 60f
        // 다른 IBoxCollidable 과 같은 inset 룰 — 그림 영역의 80% 안쪽이 충돌 판정.
        private const val COLLISION_INSET_RATIO = 0.8f

        // sharedGauge 패턴 — 모든 인스턴스 공유.
        private var sharedBitmap: Bitmap? = null
        private var sharedHitBitmap: Bitmap? = null

        fun get(gctx: GameContext, x: Float, y: Float, power: Int): HomingMissile {
            val scene = gctx.scene as? MainScene
                ?: return HomingMissile(gctx).init(x, y, power)
            val missile = scene.world.obtain(HomingMissile::class.java) ?: HomingMissile(gctx)
            return missile.init(x, y, power)
        }
    }
}
