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
        STARS,
        CONTROLLER,
        UI,
    }

    // 화면 비율이 가상 좌표계(900×1600)와 안 맞아 letterbox 가 생겨도
    // 배경 등이 그 영역으로 새 나가지 않게 한다. (a2dg 2주차 framework, 4/13 `3300abc`+`10492d7`)
    override val clipsRect = true

    private val background = VertScrollBackground(gctx, R.mipmap.sky_bg, BACKGROUND_SPEED)
    private val stars = VertScrollBackground(gctx, R.mipmap.sky_star, STARS_SPEED)
    val player = Player(gctx)
    private val enemyGenerator = EnemyGenerator(gctx)
    private val collisionChecker = CollisionChecker(gctx)
    private val scoreLabel = ScoreLabel(gctx)
    private val playerHpHud = PlayerHpHud(gctx)
    private val bossTimerHud = BossTimerHud(gctx)

    var score = 0
        private set

    override val world = World(Layer.entries.toTypedArray()).apply {
        add(background, Layer.BACKGROUND)
        add(player, Layer.PLAYER)
        add(stars, Layer.STARS)
        add(enemyGenerator, Layer.CONTROLLER)
        add(collisionChecker, Layer.CONTROLLER)
        add(scoreLabel, Layer.UI)
        add(playerHpHud, Layer.UI)
        add(bossTimerHud, Layer.UI)
    }

    fun addScore(amount: Int) {
        score += amount
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return player.onTouchEvent(event)
    }

    companion object {
        private const val BACKGROUND_SPEED = 80f
        // 별 parallax 는 배경보다 빨라야 우주선이 별빛 사이를 통과하는 느낌이 산다.
        // 너무 빠르면 시야가 산만하므로 1.5x 정도가 무난.
        private const val STARS_SPEED = 500f
    }
}
