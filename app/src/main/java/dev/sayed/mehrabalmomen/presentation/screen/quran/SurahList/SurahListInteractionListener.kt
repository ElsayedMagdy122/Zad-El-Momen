package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahList

interface SurahListInteractionListener {
    fun onSurahClick(surahId: Int, arabicName: String, englishName:String)
    fun onSearchClick()
    fun onBookmarksClick()
}