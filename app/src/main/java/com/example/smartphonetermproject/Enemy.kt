package com.example.smartphonetermproject

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.util.Gauge
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Enemy private constructor(
    private val gctx: GameContext,
) : Sprite(gctx, Type.SUICIDE.resId), IBoxCollidable, IRecyclable {

    enum class Type(
        val resId: Int,
        val width: Float,
        val height: Float,
        val hp: Int,
        val speed: Float,
        val score: Int,
    ) {
        SUICIDE(R.mipmap.enemy_suicide, 70f, 70f, 1, 400f, 10),
        RANGED(R.mipmap.enemy_ranged, 80f, 80f, 2, 200f, 20),
        SPLIT(R.mipmap.enemy_split, 60f, 60f, 3, 300f, 30),
    }

    val score: Int
        get() = type.score

    private lateinit var type: Type
    var life = 0
        private set
    var maxLife = 0
        private set
    private var speed = 0f

    val dead: Boolean
        get() = life <= 0

    override var width = 0f
    override var height = 0f
    override var x = 0f
    override var y = 0f

    // Enemy 충돌 박스는 placeholder 단계에서는 dstRect 와 같게 둔다.
    // (DragonFlight 4/9 는 11f inset 으로 그림보다 안쪽에서 충돌 — Sky Blaster 는 7주차 그래픽 교체 시 재검토)
    // collisionRect 를 별도 RectF 로 두는 이유는, 추후 inset 도입 시 dstRect 와 분리해 관리하기 위함.
    override val collisionRect = RectF()

    init {
        // 모든 Enemy 인스턴스가 정적 Gauge 하나를 공유한다 (DragonFlight 패턴, 4/3 `be491bf`).
        // Gauge 는 progress 0~1 만 받는 stateless 그리기 도구이므로 매 Enemy 마다 새로 만들 필요가 없다.
        if (sharedGauge == null) {
            sharedGauge = Gauge(GAUGE_THICKNESS, GAUGE_FG_COLOR, GAUGE_BG_COLOR)
        }
    }

    fun init(x: Float, type: Type): Enemy {
        this.type = type
        this.bitmap = gctx.res.getBitmap(type.resId)
        this.width = type.width
        this.height = type.height
        this.life = type.hp
        this.maxLife = type.hp
        this.speed = type.speed
        this.x = x
        this.y = -type.height / 2f
        syncDstRect()
        updateCollisionRect()
        return this
    }

    override fun update(gctx: GameContext) {
        y += speed * gctx.frameTime
        syncDstRect()
        updateCollisionRect()

        if (y - height / 2f > gctx.metrics.height) {
            val scene = gctx.scene as? MainScene ?: return
            scene.world.remove(this, MainScene.Layer.ENEMY)
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // HP gauge 는 Enemy 머리 위에 적의 폭의 70% 길이로 그린다.
        // life / maxLife 비율을 progress 로 넘기면 Gauge 가 알아서 잘라 그려 준다.
        val gauge = sharedGauge ?: return
        val gaugeWidth = width * 0.7f
        val gaugeX = x - gaugeWidth / 2f
        val gaugeY = y - height / 2f - GAUGE_OFFSET_FROM_TOP
        gauge.draw(canvas, gaugeX, gaugeY, gaugeWidth, life.toFloat() / maxLife)
    }

    fun decreaseLife(damage: Int) {
        life -= damage
    }

    private fun updateCollisionRect() {
        // 1주차 placeholder 는 inset 없이 dstRect 그대로 사용.
        collisionRect.set(
            x - width / 2f,
            y - height / 2f,
            x + width / 2f,
            y + height / 2f,
        )
    }

    override fun onRecycle() {
    }

    companion object {
        // gauge 는 모든 Enemy 가 공유한다.
        private var sharedGauge: Gauge? = null
        private const val GAUGE_THICKNESS = 0.12f
        private val GAUGE_FG_COLOR = Color.GREEN
        private val GAUGE_BG_COLOR = Color.argb(180, 0, 0, 0)
        private const val GAUGE_OFFSET_FROM_TOP = 8f

        fun get(gctx: GameContext, x: Float, type: Type): Enemy {
            val scene = gctx.scene as? MainScene ?: return Enemy(gctx).init(x, type)
            val enemy = scene.world.obtain(Enemy::class.java) ?: Enemy(gctx)
            return enemy.init(x, type)
        }
    }
}
