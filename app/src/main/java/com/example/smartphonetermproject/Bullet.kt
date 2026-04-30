package com.example.smartphonetermproject

import android.graphics.Bitmap
import android.graphics.Canvas
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

    // 4주차 #3 — Player.attackMul 보상이 적용된 데미지를 Bullet 인스턴스마다 들고 있음.
    // CollisionChecker 가 enemy.decreaseLife(bullet.power) 로 사용. DragonFlight Player.kt:181 의
    // power 패턴 그대로.
    var power: Int = DAMAGE
        private set

    // hit 단계. Bullet 이 적에 명중하면 즉시 layer 에서 빠지지 않고 HIT_DURATION 동안 본체 sprite
    // 대신 hit vfx 만 자기 위치에 그리다가 self-remove 한다 (dying enemy 와 같은 패턴 — laser_spark
    // "주체가 자기 draw 에서 직접 그림" 원칙). hit 상태에서는 collisionRect 가 빈 사각형이라
    // 다른 적과 또 부딪히지 않는다.
    private var hitting = false
    private var hitTime = 0f
    private val hitRect = RectF()

    // Bullet 의 충돌 박스는 그림 영역보다 80% 안쪽 inset. hit 상태면 빈 사각형으로 — 자동 검사 skip.
    private val _collisionRect = RectF()
    override val collisionRect: RectF
        get() {
            if (hitting) {
                _collisionRect.setEmpty()
                return _collisionRect
            }
            val halfW = width * COLLISION_INSET_RATIO / 2f
            val halfH = height * COLLISION_INSET_RATIO / 2f
            _collisionRect.set(x - halfW, y - halfH, x + halfW, y + halfH)
            return _collisionRect
        }

    init {
        syncDstRect()
        if (sharedHitBitmap == null) {
            sharedHitBitmap = gctx.res.getBitmap(R.mipmap.vfx_player_hit)
        }
    }

    fun init(startX: Float, startY: Float, power: Int = DAMAGE): Bullet {
        x = startX
        y = startY
        this.power = power
        hitting = false
        hitTime = 0f
        syncDstRect()
        return this
    }

    override fun update(gctx: GameContext) {
        // hit 단계는 시간만 카운트 — 이동/화면 밖 검사 모두 skip.
        if (hitting) {
            hitTime -= gctx.frameTime
            if (hitTime <= 0f) {
                val scene = gctx.scene as? MainScene ?: return
                scene.world.remove(this, MainScene.Layer.BULLET)
            }
            return
        }

        y -= SPEED * gctx.frameTime
        syncDstRect()

        if (y + height / 2f < 0f) {
            val scene = gctx.scene as? MainScene ?: return
            scene.world.remove(this, MainScene.Layer.BULLET)
        }
    }

    override fun draw(canvas: Canvas) {
        if (hitting) {
            val bmp = sharedHitBitmap ?: return
            val size = HIT_SIZE
            hitRect.set(
                x - size / 2f,
                y - size / 2f,
                x + size / 2f,
                y + size / 2f,
            )
            canvas.drawBitmap(bmp, null, hitRect, null)
            return
        }
        super.draw(canvas)
    }

    fun startHitting() {
        if (hitting) return
        hitting = true
        hitTime = HIT_DURATION
    }

    override fun onRecycle() {
    }

    companion object {
        // 12×24 → 24×48 → 40×80 → 56×112. 마지막은 3주차 #4 시점, 사용자가 모든 객체 일괄 1.4배.
        const val BULLET_WIDTH = 56f
        const val BULLET_HEIGHT = 112f
        const val SPEED = 1500f

        // 1주차 무기는 1종이라 데미지를 상수로 둔다.
        // 4주차 무기 시스템에서 Bullet.power 필드로 교체 예정.
        const val DAMAGE = 1
        private const val COLLISION_INSET_RATIO = 0.8f

        // hit vfx — vfx_player_hit (구 muzzle flash 자산을 명중 효과로 재배치). 모든 Bullet 인스턴스가
        // 공유하는 캐시 (sharedGauge 와 같은 패턴).
        private var sharedHitBitmap: Bitmap? = null
        // 깜빡 보이고 사라지는 정도 — DIE_DURATION 과 같은 0.1초.
        private const val HIT_DURATION = 0.1f
        private const val HIT_SIZE = 110f

        fun get(gctx: GameContext, x: Float, y: Float, power: Int = DAMAGE): Bullet {
            val scene = gctx.scene as? MainScene ?: return Bullet(gctx).init(x, y, power)
            val bullet = scene.world.obtain(Bullet::class.java) ?: Bullet(gctx)
            return bullet.init(x, y, power)
        }
    }
}
