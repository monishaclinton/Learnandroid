
package com.example.myapplication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BottomNav {

    // ============================================================
    // NAVIGATION LISTENER
    // ============================================================

    interface NavigationListener {

        fun onHomeClick()

        fun onProfileClick()

        fun onFavouritesClick()

        fun onRecentlyViewed()
    }

    // ============================================================
    // CREATE BOTTOM NAVIGATION
    // ============================================================

    fun create(
        activity: AppCompatActivity,
        listener: NavigationListener
    ): View {

        // --------------------------------------------------------
        // MAIN NAVIGATION CONTAINER
        // --------------------------------------------------------

        val navigation = LinearLayout(activity)

        navigation.orientation = LinearLayout.HORIZONTAL

        navigation.gravity = Gravity.CENTER

        // --------------------------------------------------------
        // NAVIGATION HEIGHT
        // --------------------------------------------------------

        val navigationParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dpToPx(activity, 70)
        )

        navigation.layoutParams = navigationParams

        // --------------------------------------------------------
        // NAVIGATION PADDING
        // --------------------------------------------------------

        navigation.setPadding(
            dpToPx(activity, 8),
            dpToPx(activity, 8),
            dpToPx(activity, 8),
            dpToPx(activity, 8)
        )

        // --------------------------------------------------------
        // WHITE ROUNDED BACKGROUND
        // --------------------------------------------------------

        val background = GradientDrawable()

        background.setColor(Color.WHITE)

        background.cornerRadius =
            dpToPx(activity, 28).toFloat()

        navigation.background = background

        // ========================================================
        // HOME
        // ========================================================

        val home = createNavItem(
            activity = activity,
            iconRes = R.drawable.house,
            title = "Home"
        )

        // ========================================================
        // PROFILE
        // ========================================================

        val profile = createNavItem(
            activity = activity,
            iconRes = R.drawable.profile,
            title = "Profile"
        )

        // ========================================================
        // FAVOURITES
        // ========================================================

        val favourites = createNavItem(
            activity = activity,
            iconRes = R.drawable.heart_liked,
            title = "Favourites"
        )

        // ========================================================
        // RECENTLY VIEWED
        // ========================================================

        val recentlyViewed = createNavItem(
            activity = activity,
            iconRes = R.drawable.wall_clock,
            title = "Recently Viewed"
        )

        // ========================================================
        // ADD ITEMS
        // ========================================================

        navigation.addView(home)

        navigation.addView(profile)

        navigation.addView(favourites)

        navigation.addView(recentlyViewed)

        // ========================================================
        // HOME CLICK
        // ========================================================

        home.setOnClickListener {

            animateItem(home)

            listener.onHomeClick()
        }

        // ========================================================
        // PROFILE CLICK
        // ========================================================

        profile.setOnClickListener {

            animateItem(profile)

            listener.onProfileClick()
        }

        // ========================================================
        // FAVOURITES CLICK
        // ========================================================

        favourites.setOnClickListener {

            animateItem(favourites)

            listener.onFavouritesClick()
        }

        // ========================================================
        // RECENTLY VIEWED CLICK
        // ========================================================

        recentlyViewed.setOnClickListener {

            animateItem(recentlyViewed)

            listener.onRecentlyViewed()
        }

        return navigation
    }

    // ============================================================
    // CREATE INDIVIDUAL NAVIGATION ITEM
    // ============================================================

    private fun createNavItem(
        activity: AppCompatActivity,
        iconRes: Int,
        title: String
    ): LinearLayout {

        val item = LinearLayout(activity)

        // --------------------------------------------------------
        // VERTICAL LAYOUT
        // --------------------------------------------------------

        item.orientation = LinearLayout.VERTICAL

        // --------------------------------------------------------
        // CENTER ICON + TEXT
        // --------------------------------------------------------

        item.gravity = Gravity.CENTER

        // --------------------------------------------------------
        // CLICKABLE
        // --------------------------------------------------------

        item.isClickable = true

        item.isFocusable = true

        // --------------------------------------------------------
        // ITEM PADDING
        // --------------------------------------------------------

        item.setPadding(
            dpToPx(activity, 5),
            dpToPx(activity, 5),
            dpToPx(activity, 5),
            dpToPx(activity, 5)
        )

        // ========================================================
        // ICON
        // ========================================================

        val icon = ImageView(activity)

        icon.setImageResource(iconRes)

        icon.scaleType =
            ImageView.ScaleType.CENTER_INSIDE

        // --------------------------------------------------------
        // ICON SIZE = 32dp
        // --------------------------------------------------------

        val iconParams = LinearLayout.LayoutParams(
            dpToPx(activity, 20),
            dpToPx(activity, 20)
        )

        icon.layoutParams = iconParams

        // ========================================================
        // TEXT
        // ========================================================

        val text = TextView(activity)

        text.text = title

        text.textSize = 12f

        text.setTextColor(Color.DKGRAY)

        text.gravity = Gravity.CENTER

        text.maxLines = 1

        // --------------------------------------------------------
        // TEXT MARGIN
        // --------------------------------------------------------

        val textParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        textParams.topMargin =
            dpToPx(activity, 4)

        text.layoutParams = textParams

        // ========================================================
        // ADD ICON
        // ========================================================

        item.addView(icon)

        // ========================================================
        // ADD TEXT
        // ========================================================

        item.addView(text)

        // ========================================================
        // EQUAL WIDTH
        // ========================================================

        val itemParams = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        itemParams.weight = 1f

        item.layoutParams = itemParams

        return item
    }

    // ============================================================
    // ITEM CLICK ANIMATION
    // ============================================================

    private fun animateItem(item: View) {

        // --------------------------------------------------------
        // SCALE X
        // --------------------------------------------------------

        val scaleX = ObjectAnimator.ofFloat(
            item,
            View.SCALE_X,
            1f,
            1.12f,
            1f
        )

        // --------------------------------------------------------
        // SCALE Y
        // --------------------------------------------------------

        val scaleY = ObjectAnimator.ofFloat(
            item,
            View.SCALE_Y,
            1f,
            1.12f,
            1f
        )

        // --------------------------------------------------------
        // MOVE UP
        // --------------------------------------------------------

        val translationY = ObjectAnimator.ofFloat(
            item,
            View.TRANSLATION_Y,
            0f,
            -8f,
            0f
        )

        // --------------------------------------------------------
        // COMBINE ANIMATIONS
        // --------------------------------------------------------

        val animatorSet = AnimatorSet()

        animatorSet.playTogether(
            scaleX,
            scaleY,
            translationY
        )

        // --------------------------------------------------------
        // ANIMATION DURATION
        // --------------------------------------------------------

        animatorSet.duration = 450

        // --------------------------------------------------------
        // SMOOTH OVERSHOOT
        // --------------------------------------------------------

        animatorSet.interpolator =
            OvershootInterpolator(2f)

        animatorSet.start()
    }

    // ============================================================
    // DP TO PX
    // ============================================================

    private fun dpToPx(
        activity: AppCompatActivity,
        dp: Int
    ): Int {

        return (
                dp *
                        activity.resources.displayMetrics.density
                ).toInt()
    }
}

