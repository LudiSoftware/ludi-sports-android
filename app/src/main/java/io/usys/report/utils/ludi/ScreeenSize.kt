package io.usys.report.utils.ludi

import kotlin.math.abs

enum class DeviceScreenSizes(val type: ScreenType, val diagonalInches: Double, val widthDp: Int, val heightDp: Int) {
    // Standard sizes
    IPHONE_8(ScreenType.STANDARD, 4.7, 414, 736),
    IPHONE_8_PLUS(ScreenType.STANDARD, 5.5, 414, 736),
    IPHONE_X(ScreenType.STANDARD, 5.8, 375, 812),
    IPHONE_11_PRO_MAX(ScreenType.STANDARD, 6.5, 414, 896),
    GALAXY_S10(ScreenType.STANDARD, 6.1, 360, 760),
    GALAXY_NOTE_10(ScreenType.STANDARD, 6.8, 412, 869),

    // Foldable sizes
    GALAXY_FOLD_CLOSED(ScreenType.STANDARD, 4.6, 320, 720),
    GALAXY_FOLD_OPEN(ScreenType.FOLD, 7.3, 768, 1024),

    // Tablets
    IPAD_PRO(ScreenType.TABLET, 12.9, 2048, 2732),
    GALAXY_TAB_S7(ScreenType.TABLET, 11.0, 1752, 2800);

    companion object {
        fun closestToDiagonal(size: Double): DeviceScreenSizes {
            val sizes = values()
            var minDiff = Double.MAX_VALUE
            var closest = sizes[0]

            for (screenSize in sizes) {
                val diff = abs(screenSize.diagonalInches - size)
                if (diff < minDiff) {
                    minDiff = diff
                    closest = screenSize
                }
            }
            return closest
        }

        fun estimateTypeFromSize(widthDp: Int, heightDp: Int): ScreenType {
            return when {
                // Assuming "small" size as < 600dp smallest width (roughly)
                widthDp < 700 && heightDp < 1000 -> ScreenType.STANDARD
                // Assuming "large" size as >= 600dp smallest width (roughly)
                widthDp < 1200 && heightDp < 1500 -> ScreenType.FOLD
                // Treat rest as foldable
                else -> ScreenType.TABLET
            }
        }
    }
}

enum class ScreenType {
    STANDARD, FOLD, TABLET
}


