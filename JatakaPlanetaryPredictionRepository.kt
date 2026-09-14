package com.apzok.vastu.jatakachakra

/**
 * Centralized prediction repository for planetary placements across houses and ascendants.
 * Routes prediction requests to dedicated specialized datasets (e.g. JatakaAriesSunPredictions),
 * or falls back to rule-based synthesis for unregistered combinations.
 */
object JatakaPlanetaryPredictionRepository {

    fun getPrediction(
        planet: JatakaPlanet,
        ascendantSign: JatakaSign,
        house: Int,
        sign: JatakaSign,
        dignity: JatakaDignity,
        language: JatakaLanguage
    ): Pair<String, String> {
        // Specialized Dataset 1: Aries Ascendant - Sun
        if (ascendantSign == JatakaSign.ARIES && planet == JatakaPlanet.SUN) {
            val pred = JatakaAriesSunPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 2: Aries Ascendant - Moon
        if (ascendantSign == JatakaSign.ARIES && planet == JatakaPlanet.MOON) {
            val pred = JatakaAriesMoonPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 3: Aries Ascendant - Mars
        if (ascendantSign == JatakaSign.ARIES && planet == JatakaPlanet.MARS) {
            val pred = JatakaAriesMarsPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 4: Aries Ascendant - Mercury
        if (ascendantSign == JatakaSign.ARIES && planet == JatakaPlanet.MERCURY) {
            val pred = JatakaAriesMercuryPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 5: Aries Ascendant - Jupiter
        if (ascendantSign == JatakaSign.ARIES && planet == JatakaPlanet.JUPITER) {
            val pred = JatakaAriesJupiterPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 6: Aries Ascendant - Venus
        if (ascendantSign == JatakaSign.ARIES && planet == JatakaPlanet.VENUS) {
            val pred = JatakaAriesVenusPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 7: Aries Ascendant - Saturn
        if (ascendantSign == JatakaSign.ARIES && planet == JatakaPlanet.SATURN) {
            val pred = JatakaAriesSaturnPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 8: Aries Ascendant - Rahu
        if (ascendantSign == JatakaSign.ARIES && planet == JatakaPlanet.RAHU) {
            val pred = JatakaAriesRahuPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 9: Aries Ascendant - Ketu
        if (ascendantSign == JatakaSign.ARIES && planet == JatakaPlanet.KETU) {
            val pred = JatakaAriesKetuPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 10: Taurus Ascendant - Sun
        if (ascendantSign == JatakaSign.TAURUS && planet == JatakaPlanet.SUN) {
            val pred = JatakaTaurusSunPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 11: Taurus Ascendant - Moon
        if (ascendantSign == JatakaSign.TAURUS && planet == JatakaPlanet.MOON) {
            val pred = JatakaTaurusMoonPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 12: Taurus Ascendant - Mars
        if (ascendantSign == JatakaSign.TAURUS && planet == JatakaPlanet.MARS) {
            val pred = JatakaTaurusMarsPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 13: Taurus Ascendant - Mercury
        if (ascendantSign == JatakaSign.TAURUS && planet == JatakaPlanet.MERCURY) {
            val pred = JatakaTaurusMercuryPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 14: Taurus Ascendant - Jupiter
        if (ascendantSign == JatakaSign.TAURUS && planet == JatakaPlanet.JUPITER) {
            val pred = JatakaTaurusJupiterPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 15: Taurus Ascendant - Venus
        if (ascendantSign == JatakaSign.TAURUS && planet == JatakaPlanet.VENUS) {
            val pred = JatakaTaurusVenusPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 16: Taurus Ascendant - Saturn
        if (ascendantSign == JatakaSign.TAURUS && planet == JatakaPlanet.SATURN) {
            val pred = JatakaTaurusSaturnPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 17: Taurus Ascendant - Rahu
        if (ascendantSign == JatakaSign.TAURUS && planet == JatakaPlanet.RAHU) {
            val pred = JatakaTaurusRahuPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 18: Taurus Ascendant - Ketu
        if (ascendantSign == JatakaSign.TAURUS && planet == JatakaPlanet.KETU) {
            val pred = JatakaTaurusKetuPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 19: Gemini Ascendant - Sun
        if (ascendantSign == JatakaSign.GEMINI && planet == JatakaPlanet.SUN) {
            val pred = JatakaGeminiSunPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 20: Gemini Ascendant - Moon
        if (ascendantSign == JatakaSign.GEMINI && planet == JatakaPlanet.MOON) {
            val pred = JatakaGeminiMoonPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 21: Gemini Ascendant - Mars
        if (ascendantSign == JatakaSign.GEMINI && planet == JatakaPlanet.MARS) {
            val pred = JatakaGeminiMarsPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 22: Gemini Ascendant - Mercury
        if (ascendantSign == JatakaSign.GEMINI && planet == JatakaPlanet.MERCURY) {
            val pred = JatakaGeminiMercuryPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 23: Gemini Ascendant - Jupiter
        if (ascendantSign == JatakaSign.GEMINI && planet == JatakaPlanet.JUPITER) {
            val pred = JatakaGeminiJupiterPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 24: Gemini Ascendant - Venus
        if (ascendantSign == JatakaSign.GEMINI && planet == JatakaPlanet.VENUS) {
            val pred = JatakaGeminiVenusPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Specialized Dataset 25: Gemini Ascendant - Saturn
        if (ascendantSign == JatakaSign.GEMINI && planet == JatakaPlanet.SATURN) {
            val pred = JatakaGeminiSaturnPredictions.getPrediction(house, language)
            return Pair(pred.title, pred.text)
        }

        // Generic fallback synthesis for un-registered planet/ascendant combinations
        val planetNameEn = planet.name.lowercase().replaceFirstChar { it.uppercase() }
        val planetNameHi = JatakaVargaEngine.getHindiPlanetName(planet)
        val signNameEn = sign.name
        val signNameHi = JatakaVargaEngine.getHindiSignName(sign)

        val isRegional = language in setOf(JatakaLanguage.KANNADA, JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU)

        return when {
            language == JatakaLanguage.HINDI -> {
                val title = "$planetNameHi का ${house}वें भाव ($signNameHi) में फल"
                val text = "जातक की जन्म कुंडली में $planetNameHi ${house}वें भाव ($signNameHi राशि) में स्थित होकर ${dignity.name} स्थिति दर्शाते हैं। यह स्थिति जीवन में इस भाव से संबंधित क्षेत्रों में विशेष प्रभाव एवं अनुभव प्रदान करती है।"
                Pair(title, text)
            }
            isRegional -> {
                val title = "$planetNameEn in House $house ($signNameEn)"
                val text = "$planetNameEn placed in House $house ($signNameEn) with ${dignity.name} dignity influences the themes of House $house."
                Pair(title, text)
            }
            else -> {
                val title = "$planetNameEn in the ${house}th House ($signNameEn)"
                val text = "$planetNameEn placed in House $house ($signNameEn) with ${dignity.name} dignity shapes your experiences, strengths, and life developments in matters governed by House $house."
                Pair(title, text)
            }
        }
    }

    fun getGocharPrediction(
        ascendantSign: JatakaSign,
        category: JatakaTaurusGocharPredictions.GocharCategory,
        language: JatakaLanguage
    ): Pair<String, String>? {
        if (ascendantSign == JatakaSign.TAURUS) {
            val pred = JatakaTaurusGocharPredictions.getPrediction(category, language)
            return Pair(pred.title, pred.text)
        }
        return null
    }
}
