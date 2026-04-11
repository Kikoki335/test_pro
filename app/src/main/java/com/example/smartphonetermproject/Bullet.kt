package com.example.smartphonetermproject

import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Bullet private constructor(
    gctx: GameContext,
) : Sprite(gctx, R.mipmap.bullet_placeholder), IBoxCollidable, IRecyclable {
    override var width = BULLET_WIDTH
    override var height = BULLET_HEIGHT
    override var x = 0f
    override var y = 0f

    // Bullet 의 충돌 박스는 그림 영역(dstRect)과 동일하게 본다.
    // syncDstRect() 가 매 update() 에서 dstRect 를 최신 위치로 맞추므로
    // collisionRect 를 별도로 저장/갱신할 필요 없이 dstRect 를 그대로 노출한다.
    override val collisionRect: RectF
        get() = dstRect

    init {
        syncDstRect()
    }

    fun init(startX: Float, startY: Float): Bullet {
        x = startX
        y = startY
        syncDstRect()
        return this
    }

    override fun update(gctx: GameContext) {
        y -= SPEED * gctx.frameTime
        syncDstRect()

        if (y + height / 2f < 0f) {
            val scene = gctx.scene as? MainScene ?: return
            scene.world.remove(this, MainScene.Layer.BULLET)
        }
    }

    override fun onRecycle() {
    }

    companion object {
        const val BULLET_WIDTH = 24f
        const val BULLET_HEIGHT = 48f
        const val SPEED = 1500f

        // 1주차 무기는 1종이라 데미지를 상수로 둔다.
        // 4주차 무기 시스템에서 Bullet.power 필드로 교체 예정.
        const val DAMAGE = 1

        fun get(gctx: GameContext, x: Float, y: Float): Bullet {
            val scene = gctx.scene as? MainScene ?: return Bullet(gctx).init(x, y)
            val bullet = scene.world.obtain(Bullet::class.java) ?: Bullet(gctx)
            return bullet.init(x, y)
        }
    }
}
