package com.apzok.vastu.jatakachakra

/**
 * Prediction dataset for Ketu in houses 1 to 12 for a Gemini Ascendant.
 *
 * The English copy is adapted into clear, familiar Indian English. Hindi is
 * supplied for Hindi readers; the English copy is used as a fallback for the
 * other supported languages until reviewed translations are available.
 */
object JatakaGeminiKetuPredictions {

    data class PredictionText(val title: String, val text: String)

    fun getPrediction(house: Int, language: JatakaLanguage): PredictionText =
        if (language == JatakaLanguage.HINDI) getHindi(house) else getEnglish(house)

    private fun getEnglish(house: Int): PredictionText = when (house) {
        1 -> PredictionText(
            "Ketu in the 1st House (Gemini)",
            "The native may experience physical weakness, anxiety, and a lack of confidence in their appearance. They can sometimes face a serious health danger and may feel weakness around the heart. However, they possess great patience and can be secretive and obstinate."
        )
        2 -> PredictionText(
            "Ketu in the 2nd House (Cancer)",
            "The native may face financial loss, frustration in building wealth, and distress within the family. They work hard to improve their finances and ultimately achieve success."
        )
        3 -> PredictionText(
            "Ketu in the 3rd House (Leo)",
            "The native may experience worries and difficulties concerning siblings, along with reduced brotherly support. They undertake serious labour and toil hard to make progress."
        )
        4 -> PredictionText(
            "Ketu in the 4th House (Virgo)",
            "The native may experience loss or deficiency concerning the mother and obstacles involving land and buildings. After many disturbances and difficulties, they establish a stable position in domestic happiness."
        )
        5 -> PredictionText(
            "Ketu in the 5th House (Libra)",
            "The native may face difficulties in education, mental worries, and some loss or distress concerning children. They may struggle to make others understand their ideas, speak somewhat bitterly, and possess a self-focused intellect that lacks gentleness."
        )
        6 -> PredictionText(
            "Ketu in the 6th House (Scorpio)",
            "The native maintains great influence over enemies but may experience loss concerning the maternal grandfather. They use inner firmness and forceful courage to overcome difficulties and worries, and can be self-focused and fearless."
        )
        7 -> PredictionText(
            "Ketu in the 7th House (Sagittarius)",
            "The spouse may possess unusual strength, and the native may enjoy abundant intimate pleasures. They accomplish important work in their daily occupation, though peace may be lacking in the management of domestic affairs."
        )
        8 -> PredictionText(
            "Ketu in the 8th House (Capricorn)",
            "The native may experience restlessness and worries in daily life. They labour hard and face difficulties while supporting their professional life, and may also suffer from stomach complaints."
        )
        9 -> PredictionText(
            "Ketu in the 9th House (Aquarius)",
            "The native may face worries concerning destiny and must work very hard to progress. They can experience some loss of Dharma, difficulties in earning fame, and periods of limited success or self-focused behaviour."
        )
        10 -> PredictionText(
            "Ketu in the 10th House (Pisces)",
            "The native may face loss and worry concerning status or position. They work hard to earn honour through government and society and ultimately attain a position of strength."
        )
        11 -> PredictionText(
            "Ketu in the 11th House (Aries)",
            "The native has strength in matters of income and makes progress in gains. They may receive gains through unconventional means and achieve success with hidden inner strength."
        )
        12 -> PredictionText(
            "Ketu in the 12th House (Taurus)",
            "The native may spend heavily and work hard to manage expenses."
        )
        else -> PredictionText(
            "Ketu in House $house (Gemini Ascendant)",
            "Ketu in House $house for a Gemini Ascendant can bring detachment, hidden strength, hard work, and important life lessons."
        )
    }

