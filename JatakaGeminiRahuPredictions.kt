package com.apzok.vastu.jatakachakra

/**
 * Prediction dataset for Rahu in houses 1 to 12 for a Gemini Ascendant.
 *
 * The English copy intentionally uses short, familiar Indian English. Hindi is
 * supplied for Hindi readers; the existing English fallback is used for the
 * other supported languages until reviewed translations are available.
 */
object JatakaGeminiRahuPredictions {

    data class PredictionText(val title: String, val text: String)

    fun getPrediction(house: Int, language: JatakaLanguage): PredictionText =
        if (language == JatakaLanguage.HINDI) getHindi(house) else getEnglish(house)

    private fun getEnglish(house: Int): PredictionText = when (house) {
        1 -> PredictionText(
            "Rahu in the 1st House (Gemini)",
            "The native may be tall and self-confident. They keep deep plans in their heart and may take their ideas too far. They can earn fame and gain spiritual knowledge."
        )
        2 -> PredictionText(
            "Rahu in the 2nd House (Cancer)",
            "The native may face loss of savings and remain worried about money. Family life can bring stress. They make secret plans to earn wealth and know how to gain from other people."
        )
        3 -> PredictionText(
            "Rahu in the 3rd House (Leo)",
            "The native may face stress or separation from siblings, but does not easily lose courage. They work hard and may become tired. They can be cautious, self-focused, and brave."
        )
        4 -> PredictionText(
            "Rahu in the 4th House (Virgo)",
            "The native may receive less happiness from the mother. Property, land, buildings, and home life can bring loss or worry. They usually have a serious nature."
        )
        5 -> PredictionText(
            "Rahu in the 5th House (Libra)",
            "The native may face difficulty in education and in making clear decisions. Children can become a cause of worry or some loss."
        )
        6 -> PredictionText(
            "Rahu in the 6th House (Scorpio)",
            "The native has a strong influence over enemies and does not fear illness or problems. The maternal family may face loss. They may ignore Dharma and rules, but they are bold and strongly focused on themselves."
        )
        7 -> PredictionText(
            "Rahu in the 7th House (Sagittarius)",
            "The native may face problems in marriage and daily work. Intimate life may bring dissatisfaction, secrecy, or unsuitable choices. They may also become lazy."
        )
        8 -> PredictionText(
            "Rahu in the 8th House (Capricorn)",
            "The native worries a lot and may sometimes face serious or nervous health problems. They think secretly, feel that daily life is incomplete, and may suffer from stomach problems."
        )
        9 -> PredictionText(
            "Rahu in the 9th House (Aquarius)",
            "The native may have a weak relationship with the father. Interest in Dharma and faith in God may be low. With time, destiny can become more stable."
        )
        10 -> PredictionText(
            "Rahu in the 10th House (Pisces)",
            "The native may worry about the father. They use unusual or difficult methods to progress in work. Government and society may create problems, but careful handling can finally bring success."
        )
        11 -> PredictionText(
            "Rahu in the 11th House (Aries)",
            "The native can earn a high income and receive good gains. However, they may become too self-focused in matters of profit."
        )
        12 -> PredictionText(
            "Rahu in the 12th House (Taurus)",
            "The native may face trouble because of heavy expenses. They can be careless about foreign places or life away from home. They work through secret plans and think about distant matters."
        )
        else -> PredictionText(
            "Rahu in House $house (Gemini Ascendant)",
            "Rahu in House $house for a Gemini Ascendant can bring unusual desires, hidden plans, and important life lessons."
        )
    }

    private fun getHindi(house: Int): PredictionText = when (house) {
        1 -> PredictionText("राहु प्रथम भाव में (मिथुन)", "जातक लंबा और आत्मविश्वासी हो सकता है। वह मन में गहरी योजनाएं रखता है और कभी-कभी अपने विचारों को सीमा से आगे ले जाता है। उसे प्रसिद्धि और आध्यात्मिक ज्ञान मिल सकता है।")
        2 -> PredictionText("राहु द्वितीय भाव में (कर्क)", "जातक को बचत में हानि और धन की चिंता हो सकती है। पारिवारिक जीवन में तनाव रह सकता है। वह धन कमाने के लिए गुप्त योजनाएं बनाता है और दूसरों से लाभ लेना जानता है।")
        3 -> PredictionText("राहु तृतीय भाव में (सिंह)", "भाई-बहनों से तनाव या दूरी हो सकती है, पर जातक साहस नहीं छोड़ता। वह मेहनत करता है और थक सकता है। वह सावधान, स्वार्थी और साहसी हो सकता है।")
        4 -> PredictionText("राहु चतुर्थ भाव में (कन्या)", "माता से मिलने वाले सुख में कमी हो सकती है। भूमि, भवन, संपत्ति और घरेलू जीवन चिंता या हानि दे सकते हैं। जातक का स्वभाव गंभीर रहता है।")
        5 -> PredictionText("राहु पंचम भाव में (तुला)", "शिक्षा और सही निर्णय लेने में कठिनाई हो सकती है। संतान के कारण चिंता या कुछ हानि हो सकती है।")
        6 -> PredictionText("राहु षष्ठ भाव में (वृश्चिक)", "जातक शत्रुओं पर प्रभाव रखता है और रोग या परेशानी से नहीं डरता। ननिहाल पक्ष को हानि हो सकती है। वह धर्म और नियमों की अनदेखी कर सकता है, पर साहसी रहता है।")
        7 -> PredictionText("राहु सप्तम भाव में (धनु)", "विवाह और रोज़गार में कठिनाइयां हो सकती हैं। दाम्पत्य सुख में कमी, गोपनीयता या गलत चुनाव हो सकते हैं। जातक आलसी भी हो सकता है।")
        8 -> PredictionText("राहु अष्टम भाव में (मकर)", "जातक बहुत चिंता करता है और कभी-कभी गंभीर या नसों से जुड़ी समस्या हो सकती है। वह बातें छिपाकर सोचता है और पेट की परेशानी हो सकती है।")
        9 -> PredictionText("राहु नवम भाव में (कुंभ)", "पिता से संबंध कमजोर हो सकते हैं। धर्म और ईश्वर में विश्वास कम हो सकता है। समय के साथ भाग्य में कुछ स्थिरता आती है।")
        10 -> PredictionText("राहु दशम भाव में (मीन)", "पिता की चिंता रह सकती है। जातक काम में आगे बढ़ने के लिए असामान्य या कठिन तरीके अपनाता है। सरकार और समाज से परेशानी के बाद सावधानी से सफलता मिल सकती है।")
        11 -> PredictionText("राहु एकादश भाव में (मेष)", "जातक अच्छी आय और बड़ा लाभ कमा सकता है। लाभ के मामलों में वह अधिक स्वार्थी हो सकता है।")
        12 -> PredictionText("राहु द्वादश भाव में (वृषभ)", "अधिक खर्च के कारण परेशानी हो सकती है। विदेश या घर से दूर के मामलों में लापरवाही रह सकती है। जातक गुप्त योजनाओं से काम करता है और दूर की बातें सोचता है।")
        else -> PredictionText("मिथुन लग्न में राहु $house भाव में", "मिथुन लग्न में $house भाव का राहु असामान्य इच्छाएं, गुप्त योजनाएं और जीवन के महत्वपूर्ण अनुभव दे सकता है।")
    }
}
