package com.example.smartphonetermproject

import android.graphics.RectF
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.hypot

class Player(val gctx: GameContext) : Sprite(gctx, R.mipmap.player_placeholder), IBoxCollidable {
    override var width = PLAYER_WIDTH
    override var height = PLAYER_HEIGHT
    override var x = gctx.metrics.width / 2f
    override var y = gctx.metrics.height - PLAYER_HEIGHT * 1.5f

    // 충돌 박스는 그림 영역(dstRect)보다 살짝 안쪽 (가로·세로 80%) 으로 줄여 둔다.
    // 진짜 캐릭터 PNG 의 투명 여백/비주얼 외곽을 고려해 "스쳤다" 싶은 충돌이 덜 잡히도록.
    private val _collisionRect = RectF()
    override val collisionRect: RectF
        get() {
            val halfW = width * COLLISION_INSET_RATIO / 2f
            val halfH = height * COLLISION_INSET_RATIO / 2f
            _collisionRect.set(x - halfW, y - halfH, x + halfW, y + halfH)
            return _collisionRect
        }

    var life = MAX_LIFE
        private set
    val maxLife: Int
        get() = MAX_LIFE
    val dead: Boolean
        get() = life <= 0

    private val minX = PLAYER_WIDTH / 2f
    private val maxX = gctx.metrics.width - PLAYER_WIDTH / 2f
    private val minY = PLAYER_HEIGHT / 2f
    private val maxY = gctx.metrics.height - PLAYER_HEIGHT / 2f

    private var targetX = x
    private var targetY = y
    private var fireCooldown = 0f

    init {
        syncDstRect()
    }

    fun decreaseLife(damage: Int) {
        life -= damage
    }

    override fun update(gctx: GameContext) {
        val step = SPEED * gctx.frameTime
        val dx = targetX - x
        val dy = targetY - y
        val dist = hypot(dx, dy)
        if (dist <= step || dist < 0.5f) {
            x = targetX
            y = targetY
        } else {
            x += dx / dist * step
            y += dy / dist * step
        }
        x = x.coerceIn(minX, maxX)
        y = y.coerceIn(minY, maxY)
        syncDstRect()

        fireBullet(gctx)
    }

    private fun fireBullet(gctx: GameContext) {
        fireCooldown -= gctx.frameTime
        if (fireCooldown > 0f) return
        fireCooldown = FIRE_INTERVAL

        val scene = gctx.scene as? MainScene ?: return
        val bullet = Bullet.get(gctx, x, y - PLAYER_HEIGHT / 2f - BULLET_OFFSET)
        scene.world.add(bullet, MainScene.Layer.BULLET)
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        val pt = gctx.metrics.fromScreen(event.x, event.y)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                targetX = pt.x.coerceIn(minX, maxX)
                targetY = pt.y.coerceIn(minY, maxY)
            }
        }
        return true
    }

    companion object {
        const val SPEED = 1100f
        const val PLAYER_WIDTH = 140f
        const val PLAYER_HEIGHT = 140f
        const val FIRE_INTERVAL = 0.3f
        const val BULLET_OFFSET = 8f
        const val MAX_LIFE = 5
        private const val COLLISION_INSET_RATIO = 0.8f
    }
}
