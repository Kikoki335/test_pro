package com.example.smartphonetermproject

import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.VertScrollBackground
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.Scene
import kr.ac.tukorea.ge.spgp2026.a2dg.scene.World
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// open class — BossScene 이 같은 게임 로직을 그대로 쓰고 배경만 다르게 가져가기 위해 상속 허용.
open class MainScene(
    gctx: GameContext,
    backgroundResId: Int = R.mipmap.sky_bg,
    // BossScene 으로 들어온 인스턴스에서는 다시 보스 진입을 트리거하지 않도록 막는 플래그.
    isBossStage: Boolean = false,
) : Scene(gctx) {
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

    private val background = VertScrollBackground(gctx, backgroundResId, BACKGROUND_SPEED)
    private val stars = VertScrollBackground(gctx, R.mipmap.sky_star, STARS_SPEED)
    val player = Player(gctx)
    private val enemyGenerator = EnemyGenerator(gctx)
    private val collisionChecker = CollisionChecker(gctx)
    private val scoreLabel = ScoreLabel(gctx)
    private val playerHpHud = PlayerHpHud(gctx)
    private val bossTimerHud = BossTimerHud(gctx)

    var score = 0
        private set

    // Scene 진입 후 경과 시간 (초 단위, frameTime 누적).
    // BossTimerHud 가 읽어 표시하고, BOSS_ENTER_TIME 을 넘기면 BossScene 으로 전환한다.
    var elapsedSec = 0f
        private set
    // BossScene(=isBossStage true) 으로 진입한 인스턴스는 시작부터 트리거 막힘.
    private var bossEntered = isBossStage

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

    override fun update(gctx: GameContext) {
        super.update(gctx)
        if (bossEntered) return
        elapsedSec += gctx.frameTime
        if (elapsedSec >= BOSS_ENTER_TIME) {
            bossEntered = true
            BossScene(gctx).change()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return player.onTouchEvent(event)
    }

    companion object {
        private const val BACKGROUND_SPEED = 80f
        // 별 parallax 는 배경보다 빨라야 우주선이 별빛 사이를 통과하는 느낌이 산다.
        // 너무 빠르면 시야가 산만하므로 1.5x 정도가 무난.
        private const val STARS_SPEED = 100f
        // 보스 Scene 진입 시점.
        // 사양 (README §1) 은 60초이지만 2주차 #5 단계에서는 테스트 편의상 10초로 둔다.
        private const val BOSS_ENTER_TIME = 10f
    }
}
