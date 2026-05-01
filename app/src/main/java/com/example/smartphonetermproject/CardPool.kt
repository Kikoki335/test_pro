package com.example.smartphonetermproject

// LevelUpScene 카드 풀. MainScene 인스턴스마다 하나 (BossScene 진입 시 새 MainScene 이라 풀 reset
// 되지만 placeholder — 추후 GameContext 같은 공유 객체로 영구화 가능).
//
// 풀 구성:
//   - statCards: 공격력 / 공속 / 치명타 — 항상 풀에 있음 (받아도 안 빠짐).
//   - weaponCards: 3종 × 2등급 = 6장 — 받으면 그 (무기, 등급) 조합만 풀에서 빠짐.
//
// 매 LevelUp 시 pickThree() 가 (stat 3 + 남은 무기) 풀에서 무작위 3장 추출. 무기 6개 다 받으면
// stat 3장만 남아 그대로 등장.
class CardPool {
    private val statCards: List<RewardCard> = listOf(
        AttackStatCard,
        FireRateStatCard,
        CritRateStatCard,
    )

    private val weaponCards: MutableList<WeaponCard> = mutableListOf(
        WeaponCard(ShotgunWeapon, WeaponGrade.RARE),
        WeaponCard(ShotgunWeapon, WeaponGrade.EPIC),
        WeaponCard(LaserWeapon, WeaponGrade.RARE),
        WeaponCard(LaserWeapon, WeaponGrade.EPIC),
        WeaponCard(MissileWeapon, WeaponGrade.RARE),
        WeaponCard(MissileWeapon, WeaponGrade.EPIC),
    )

    fun pickThree(): List<RewardCard> {
        // stat 3 + 무기 6 = 9 → 3 (무기 다 모은 후) 사이라 항상 ≥ 3 보장.
        val pool = (statCards + weaponCards).toMutableList()
        pool.shuffle()
        return pool.take(3)
    }

    fun consume(card: RewardCard) {
        if (card is WeaponCard) {
            weaponCards.remove(card)
        }
        // stat 카드는 풀에 그대로 — 매번 다시 등장 가능.
    }
}
