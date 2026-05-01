package com.example.smartphonetermproject

import android.graphics.RectF
import android.view.MotionEvent
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.IBoxCollidable
import kr.ac.tukorea.ge.spgp2026.a2dg.objects.Sprite
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.hypot
import kotlin.random.Random

class Player(val gctx: GameContext) : Sprite(gctx, R.mipmap.player_placeholder), IBoxCollidable {
    override var width = PLAYER_WIDTH
    override var height = PLAYER_HEIGHT
    override var x = gctx.metrics.width / 2f
    override var y = gctx.metrics.height - PLAYER_HEIGHT * 1.5f

    // 충돌 박스는 그림 영역(dstRect)보다 살짝 안쪽 (가로·세로 80%) 으로 줄여 둔다.
    // 진짜 캐릭터 PNG 의 투명 여백/비주얼 외곽을 고려해 "스쳤다" 싶은 충돌이 덜 잡히도록.
    private val _collisionRect = RectF()
    override val collisionRect: RectF
        get() {
            val halfW = width * COLLISION_INSET_RATIO / 2f
            val halfH = height * COLLISION_INSET_RATIO / 2f
            _collisionRect.set(x - halfW, y - halfH, x + halfW, y + halfH)
            return _collisionRect
        }

    var life = MAX_LIFE
        private set
    val maxLife: Int
        get() = MAX_LIFE
    val dead: Boolean
        get() = life <= 0

    // 4주차 #1~#2 — Enemy 사망 시 drop 되는 ExpOrb 를 흡수해 누적, maxExp 도달 시 MainScene 이
    // LevelUpScene 을 push 해서 카드 3장을 띄운다. 카드 선택 시 levelUp() 으로 차감 + 다음 단계 maxExp.
    var exp = 0
        private set
    var level = 1
        private set
    var maxExp = INITIAL_MAX_EXP
        private set

    fun gainExp(amount: Int) {
        exp += amount
    }

    fun levelUp() {
        // exp 가 maxExp 보다 많이 누적된 상태에서 카드 선택까지 시간이 걸려도 잉여분이 다음 레벨로 이월.
        exp -= maxExp
        if (exp < 0) exp = 0
        level += 1
        maxExp = (maxExp * MAX_EXP_GROWTH).toInt().coerceAtLeast(maxExp + 1)
    }

    // 4주차 #3 — LevelUpScene 카드 보상으로 누적되는 stat. 디버그 HUD (DebugStatLabel) 가 매 draw
    // 시점 이 값들을 읽어 화면에 표시하므로 적용이 즉시 시각 확인 가능.
    var attackMul: Float = 1f
    var fireRateMul: Float = 1f
    var critRate: Float = 0f

    // 4주차 #4 — 무기 종류 + 등급. 발사는 currentWeapon.fire(this, ...) 한 줄로 위임.
    // 시작 무기 / 등급은 사용자가 무기 검증 흐름에 따라 한 줄씩 바꿔가며 테스트
    // (EnemyGenerator 의 single-type spawn 검증과 같은 패턴).
    var currentWeapon: Weapon = ShotgunWeapon
    var weaponGrade: WeaponGrade = WeaponGrade.RARE

    // Bullet/HomingMissile/LaserBeam 등 모든 발사물이 사용하는 데미지 계산. attackMul + critRate 반영.
    fun calculatePower(): Int {
        val basePower = (Bullet.DAMAGE * attackMul).toInt().coerceAtLeast(1)
        return if (Random.nextFloat() < critRate) basePower * CRIT_MUL else basePower
    }

    private val minX = PLAYER_WIDTH / 2f
    private val maxX = gctx.metrics.width - PLAYER_WIDTH / 2f
    private val minY = PLAYER_HEIGHT / 2f
    private val maxY = gctx.metrics.height - PLAYER_HEIGHT / 2f

    private var targetX = x
    private var targetY = y
    private var fireCooldown = 0f

    init {
        syncDstRect()
    }

    fun decreaseLife(damage: Int) {
        life -= damage
    }

    override fun update(gctx: GameContext) {
        val step = SPEED * gctx.frameTime
        val dx = targetX - x
        val dy = targetY - y
        val dist = hypot(dx, dy)
        if (dist <= step || dist < 0.5f) {
            x = targetX
            y = targetY
        } else {
            x += dx / dist * step
            y += dy / dist * step
        }
        x = x.coerceIn(minX, maxX)
        y = y.coerceIn(minY, maxY)
        syncDstRect()

        fireBullet(gctx)
    }

    private fun fireBullet(gctx: GameContext) {
        fireCooldown -= gctx.frameTime
        if (fireCooldown > 0f) return
        // 무기마다 다른 fireInterval. fireRateMul 이 클수록 발사 간격 짧아짐.
        fireCooldown = currentWeapon.fireInterval / fireRateMul

        val scene = gctx.scene as? MainScene ?: return
        currentWeapon.fire(this, scene, gctx, weaponGrade)
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        val pt = gctx.metrics.fromScreen(event.x, event.y)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                targetX = pt.x.coerceIn(minX, maxX)
                targetY = pt.y.coerceIn(minY, maxY)
            }
        }
        return true
    }

    companion object {
        const val SPEED = 1100f
        // 80 → 100 → 140 → 200. 마지막 단계는 3주차 #4 시점, 사용자가 모든 게임 객체 가시성을
        // 더 크게 원해서 일괄 1.4배 적용. 화면 폭 900 의 22% — 모바일 슈팅 기준 큰 편.
        const val PLAYER_WIDTH = 200f
        const val PLAYER_HEIGHT = 200f
        const val FIRE_INTERVAL = 0.3f
        const val BULLET_OFFSET = 8f
        // 5 → 10. 3주차 #4 시점, 사용자가 RANGED 의 EnemyBullet 데미지 비중이 너무 강하다고 결정.
        // hitDamage 는 그대로 두고 MAX_LIFE 만 늘려 한 방의 비중을 절반으로 (20% → 10%).
        const val MAX_LIFE = 10
        private const val COLLISION_INSET_RATIO = 0.8f

        // 첫 레벨업 필요 EXP. orb 1개 = exp 1 이고 적 1마리 처치 = orb 1개라
        // 5 = 적 5마리 처치 후 첫 레벨업 (placeholder, 4주차 #5 능력치/밸런싱에서 재조정).
        private const val INITIAL_MAX_EXP = 5
        // 레벨이 오를수록 다음 maxExp 가 1.5 배 증가 — 5, 7, 10, 15, 22, ...
        private const val MAX_EXP_GROWTH = 1.5f
        // 치명타 발생 시 데미지 배수.
        private const val CRIT_MUL = 3
    }
}
