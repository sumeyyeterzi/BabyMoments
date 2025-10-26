package com.sumeyyaterzi.babymoments.data

import kotlinx.serialization.Serializable

@Serializable
data class WHOGrowthStandard(
    val ageInMonths: Int,
    val weight: Percentiles,
    val height: Percentiles
)

@Serializable
data class Percentiles(
    val p3: Float,
    val p15: Float,
    val p50: Float,
    val p85: Float,
    val p97: Float
)

@Serializable
data class WHOData(
    val male: List<WHOGrowthStandard>,
    val female: List<WHOGrowthStandard>
)

enum class Gender {
    MALE, FEMALE
}

data class GrowthAssessment(
    val weightPercentile: String,
    val heightPercentile: String,
    val weightStatus: GrowthStatus,
    val heightStatus: GrowthStatus,
    val advice: String
)

enum class GrowthStatus {
    VERY_LOW,
    LOW,
    NORMAL,
    HIGH,
    VERY_HIGH
}