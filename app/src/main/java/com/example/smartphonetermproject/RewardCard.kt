package com.example.smartphonetermproject

// LevelUpScene 카드 풀에 들어가는 보상의 추상 단위.
// stat 카드는 풀에서 제거되지 않고 매번 등장 가능 (사용자 결정 — stat 은 항상 뽑힐 수 있음).
// 무기 카드는 받으면 (그 무기, 등급) 조합만 풀에서 제거 — 다른 등급 / 다른 무기는 계속 등장 가능.
sealed class RewardCard {
    abstract val title: String
    abstract val effect: String
    abstract fun apply(player: Player)
}

object AttackStatCard : RewardCard() {
    override val title = "공격력"
    override val effect = "x2"
    override fun apply(player: Player) {
        player.attackMul *= 2.0f
    }
}

object FireRateStatCard : RewardCard() {
    override val title = "공속"
    override val effect = "+30%"
    override fun apply(player: Player) {
        player.fireRateMul *= 1.3f
    }
}

object CritRateStatCard : RewardCard() {
    override val title = "치명타"
    override val effect = "+50%"
    override fun apply(player: Player) {
        player.critRate = (player.critRate + 0.5f).coerceAtMost(1f)
    }
}

class WeaponCard(
    val weapon: Weapon,
    val grade: WeaponGrade,
) : RewardCard() {
    override val title = "${grade.displayName} ${weapon.displayName}"
    override val effect = "장착"
    override fun apply(player: Player) {
        player.currentWeapon = weapon
        player.weaponGrade = grade
    }
}
