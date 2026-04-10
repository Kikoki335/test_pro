package com.example.smartphonetermproject

import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Bullet private constructor(
    gctx: GameContext,
) : Sprite(gctx, R.mipmap.bullet_placeholder), IRecyclable {
    override var width = BULLET_WIDTH
    override var height = BULLET_HEIGHT
    override var x = 0f
    override var y = 0f

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

        fun get(gctx: GameContext, x: Float, y: Float): Bullet {
            val scene = gctx.scene as? MainScene ?: return Bullet(gctx).init(x, y)
            val bullet = scene.world.obtain(Bullet::class.java) ?: Bullet(gctx)
            return bullet.init(x, y)
        }
    }
}
