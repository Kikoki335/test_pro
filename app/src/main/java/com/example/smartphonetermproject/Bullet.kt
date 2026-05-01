package com.example.smartphonetermproject

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

class Bullet private constructor(
    private val gctx: GameContext,
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

    // 4주차 #4 — 무기 종류에 따라 직진(default) / 부채꼴(샷건) 등 다른 방향으로 발사. EnemyBullet 과
    // 같은 (vx, vy) 인자 패턴. default = 위쪽 직진 (vy = -SPEED).
    private var vx = 0f
    private var vy = -SPEED

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

    // 무기별 hit vfx 가 다르므로 인스턴스 필드. init 시점에 결정.
    private var hitBitmap: Bitmap? = null

    init {
        syncDstRect()
    }

    fun init(
        startX: Float,
        startY: Float,
        power: Int = DAMAGE,
        vx: Float = 0f,
        vy: Float = -SPEED,
        // 무기 종류에 따라 sprite 와 hit vfx 가 다름. default = 직진 무기용 placeholder + 흰 충격파.
        spriteResId: Int = R.mipmap.bullet_placeholder,
        hitVfxResId: Int = R.mipmap.vfx_player_hit,
    ): Bullet {
        x = startX
        y = startY
        this.power = power
        this.vx = vx
        this.vy = vy
        bitmap = gctx.res.getBitmap(spriteResId)
        hitBitmap = sharedHitBitmaps.getOrPut(hitVfxResId) { gctx.res.getBitmap(hitVfxResId) }
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

        x += vx * gctx.frameTime
        y += vy * gctx.frameTime
        syncDstRect()

        // 사방 화면 밖 검사 — 사선/부채꼴 발사가 좌·우로도 빠질 수 있음.
        val outBottom = y - height / 2f > gctx.metrics.height
        val outTop = y + height / 2f < 0f
        val outRight = x - width / 2f > gctx.metrics.width
        val outLeft = x + width / 2f < 0f
        if (outBottom || outTop || outRight || outLeft) {
            val scene = gctx.scene as? MainScene ?: return
            scene.world.remove(this, MainScene.Layer.BULLET)
        }
    }

    override fun draw(canvas: Canvas) {
        if (hitting) {
            val bmp = hitBitmap ?: return
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

        // 무기 종류별 hit vfx 캐시 — 같은 vfxResId 면 재사용 (sharedGauge / sharedDieBitmaps 와 같은 패턴).
        private val sharedHitBitmaps = mutableMapOf<Int, Bitmap>()
        // 깜빡 보이고 사라지는 정도 — DIE_DURATION 과 같은 0.1초.
        private const val HIT_DURATION = 0.1f
        private const val HIT_SIZE = 110f

        fun get(
            gctx: GameContext,
            x: Float,
            y: Float,
            power: Int = DAMAGE,
            vx: Float = 0f,
            vy: Float = -SPEED,
            spriteResId: Int = R.mipmap.bullet_placeholder,
            hitVfxResId: Int = R.mipmap.vfx_player_hit,
        ): Bullet {
            val scene = gctx.scene as? MainScene
                ?: return Bullet(gctx).init(x, y, power, vx, vy, spriteResId, hitVfxResId)
            val bullet = scene.world.obtain(Bullet::class.java) ?: Bullet(gctx)
            return bullet.init(x, y, power, vx, vy, spriteResId, hitVfxResId)
        }
    }
}
