package com.apzok.vastu.jatakachakra

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

class JatakaChakraTests {

    private val narasimha1030Input = JatakaBirthInput(
        name = "Narasimha",
        date = LocalDate.of(1983, 10, 9),
        time = LocalTime.of(10, 30),
        placeName = "Kumaraswamy Layout, Bengaluru Urban",
        latitude = 12.8963,
        longitude = 77.5592,
        zoneId = "Asia/Kolkata"
    )

    @Test
    fun longitudeNormalizationAndSigns() {
        assertEquals(0.0, JatakaMath.normalize(360.0), 1e-6)
        assertEquals(10.0, JatakaMath.normalize(-350.0), 1e-6)
        assertEquals(JatakaSign.ARIES, JatakaSign.fromLongitude(15.0))
        assertEquals(JatakaSign.PISCES, JatakaSign.fromLongitude(350.0))
    }

    @Test
    fun wholeSignHouse_wrapsCorrectly() {
        assertEquals(1, JatakaMath.wholeSignHouse(241.0, 250.0))
        assertEquals(2, JatakaMath.wholeSignHouse(275.0, 250.0))
        assertEquals(12, JatakaMath.wholeSignHouse(220.0, 250.0))
    }

    @Test
    fun nakshatraAndPada_areBounded() {
        assertEquals(0, JatakaMath.nakshatraIndex(0.0))
        assertEquals(1, JatakaMath.pada(0.0))
        assertEquals(26, JatakaMath.nakshatraIndex(359.999999))
        assertEquals(4, JatakaMath.pada(359.999999))
    }

