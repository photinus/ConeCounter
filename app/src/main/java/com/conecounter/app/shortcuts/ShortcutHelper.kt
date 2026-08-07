package com.conecounter.app.shortcuts

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.Toast
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.conecounter.app.MainActivity
import com.conecounter.app.data.Kid

/**
 * Manages Android home-screen shortcuts so a parent can tap straight to
 * "+1 scoop" for a specific kid without opening the app first.
 */
object ShortcutHelper {

    const val ACTION_QUICK_LOG = "com.conecounter.app.ACTION_QUICK_LOG"
    const val ACTION_QUICK_LOG_PICKER = "com.conecounter.app.ACTION_QUICK_LOG_PICKER"
    const val EXTRA_KID_ID = "extra_kid_id"

    private fun quickLogIntent(context: Context, kid: Kid): Intent =
        Intent(context, MainActivity::class.java).apply {
            action = ACTION_QUICK_LOG
            putExtra(EXTRA_KID_ID, kid.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

    private fun shortcutId(kid: Kid) = "kid_${kid.id}"

    private fun buildShortcut(context: Context, kid: Kid): ShortcutInfoCompat =
        ShortcutInfoCompat.Builder(context, shortcutId(kid))
            .setShortLabel(kid.name)
            .setLongLabel("Log a scoop for ${kid.name}")
            .setIcon(IconCompat.createWithBitmap(avatarBitmap(kid)))
            .setIntent(quickLogIntent(context, kid))
            .build()

    /** Keeps the app's "long-press the icon" shortcut list in sync with the current kids. */
    fun syncDynamicShortcuts(context: Context, kids: List<Kid>) {
        val maxCount = ShortcutManagerCompat.getMaxShortcutCountPerActivity(context)
            .takeIf { it > 0 } ?: 4
        val shortcuts = kids.take(maxCount).map { buildShortcut(context, it) }
        ShortcutManagerCompat.setDynamicShortcuts(context, shortcuts)
    }

    /** Asks the launcher to pin a one-tap "+1 scoop" icon for this kid on the home screen. */
    fun requestPinShortcut(context: Context, kid: Kid) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            ShortcutManagerCompat.requestPinShortcut(context, buildShortcut(context, kid), null)
        } else {
            Toast.makeText(
                context,
                "This launcher doesn't support home screen shortcuts",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun avatarBitmap(kid: Kid): Bitmap {
        val size = 192
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = try {
                Color.parseColor(kid.colorHex)
            } catch (e: IllegalArgumentException) {
                Color.parseColor("#4FC3F7")
            }
        }
        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, bgPaint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = size * 0.55f
            textAlign = Paint.Align.CENTER
        }
        val metrics = textPaint.fontMetrics
        val textY = radius - (metrics.ascent + metrics.descent) / 2f
        canvas.drawText(kid.emoji, radius, textY, textPaint)
        return bitmap
    }
}
