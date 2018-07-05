package com.zn.popuplist

import android.content.Context
import android.graphics.Color
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView


/**
 * @author zhangnan
 * @date 2018/6/21
 */
class MultiStageSelectPopupWindow(private val context: Context, private val data: MultiStageModel) : CompatPopupWindow() {

    var onItemSelectedListener: OnItemSelectedListener? = null
    var onResetListener: OnResetListener? = null
    private var tbTop: TabLayout? = null
    private var rvOne: RecyclerView? = null
    private var rvTwo: RecyclerView? = null
    private var adapterOne: DefaultSelectedAdapter? = null
    private var adapterTwo: DefaultSelectedAdapter? = null
    private val oneData: MutableList<DefaultSelectedAdapter.DefaultSelectedModel> = mutableListOf()
    private val twoData: MutableList<DefaultSelectedAdapter.DefaultSelectedModel> = mutableListOf()
    private val oldSelectedIndex: Array<Int> = arrayOf(0, 0, 0)

    private var hasInitialized = false
    private var tabIndex = 0
    private var beenSelected = false

    inner class LevelOneAdapter(data: List<DefaultSelectedAdapter.DefaultSelectedModel>) : DefaultSelectedAdapter(data) {

        override fun onBindViewHolders(textView: TextView, position: Int) {
            textView.gravity = Gravity.CENTER
        }

        override fun onItemSelected(holder: RecyclerView.ViewHolder, model: DefaultSelectedModel, position: Int) {
            super.onItemSelected(holder, model, position)
            adapterTwo?.clearOldIndex()
            fillTwoData(tabIndex, position)
            rvTwo?.scrollToPosition(0)
        }
    }

