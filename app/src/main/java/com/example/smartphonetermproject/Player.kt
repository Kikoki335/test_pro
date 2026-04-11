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

    // Player 충돌 박스는 그림 영역(dstRect)과 동일하게 본다.
    // syncDstRect() 가 update() 마다 dstRect 를 최신 위치로 맞추므로,
    // 별도 RectF 를 들고 있을 필요 없이 dstRect 를 그대로 노출한다.
    override val collisionRect: RectF
        get() = dstRect

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
        const val SPEED = 1500f
        const val PLAYER_WIDTH = 100f
        const val PLAYER_HEIGHT = 100f
        const val FIRE_INTERVAL = 0.3f
        const val BULLET_OFFSET = 8f
        const val MAX_LIFE = 5
    }
}
