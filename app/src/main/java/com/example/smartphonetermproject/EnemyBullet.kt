package com.example.smartphonetermproject

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext

// Player 의 Bullet 과 거의 대칭인 ObjectPool 패턴 (private constructor + IRecyclable + get).
// 진행 방향은 (vx, vy) 자유 — RANGED 가 Player 위치를 향해 aimed 단위벡터를 넘겨 발사한다.
// default 인자 (vx=0, vy=SPEED) 는 직하 발사 호출자를 위한 fallback. hit 단계는 Bullet 과 동일.
class EnemyBullet private constructor(
    gctx: GameContext,
) : Sprite(gctx, R.mipmap.enemy_bullet), IBoxCollidable, IRecyclable {
    override var width = ENEMY_BULLET_WIDTH
    override var height = ENEMY_BULLET_HEIGHT
    override var x = 0f
    override var y = 0f

    private var vx = 0f
    private var vy = SPEED

    private var hitting = false
    private var hitTime = 0f
    private val hitRect = RectF()

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
            sharedHitBitmap = gctx.res.getBitmap(R.mipmap.vfx_enemy_hit)
        }
    }

    fun init(startX: Float, startY: Float, vx: Float = 0f, vy: Float = SPEED): EnemyBullet {
        x = startX
        y = startY
        this.vx = vx
        this.vy = vy
        hitting = false
        hitTime = 0f
        syncDstRect()
        return this
    }

    override fun update(gctx: GameContext) {
        if (hitting) {
            hitTime -= gctx.frameTime
            if (hitTime <= 0f) {
                val scene = gctx.scene as? MainScene ?: return
                scene.world.remove(this, MainScene.Layer.ENEMY_BULLET)
            }
            return
        }

        x += vx * gctx.frameTime
        y += vy * gctx.frameTime
        syncDstRect()

        // aimed 탄은 좌·우·위로도 빠질 수 있으므로 사방 검사.
        val outBottom = y - height / 2f > gctx.metrics.height
        val outTop = y + height / 2f < 0f
        val outRight = x - width / 2f > gctx.metrics.width
        val outLeft = x + width / 2f < 0f
        if (outBottom || outTop || outRight || outLeft) {
            val scene = gctx.scene as? MainScene ?: return
            scene.world.remove(this, MainScene.Layer.ENEMY_BULLET)
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
        // 24×36 → 40×60 → 56×84 → 80×120. 마지막은 4주차 #5 시점, RANGED 의 aimed 발사가 사선이라
        // 회피 시 시각 인지가 더 중요해서 한 번 더 키움 (사용자 결정).
        const val ENEMY_BULLET_WIDTH = 80f
        const val ENEMY_BULLET_HEIGHT = 120f
        // Player Bullet (1500f) 보다 한참 느리게 — RANGED 의 탄을 옆으로 회피할 여지가 있어야 함.
        const val SPEED = 700f
        const val DAMAGE = 1
        private const val COLLISION_INSET_RATIO = 0.8f

        private var sharedHitBitmap: Bitmap? = null
        private const val HIT_DURATION = 0.1f
        private const val HIT_SIZE = 90f

        fun get(
            gctx: GameContext,
            x: Float,
            y: Float,
            vx: Float = 0f,
            vy: Float = SPEED,
        ): EnemyBullet {
            val scene = gctx.scene as? MainScene
                ?: return EnemyBullet(gctx).init(x, y, vx, vy)
            val bullet = scene.world.obtain(EnemyBullet::class.java) ?: EnemyBullet(gctx)
            return bullet.init(x, y, vx, vy)
        }
    }
}
