package kve.ru.firstproject.utils

object FeatureToggles {
    const val CACHE_CLEAR_ENABLED = "CacheClearEnabled"
    const val APP_TITLE = "ApplicationTitle"

    val defaults = mapOf(
        Pair(CACHE_CLEAR_ENABLED, false),
        Pair(APP_TITLE, "BEST FILMS")
    )

}