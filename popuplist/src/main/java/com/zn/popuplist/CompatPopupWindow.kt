package com.zn.popuplist

import android.graphics.Rect
import android.os.Build
import android.view.View
import android.widget.PopupWindow


/**
 * @author zhangnan
 * @date 2018/6/21
 * @description 解决 API24 以上版本 showAsDropDown 不显示在指定控件下方问题
 */
open class CompatPopupWindow : PopupWindow() {

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && anchor != null) {
            val rect = Rect()
            anchor.getGlobalVisibleRect(rect)
            val h = anchor.resources.displayMetrics.heightPixels - rect.bottom
            height = h
        }
        super.showAsDropDown(anchor, xoff, yoff, gravity)
    }
}

fun List<Any>?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}