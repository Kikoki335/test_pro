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
    // BossScene 으로 들어온 인스턴스에서는 (1) 다시 보스 진입을 트리거하지 않고
    // (2) 일반 EnemyGenerator 를 world 에 추가하지 않으며 (3) 타이머 HUD 에 "BOSS STAGE" 텍스트.
    // BossTimerHud / world 초기화에서 읽어야 하므로 val 로 노출.
    val isBossStage: Boolean = false,
) : Scene(gctx) {
    enum class Layer {
        BACKGROUND,
        PLAYER,
        BULLET,
        // LaserWeapon 의 빔 — 화면 위에서 Player 머리까지 세로 직사각형. BULLET 위에 떠야 빔이 일반
        // 탄을 가려서 시인성 ↑.
        LASER,
        // MissileWeapon 의 추적 미사일 — Bullet 변종이지만 추적 로직이 달라 별도 layer 로 분리.
        MISSILE,
        ENEMY,
        // 적이 발사한 탄. ENEMY 위에 그려져 적이 자기가 쏜 탄을 가리지 않게 한다.
        // STARS parallax 보다는 아래라 별빛이 탄을 살짝 덮을 수 있는데, 시인성 측면에서 큰 문제는 없다.
        ENEMY_BULLET,
        // EXP 구슬 — Enemy 사망 자리에 drop. 적/탄과 함께 떠 보이도록 ENEMY_BULLET 위, STARS 아래.
        EXP_ORB,
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
    private val expLabel = ExpLabel(gctx)
    private val debugStatLabel = DebugStatLabel(gctx)
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
        // 보스 스테이지에서는 일반 적이 spawn 되지 않는다 — 6주차 보스 패턴 작업 자리.
        // EnemyGenerator 인스턴스 자체는 만들지만 world 에 안 들어가면 update/draw 가 호출되지 않음.
        if (!isBossStage) add(enemyGenerator, Layer.CONTROLLER)
        add(collisionChecker, Layer.CONTROLLER)
        add(scoreLabel, Layer.UI)
        add(playerHpHud, Layer.UI)
        add(expLabel, Layer.UI)
        add(debugStatLabel, Layer.UI)
        add(bossTimerHud, Layer.UI)
    }

    fun addScore(amount: Int) {
        score += amount
    }

    override fun update(gctx: GameContext) {
        super.update(gctx)

        // 레벨업 트리거 — exp 가 maxExp 도달하면 LevelUpScene 을 stack 에 push 해서 게임 정지 +
        // 카드 3장. 카드 선택 시 player.levelUp() 후 pop 되어 여기로 돌아온다.
        // (push 후 SceneStack 이 top 만 update 하므로 같은 프레임에 또 push 되지 않음 — 가드 불필요)
        if (player.exp >= player.maxExp) {
            LevelUpScene(gctx, this).push()
            return
        }

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
        // 보스 Scene 진입 시점. README §1 사양 그대로 60초.
        private const val BOSS_ENTER_TIME = 60f
    }
}