    @Test
    fun allSixteenVargasCalculatedCorrectly() {
        val Aries02 = 0.2
        assertEquals(JatakaSign.ARIES, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D1_RASHI, Aries02))
        assertEquals(JatakaSign.LEO, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D2_HORA, Aries02))
        assertEquals(JatakaSign.ARIES, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D3_DREKKANA, Aries02))
        assertEquals(JatakaSign.ARIES, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D4_CHATURTHAMSA, Aries02))
        assertEquals(JatakaSign.ARIES, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D7_SAPTAMSA, Aries02))
        assertEquals(JatakaSign.ARIES, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D9_NAVAMSA, Aries02))
        assertEquals(JatakaSign.ARIES, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D10_DASAMSA, Aries02))
        assertEquals(JatakaSign.ARIES, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D12_DWADASAMSA, Aries02))
        assertEquals(JatakaSign.ARIES, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D16_SHODASAMSA, Aries02))
        assertEquals(JatakaSign.ARIES, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D20_VIMSAMSA, Aries02))
        assertEquals(JatakaSign.LEO, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D24_CHATURVIMSAMSA, Aries02))
        assertEquals(JatakaSign.ARIES, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D27_BHAMSA, Aries02))
        assertEquals(JatakaSign.ARIES, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D30_TRIMSAMSA, Aries02))
        assertEquals(JatakaSign.ARIES, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D40_KHAVEDAMSA, Aries02))
        assertEquals(JatakaSign.ARIES, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D45_AKSHAVEDAMSA, Aries02))
        assertEquals(JatakaSign.ARIES, JatakaVargaEngine.calculateVargaSign(JatakaVarga.D60_SHASHTIAMSA, Aries02))
    }

    @Test
    fun vimshottariProducesFiveLevelActivePath() {
        val engine = JatakaVimshottariDashaEngine()
        val birth = Instant.parse("2000-01-01T00:00:00Z")
        val periods = engine.calculate(birth, 210.0)
        assertEquals(9, periods.size)

        val active = engine.activePath(periods, Instant.parse("2026-09-10T00:00:00Z"))
        assertTrue(active.size >= 3)
    }

    @Test
    fun ageCalculationAccuracyAndLeapYearBoundaries() {
        val reportDate = LocalDate.of(2026, 9, 13)

        // 13-09-2006 -> 20
        assertEquals(20, JatakaPersonalLifeAnalysis.completedAge(LocalDate.of(2006, 9, 13), reportDate))
        // 14-09-2006 -> 19
        assertEquals(19, JatakaPersonalLifeAnalysis.completedAge(LocalDate.of(2006, 9, 14), reportDate))
        // 13-09-1971 -> 55
        assertEquals(55, JatakaPersonalLifeAnalysis.completedAge(LocalDate.of(1971, 9, 13), reportDate))

        // Leap Year Feb 29 Birthday test:
        // Born Feb 29 2004, on Feb 28 2025 -> completed age 20
        assertEquals(20, JatakaPersonalLifeAnalysis.completedAge(LocalDate.of(2004, 2, 29), LocalDate.of(2025, 2, 28)))
        // On March 1 2025 -> completed age 21
        assertEquals(21, JatakaPersonalLifeAnalysis.completedAge(LocalDate.of(2004, 2, 29), LocalDate.of(2025, 3, 1)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun birthDateAfterReportDateThrowsException() {
        JatakaPersonalLifeAnalysis.completedAge(LocalDate.of(2026, 9, 14), LocalDate.of(2026, 9, 13))
    }

    @Test
    fun indianDoshaFinderEvaluatesKeyDoshasCorrectly() {
        val engine = JatakaSimpleEngine()
        val result = engine.calculate(narasimha1030Input)

        val doshas = result.doshas
        assertTrue("Doshas list must contain at least 10 traditional evaluations", doshas.size >= 10)

        val pitru = doshas.firstOrNull { it.doshaId == "pitru_dosha" }
        assertNotNull("Pitru Dosha must be evaluated", pitru)

        val guruChandal = doshas.firstOrNull { it.doshaId == "guru_chandal_dosha" }
        assertNotNull("Guru Chandal Dosha must be evaluated", guruChandal)

        val grahan = doshas.firstOrNull { it.doshaId == "grahan_dosha" }
        assertNotNull("Grahan Dosha must be evaluated", grahan)
    }

    @Test
    fun planetHousePredictions_generatesSunHousePrediction() {
        val engine = JatakaSimpleEngine()
        val result = engine.calculate(narasimha1030Input)

        val sunPred = result.lifeAreaPredictions.firstOrNull { it.categoryKey == "sun" }
        assertNotNull("Sun house prediction must be present", sunPred)
        assertTrue("Sun prediction title must not be blank", sunPred!!.title.isNotBlank())
        assertTrue("Sun prediction text must not be blank", sunPred.summary.isNotBlank())
    }

    @Test
    fun ariesAscendantSunPredictions_returnsExactProvidedTextForAll12Houses() {
        val house1Text = JatakaAriesSunPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 1st House (Aries)", house1Text.title)
        assertTrue(house1Text.text.contains("learned, well-educated, and possesses deep self-knowledge"))
        assertTrue(house1Text.text.contains("difficulties in marital life"))

        val house2Text = JatakaAriesSunPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 2nd House (Taurus)", house2Text.title)
        assertTrue(house2Text.text.contains("encounters hurdles in acquiring an education"))

        val house3Text = JatakaAriesSunPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 3rd House (Gemini)", house3Text.title)
        assertTrue(house3Text.text.contains("influential, bold speech and strong physical vitality"))

        val house4Text = JatakaAriesSunPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 4th House (Cancer)", house4Text.title)
        assertTrue(house4Text.text.contains("inherits nurturing maternal qualities"))

        val house5Text = JatakaAriesSunPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 5th House (Leo)", house5Text.title)
        assertTrue(house5Text.text.contains("Intellectual, articulate, and farsighted"))

        val house6Text = JatakaAriesSunPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 6th House (Virgo)", house6Text.title)
        assertTrue(house6Text.text.contains("grit to defeat enemies and overcome obstacles"))

        val house7Text = JatakaAriesSunPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 7th House (Libra)", house7Text.title)
        assertTrue(house7Text.text.contains("unhappy marital life, strained ties with children"))

        val house8Text = JatakaAriesSunPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 8th House (Scorpio)", house8Text.title)
        assertTrue(house8Text.text.contains("endures distress concerning children and setbacks in education"))

        val house9Text = JatakaAriesSunPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 9th House (Sagittarius)", house9Text.title)
        assertTrue(house9Text.text.contains("Learned, just, and influential"))

        val house10Text = JatakaAriesSunPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 10th House (Capricorn)", house10Text.title)
        assertTrue(house10Text.text.contains("immature behavior, harbors conflict with their father"))

        val house11Text = JatakaAriesSunPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 11th House (Aquarius)", house11Text.title)
        assertTrue(house11Text.text.contains("indifferent attitude toward formal learning"))

        val house12Text = JatakaAriesSunPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 12th House (Pisces)", house12Text.title)
        assertTrue(house12Text.text.contains("lacks formal educational achievements, suffers from weak eyesight"))
    }

    @Test
    fun ariesAscendantSunPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaAriesSunPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun ariesAscendantMoonPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaAriesMoonPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 1st House (Aries)", h1.title)
        assertTrue(h1.text.contains("enjoys life, attains all comforts, and receives abundant affection from the mother"))

        val h2 = JatakaAriesMoonPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 2nd House (Taurus)", h2.title)
        assertTrue(h2.text.contains("accumulates wealth, extensive land, and real estate"))

        val h3 = JatakaAriesMoonPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 3rd House (Gemini)", h3.title)
        assertTrue(h3.text.contains("harmonious ties with siblings, receives gains through the mother"))

        val h4 = JatakaAriesMoonPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 4th House (Cancer)", h4.title)
        assertTrue(h4.text.contains("receives deep maternal love, inherits property through the mother"))

        val h5 = JatakaAriesMoonPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 5th House (Leo)", h5.title)
        assertTrue(h5.text.contains("deeply thoughtful, values their intellect"))

        val h6 = JatakaAriesMoonPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 6th House (Virgo)", h6.title)
        assertTrue(h6.text.contains("hurdles in domestic comfort, lacks maternal affection"))

        val h7 = JatakaAriesMoonPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 7th House (Libra)", h7.title)
        assertTrue(h7.text.contains("enjoys domestic harmony, is physically healthy and attractive"))

        val h8 = JatakaAriesMoonPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 8th House (Scorpio)", h8.title)
        assertTrue(h8.text.contains("early separation from or loss of the mother"))

        val h9 = JatakaAriesMoonPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 9th House (Sagittarius)", h9.title)
        assertTrue(h9.text.contains("fortune bolstered by the mother's side"))

        val h10 = JatakaAriesMoonPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 10th House (Capricorn)", h10.title)
        assertTrue(h10.text.contains("affection from the father, acquires real estate"))

        val h11 = JatakaAriesMoonPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 11th House (Aquarius)", h11.title)
        assertTrue(h11.text.contains("enjoys strong earnings, good education, and sweet speech"))

        val h12 = JatakaAriesMoonPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 12th House (Pisces)", h12.title)
        assertTrue(h12.text.contains("spends lavishly on pleasures as well as noble and charitable causes"))
    }

    @Test
    fun ariesAscendantMoonPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaAriesMoonPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun ariesAscendantMarsPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaAriesMarsPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 1st House (Aries)", h1.title)
        assertTrue(h1.text.contains("attains fame, commanding physical vitality, and spiritual or intuitive depth"))

        val h2 = JatakaAriesMarsPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 2nd House (Taurus)", h2.title)
        assertTrue(h2.text.contains("absorbed in accumulating wealth, the native nonetheless experiences severe financial drains"))

        val h3 = JatakaAriesMarsPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 3rd House (Gemini)", h3.title)
        assertTrue(h3.text.contains("energetic and self-made, the native rises entirely through personal merit"))

        val h4 = JatakaAriesMarsPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 4th House (Cancer)", h4.title)
        assertTrue(h4.text.contains("short in stature and endures a lack of maternal affection"))

        val h5 = JatakaAriesMarsPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 5th House (Leo)", h5.title)
        assertTrue(h5.text.contains("possesses sharp intellect combined with an impulsive, rash temperament"))

        val h6 = JatakaAriesMarsPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 6th House (Virgo)", h6.title)
        assertTrue(h6.text.contains("Influential, industrious, and brave, the native earns renown"))

        val h7 = JatakaAriesMarsPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 7th House (Libra)", h7.title)
        assertTrue(h7.text.contains("manages personal and professional duties with great strain"))

        val h8 = JatakaAriesMarsPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 8th House (Scorpio)", h8.title)
        assertTrue(h8.text.contains("Possessing a lean frame, the native endures chronic restlessness"))

        val h9 = JatakaAriesMarsPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 9th House (Sagittarius)", h9.title)
        assertTrue(h9.text.contains("experiences erratic fortune with frequent stumbling blocks"))

        val h10 = JatakaAriesMarsPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 10th House (Capricorn)", h10.title)
        assertTrue(h10.text.contains("Mars attains exaltation here, bestowing immense physical stamina"))

        val h11 = JatakaAriesMarsPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 11th House (Aquarius)", h11.title)
        assertTrue(h11.text.contains("Industrious and relentless, the native earns through hard struggle"))

        val h12 = JatakaAriesMarsPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 12th House (Pisces)", h12.title)
        assertTrue(h12.text.contains("suffers from physical weakness, chronic restlessness, and domestic discontent"))
    }

    @Test
    fun ariesAscendantMarsPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaAriesMarsPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun ariesAscendantMercuryPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaAriesMercuryPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 1st House (Aries)", h1.title)
        assertTrue(h1.text.contains("maintains friendly ties with siblings, receives active support from maternal relatives"))

        val h2 = JatakaAriesMercuryPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 2nd House (Taurus)", h2.title)
        assertTrue(h2.text.contains("Intellectual and ambitious, the native steadily accumulates wealth"))

        val h3 = JatakaAriesMercuryPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 3rd House (Gemini)", h3.title)
        assertTrue(h3.text.contains("Mercury resides in its own sign, bestowing exceptional intellect, precision, and perfectionism"))

        val h4 = JatakaAriesMercuryPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 4th House (Cancer)", h4.title)
        assertTrue(h4.text.contains("Energetic and industrious, the native faces domestic restlessness"))

        val h5 = JatakaAriesMercuryPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 5th House (Leo)", h5.title)
        assertTrue(h5.text.contains("Cerebral, prudent, and exceptionally clever, the native exercises keen judgment"))

        val h6 = JatakaAriesMercuryPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 6th House (Virgo)", h6.title)
        assertTrue(h6.text.contains("Mercury attains exaltation in its own moolatrikona sign here"))

        val h7 = JatakaAriesMercuryPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 7th House (Libra)", h7.title)
        assertTrue(h7.text.contains("must toil hard to navigate vocational hurdles, yet they achieve steady progress"))

        val h8 = JatakaAriesMercuryPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 8th House (Scorpio)", h8.title)
        assertTrue(h8.text.contains("faces financial vulnerability, abdominal ailments, and daily vocational stress"))

        val h9 = JatakaAriesMercuryPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 9th House (Sagittarius)", h9.title)
        assertTrue(h9.text.contains("attains renown, authority, and professional success through diligent enterprise"))

        val h10 = JatakaAriesMercuryPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 10th House (Capricorn)", h10.title)
        assertTrue(h10.text.contains("Possessing sharp analytical and discriminative acumen, the native receives support"))

        val h11 = JatakaAriesMercuryPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 11th House (Aquarius)", h11.title)
        assertTrue(h11.text.contains("earns substantial wealth, benefits through brothers and sisters"))

        val h12 = JatakaAriesMercuryPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 12th House (Pisces)", h12.title)
        assertTrue(h12.text.contains("Mercury is debilitated here. The native lacks warm ties with siblings"))
    }

    @Test
    fun ariesAscendantMercuryPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaAriesMercuryPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun ariesAscendantJupiterPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaAriesJupiterPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 1st House (Aries)", h1.title)
        assertTrue(h1.text.contains("handsome, fortunate, and honored, spending money righteously"))

        val h2 = JatakaAriesJupiterPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 2nd House (Taurus)", h2.title)
        assertTrue(h2.text.contains("fortunate and earns substantial wealth with divine assistance"))

        val h3 = JatakaAriesJupiterPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 3rd House (Gemini)", h3.title)
        assertTrue(h3.text.contains("Fortunate and industrious, the native exercises good control over expenses"))

        val h4 = JatakaAriesJupiterPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 4th House (Cancer)", h4.title)
        assertTrue(h4.text.contains("enjoys happiness and gains through real estate and property"))

        val h5 = JatakaAriesJupiterPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 5th House (Leo)", h5.title)
        assertTrue(h5.text.contains("Highly fortunate, intellectual, and well-educated"))

        val h6 = JatakaAriesJupiterPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 6th House (Virgo)", h6.title)
        assertTrue(h6.text.contains("less fortunate and somewhat irreligious, facing potential loss of fame"))

        val h7 = JatakaAriesJupiterPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 7th House (Libra)", h7.title)
        assertTrue(h7.text.contains("achieves success in daily life and domestic affairs"))

        val h8 = JatakaAriesJupiterPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 8th House (Scorpio)", h8.title)
        assertTrue(h8.text.contains("faces misfortune and lacks religious inclination, yet is endowed with longevity"))

        val h9 = JatakaAriesJupiterPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 9th House (Sagittarius)", h9.title)
        assertTrue(h9.text.contains("Highly fortunate with brilliant success in all undertakings"))

        val h10 = JatakaAriesJupiterPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 10th House (Capricorn)", h10.title)
        assertTrue(h10.text.contains("achieves success in business and profession"))

        val h11 = JatakaAriesJupiterPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 11th House (Aquarius)", h11.title)
        assertTrue(h11.text.contains("Reaping gains propelled by destiny, the native receives timely assistance"))

        val h12 = JatakaAriesJupiterPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 12th House (Pisces)", h12.title)
        assertTrue(h12.text.contains("possesses a religious outlook, though overall fortune is moderate"))
    }

    @Test
    fun ariesAscendantJupiterPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaAriesJupiterPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun ariesAscendantVenusPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaAriesVenusPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 1st House (Aries)", h1.title)
        assertTrue(h1.text.contains("establishes a flourishing profession, earns substantial wealth, and is blessed with a beautiful spouse"))

        val h2 = JatakaAriesVenusPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 2nd House (Taurus)", h2.title)
        assertTrue(h2.text.contains("Venus is placed in its own sign (Swakshetra). The native amasses extensive riches"))

        val h3 = JatakaAriesVenusPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 3rd House (Gemini)", h3.title)
        assertTrue(h3.text.contains("accumulates enormous wealth through keen intellect and tact"))

        val h4 = JatakaAriesVenusPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 4th House (Cancer)", h4.title)
        assertTrue(h4.text.contains("prosperous, wealthy, and mentally content. They marry an accomplished spouse"))

        val h5 = JatakaAriesVenusPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 5th House (Leo)", h5.title)
        assertTrue(h5.text.contains("secures a respectable profession, earns good wealth, and displays fine conversational skills"))

        val h6 = JatakaAriesVenusPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 6th House (Virgo)", h6.title)
        assertTrue(h6.text.contains("Venus is debilitated (Neecha) here. The native endures financial anxiety"))

        val h7 = JatakaAriesVenusPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 7th House (Libra)", h7.title)
        assertTrue(h7.text.contains("Venus occupies its own sign (forming Malavya Yoga). The native is handsome"))

        val h8 = JatakaAriesVenusPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 8th House (Scorpio)", h8.title)
        assertTrue(h8.text.contains("suffers from delicate health, depletion of wealth, and deep dissatisfaction"))

        val h9 = JatakaAriesVenusPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 9th House (Sagittarius)", h9.title)
        assertTrue(h9.text.contains("Highly fortunate, clever, and courageous, the native amasses abundant wealth"))

        val h10 = JatakaAriesVenusPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 10th House (Capricorn)", h10.title)
        assertTrue(h10.text.contains("derives substantial advantages through the father, inherits or acquires real estate"))

        val h11 = JatakaAriesVenusPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 11th House (Aquarius)", h11.title)
        assertTrue(h11.text.contains("attains an enviable professional status and accrues immense wealth"))

        val h12 = JatakaAriesVenusPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 12th House (Pisces)", h12.title)
        assertTrue(h12.text.contains("Venus attains exaltation (Uccha) here. The native spends lavishly on luxuries"))
    }

    @Test
    fun ariesAscendantVenusPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaAriesVenusPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun ariesAscendantSaturnPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaAriesSaturnPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 1st House (Aries)", h1.title)
        assertTrue(h1.text.contains("Saturn is debilitated (Neecha) here. The native lacks physical charm"))

        val h2 = JatakaAriesSaturnPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 2nd House (Taurus)", h2.title)
        assertTrue(h2.text.contains("earns substantial income and receives recognition and gains from the government"))

        val h3 = JatakaAriesSaturnPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 3rd House (Gemini)", h3.title)
        assertTrue(h3.text.contains("Valorous, energetic, and educated, the native receives assistance from siblings"))

        val h4 = JatakaAriesSaturnPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 4th House (Cancer)", h4.title)
        assertTrue(h4.text.contains("receives support from the father as well as patronage from the state"))

        val h5 = JatakaAriesSaturnPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 5th House (Leo)", h5.title)
        assertTrue(h5.text.contains("derives gains through personal intellect and education, striving hard"))

        val h6 = JatakaAriesSaturnPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 6th House (Virgo)", h6.title)
        assertTrue(h6.text.contains("experiences strain with the father, financial insecurity, and persistent anxiety"))

        val h7 = JatakaAriesSaturnPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 7th House (Libra)", h7.title)
        assertTrue(h7.text.contains("Saturn attains exaltation (Uccha) here (forming Sasa Yoga)"))

        val h8 = JatakaAriesSaturnPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 8th House (Scorpio)", h8.title)
        assertTrue(h8.text.contains("endures distress regarding the father, lives in isolation or away from kin"))

        val h9 = JatakaAriesSaturnPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 9th House (Sagittarius)", h9.title)
        assertTrue(h9.text.contains("Highly fortunate and virtuous, the native secures a stable, regular income stream"))

        val h10 = JatakaAriesSaturnPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 10th House (Capricorn)", h10.title)
        assertTrue(h10.text.contains("Saturn occupies its own sign (Swakshetra). The native conducts large-scale business"))

        val h11 = JatakaAriesSaturnPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 11th House (Aquarius)", h11.title)
        assertTrue(h11.text.contains("Saturn occupies its Moolatrikona sign. The native secures fixed, dependable income"))

        val h12 = JatakaAriesSaturnPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 12th House (Pisces)", h12.title)
        assertTrue(h12.text.contains("experiences financial drain or separation linked to the father"))
    }

    @Test
    fun ariesAscendantSaturnPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaAriesSaturnPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun ariesAscendantRahuPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaAriesRahuPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 1st House (Aries)", h1.title)
        assertTrue(h1.text.contains("prone to frequent physical ailments or facial afflictions"))

        val h2 = JatakaAriesRahuPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 2nd House (Taurus)", h2.title)
        assertTrue(h2.text.contains("experiences depletion of accumulated wealth, domestic friction"))

        val h3 = JatakaAriesRahuPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 3rd House (Gemini)", h3.title)
        assertTrue(h3.text.contains("Rahu performs strongly here. Endowed with fearless valor"))

        val h4 = JatakaAriesRahuPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 4th House (Cancer)", h4.title)
        assertTrue(h4.text.contains("suffers early separation from or sorrow regarding the mother"))

        val h5 = JatakaAriesRahuPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 5th House (Leo)", h5.title)
        assertTrue(h5.text.contains("encounters severe stumbling blocks in formal education"))

        val h6 = JatakaAriesRahuPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 6th House (Virgo)", h6.title)
        assertTrue(h6.text.contains("gains a decisive upper hand over all adversaries"))

        val h7 = JatakaAriesRahuPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 7th House (Libra)", h7.title)
        assertTrue(h7.text.contains("faces severe friction with the spouse and manages domestic life"))

        val h8 = JatakaAriesRahuPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 8th House (Scorpio)", h8.title)
        assertTrue(h8.text.contains("endures disruptions in livelihood, chronic worries in daily life"))

        val h9 = JatakaAriesRahuPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 9th House (Sagittarius)", h9.title)
        assertTrue(h9.text.contains("experiences distress, nervousness, and frequent setbacks"))

        val h10 = JatakaAriesRahuPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 10th House (Capricorn)", h10.title)
        assertTrue(h10.text.contains("experiences strain with the father and encounters roadblocks"))

        val h11 = JatakaAriesRahuPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 11th House (Aquarius)", h11.title)
        assertTrue(h11.text.contains("secures massive financial gains and accumulates wealth"))

        val h12 = JatakaAriesRahuPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 12th House (Pisces)", h12.title)
        assertTrue(h12.text.contains("faces regret or dissatisfaction over expenditures"))
    }

    @Test
    fun ariesAscendantRahuPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaAriesRahuPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun ariesAscendantKetuPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaAriesKetuPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 1st House (Aries)", h1.title)
        assertTrue(h1.text.contains("suffers from physical weakness, chronic restlessness"))

        val h2 = JatakaAriesKetuPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 2nd House (Taurus)", h2.title)
        assertTrue(h2.text.contains("endures depletion of accumulated savings, chronic monetary shortages"))

        val h3 = JatakaAriesKetuPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 3rd House (Gemini)", h3.title)
        assertTrue(h3.text.contains("experiences discord, separation, and enmity with siblings"))

        val h4 = JatakaAriesKetuPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 4th House (Cancer)", h4.title)
        assertTrue(h4.text.contains("suffers from a lack of maternal affection, separation from the ancestral homeland"))

        val h5 = JatakaAriesKetuPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 5th House (Leo)", h5.title)
        assertTrue(h5.text.contains("experiences interruptions in formal education, sorrow regarding children"))

        val h6 = JatakaAriesKetuPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 6th House (Virgo)", h6.title)
        assertTrue(h6.text.contains("Brave and resilient, the native decisively overcomes open and sudden adversaries"))

        val h7 = JatakaAriesKetuPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 7th House (Libra)", h7.title)
        assertTrue(h7.text.contains("faces early separation from the spouse, severe marital disharmony"))

        val h8 = JatakaAriesKetuPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 8th House (Scorpio)", h8.title)
        assertTrue(h8.text.contains("experiences physical debility in the spine, lower back, or lower abdomen"))

        val h9 = JatakaAriesKetuPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 9th House (Sagittarius)", h9.title)
        assertTrue(h9.text.contains("labors tirelessly to trigger fortune and destiny, often progressing"))

        val h10 = JatakaAriesKetuPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 10th House (Capricorn)", h10.title)
        assertTrue(h10.text.contains("endures distress, estrangement, or loss concerning the father"))

        val h11 = JatakaAriesKetuPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 11th House (Aquarius)", h11.title)
        assertTrue(h11.text.contains("secures massive financial gains through relentless, aggressive efforts"))

        val h12 = JatakaAriesKetuPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 12th House (Pisces)", h12.title)
        assertTrue(h12.text.contains("incurs substantial expenditures and remains anxious over financial drain"))
    }

    @Test
    fun ariesAscendantKetuPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaAriesKetuPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun taurusAscendantSunPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaTaurusSunPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 1st House (Taurus)", h1.title)
        assertTrue(h1.text.contains("acquires landed property and achieves professional fulfillment"))

        val h2 = JatakaTaurusSunPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 2nd House (Gemini)", h2.title)
        assertTrue(h2.text.contains("accumulates wealth, buildings, and real estate, deriving deep satisfaction"))

        val h3 = JatakaTaurusSunPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 3rd House (Cancer)", h3.title)
        assertTrue(h3.text.contains("derives pride and happiness from influential siblings"))

        val h4 = JatakaTaurusSunPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 4th House (Leo)", h4.title)
        assertTrue(h4.text.contains("Sun is strongly placed in its own sign (Swakshetra). The native acquires extensive lands"))

        val h5 = JatakaTaurusSunPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 5th House (Virgo)", h5.title)
        assertTrue(h5.text.contains("Endowed with sharp intellect, wisdom, and formal learning"))

        val h6 = JatakaTaurusSunPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 6th House (Libra)", h6.title)
        assertTrue(h6.text.contains("Sun is debilitated (Neecha) here. The native endures discord"))

        val h7 = JatakaTaurusSunPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 7th House (Scorpio)", h7.title)
        assertTrue(h7.text.contains("enjoys domestic happiness, support from the mother, and harmony with their spouse"))

        val h8 = JatakaTaurusSunPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 8th House (Sagittarius)", h8.title)
        assertTrue(h8.text.contains("suffers distress and separation concerning the mother and may travel abroad"))

        val h9 = JatakaTaurusSunPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 9th House (Capricorn)", h9.title)
        assertTrue(h9.text.contains("fortunate, righteous, and devoted to Dharma"))

        val h10 = JatakaTaurusSunPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 10th House (Aquarius)", h10.title)
        assertTrue(h10.text.contains("earns vocational satisfaction, property gains, and recognition"))

        val h11 = JatakaTaurusSunPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 11th House (Pisces)", h11.title)
        assertTrue(h11.text.contains("secures steady income, gains landed assets through or alongside the mother"))

        val h12 = JatakaTaurusSunPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 12th House (Aries)", h12.title)
        assertTrue(h12.text.contains("Although the Sun is exalted (Uccha) here, its 12th house placement brings excessive expenditures"))
    }

    @Test
    fun taurusAscendantSunPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaTaurusSunPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun taurusAscendantMoonPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaTaurusMoonPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 1st House (Taurus)", h1.title)
        assertTrue(h1.text.contains("Moon attains exaltation (Uccha) here. The native receives active cooperation"))

        val h2 = JatakaTaurusMoonPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 2nd House (Gemini)", h2.title)
        assertTrue(h2.text.contains("steadily accumulates wealth, enjoys cordial bonds with siblings"))

        val h3 = JatakaTaurusMoonPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 3rd House (Cancer)", h3.title)
        assertTrue(h3.text.contains("Moon is strongly situated in its own sign (Swakshetra). The native is patient"))

        val h4 = JatakaTaurusMoonPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 4th House (Leo)", h4.title)
        assertTrue(h4.text.contains("acquires houses, landed property, and domestic comforts, supported by siblings"))

        val h5 = JatakaTaurusMoonPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 5th House (Virgo)", h5.title)
        assertTrue(h5.text.contains("Endowed with fine educational accomplishments, the native shines"))

        val h6 = JatakaTaurusMoonPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 6th House (Libra)", h6.title)
        assertTrue(h6.text.contains("faces ideological friction with siblings and suffers from occasional physical exhaustion"))

        val h7 = JatakaTaurusMoonPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 7th House (Scorpio)", h7.title)
        assertTrue(h7.text.contains("Moon is debilitated (Neecha) here. The native experiences discontent in marital life"))

        val h8 = JatakaTaurusMoonPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 8th House (Sagittarius)", h8.title)
        assertTrue(h8.text.contains("faces distance or separation from siblings. Resilient in handling daily routines"))

        val h9 = JatakaTaurusMoonPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 9th House (Capricorn)", h9.title)
        assertTrue(h9.text.contains("Blessed by fortune and destiny, the native maintains warm unity"))

        val h10 = JatakaTaurusMoonPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 10th House (Aquarius)", h10.title)
        assertTrue(h10.text.contains("achieves steady vocational advancement, securing cooperation from siblings"))

        val h11 = JatakaTaurusMoonPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 11th House (Pisces)", h11.title)
        assertTrue(h11.text.contains("Shrewd, intelligent, and hardworking, the native amasses wealth"))

        val h12 = JatakaTaurusMoonPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 12th House (Aries)", h12.title)
        assertTrue(h12.text.contains("incurs substantial expenditures and suffers from chronic mental unrest"))
    }

    @Test
    fun taurusAscendantMoonPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaTaurusMoonPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun taurusAscendantMarsPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaTaurusMarsPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 1st House (Taurus)", h1.title)
        assertTrue(h1.text.contains("prone to blood impurities, physical heat, and continuous daily expenditures"))

        val h2 = JatakaTaurusMarsPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 2nd House (Gemini)", h2.title)
        assertTrue(h2.text.contains("encounters persistent hurdles in monetary accumulation and domestic harmony"))

        val h3 = JatakaTaurusMarsPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 3rd House (Cancer)", h3.title)
        assertTrue(h3.text.contains("Mars is debilitated (Neecha) here. The native endures deep distress"))

        val h4 = JatakaTaurusMarsPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 4th House (Leo)", h4.title)
        assertTrue(h4.text.contains("suffers early loss of the mother or separation from the homeland"))

        val h5 = JatakaTaurusMarsPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 5th House (Virgo)", h5.title)
        assertTrue(h5.text.contains("spends excessively, exhibits academic inconsistencies"))

        val h6 = JatakaTaurusMarsPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 6th House (Libra)", h6.title)
        assertTrue(h6.text.contains("faces vocational anxieties, irregular expenditures, and concerns regarding the spouse's well-being"))

        val h7 = JatakaTaurusMarsPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 7th House (Scorpio)", h7.title)
        assertTrue(h7.text.contains("Mars occupies its own sign (Swakshetra). The native incurs high-status expenditures"))

        val h8 = JatakaTaurusMarsPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 8th House (Sagittarius)", h8.title)
        assertTrue(h8.text.contains("toils under severe professional stress, facing disputes, losses through the spouse"))

        val h9 = JatakaTaurusMarsPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 9th House (Capricorn)", h9.title)
        assertTrue(h9.text.contains("Mars attains exaltation (Uccha) here. The native incurs massive business"))

        val h10 = JatakaTaurusMarsPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 10th House (Aquarius)", h10.title)
        assertTrue(h10.text.contains("performs impactful work in their professional sphere, yet suffers from bodily disorders"))

        val h11 = JatakaTaurusMarsPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 11th House (Pisces)", h11.title)
        assertTrue(h11.text.contains("amasses wealth through their profession and successfully keeps adversaries subdued"))

        val h12 = JatakaTaurusMarsPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 12th House (Aries)", h12.title)
        assertTrue(h12.text.contains("Mars occupies its own sign (Swakshetra). The native incurs financial and emotional losses"))
    }

    @Test
    fun taurusAscendantMarsPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaTaurusMarsPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun taurusAscendantMercuryPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaTaurusMercuryPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 1st House (Taurus)", h1.title)
        assertTrue(h1.text.contains("possesses deep wisdom, fine education, and sharp analytical discernment"))

        val h2 = JatakaTaurusMercuryPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 2nd House (Gemini)", h2.title)
        assertTrue(h2.text.contains("Mercury resides in its own sign (Swakshetra). The native amasses vast fortunes"))

        val h3 = JatakaTaurusMercuryPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 3rd House (Cancer)", h3.title)
        assertTrue(h3.text.contains("achieves good educational standing, steady earnings, and pride through their children"))

        val h4 = JatakaTaurusMercuryPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 4th House (Leo)", h4.title)
        assertTrue(h4.text.contains("attains excellent education, builds substantial wealth, and acquires landed estates"))

        val h5 = JatakaTaurusMercuryPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 5th House (Virgo)", h5.title)
        assertTrue(h5.text.contains("Mercury attains exaltation (Uccha) in its Moolatrikona sign here"))

        val h6 = JatakaTaurusMercuryPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 6th House (Libra)", h6.title)
        assertTrue(h6.text.contains("experiences interruptions in formal education and mild discord in family life"))

        val h7 = JatakaTaurusMercuryPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 7th House (Scorpio)", h7.title)
        assertTrue(h7.text.contains("shrewd and enterprising in generating wealth, motivated by a constant ambition"))

        val h8 = JatakaTaurusMercuryPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 8th House (Sagittarius)", h8.title)
        assertTrue(h8.text.contains("experiences sorrow or setbacks concerning children, disruptions in higher education"))

        val h9 = JatakaTaurusMercuryPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 9th House (Capricorn)", h9.title)
        assertTrue(h9.text.contains("Highly fortunate, devout, and God-fearing, the native acquires higher education"))

        val h10 = JatakaTaurusMercuryPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 10th House (Aquarius)", h10.title)
        assertTrue(h10.text.contains("secures advanced education, gains from the father and children"))

        val h11 = JatakaTaurusMercuryPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 11th House (Pisces)", h11.title)
        assertTrue(h11.text.contains("Mercury is debilitated (Neecha) here. The native faces obstacles in accumulating liquid wealth"))

        val h12 = JatakaTaurusMercuryPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 12th House (Aries)", h12.title)
        assertTrue(h12.text.contains("suffers financial losses, high expenditures, and setbacks in academic pursuits"))
    }

    @Test
    fun taurusAscendantMercuryPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaTaurusMercuryPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun taurusAscendantJupiterPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaTaurusJupiterPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 1st House (Taurus)", h1.title)
        assertTrue(h1.text.contains("clever, well-educated, and commands respect and dignity in their daily life"))

        val h2 = JatakaTaurusJupiterPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 2nd House (Gemini)", h2.title)
        assertTrue(h2.text.contains("accumulates wealth and sees progressive financial growth. They experience friction"))

        val h3 = JatakaTaurusJupiterPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 3rd House (Cancer)", h3.title)
        assertTrue(h3.text.contains("Jupiter attains exaltation (Uccha) here. The native is exceptionally valorous"))

        val h4 = JatakaTaurusJupiterPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 4th House (Leo)", h4.title)
        assertTrue(h4.text.contains("enjoys longevity, vocational fame, and intuitive or occult depth"))

        val h5 = JatakaTaurusJupiterPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 5th House (Virgo)", h5.title)
        assertTrue(h5.text.contains("experiences interruptions or minor deficits in formal education"))

        val h6 = JatakaTaurusJupiterPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 6th House (Libra)", h6.title)
        assertTrue(h6.text.contains("Financial gains come through arduous struggle. The native suffers from health complications"))

        val h7 = JatakaTaurusJupiterPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 7th House (Scorpio)", h7.title)
        assertTrue(h7.text.contains("Maintaining self-respect and a noble demeanor, the native secures daily occupational gains"))

        val h8 = JatakaTaurusJupiterPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 8th House (Sagittarius)", h8.title)
        assertTrue(h8.text.contains("Jupiter occupies its own sign (Moolatrikona). The native commands personal dignity"))

        val h9 = JatakaTaurusJupiterPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 9th House (Capricorn)", h9.title)
        assertTrue(h9.text.contains("Jupiter is debilitated (Neecha) here. The native suffers severe erosion of fortune"))

        val h10 = JatakaTaurusJupiterPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 10th House (Aquarius)", h10.title)
        assertTrue(h10.text.contains("secures gains through relentless hard work and establishes foreign associations"))

        val h11 = JatakaTaurusJupiterPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 11th House (Pisces)", h11.title)
        assertTrue(h11.text.contains("Jupiter resides in its own sign (Swakshetra). The native enjoys longevity"))

        val h12 = JatakaTaurusJupiterPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 12th House (Aries)", h12.title)
        assertTrue(h12.text.contains("incurs substantial yet judicious expenditures and labors tirelessly"))
    }

    @Test
    fun taurusAscendantJupiterPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaTaurusJupiterPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun taurusAscendantVenusPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaTaurusVenusPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 1st House (Taurus)", h1.title)
        assertTrue(h1.text.contains("Venus is strongly placed in its own sign (forming Malavya Yoga)"))

        val h2 = JatakaTaurusVenusPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 2nd House (Gemini)", h2.title)
        assertTrue(h2.text.contains("puts forth dedicated, whole-hearted efforts to expand resources"))

        val h3 = JatakaTaurusVenusPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 3rd House (Cancer)", h3.title)
        assertTrue(h3.text.contains("progresses through arduous daily toil and champions meaningful, noble causes"))

        val h4 = JatakaTaurusVenusPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 4th House (Leo)", h4.title)
        assertTrue(h4.text.contains("faces emotional strain with the mother, friction regarding the father"))

        val h5 = JatakaTaurusVenusPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 5th House (Virgo)", h5.title)
        assertTrue(h5.text.contains("Venus is debilitated (Neecha) here. The native suffers from physical weakness"))

        val h6 = JatakaTaurusVenusPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 6th House (Libra)", h6.title)
        assertTrue(h6.text.contains("Venus occupies its own sign (Swakshetra). The native undertakes independent"))

        val h7 = JatakaTaurusVenusPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 7th House (Scorpio)", h7.title)
        assertTrue(h7.text.contains("Possessing an attractive and charming appearance, the native toils diligently"))

        val h8 = JatakaTaurusVenusPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 8th House (Sagittarius)", h8.title)
        assertTrue(h8.text.contains("endures physical distress, losses linked to maternal relatives"))

        val h9 = JatakaTaurusVenusPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 9th House (Capricorn)", h9.title)
        assertTrue(h9.text.contains("Courteous, clever, and refined, the native is fundamentally fortunate"))

        val h10 = JatakaTaurusVenusPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 10th House (Aquarius)", h10.title)
        assertTrue(h10.text.contains("earns vocational distinction, business growth, and patronage"))

        val h11 = JatakaTaurusVenusPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 11th House (Pisces)", h11.title)
        assertTrue(h11.text.contains("Venus attains exaltation (Uccha) here. Handsome, clever, and hardworking"))

        val h12 = JatakaTaurusVenusPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 12th House (Aries)", h12.title)
        assertTrue(h12.text.contains("incurs heavy, uncontrolled expenditures and suffers from recurring physical illnesses"))
    }

    @Test
    fun taurusAscendantVenusPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaTaurusVenusPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun taurusAscendantSaturnPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaTaurusSaturnPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 1st House (Taurus)", h1.title)
        assertTrue(h1.text.contains("Regarded as fortunate and influential, the native earns high respect"))

        val h2 = JatakaTaurusSaturnPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 2nd House (Gemini)", h2.title)
        assertTrue(h2.text.contains("amasses wealth through the grace of fortune and destiny"))

        val h3 = JatakaTaurusSaturnPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 3rd House (Cancer)", h3.title)
        assertTrue(h3.text.contains("achieves massive progress, success, and renown entirely through persistent hard labor"))

        val h4 = JatakaTaurusSaturnPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 4th House (Leo)", h4.title)
        assertTrue(h4.text.contains("commands formidable authority and dominance over adversaries"))

        val h5 = JatakaTaurusSaturnPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 5th House (Virgo)", h5.title)
        assertTrue(h5.text.contains("derives gains through the father and secures honor and wealth"))

        val h6 = JatakaTaurusSaturnPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 6th House (Libra)", h6.title)
        assertTrue(h6.text.contains("Saturn attains exaltation (Uccha) here. The native draws great strength"))

        val h7 = JatakaTaurusSaturnPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 7th House (Scorpio)", h7.title)
        assertTrue(h7.text.contains("Endowed with good fortune, the native secures tangible gains and recognition"))

        val h8 = JatakaTaurusSaturnPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 8th House (Sagittarius)", h8.title)
        assertTrue(h8.text.contains("experiences sorrow and setbacks concerning the father alongside fluctuations"))

        val h9 = JatakaTaurusSaturnPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 9th House (Capricorn)", h9.title)
        assertTrue(h9.text.contains("Saturn resides in its own sign (Swakshetra). The native enjoys immense paternal happiness"))

        val h10 = JatakaTaurusSaturnPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 10th House (Aquarius)", h10.title)
        assertTrue(h10.text.contains("Saturn occupies its Moolatrikona sign (forming Sasa Yoga)"))

        val h11 = JatakaTaurusSaturnPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 11th House (Pisces)", h11.title)
        assertTrue(h11.text.contains("secures massive financial gains and continuous income streams"))

        val h12 = JatakaTaurusSaturnPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 12th House (Aries)", h12.title)
        assertTrue(h12.text.contains("Saturn is debilitated (Neecha) here. The native endures setbacks, sorrow"))
    }

    @Test
    fun taurusAscendantSaturnPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaTaurusSaturnPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun taurusAscendantRahuPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaTaurusRahuPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 1st House (Taurus)", h1.title)
        assertTrue(h1.text.contains("possesses a delicate physical constitution and is prone to mental worries"))

        val h2 = JatakaTaurusRahuPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 2nd House (Gemini)", h2.title)
        assertTrue(h2.text.contains("labors tirelessly and devises complex plans to amass wealth"))

        val h3 = JatakaTaurusRahuPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 3rd House (Cancer)", h3.title)
        assertTrue(h3.text.contains("endures loss, friction, or separation regarding siblings"))

        val h4 = JatakaTaurusRahuPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 4th House (Leo)", h4.title)
        assertTrue(h4.text.contains("experiences loss or sorrow regarding the mother, loss of landed property"))

        val h5 = JatakaTaurusRahuPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 5th House (Virgo)", h5.title)
        assertTrue(h5.text.contains("suffers setbacks or breaks in formal education, alongside sorrow"))

        val h6 = JatakaTaurusRahuPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 6th House (Libra)", h6.title)
        assertTrue(h6.text.contains("establishes commanding dominance over rivals, litigations, and open enemies"))

        val h7 = JatakaTaurusRahuPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 7th House (Scorpio)", h7.title)
        assertTrue(h7.text.contains("suffers distress in marital life, acute deficits in sensual fulfillment"))

        val h8 = JatakaTaurusRahuPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 8th House (Sagittarius)", h8.title)
        assertTrue(h8.text.contains("faces acute threats to physical vitality, health disruptions"))

        val h9 = JatakaTaurusRahuPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 9th House (Capricorn)", h9.title)
        assertTrue(h9.text.contains("suffers erosion of luck and erratic fortune. Prone to severe moral missteps"))

        val h10 = JatakaTaurusRahuPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 10th House (Aquarius)", h10.title)
        assertTrue(h10.text.contains("endures dissatisfaction and strain with the father"))

        val h11 = JatakaTaurusRahuPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 11th House (Pisces)", h11.title)
        assertTrue(h11.text.contains("achieves decisive success in securing financial windfalls and material gains"))

        val h12 = JatakaTaurusRahuPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 12th House (Aries)", h12.title)
        assertTrue(h12.text.contains("incurs massive losses and uncontrollable, chaotic expenditures"))
    }

    @Test
    fun taurusAscendantRahuPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaTaurusRahuPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun taurusAscendantKetuPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaTaurusKetuPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 1st House (Taurus)", h1.title)
        assertTrue(h1.text.contains("endures physical weakness, chronic mental agitation, and severe restlessness"))

        val h2 = JatakaTaurusKetuPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 2nd House (Gemini)", h2.title)
        assertTrue(h2.text.contains("experiences heavy depletion of wealth and recurring financial setbacks"))

        val h3 = JatakaTaurusKetuPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 3rd House (Cancer)", h3.title)
        assertTrue(h3.text.contains("undertakes bold, energetic ventures and toils relentlessly for personal advancement"))

        val h4 = JatakaTaurusKetuPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 4th House (Leo)", h4.title)
        assertTrue(h4.text.contains("faces estrangement or separation from the motherland and endures persistent disputes"))

        val h5 = JatakaTaurusKetuPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 5th House (Virgo)", h5.title)
        assertTrue(h5.text.contains("encounters severe interruptions in formal studies, struggles to express thoughts clearly"))

        val h6 = JatakaTaurusKetuPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 6th House (Libra)", h6.title)
        assertTrue(h6.text.contains("confronts adversaries with courage, though they are subject to physical ailments"))

        val h7 = JatakaTaurusKetuPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 7th House (Scorpio)", h7.title)
        assertTrue(h7.text.contains("endures distress, separation, or losses through the spouse, accompanied by severe dissatisfaction"))

        val h8 = JatakaTaurusKetuPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 8th House (Sagittarius)", h8.title)
        assertTrue(h8.text.contains("may engage in questionable or secretive dealings, striving tirelessly to secure gains"))

        val h9 = JatakaTaurusKetuPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 9th House (Capricorn)", h9.title)
        assertTrue(h9.text.contains("experiences an erosion of spiritual merit, lack of faith in God"))

        val h10 = JatakaTaurusKetuPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 10th House (Aquarius)", h10.title)
        assertTrue(h10.text.contains("experiences strain and sorrow regarding the father, facing ongoing complications"))

        val h11 = JatakaTaurusKetuPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 11th House (Pisces)", h11.title)
        assertTrue(h11.text.contains("amasses substantial wealth through relentless drive, occasionally adopting unauthorized"))

        val h12 = JatakaTaurusKetuPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 12th House (Aries)", h12.title)
        assertTrue(h12.text.contains("incurs substantial, uncontrollable expenditures and maintains an unconventional"))
    }

    @Test
    fun taurusAscendantKetuPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaTaurusKetuPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun taurusAscendantGocharPredictions_returnsExactProvidedTextForAllCategories() {
        val c1 = JatakaTaurusGocharPredictions.getPrediction(JatakaTaurusGocharPredictions.GocharCategory.DAILY_MONETARY_GAINS, JatakaLanguage.ENGLISH)
        assertEquals("Daily Predictions on Monetary Gains", c1.title)
        assertTrue(c1.text.contains("experiences significant financial gains on days when the transiting Moon traverses either Gemini"))

        val c2 = JatakaTaurusGocharPredictions.getPrediction(JatakaTaurusGocharPredictions.GocharCategory.MONTHLY_MONETARY_GAINS, JatakaLanguage.ENGLISH)
        assertEquals("Monthly Predictions on Monetary Gains", c2.title)
        assertTrue(c2.text.contains("Notable monthly wealth gains materialize during months when transiting Mercury occupies Gemini"))

        val c3 = JatakaTaurusGocharPredictions.getPrediction(JatakaTaurusGocharPredictions.GocharCategory.YEARLY_MONETARY_GAINS, JatakaLanguage.ENGLISH)
        assertEquals("Yearly Predictions on Monetary Gains", c3.title)
        assertTrue(c3.text.contains("secures peak monetary prosperity, career growth, and avenues of advancement"))

        val c4 = JatakaTaurusGocharPredictions.getPrediction(JatakaTaurusGocharPredictions.GocharCategory.EDUCATION_AND_CHILDREN, JatakaLanguage.ENGLISH)
        assertEquals("Predictions Regarding Education and Children", c4.title)
        assertTrue(c4.text.contains("Academic advancement and auspicious progress regarding children manifest during periods"))

        val c5 = JatakaTaurusGocharPredictions.getPrediction(JatakaTaurusGocharPredictions.GocharCategory.WIFE_AND_DAILY_OCCUPATION, JatakaLanguage.ENGLISH)
        assertEquals("Predictions Regarding Wife and Daily Occupation", c5.title)
        assertTrue(c5.text.contains("Auspicious times for marital affairs and diurnal occupations occur when Mars transits Scorpio"))

        val c6 = JatakaTaurusGocharPredictions.getPrediction(JatakaTaurusGocharPredictions.GocharCategory.LONGEVITY_STOMACH_VITALITY, JatakaLanguage.ENGLISH)
        assertEquals("Predictions Regarding Longevity, Stomach, and Vitality", c6.title)
        assertTrue(c6.text.contains("When Jupiter transits Scorpio, Sagittarius, Pisces, Taurus, Gemini, or Leo"))

        val c7 = JatakaTaurusGocharPredictions.getPrediction(JatakaTaurusGocharPredictions.GocharCategory.PHYSIQUE_WILLPOWER_FAME, JatakaLanguage.ENGLISH)
        assertEquals("Predictions Regarding Physique, Attractiveness, Willpower, and Fame", c7.title)
        assertTrue(c7.text.contains("During periods when Venus transits Taurus, Cancer, Leo, Scorpio, Capricorn, Aquarius, or Pisces"))

        val c8 = JatakaTaurusGocharPredictions.getPrediction(JatakaTaurusGocharPredictions.GocharCategory.MOTHER_REAL_ESTATE_PEACE, JatakaLanguage.ENGLISH)
        assertEquals("Predictions Regarding Mother, Real Estate, Domestic Peace, and Happiness", c8.title)
        assertTrue(c8.text.contains("When the transiting Sun occupies Leo (4th house of home/mother), Scorpio"))
    }

    @Test
    fun taurusAscendantGocharPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            JatakaTaurusGocharPredictions.GocharCategory.entries.forEach { cat ->
                val pred = JatakaTaurusGocharPredictions.getPrediction(cat, lang)
                assertTrue("Title for ${lang.name} category ${cat.name} must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} category ${cat.name} must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun geminiAscendantSunPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaGeminiSunPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 1st House (Gemini)", h1.title)
        assertTrue(h1.text.contains("performs influential and commanding deeds, supported actively by brothers and sisters"))

        val h2 = JatakaGeminiSunPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 2nd House (Cancer)", h2.title)
        assertTrue(h2.text.contains("toils hard to expand their financial reserves, channeling physical energy"))

        val h3 = JatakaGeminiSunPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 3rd House (Leo)", h3.title)
        assertTrue(h3.text.contains("Sun occupies its own sign (Swakshetra). The native commands extraordinary physical vitality"))

        val h4 = JatakaGeminiSunPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 4th House (Virgo)", h4.title)
        assertTrue(h4.text.contains("enjoys cordial ties with siblings and acquires extensive landed property"))

        val h5 = JatakaGeminiSunPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 5th House (Libra)", h5.title)
        assertTrue(h5.text.contains("Sun is debilitated (Neecha) here. The native experiences friction and loss regarding children"))

        val h6 = JatakaGeminiSunPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 6th House (Scorpio)", h6.title)
        assertTrue(h6.text.contains("channels their stamina and courage into crushing rivals, commanding immense authority"))

        val h7 = JatakaGeminiSunPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 7th House (Sagittarius)", h7.title)
        assertTrue(h7.text.contains("experiences notable post-marital advancement and commands strong influence"))

        val h8 = JatakaGeminiSunPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 8th House (Capricorn)", h8.title)
        assertTrue(h8.text.contains("suffers from depleted stamina and physical vitality, alongside sorrow"))

        val h9 = JatakaGeminiSunPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 9th House (Aquarius)", h9.title)
        assertTrue(h9.text.contains("actively works to awaken and harness destiny through personal initiative"))

        val h10 = JatakaGeminiSunPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 10th House (Pisces)", h10.title)
        assertTrue(h10.text.contains("achieves massive career advancement through sheer vigor and industrious drive"))

        val h11 = JatakaGeminiSunPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 11th House (Aries)", h11.title)
        assertTrue(h11.text.contains("Sun attains exaltation (Uccha) here. The native secures massive financial gains"))

        val h12 = JatakaGeminiSunPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Sun in the 12th House (Taurus)", h12.title)
        assertTrue(h12.text.contains("faces heavy, uncontrollable expenditures and persistent anxiety regarding financial drains"))
    }

    @Test
    fun geminiAscendantSunPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaGeminiSunPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun geminiAscendantMoonPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaGeminiMoonPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 1st House (Gemini)", h1.title)
        assertTrue(h1.text.contains("accumulates wealth, possesses an attractive and charming personality"))

        val h2 = JatakaGeminiMoonPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 2nd House (Cancer)", h2.title)
        assertTrue(h2.text.contains("Moon resides in its own sign (Swakshetra). The native amasses vast fortunes"))

        val h3 = JatakaGeminiMoonPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 3rd House (Leo)", h3.title)
        assertTrue(h3.text.contains("engages in high-value, arduous enterprises and toils diligently"))

        val h4 = JatakaGeminiMoonPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 4th House (Virgo)", h4.title)
        assertTrue(h4.text.contains("amasses substantial liquid wealth, real estate, and fine buildings"))

        val h5 = JatakaGeminiMoonPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 5th House (Libra)", h5.title)
        assertTrue(h5.text.contains("receives a refined education, experiences steady growth through children"))

        val h6 = JatakaGeminiMoonPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 6th House (Scorpio)", h6.title)
        assertTrue(h6.text.contains("Moon is debilitated (Neecha) here. The native endures chronic restlessness"))

        val h7 = JatakaGeminiMoonPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 7th House (Sagittarius)", h7.title)
        assertTrue(h7.text.contains("earns substantial wealth through daily occupation, business, and partnerships"))

        val h8 = JatakaGeminiMoonPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 8th House (Capricorn)", h8.title)
        assertTrue(h8.text.contains("experiences bodily debility, financial instability, and sorrow"))

        val h9 = JatakaGeminiMoonPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 9th House (Aquarius)", h9.title)
        assertTrue(h9.text.contains("Fortunate and righteous, the native accumulates wealth through the grace of destiny"))

        val h10 = JatakaGeminiMoonPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 10th House (Pisces)", h10.title)
        assertTrue(h10.text.contains("inherits paternal property and earns immense wealth through commercial enterprise"))

        val h11 = JatakaGeminiMoonPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 11th House (Aries)", h11.title)
        assertTrue(h11.text.contains("secures massive financial gains and joyous domestic expansion"))

        val h12 = JatakaGeminiMoonPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Moon in the 12th House (Taurus)", h12.title)
        assertTrue(h12.text.contains("Moon attains exaltation (Uccha) here. The native generates wealth through diplomatic tact"))
    }

    @Test
    fun geminiAscendantMoonPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaGeminiMoonPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun geminiAscendantMarsPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaGeminiMarsPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 1st House (Gemini)", h1.title)
        assertTrue(h1.text.contains("secures substantial material gains through rigorous physical labor and personal initiative"))

        val h2 = JatakaGeminiMarsPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 2nd House (Cancer)", h2.title)
        assertTrue(h2.text.contains("Mars is debilitated (Neecha) here. The native suffers severe depletion of wealth"))

        val h3 = JatakaGeminiMarsPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 3rd House (Leo)", h3.title)
        assertTrue(h3.text.contains("possesses heroic valor and pride, channeling immense energy"))

        val h4 = JatakaGeminiMarsPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 4th House (Virgo)", h4.title)
        assertTrue(h4.text.contains("experiences mixed fortunes regarding the mother, marked by both gains and emotional strains"))

        val h5 = JatakaGeminiMarsPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 5th House (Libra)", h5.title)
        assertTrue(h5.text.contains("generates income through intellectual exertion, technical skills"))

        val h6 = JatakaGeminiMarsPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 6th House (Scorpio)", h6.title)
        assertTrue(h6.text.contains("Mars occupies its own sign (Swakshetra). The native possesses exceptional courage"))

        val h7 = JatakaGeminiMarsPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 7th House (Sagittarius)", h7.title)
        assertTrue(h7.text.contains("secures wealth through grueling, relentless toil in their daily occupation"))

        val h8 = JatakaGeminiMarsPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 8th House (Capricorn)", h8.title)
        assertTrue(h8.text.contains("Mars attains exaltation (Uccha) here. The native enjoys enhanced longevity"))

        val h9 = JatakaGeminiMarsPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 9th House (Aquarius)", h9.title)
        assertTrue(h9.text.contains("secures periodic gains through the intervention of fortune, yet incurs high expenditures"))

        val h10 = JatakaGeminiMarsPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 10th House (Pisces)", h10.title)
        assertTrue(h10.text.contains("undertakes influential, highly industrious ventures, securing honors"))

        val h11 = JatakaGeminiMarsPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 11th House (Aries)", h11.title)
        assertTrue(h11.text.contains("Mars occupies its Moolatrikona sign (Swakshetra). The native secures massive profits"))

        val h12 = JatakaGeminiMarsPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Mars in the 12th House (Taurus)", h12.title)
        assertTrue(h12.text.contains("endures financial anxiety, income volatility, and heavy, uncontrolled expenditures"))
    }

    @Test
    fun geminiAscendantMarsPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaGeminiMarsPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun geminiAscendantMercuryPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaGeminiMercuryPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 1st House (Gemini)", h1.title)
        assertTrue(h1.text.contains("Mercury resides in its own sign (forming Bhadra Mahapurusha Yoga)"))

        val h2 = JatakaGeminiMercuryPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 2nd House (Cancer)", h2.title)
        assertTrue(h2.text.contains("amasses vast financial reserves, acquires extensive lands and houses"))

        val h3 = JatakaGeminiMercuryPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 3rd House (Leo)", h3.title)
        assertTrue(h3.text.contains("enjoys cheerful ties with siblings and receives steadfast backing"))

        val h4 = JatakaGeminiMercuryPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 4th House (Virgo)", h4.title)
        assertTrue(h4.text.contains("Mercury attains exaltation (Uccha) in its Moolatrikona sign (forming a powerful Bhadra Yoga)"))

        val h5 = JatakaGeminiMercuryPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 5th House (Libra)", h5.title)
        assertTrue(h5.text.contains("attains exceptional scholarly intellect, enjoys maternal support"))

        val h6 = JatakaGeminiMercuryPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 6th House (Scorpio)", h6.title)
        assertTrue(h6.text.contains("experiences domestic distress, chronic anxiety, and periodic dependence on others"))

        val h7 = JatakaGeminiMercuryPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 7th House (Sagittarius)", h7.title)
        assertTrue(h7.text.contains("attains high vocational success, domestic harmony, and marital bliss"))

        val h8 = JatakaGeminiMercuryPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 8th House (Capricorn)", h8.title)
        assertTrue(h8.text.contains("experiences physical debility, chaotic domestic environments, and an initial shortage"))

        val h9 = JatakaGeminiMercuryPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 9th House (Aquarius)", h9.title)
        assertTrue(h9.text.contains("Highly fortunate and endowed with deep foresight, the native possesses an attractive physique"))

        val h10 = JatakaGeminiMercuryPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 10th House (Pisces)", h10.title)
        assertTrue(h10.text.contains("Mercury is debilitated (Neecha) here. The native faces bodily fatigue"))

        val h11 = JatakaGeminiMercuryPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 11th House (Aries)", h11.title)
        assertTrue(h11.text.contains("achieves massive financial gains through personal energy, enterprise, and sharp intellect"))

        val h12 = JatakaGeminiMercuryPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Mercury in the 12th House (Taurus)", h12.title)
        assertTrue(h12.text.contains("experiences physical exhaustion, separation from the mother and homeland"))
    }

    @Test
    fun geminiAscendantMercuryPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaGeminiMercuryPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun geminiAscendantJupiterPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaGeminiJupiterPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 1st House (Gemini)", h1.title)
        assertTrue(h1.text.contains("commands great dignity and importance in marital life and engages in a prestigious"))

        val h2 = JatakaGeminiJupiterPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 2nd House (Cancer)", h2.title)
        assertTrue(h2.text.contains("Jupiter attains exaltation (Uccha) here. Hailing from an affluent family"))

        val h3 = JatakaGeminiJupiterPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 3rd House (Leo)", h3.title)
        assertTrue(h3.text.contains("commands prominence, energy, and charm through or alongside the spouse"))

        val h4 = JatakaGeminiJupiterPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 4th House (Virgo)", h4.title)
        assertTrue(h4.text.contains("attains happiness and distinction in business and profession, conducting daily affairs"))

        val h5 = JatakaGeminiJupiterPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 5th House (Libra)", h5.title)
        assertTrue(h5.text.contains("attains advanced education, high skill, and intellectual tact"))

        val h6 = JatakaGeminiJupiterPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 6th House (Scorpio)", h6.title)
        assertTrue(h6.text.contains("may engage in service-oriented or dependent professions"))

        val h7 = JatakaGeminiJupiterPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 7th House (Sagittarius)", h7.title)
        assertTrue(h7.text.contains("Jupiter resides in its own sign (forming Hamsa Mahapurusha Yoga)"))

        val h8 = JatakaGeminiJupiterPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 8th House (Capricorn)", h8.title)
        assertTrue(h8.text.contains("Jupiter is debilitated (Neecha) here. The native suffers losses and sorrow"))

        val h9 = JatakaGeminiJupiterPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 9th House (Aquarius)", h9.title)
        assertTrue(h9.text.contains("strengthens their fortune and destiny, receives academic distinction"))

        val h10 = JatakaGeminiJupiterPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 10th House (Pisces)", h10.title)
        assertTrue(h10.text.contains("Jupiter occupies its own sign (forming Hamsa Mahapurusha Yoga)"))

        val h11 = JatakaGeminiJupiterPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 11th House (Aries)", h11.title)
        assertTrue(h11.text.contains("amasses massive financial gains and achieves high ambitions, enjoying paternal prosperity"))

        val h12 = JatakaGeminiJupiterPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Jupiter in the 12th House (Taurus)", h12.title)
        assertTrue(h12.text.contains("faces severe distress, separation, or loss regarding the spouse and setbacks concerning the father"))
    }

    @Test
    fun geminiAscendantJupiterPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaGeminiJupiterPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun geminiAscendantVenusPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaGeminiVenusPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 1st House (Gemini)", h1.title)
        assertTrue(h1.text.contains("possesses a healthy, attractive body, keen mental strength, and sharp intellect"))

        val h2 = JatakaGeminiVenusPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 2nd House (Cancer)", h2.title)
        assertTrue(h2.text.contains("experiences periodic financial fluctuations and drains on accumulated resources"))

        val h3 = JatakaGeminiVenusPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 3rd House (Leo)", h3.title)
        assertTrue(h3.text.contains("encounters strain or limited support from siblings, setbacks in formal studies"))

        val h4 = JatakaGeminiVenusPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 4th House (Virgo)", h4.title)
        assertTrue(h4.text.contains("Venus is debilitated (Neecha) here. The native suffers separation from the mother"))

        val h5 = JatakaGeminiVenusPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 5th House (Libra)", h5.title)
        assertTrue(h5.text.contains("Venus occupies its own Moolatrikona sign (Swakshetra). The native attains fine scholarship"))

        val h6 = JatakaGeminiVenusPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 6th House (Scorpio)", h6.title)
        assertTrue(h6.text.contains("experiences interruptions in formal education, heavy expenditures, and grief"))

        val h7 = JatakaGeminiVenusPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 7th House (Sagittarius)", h7.title)
        assertTrue(h7.text.contains("commands strength and stability in their daily occupation and partnerships"))

        val h8 = JatakaGeminiVenusPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 8th House (Capricorn)", h8.title)
        assertTrue(h8.text.contains("encounters academic hurdles and sorrow or deficiency regarding children"))

        val h9 = JatakaGeminiVenusPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 9th House (Aquarius)", h9.title)
        assertTrue(h9.text.contains("Though facing initial stumbling blocks in formal education, the native possesses the inner grit"))

        val h10 = JatakaGeminiVenusPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 10th House (Pisces)", h10.title)
        assertTrue(h10.text.contains("Venus attains exaltation (Uccha) here. Highly clever, influential, and dignified"))

        val h11 = JatakaGeminiVenusPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 11th House (Aries)", h11.title)
        assertTrue(h11.text.contains("leverages advanced education and sharp intellect to secure substantial financial gains"))

        val h12 = JatakaGeminiVenusPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Venus in the 12th House (Taurus)", h12.title)
        assertTrue(h12.text.contains("Venus resides in its own sign (Swakshetra). The native incurs massive expenditures"))
    }

    @Test
    fun geminiAscendantVenusPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaGeminiVenusPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun geminiAscendantSaturnPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaGeminiSaturnPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 1st House (Gemini)", h1.title)
        assertTrue(h1.text.contains("fortunate, endowed with good longevity, and deeply focused on their future growth"))

        val h2 = JatakaGeminiSaturnPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 2nd House (Cancer)", h2.title)
        assertTrue(h2.text.contains("experiences depletion of accumulated savings and recurring friction within the family circle"))

        val h3 = JatakaGeminiSaturnPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 3rd House (Leo)", h3.title)
        assertTrue(h3.text.contains("Endowed with long life, the native toils relentlessly and engages in grueling physical labor"))

        val h4 = JatakaGeminiSaturnPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 4th House (Virgo)", h4.title)
        assertTrue(h4.text.contains("experiences some deficiency in maternal warmth or domestic ease"))

        val h5 = JatakaGeminiSaturnPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 5th House (Libra)", h5.title)
        assertTrue(h5.text.contains("Saturn attains exaltation (Uccha) here. The native possesses deep wisdom"))

        val h6 = JatakaGeminiSaturnPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 6th House (Scorpio)", h6.title)
        assertTrue(h6.text.contains("accomplishes difficult tasks through elaborate, demanding, and intricate strategies"))

        val h7 = JatakaGeminiSaturnPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 7th House (Sagittarius)", h7.title)
        assertTrue(h7.text.contains("fortunate, enjoys good longevity, and achieves steady progress and fame"))

        val h8 = JatakaGeminiSaturnPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 8th House (Capricorn)", h8.title)
        assertTrue(h8.text.contains("Saturn resides in its own sign (Swakshetra). The native enjoys long life"))

        val h9 = JatakaGeminiSaturnPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 9th House (Aquarius)", h9.title)
        assertTrue(h9.text.contains("Saturn occupies its Moolatrikona sign. Highly fortunate and blessed with longevity"))

        val h10 = JatakaGeminiSaturnPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 10th House (Pisces)", h10.title)
        assertTrue(h10.text.contains("experiences loss, distance, or sorrow regarding the father, yet achieves a commanding rise"))

        val h11 = JatakaGeminiSaturnPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 11th House (Aries)", h11.title)
        assertTrue(h11.text.contains("Saturn is debilitated (Neecha) here. The native faces financial volatility"))

        val h12 = JatakaGeminiSaturnPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Saturn in the 12th House (Taurus)", h12.title)
        assertTrue(h12.text.contains("endures inner restlessness, high expenditures, and occasional financial losses"))
    }

    @Test
    fun geminiAscendantSaturnPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaGeminiSaturnPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun geminiAscendantRahuPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaGeminiRahuPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 1st House (Gemini)", h1.title)
        assertTrue(h1.text.contains("may be tall and self-confident"))

        val h2 = JatakaGeminiRahuPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 2nd House (Cancer)", h2.title)
        assertTrue(h2.text.contains("may face loss of savings and remain worried about money"))

        val h3 = JatakaGeminiRahuPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 3rd House (Leo)", h3.title)
        assertTrue(h3.text.contains("may face stress or separation from siblings"))

        val h4 = JatakaGeminiRahuPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 4th House (Virgo)", h4.title)
        assertTrue(h4.text.contains("may receive less happiness from the mother"))

        val h5 = JatakaGeminiRahuPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 5th House (Libra)", h5.title)
        assertTrue(h5.text.contains("may face difficulty in education and in making clear decisions"))

        val h6 = JatakaGeminiRahuPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 6th House (Scorpio)", h6.title)
        assertTrue(h6.text.contains("has a strong influence over enemies"))

        val h7 = JatakaGeminiRahuPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 7th House (Sagittarius)", h7.title)
        assertTrue(h7.text.contains("may face problems in marriage and daily work"))

        val h8 = JatakaGeminiRahuPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 8th House (Capricorn)", h8.title)
        assertTrue(h8.text.contains("worries a lot and may sometimes face serious or nervous health problems"))

        val h9 = JatakaGeminiRahuPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 9th House (Aquarius)", h9.title)
        assertTrue(h9.text.contains("may have a weak relationship with the father"))

        val h10 = JatakaGeminiRahuPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 10th House (Pisces)", h10.title)
        assertTrue(h10.text.contains("use unusual or difficult methods to progress in work"))

        val h11 = JatakaGeminiRahuPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 11th House (Aries)", h11.title)
        assertTrue(h11.text.contains("can earn a high income and receive good gains"))

        val h12 = JatakaGeminiRahuPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Rahu in the 12th House (Taurus)", h12.title)
        assertTrue(h12.text.contains("may face trouble because of heavy expenses"))
    }

    @Test
    fun geminiAscendantRahuPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaGeminiRahuPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun geminiAscendantKetuPredictions_returnsExactProvidedTextForAll12Houses() {
        val h1 = JatakaGeminiKetuPredictions.getPrediction(1, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 1st House (Gemini)", h1.title)
        assertTrue(h1.text.contains("physical weakness, anxiety"))

        val h2 = JatakaGeminiKetuPredictions.getPrediction(2, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 2nd House (Cancer)", h2.title)
        assertTrue(h2.text.contains("financial loss, frustration in building wealth"))

        val h3 = JatakaGeminiKetuPredictions.getPrediction(3, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 3rd House (Leo)", h3.title)
        assertTrue(h3.text.contains("worries and difficulties concerning siblings"))

        val h4 = JatakaGeminiKetuPredictions.getPrediction(4, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 4th House (Virgo)", h4.title)
        assertTrue(h4.text.contains("obstacles involving land and buildings"))

        val h5 = JatakaGeminiKetuPredictions.getPrediction(5, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 5th House (Libra)", h5.title)
        assertTrue(h5.text.contains("difficulties in education, mental worries"))

        val h6 = JatakaGeminiKetuPredictions.getPrediction(6, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 6th House (Scorpio)", h6.title)
        assertTrue(h6.text.contains("great influence over enemies"))

        val h7 = JatakaGeminiKetuPredictions.getPrediction(7, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 7th House (Sagittarius)", h7.title)
        assertTrue(h7.text.contains("spouse may possess unusual strength"))

        val h8 = JatakaGeminiKetuPredictions.getPrediction(8, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 8th House (Capricorn)", h8.title)
        assertTrue(h8.text.contains("restlessness and worries in daily life"))

        val h9 = JatakaGeminiKetuPredictions.getPrediction(9, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 9th House (Aquarius)", h9.title)
        assertTrue(h9.text.contains("worries concerning destiny"))

        val h10 = JatakaGeminiKetuPredictions.getPrediction(10, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 10th House (Pisces)", h10.title)
        assertTrue(h10.text.contains("loss and worry concerning status or position"))

        val h11 = JatakaGeminiKetuPredictions.getPrediction(11, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 11th House (Aries)", h11.title)
        assertTrue(h11.text.contains("strength in matters of income"))

        val h12 = JatakaGeminiKetuPredictions.getPrediction(12, JatakaLanguage.ENGLISH)
        assertEquals("Ketu in the 12th House (Taurus)", h12.title)
        assertTrue(h12.text.contains("work hard to manage expenses"))
    }

    @Test
    fun geminiAscendantKetuPredictions_multilingualSupport() {
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            (1..12).forEach { house ->
                val pred = JatakaGeminiKetuPredictions.getPrediction(house, lang)
                assertTrue("Title for ${lang.name} house $house must not be blank", pred.title.isNotBlank())
                assertTrue("Text for ${lang.name} house $house must not be blank", pred.text.isNotBlank())
            }
        }
    }

    @Test
    fun genderNeutrality_producesIdenticalAnalysisContent() {
        val engine = JatakaSimpleEngine()
        val maleInput = narasimha1030Input.copy(gender = "Male")
        val femaleInput = narasimha1030Input.copy(gender = "Female")
        val neutralInput = narasimha1030Input.copy(gender = "Neutral")

        val resMale = engine.calculate(maleInput)
        val resFemale = engine.calculate(femaleInput)
        val resNeutral = engine.calculate(neutralInput)

        val maleParas = resMale.lifeAreaPredictions.map { it.fullAnalysisParagraphs }
        val femaleParas = resFemale.lifeAreaPredictions.map { it.fullAnalysisParagraphs }
        val neutralParas = resNeutral.lifeAreaPredictions.map { it.fullAnalysisParagraphs }

        assertEquals(maleParas, femaleParas)
        assertEquals(maleParas, neutralParas)
    }

    @Test
    fun languageParity_producesFullPredictionsInAllSixLanguages() {
        val engine = JatakaSimpleEngine()
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        languages.forEach { lang ->
            val input = narasimha1030Input.copy(language = lang)
            val res = engine.calculate(input)
            assertTrue("Every language must produce life area predictions", res.lifeAreaPredictions.isNotEmpty())
            res.lifeAreaPredictions.forEach { pred ->
                assertTrue("Prediction title must not be blank for ${lang.name}", pred.title.isNotBlank())
                assertTrue("Prediction summary must not be blank for ${lang.name}", pred.summary.isNotBlank())
                assertTrue("Analysis paragraphs must not be empty for ${lang.name}", pred.fullAnalysisParagraphs.isNotEmpty())
            }
        }
    }

    @Test
    fun predictionsAreAffirmativeAndMeaningfulAcrossAllLanguages() {
        val engine = JatakaSimpleEngine()
        val languages = listOf(
            JatakaLanguage.ENGLISH, JatakaLanguage.HINDI, JatakaLanguage.KANNADA,
            JatakaLanguage.TAMIL, JatakaLanguage.MALAYALAM, JatakaLanguage.TELUGU
        )

        val forbiddenDisclaimers = listOf(
            "ಲಭ್ಯ ಸೂಚನೆಗಳು ನಿಮ್ಮ ಸ್ವಭಾವಕ್ಕೆ ಒಂದೇ ಬಲವಾದ ಹಣೆಯಪಟ್ಟಿ ಕಟ್ಟಲು ಸಾಲುವುದಿಲ್ಲ",
            "ನಿರ್ದಿಷ್ಟ ಆಸ್ತಿ ಭವಿಷ್ಯ ಹೇಳಲು ಇಲ್ಲಿ ಸಾಕಷ್ಟು ಆಧಾರವಿಲ್ಲ",
            "do not justify one strong label",
            "not enough support here to make a specific property prediction"
        )

        languages.forEach { lang ->
            val input = narasimha1030Input.copy(language = lang)
            val res = engine.calculate(input)
            res.lifeAreaPredictions.forEach { pred ->
                forbiddenDisclaimers.forEach { forbidden ->
                    assertFalse("Prediction summary/paragraphs for ${lang.name} (${pred.categoryKey}) must not contain dismissive disclaimers ($forbidden)",
                        pred.summary.contains(forbidden) || pred.fullAnalysisParagraphs.any { it.contains(forbidden) })
                }
            }
        }
    }
}
