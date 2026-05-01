package com.example.smartphonetermproject

import android.graphics.Color
import kr.ac.tukorea.ge.spgp2026.a2dg.view.GameContext
import kotlin.math.cos
import kotlin.math.sin

// 무기 등급 — 희귀 / 영웅 두 단계. 전설은 5주차 스킬 시스템에서 사용 (사용자 결정).
// 각 무기의 fire 안에서 grade 를 받아 스탯 (탄환 발수, 빔 지속시간, 미사일 동시 발사 수 등) 을 분기.
enum class WeaponGrade(val displayName: String, val cardColor: Int) {
    RARE("희귀", Color.rgb(96, 165, 250)),
    EPIC("영웅", Color.rgb(168, 85, 247)),
}

// CookieRun 4/29 commit `8017a48 MapObject 생성 규칙을 Registry 로 분리한다` 패턴 응용 —
// 각 무기는 sealed class 의 object (singleton) 로, 발사 동작만 다르게 override.
// fireInterval / displayName / fire 셋만 자기 일.
sealed class Weapon {
    abstract val displayName: String
    // 기본 발사 간격 (초). Player.fireRateMul 가 이 값을 나눠 실제 cooldown 결정.
    abstract val fireInterval: Float
    // LevelUpScene 의 무기 카드에 표시할 sprite resource. DefaultWeapon 은 카드 풀에 안 나오므로
    // placeholder.
    abstract val cardSpriteResId: Int

    // 발사 시점에 호출됨. Bullet/LaserBeam/HomingMissile 을 만들어 world.add.
    abstract fun fire(player: Player, scene: MainScene, gctx: GameContext, grade: WeaponGrade)
}

// 1주차 #4 의 단순 직진 발사 — 시작 무기로도 쓸 수 있고, 카드 보상 풀 기준선이 됨.
object DefaultWeapon : Weapon() {
    override val displayName = "직진"
    override val fireInterval = 0.3f
    override val cardSpriteResId = R.mipmap.bullet_placeholder

    override fun fire(player: Player, scene: MainScene, gctx: GameContext, grade: WeaponGrade) {
        val muzzleY = player.y - Player.PLAYER_HEIGHT / 2f - Player.BULLET_OFFSET
        val bullet = Bullet.get(gctx, player.x, muzzleY, player.calculatePower())
        scene.world.add(bullet, MainScene.Layer.BULLET)
    }
}

// 부채꼴 산탄. 등급별 발수가 다름 (희귀 3 / 영웅 5), spread 도 약간 더 넓게.
object ShotgunWeapon : Weapon() {
    override val displayName = "샷건"
    override val fireInterval = 0.6f
    override val cardSpriteResId = R.mipmap.weapon_shotgun

    override fun fire(player: Player, scene: MainScene, gctx: GameContext, grade: WeaponGrade) {
        val pelletCount = if (grade == WeaponGrade.EPIC) 5 else 3
        val totalSpreadDeg = if (grade == WeaponGrade.EPIC) 40f else 30f
        val muzzleY = player.y - Player.PLAYER_HEIGHT / 2f - Player.BULLET_OFFSET
        val power = player.calculatePower()

        val startAngle = -totalSpreadDeg / 2f
        val step = if (pelletCount > 1) totalSpreadDeg / (pelletCount - 1) else 0f
        for (i in 0 until pelletCount) {
            val angleDeg = startAngle + i * step
            val rad = Math.toRadians(angleDeg.toDouble())
            val vx = sin(rad).toFloat() * Bullet.SPEED
            // 위 방향이 음수 (Bullet 의 default vy = -SPEED 와 일관).
            val vy = -cos(rad).toFloat() * Bullet.SPEED
            val pellet = Bullet.get(
                gctx, player.x, muzzleY, power, vx, vy,
                spriteResId = R.mipmap.weapon_shotgun,
                hitVfxResId = R.mipmap.vfx_shotgun_hit,
            )
            scene.world.add(pellet, MainScene.Layer.BULLET)
        }
    }
}

// 직선 관통 빔. 등급별 지속시간이 다름 (희귀 0.6 / 영웅 1.0). 빔 자체가 LASER_TICK_INTERVAL 마다
// 자기 안에 있는 enemy 들에게 데미지 — 무기는 단지 빔 객체를 spawn 하는 역할.
object LaserWeapon : Weapon() {
    override val displayName = "레이저"
    override val fireInterval = 1.2f
    override val cardSpriteResId = R.mipmap.weapon_laser

    override fun fire(player: Player, scene: MainScene, gctx: GameContext, grade: WeaponGrade) {
        // 사용자 결정 — 레이저 등급 차이는 굵기. lifetime / fireInterval 은 통일.
        // 시각 임팩트 강화 (가로 stretch) — 희귀 = 반경 60 (폭 120), 영웅 = 반경 100 (폭 200).
        // 영웅 폭 200 ≈ 화면 폭 900 의 22% (Player 사이즈와 비슷). 가로만 키우니 sprite 의 세로
        // 글로우 라인은 drawBitmap dstRect stretch 로 자동 늘어남 (PNG 비율 무관).
        val beamHalf = if (grade == WeaponGrade.EPIC) 150f else 60f
        val muzzleY = player.y - Player.PLAYER_HEIGHT / 2f - Player.BULLET_OFFSET
        val tickPower = (player.calculatePower() / 2).coerceAtLeast(1)
        val laser = LaserBeam.get(gctx, muzzleY, LASER_LIFETIME, tickPower, beamHalf)
        scene.world.add(laser, MainScene.Layer.LASER)
    }

    // 등급 통일 lifetime — 굵기 차이가 곧 화력 차이라 시간까지 차이 둘 필요 없음.
    private const val LASER_LIFETIME = 1.0f
}

// 가장 가까운 적을 추적하는 미사일. 등급별 동시 발사 수 (희귀 1 / 영웅 2).
object MissileWeapon : Weapon() {
    override val displayName = "유도 미사일"
    override val fireInterval = 0.8f
    override val cardSpriteResId = R.mipmap.weapon_homing

    override fun fire(player: Player, scene: MainScene, gctx: GameContext, grade: WeaponGrade) {
        val missileCount = if (grade == WeaponGrade.EPIC) 2 else 1
        val muzzleY = player.y - Player.PLAYER_HEIGHT / 2f - Player.BULLET_OFFSET
        val power = player.calculatePower()
        for (i in 0 until missileCount) {
            // 영웅 = 좌·우 ±30 px 에서 동시 발사. 희귀 = 가운데 1발.
            val offsetX = if (missileCount == 2) (i * 2 - 1) * 30f else 0f
            val missile = HomingMissile.get(gctx, player.x + offsetX, muzzleY, power)
            scene.world.add(missile, MainScene.Layer.MISSILE)
        }
    }
}

object WeaponRegistry {
    val all = listOf(DefaultWeapon, ShotgunWeapon, LaserWeapon, MissileWeapon)
}
