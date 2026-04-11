package com.example.smartphonetermproject

import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.VertScrollBackground
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class MainScene(gctx: GameContext) : Scene(gctx) {
    enum class Layer {
        BACKGROUND,
        PLAYER,
        BULLET,
        ENEMY,
        CONTROLLER,
    }

    private val background = VertScrollBackground(gctx, R.mipmap.sky_bg, BACKGROUND_SPEED)
    private val player = Player(gctx)
    private val enemyGenerator = EnemyGenerator(gctx)
    private val collisionChecker = CollisionChecker(gctx)

    override val world = World(Layer.entries.toTypedArray()).apply {
        add(background, Layer.BACKGROUND)
        add(player, Layer.PLAYER)
        add(enemyGenerator, Layer.CONTROLLER)
        add(collisionChecker, Layer.CONTROLLER)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return player.onTouchEvent(event)
    }

    companion object {
        private const val BACKGROUND_SPEED = 80f
    }
}
