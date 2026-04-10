package com.example.smartphonetermproject

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IGameObject
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IRecyclable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.collidesWith
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.hypot

// Enemy 가 죽은 자리에 drop 되는 EXP 구슬. placeholder 단계는 cyan 원 (7주차 그래픽 폴리싱에서 PNG 교체).
//
// 동작:
//   1) drop 즉시부터 매 프레임 Player 방향으로 ATTRACT_SPEED 만큼 끌려감 — Bullet/EnemyBullet 처럼
//      "spawn 후 정해진 목표를 향해 계속 이동" 패턴 (탄환과 같음, 다만 목표가 매 프레임 Player 위치라
//      Player 가 움직이면 방향이 갱신되는 homing). 거리 제한 없음.
//   2) 흡수 — collisionRect 가 player.collisionRect 와 겹치면 player.gainExp(VALUE) 후 self-remove.
//
// framework (CookieRun MapObject 4/28) 의 공통 부모/Pull-up 패턴을 따라가는 대신, Sprite 도 상속하지
// 않고 IGameObject + IBoxCollidable + IRecyclable 만 직접 구현하는 가장 가벼운 형태로 둔다 — 본격
// 부모 추출은 4주차 무기/EXP 시스템이 다 깔린 뒤 같은 패턴이 여러 군데 보일 때 검토.
class ExpOrb private constructor(
    private val gctx: GameContext,
) : IGameObject, IBoxCollidable, IRecyclable {
    var x = 0f
        private set
    var y = 0f
        private set

    override val collisionRect = RectF()

    fun init(x: Float, y: Float): ExpOrb {
        this.x = x
        this.y = y
        updateCollisionRect()
        return this
    }

    override fun update(gctx: GameContext) {
        val scene = gctx.scene as? MainScene ?: return
        val player = scene.player

        // Player 가 dying / 게임오버 직후 같은 비정상 상태에서도 일단 흡수만 안 되게 가드.
        if (player.dead) return

        val dx = player.x - x
        val dy = player.y - y
        val dist = hypot(dx, dy)

        if (collidesWith(player)) {
            player.gainExp(VALUE)
            scene.world.remove(this, MainScene.Layer.EXP_ORB)
            return
        }

        if (dist > 0f) {
            val step = ATTRACT_SPEED * gctx.frameTime
            x += dx / dist * step
            y += dy / dist * step
        }

        updateCollisionRect()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawCircle(x, y, RADIUS, fillPaint)
        canvas.drawCircle(x, y, RADIUS, strokePaint)
    }

    private fun updateCollisionRect() {
        // 충돌 박스는 시각 반지름 × 0.8 정사각 — 다른 IBoxCollidable 과 같은 inset 룰.
        val r = RADIUS * COLLISION_INSET_RATIO
        collisionRect.set(x - r, y - r, x + r, y + r)
    }

    override fun onRecycle() {
    }

    companion object {
        const val VALUE = 1
        private const val RADIUS = 18f
        // drop 즉시부터 Player 방향 추적 속도. EnemyBullet (700) 보다 약간 빠르게 — Player 가 움직여도
        // 결국 따라잡혀 흡수되도록.
        private const val ATTRACT_SPEED = 800f
        private const val COLLISION_INSET_RATIO = 0.8f

        // placeholder 도형 — cyan 원 + 밝은 청록 테두리. 모든 ExpOrb 인스턴스가 공유.
        private val fillPaint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.rgb(34, 211, 238)
            isAntiAlias = true
        }
        private val strokePaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = Color.rgb(165, 233, 255)
            isAntiAlias = true
        }

        fun get(gctx: GameContext, x: Float, y: Float): ExpOrb {
            val scene = gctx.scene as? MainScene ?: return ExpOrb(gctx).init(x, y)
            val orb = scene.world.obtain(ExpOrb::class.java) ?: ExpOrb(gctx)
            return orb.init(x, y)
        }
    }
}
