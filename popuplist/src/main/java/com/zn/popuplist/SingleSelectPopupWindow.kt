package com.zn.popuplist

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout


/**
 * @author zhangnan
 * @date 2018/6/21
 */
class SingleSelectPopupWindow(private val context: Context, private val data: List<DefaultSelectedAdapter.DefaultSelectedModel>) : CompatPopupWindow() {

    var onItemSelectedListener: OnItemSelectedListener? = null
    private val recyclerView: RecyclerView by lazy { RecyclerView(context) }
    private var adapter: DefaultSelectedAdapter? = null
    private var hasInitialized = false
    private var selectPosition = 0

    inner class SingleSelectAdapter(data: List<DefaultSelectedAdapter.DefaultSelectedModel>) : DefaultSelectedAdapter(data) {

        override fun onItemSelected(holder: RecyclerView.ViewHolder, model: DefaultSelectedModel, position: Int) {
            super.onItemSelected(holder, model, position)
            selectPosition = position
            onItemSelectedListener?.onItemSelected(model)
        }
    }

    override fun showAsDropDown(anchor: View, xoff: Int, yoff: Int, gravity: Int) {
        showPopupWindow()
        super.showAsDropDown(anchor, xoff, yoff, gravity)
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        showPopupWindow()
        super.showAtLocation(parent, gravity, x, y)
    }

    /**
     * 仅供初次默认选择，不会调用点击事件
     */
    fun setDefaultSelectPosition(position: Int) {
        selectPosition = position
        adapter?.showItemSelected(position)
    }

    private fun showPopupWindow() {
        if (!hasInitialized) {
            initContentView()
        } else {
            notifyDataChange()
        }
        setDefaultSelectPosition(selectPosition)
    }

    private fun initContentView() {
        if (data.isNullOrEmpty()) return
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        val dividerColor = Color.parseColor("#F5F5F5")
        val dividerWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1F, context.resources.displayMetrics).toInt()
        val viewGroup = LinearLayout(context)
        val topLine = View(context)
        divider.setDrawable(ContextCompat.getDrawable(context, R.drawable.shape_fine_divider)!!)
        recyclerView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        adapter = SingleSelectAdapter((data))
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setBackgroundColor(Color.WHITE)
        recyclerView.addItemDecoration(divider)
        recyclerView.setHasFixedSize(true)
        viewGroup.orientation = LinearLayout.VERTICAL
        topLine.setBackgroundColor(dividerColor)
        topLine.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerWidth)
        viewGroup.addView(topLine)
        viewGroup.addView(recyclerView)
        contentView = viewGroup
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        hasInitialized = true
    }

    fun notifyDataChange() {
        recyclerView.adapter.notifyDataSetChanged()
    }

    interface OnItemSelectedListener {

        fun onItemSelected(model: DefaultSelectedAdapter.DefaultSelectedModel)
    }
}