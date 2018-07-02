package com.zn.popuplist

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.annotation.CallSuper
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.lang.ref.WeakReference

/**
 * @author zhangnan
 * @date 2018/6/22
 */

abstract class DefaultSelectedAdapter(private val data: List<DefaultSelectedModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var recyclerView: WeakReference<RecyclerView>? = null

    private var oldSelectIndex = -1

    var isDefaultSelectFirst: Boolean = false

    @CallSuper
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return DefaultSelectedHolder(view)
    }

    @CallSuper
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val textView = holder.itemView.findViewById<TextView>(android.R.id.text1)
        textView.text = data[position].text
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
        textView.setOnClickListener {
            if (oldSelectIndex != position) {
                val oldIndex = oldSelectIndex
                oldSelectIndex = position
                notifyItemChanged(oldIndex)
                notifyItemChanged(position)
            }
            onItemSelected(holder, data[position], position)
        }
        if (isDefaultSelectFirst && position == 0) {
            oldSelectIndex = position
            updateSelectItem(position, textView)
            onItemSelected(holder, data[position], position)
            isDefaultSelectFirst = false
        } else {
            updateSelectItem(position, textView)
        }
        onBindViewHolders(textView, position)
    }

    override fun getItemCount(): Int = data.size

    private fun updateSelectItem(position: Int, textView: TextView) {
        if (oldSelectIndex == position) {
            textView.setTextColor(Color.parseColor("#FA482D"))
            textView.setBackgroundColor(Color.parseColor("#F5F5F5"))
            applySelectedItem(position, textView)
        } else {
            textView.setTextColor(Color.parseColor("#999999"))
            textView.setBackgroundColor(Color.WHITE)
            applyUnSelectedItem(position, textView)
        }
    }

    fun getSelectedIndex() = oldSelectIndex

    fun clearOldIndex() {
        val oldIndex = oldSelectIndex
        oldSelectIndex = -1
        notifyItemChanged(oldIndex)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = WeakReference<RecyclerView>(recyclerView)
    }

    fun showItemSelected(position: Int) {
        val oldIndex = oldSelectIndex
        oldSelectIndex = position
        notifyItemChanged(oldIndex)
        notifyItemChanged(position)
    }

    protected open fun onBindViewHolders(textView: TextView, position: Int) {}

    protected open fun onItemSelected(holder: RecyclerView.ViewHolder, model: DefaultSelectedModel, position: Int) {}

    protected open fun applySelectedItem(position: Int, itemView: View) {}

    protected open fun applyUnSelectedItem(position: Int, itemView: View) {}

    inner class DefaultSelectedHolder(view: View) : RecyclerView.ViewHolder(view)

    abstract class DefaultSelectedModel(val text: String)
}