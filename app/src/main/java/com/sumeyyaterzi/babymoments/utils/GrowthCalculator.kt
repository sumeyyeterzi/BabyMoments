package com.sumeyyaterzi.babymoments.utils

import com.sumeyyaterzi.babymoments.data.*

object GrowthCalculator {

    fun assessGrowth(
        ageInMonths: Int,
        gender: Gender,
        weightKg: Float,
        heightCm: Float,
        whoData: WHOData
    ): GrowthAssessment {

        val standards = when (gender) {
            Gender.MALE -> whoData.male
            Gender.FEMALE -> whoData.female
        }

        if (standards.isEmpty()) {
            return GrowthAssessment(
                weightPercentile = "Veri bulunamadı",
                heightPercentile = "Veri bulunamadı",
                weightStatus = GrowthStatus.NORMAL,
                heightStatus = GrowthStatus.NORMAL,
                advice = "WHO standart verileri yüklenemedi."
            )
        }

        // Tam eşleşme var mı kontrol et
        val exactMatch = standards.find { it.ageInMonths == ageInMonths }

        val standard = if (exactMatch != null) {
            exactMatch
        } else {
            // En yakın alt ve üst yaşı bul (interpolasyon için)
            val sortedStandards = standards.sortedBy { it.ageInMonths }
            val lower = sortedStandards.lastOrNull { it.ageInMonths < ageInMonths }
            val upper = sortedStandards.firstOrNull { it.ageInMonths > ageInMonths }

            when {
                lower != null && upper != null -> {
                    // İki nokta arasında interpolasyon yap
                    interpolateStandard(ageInMonths, lower, upper)
                }
                lower != null -> lower // Sadece alt var, onu kullan
                upper != null -> upper // Sadece üst var, onu kullan
                else -> sortedStandards.first() // Hiçbiri yoksa ilkini kullan
            }
        }

        val weightStatus = calculateStatus(weightKg, standard.weight)
        val heightStatus = calculateStatus(heightCm, standard.height)

        return GrowthAssessment(
            weightPercentile = getPercentileText(weightStatus),
            heightPercentile = getPercentileText(heightStatus),
            weightStatus = weightStatus,
            heightStatus = heightStatus,
            advice = generateAdvice(weightStatus, heightStatus)
        )
    }

    // YENİ: İki standart arasında interpolasyon yap
    private fun interpolateStandard(
        targetAge: Int,
        lower: WHOGrowthStandard,
        upper: WHOGrowthStandard
    ): WHOGrowthStandard {
        val ageDiff = upper.ageInMonths - lower.ageInMonths
        val ratio = (targetAge - lower.ageInMonths).toFloat() / ageDiff.toFloat()

        return WHOGrowthStandard(
            ageInMonths = targetAge,
            weight = Percentiles(
                p3 = interpolate(lower.weight.p3, upper.weight.p3, ratio),
                p15 = interpolate(lower.weight.p15, upper.weight.p15, ratio),
                p50 = interpolate(lower.weight.p50, upper.weight.p50, ratio),
                p85 = interpolate(lower.weight.p85, upper.weight.p85, ratio),
                p97 = interpolate(lower.weight.p97, upper.weight.p97, ratio)
            ),
            height = Percentiles(
                p3 = interpolate(lower.height.p3, upper.height.p3, ratio),
                p15 = interpolate(lower.height.p15, upper.height.p15, ratio),
                p50 = interpolate(lower.height.p50, upper.height.p50, ratio),
                p85 = interpolate(lower.height.p85, upper.height.p85, ratio),
                p97 = interpolate(lower.height.p97, upper.height.p97, ratio)
            )
        )
    }

    // YENİ: İki değer arasında doğrusal interpolasyon
    private fun interpolate(lower: Float, upper: Float, ratio: Float): Float {
        return lower + (upper - lower) * ratio
    }

    private fun calculateStatus(value: Float, percentiles: Percentiles): GrowthStatus {
        return when {
            value < percentiles.p3 -> GrowthStatus.VERY_LOW
            value < percentiles.p15 -> GrowthStatus.LOW
            value < percentiles.p85 -> GrowthStatus.NORMAL
            value < percentiles.p97 -> GrowthStatus.HIGH
            else -> GrowthStatus.VERY_HIGH
        }
    }

    private fun getPercentileText(status: GrowthStatus): String {
        return when (status) {
            GrowthStatus.VERY_LOW -> "Çok Düşük (%3'ün altında)"
            GrowthStatus.LOW -> "Düşük (%3-15 arası)"
            GrowthStatus.NORMAL -> "Normal (%15-85 arası)"
            GrowthStatus.HIGH -> "Yüksek (%85-97 arası)"
            GrowthStatus.VERY_HIGH -> "Çok Yüksek (%97'nin üstünde)"
        }
    }

    private fun generateAdvice(
        weightStatus: GrowthStatus,
        heightStatus: GrowthStatus
    ): String {
        return when {
            weightStatus == GrowthStatus.VERY_LOW || heightStatus == GrowthStatus.VERY_LOW -> {
                "⚠️ Bebeğinizin gelişimi standartların altında. Lütfen doktorunuza danışın."
            }
            weightStatus == GrowthStatus.LOW || heightStatus == GrowthStatus.LOW -> {
                "💡 Bebeğinizin gelişimi normalin altında. Beslenme konusunda pediatristinize danışabilirsiniz."
            }
            weightStatus == GrowthStatus.VERY_HIGH || heightStatus == GrowthStatus.VERY_HIGH -> {
                "⚠️ Bebeğinizin gelişimi standartların üstünde. Doktor kontrolü önerilir."
            }
            weightStatus == GrowthStatus.HIGH || heightStatus == GrowthStatus.HIGH -> {
                "💡 Bebeğinizin gelişimi normalin üstünde. Her şey yolunda görünüyor!"
            }
            else -> {
                "✅ Bebeğinizin gelişimi WHO standartlarına uygun! Harika gidiyorsunuz!"
            }
        }
    }

    fun calculateAgeInMonths(birthDateMillis: Long): Int {
        val now = System.currentTimeMillis()
        val ageInDays = (now - birthDateMillis) / (1000 * 60 * 60 * 24)
        return (ageInDays / 30).toInt()
    }
}