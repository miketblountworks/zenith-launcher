package com.example.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration

class CustomAppWidgetHost(context: Context, hostId: Int) : AppWidgetHost(context, hostId) {
    var onLongPressListener: ((Int) -> Unit)? = null
    var onDragStartListener: ((Int) -> Unit)? = null
    var onDragListener: ((Int, Float, Float) -> Unit)? = null
    var onDragEndListener: ((Int) -> Unit)? = null
    
    override fun onCreateView(
        context: Context,
        appWidgetId: Int,
        appWidget: AppWidgetProviderInfo?
    ): AppWidgetHostView {
        return CustomAppWidgetHostView(context).apply {
            this.onLongPress = {
                onLongPressListener?.invoke(appWidgetId)
            }
            this.onDragStart = {
                onDragStartListener?.invoke(appWidgetId)
            }
            this.onDrag = { dx, dy ->
                onDragListener?.invoke(appWidgetId, dx, dy)
            }
            this.onDragEnd = {
                onDragEndListener?.invoke(appWidgetId)
            }
        }
    }
    
    class CustomAppWidgetHostView(context: Context) : AppWidgetHostView(context) {
        var onLongPress: (() -> Unit)? = null
        var onDragStart: (() -> Unit)? = null
        var onDrag: ((Float, Float) -> Unit)? = null
        var onDragEnd: (() -> Unit)? = null
        
        private var downX = 0f
        private var downY = 0f
        private var lastX = 0f
        private var lastY = 0f
        private var hasTriggeredLongPress = false
        
        private val longPressRunnable = Runnable {
            hasTriggeredLongPress = true
            onLongPress?.invoke()
            onDragStart?.invoke()
            parent?.requestDisallowInterceptTouchEvent(true)
        }
        
        override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
            when (ev.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    downX = ev.rawX
                    downY = ev.rawY
                    lastX = ev.rawX
                    lastY = ev.rawY
                    hasTriggeredLongPress = false
                    postDelayed(longPressRunnable, ViewConfiguration.getLongPressTimeout().toLong())
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!hasTriggeredLongPress) {
                        val slop = ViewConfiguration.get(context).scaledTouchSlop
                        if (java.lang.Math.abs(ev.rawX - downX) > slop || java.lang.Math.abs(ev.rawY - downY) > slop) {
                            removeCallbacks(longPressRunnable)
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    removeCallbacks(longPressRunnable)
                }
            }
            return hasTriggeredLongPress
        }
        
        override fun onTouchEvent(ev: MotionEvent): Boolean {
            if (hasTriggeredLongPress) {
                when (ev.actionMasked) {
                    MotionEvent.ACTION_MOVE -> {
                        val dx = ev.rawX - lastX
                        val dy = ev.rawY - lastY
                        lastX = ev.rawX
                        lastY = ev.rawY
                        onDrag?.invoke(dx, dy)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        hasTriggeredLongPress = false
                        onDragEnd?.invoke()
                    }
                }
                return true
            }
            return super.onTouchEvent(ev)
        }
    }
}
