package dev.papershelf.domain.model

data class AppSettings(
    val booksFolderPath: String,
    val themeMode: ThemeMode,
    val animationsEnabled: Boolean,
    val thumbnailSizeDp: Int,
) {
    companion object {
        val Default = AppSettings(
            booksFolderPath = "/storage/emulated/0/Books",
            themeMode = ThemeMode.System,
            animationsEnabled = true,
            thumbnailSizeDp = 72,
        )
    }
}

enum class ThemeMode {
    Light,
    Dark,
    System,
}