    inner class LevelTwoAdapter(data: List<DefaultSelectedAdapter.DefaultSelectedModel>) : DefaultSelectedAdapter(data) {

        override fun onBindViewHolders(textView: TextView, position: Int) {
            textView.setPadding(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50F, context.resources.displayMetrics).toInt(),
                    0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50F, context.resources.displayMetrics).toInt(), 0)
        }

        override fun applyUnSelectedItem(position: Int, itemView: View) {
            super.applyUnSelectedItem(position, itemView)
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        override fun onItemSelected(holder: RecyclerView.ViewHolder, model: DefaultSelectedModel, position: Int) {
            super.onItemSelected(holder, model, position)
            val tabModel = data.tabModel[tabIndex]
            val oneIndex = adapterOne?.getSelectedIndex() ?: 0
            val oneModel = tabModel.levelOneModel[oneIndex]
            val twoIndex = adapterTwo?.getSelectedIndex() ?: 0
            val twoModel = oneModel.levelTwoModel[twoIndex]
            onItemSelectedListener?.onItemSelected(tabModel, oneModel, twoModel)
            oldSelectedIndex[TAB] = tabIndex
            oldSelectedIndex[ONE] = oneIndex
            oldSelectedIndex[TWO] = twoIndex
            beenSelected = true
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

    private fun showPopupWindow() {
        if (!hasInitialized) {
            initContentView()
        } else {
            initTab()
            if (data.tabModel.isNullOrEmpty()) return
            fillOneData(oldSelectedIndex[ONE])
            if (data.tabModel[0].levelOneModel.isNullOrEmpty()) return
            fillTwoData(oldSelectedIndex[ONE], oldSelectedIndex[TWO])
            tbTop?.getTabAt(oldSelectedIndex[TAB])?.select()
            adapterOne?.showItemSelected(oldSelectedIndex[ONE])
            if (beenSelected) {
                adapterTwo?.showItemSelected(oldSelectedIndex[TWO])
            }
        }
    }

    private fun initContentView() {
        contentView = LayoutInflater.from(context).inflate(R.layout.popup_multi_stage_select_layout, null, false)

        initTab()

        if (data.tabModel.isNullOrEmpty()) return

        initLevelOne()

        if (data.tabModel[0].levelOneModel.isNullOrEmpty()) return

        initLevelTwo()

        initResetEvent()

        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        hasInitialized = true
    }

    private fun initTab() {
        tbTop = contentView.findViewById(R.id.tb_top)
        tbTop?.removeAllTabs()
        data.tabModel.forEach {
            val tab = tbTop?.newTab()
            tab?.text = it.tabName
            tbTop?.addTab(tab!!)
        }
        tbTop?.getTabAt(0)?.select()
        tbTop?.addOnTabSelectedListener(object : DefaultTabSelectedListener() {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabIndex = tab?.position ?: 0
                clearAllIndex()
                fillOneData(tabIndex)
                fillTwoData(0, 0)
                notifyAllDataChange()
                if (oldSelectedIndex[TAB] == tabIndex) {
                    rvOne?.scrollToPosition(oldSelectedIndex[ONE])
                    rvTwo?.scrollToPosition(oldSelectedIndex[TWO])
                    adapterOne?.showItemSelected(oldSelectedIndex[ONE])
                    if (beenSelected) {
                        adapterTwo?.showItemSelected(oldSelectedIndex[TWO])
                    }
                } else {
                    rvOne?.scrollToPosition(0)
                    rvTwo?.scrollToPosition(0)
                    adapterOne?.showItemSelected(0)
                }
            }
        })
    }

    private fun initLevelOne() {
        rvOne = contentView.findViewById(R.id.rv_one)
        adapterOne = LevelOneAdapter(oneData)
        adapterOne?.isDefaultSelectFirst = true
        rvOne?.adapter = adapterOne
        rvOne?.layoutManager = LinearLayoutManager(context)
        rvOne?.setHasFixedSize(true)
        fillOneData(0)
    }

    private fun initLevelTwo() {
        rvTwo = contentView.findViewById(R.id.rv_two)
        adapterTwo = LevelTwoAdapter(twoData)
        rvTwo?.adapter = adapterTwo
        rvTwo?.layoutManager = LinearLayoutManager(context)
        rvTwo?.setHasFixedSize(true)
        fillTwoData(0, 0)
    }

    private fun initResetEvent() {
        contentView.findViewById<Button>(R.id.btn_reset).setOnClickListener {
            if (tabIndex != 0) {
                tbTop?.getTabAt(0)?.select()
            } else {
                clearAllIndex()
                tabIndex = 0
                fillOneData(tabIndex)
                fillTwoData(0, 0)
                notifyAllDataChange()
                rvOne?.scrollToPosition(0)
                rvTwo?.scrollToPosition(0)
                adapterOne?.showItemSelected(0)
            }
            oldSelectedIndex[TAB] = 0
            oldSelectedIndex[ONE] = 0
            oldSelectedIndex[TWO] = 0
            beenSelected = false
            if (data.tabModel.isNullOrEmpty()) {
                onResetListener?.onReset(null, null, null)
                return@setOnClickListener
            }
            val defaultTab = data.tabModel[0]
            if (defaultTab.levelOneModel.isNullOrEmpty()) {
                onResetListener?.onReset(defaultTab, null, null)
                return@setOnClickListener
            }
            val defaultOne = defaultTab.levelOneModel[0]
            if (defaultOne.levelTwoModel.isNullOrEmpty()) {
                onResetListener?.onReset(defaultTab, defaultOne, null)
                return@setOnClickListener
            }
            val defaultTwo = defaultOne.levelTwoModel[0]
            onResetListener?.onReset(defaultTab, defaultOne, defaultTwo)
        }
    }

    fun notifyAllDataChange() {
        adapterOne?.notifyDataSetChanged()
        adapterTwo?.notifyDataSetChanged()
    }

    private fun fillOneData(tabIndex: Int) {
        oneData.clear()
        oneData.addAll(data.tabModel[tabIndex].levelOneModel)
        adapterOne?.notifyDataSetChanged()
    }

    private fun fillTwoData(tabIndex: Int, oneIndex: Int) {
        twoData.clear()
        twoData.addAll(data.tabModel[tabIndex].levelOneModel[oneIndex].levelTwoModel)
        adapterTwo?.notifyDataSetChanged()
    }

    private fun clearAllIndex() {
        adapterOne?.clearOldIndex()
        adapterTwo?.clearOldIndex()
    }

    abstract class TabModel(val tabName: String, val levelOneModel: List<LevelOneModel> = emptyList())

    abstract class LevelOneModel(val data: String, val levelTwoModel: List<LevelTwoModel> = emptyList()) : DefaultSelectedAdapter.DefaultSelectedModel(data)

    abstract class LevelTwoModel(val data: String) : DefaultSelectedAdapter.DefaultSelectedModel(data)

    abstract class MultiStageModel(val tabModel: List<TabModel> = emptyList())

    open class DefaultTabSelectedListener : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {}

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabSelected(tab: TabLayout.Tab?) {}
    }

    interface OnItemSelectedListener {

        fun onItemSelected(tabModel: TabModel, oneModel: LevelOneModel, twoModel: LevelTwoModel)
    }

    interface OnResetListener {

        fun onReset(defaultTab: TabModel?, defaultOne: LevelOneModel?, defaultTwo: LevelTwoModel?)
    }

    companion object {
        private const val TAB = 0
        private const val ONE = 1
        private const val TWO = 2
    }
}