    private fun getHindi(house: Int): PredictionText = when (house) {
        1 -> PredictionText("केतु प्रथम भाव में (मिथुन)", "जातक को शारीरिक कमजोरी, चिंता और रूप-सौंदर्य में कमी का अनुभव हो सकता है। कभी-कभी स्वास्थ्य का गंभीर संकट या हृदय के आसपास कमजोरी महसूस हो सकती है। फिर भी उसमें बहुत धैर्य होता है और वह गुप्त स्वभाव का तथा हठी हो सकता है।")
        2 -> PredictionText("केतु द्वितीय भाव में (कर्क)", "जातक को धन संचय में हानि और निराशा तथा परिवार में परेशानी हो सकती है। वह आर्थिक स्थिति सुधारने के लिए कठिन परिश्रम करता है और अंततः सफलता पाता है।")
        3 -> PredictionText("केतु तृतीय भाव में (सिंह)", "जातक को भाई-बहनों से जुड़ी चिंता और कठिनाई हो सकती है तथा उनका सहयोग कम मिल सकता है। वह गंभीर श्रम करता है और उन्नति के लिए बहुत मेहनत करता है।")
        4 -> PredictionText("केतु चतुर्थ भाव में (कन्या)", "जातक को माता से संबंधित सुख में कमी और भूमि-भवन के विषय में बाधाएं हो सकती हैं। अनेक परेशानियों और कठिनाइयों के बाद वह घरेलू सुख में स्थिरता प्राप्त करता है।")
        5 -> PredictionText("केतु पंचम भाव में (तुला)", "जातक को शिक्षा में कठिनाई, मानसिक चिंता और संतान से संबंधित कुछ हानि या परेशानी हो सकती है। वह अपने विचार दूसरों को समझाने में कठिनाई महसूस कर सकता है, कुछ कटु बोल सकता है और उसकी बुद्धि में कोमलता की कमी हो सकती है।")
        6 -> PredictionText("केतु षष्ठ भाव में (वृश्चिक)", "जातक शत्रुओं पर बड़ा प्रभाव रखता है, पर नाना से संबंधित कुछ हानि हो सकती है। वह आंतरिक दृढ़ता और प्रबल साहस से कठिनाइयों तथा चिंताओं पर विजय पाता है और निडर हो सकता है।")
        7 -> PredictionText("केतु सप्तम भाव में (धनु)", "जीवनसाथी में विशेष शक्ति हो सकती है और जातक को पर्याप्त दाम्पत्य सुख मिल सकता है। वह रोज़गार में बड़े कार्य करता है, हालांकि घरेलू मामलों के प्रबंधन में शांति की कुछ कमी रह सकती है।")
        8 -> PredictionText("केतु अष्टम भाव में (मकर)", "जातक को दैनिक जीवन में बेचैनी और चिंता हो सकती है। वह पेशेवर जीवन को संभालने के लिए कठिन परिश्रम करता और बाधाओं का सामना करता है। पेट से जुड़ी शिकायत भी हो सकती है।")
        9 -> PredictionText("केतु नवम भाव में (कुंभ)", "जातक को भाग्य के विषय में चिंता हो सकती है और प्रगति के लिए बहुत मेहनत करनी पड़ती है। धर्म में कुछ कमी, यश पाने में कठिनाई तथा सीमित सफलता के समय आ सकते हैं।")
        10 -> PredictionText("केतु दशम भाव में (मीन)", "जातक को पद और प्रतिष्ठा के मार्ग में हानि या चिंता हो सकती है। वह सरकार और समाज के कार्यों में सम्मान पाने के लिए कठिन परिश्रम करता है और अंत में मजबूत स्थिति प्राप्त करता है।")
        11 -> PredictionText("केतु एकादश भाव में (मेष)", "जातक की आय और लाभ के क्षेत्र में शक्ति तथा प्रगति होती है। उसे असामान्य साधनों से लाभ मिल सकता है और वह छिपी हुई आंतरिक शक्ति से सफलता पाता है।")
        12 -> PredictionText("केतु द्वादश भाव में (वृषभ)", "जातक अधिक खर्च कर सकता है और व्यय को संभालने के लिए कठिन परिश्रम करता है।")
        else -> PredictionText("मिथुन लग्न में केतु $house भाव में", "मिथुन लग्न में $house भाव का केतु वैराग्य, छिपी शक्ति, कठिन परिश्रम और जीवन के महत्वपूर्ण अनुभव दे सकता है।")
    }
}
