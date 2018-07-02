package com.zn.multistagepopuplist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zn.popuplist.DefaultSelectedAdapter
import com.zn.popuplist.MultiStageSelectPopupWindow
import com.zn.popuplist.SingleSelectPopupWindow
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val singleModel: MutableList<SingleModel> = mutableListOf()
    private val multiModel: MultiModel by lazy { MultiModel(mutableListOf()) }
    private val singleSelectPopupWindow: SingleSelectPopupWindow by lazy { SingleSelectPopupWindow(this, singleModel) }
    private val multiStageSelectPopupWindow: MultiStageSelectPopupWindow  by lazy { MultiStageSelectPopupWindow(this, multiModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_show_single.setOnClickListener { showSingleWindow() }
        btn_show_multi.setOnClickListener { showMultiStageWindow() }
        singleSelectPopupWindow.onItemSelectedListener = object : SingleSelectPopupWindow.OnItemSelectedListener {
            override fun onItemSelected(model: DefaultSelectedAdapter.DefaultSelectedModel) {
                // todo selected
            }
        }
        multiStageSelectPopupWindow.onItemSelectedListener = object : MultiStageSelectPopupWindow.OnItemSelectedListener {
            override fun onItemSelected(tabModel: MultiStageSelectPopupWindow.TabModel, oneModel: MultiStageSelectPopupWindow.LevelOneModel, twoModel: MultiStageSelectPopupWindow.LevelTwoModel) {
                // todo selected
            }
        }
        multiStageSelectPopupWindow.onResetListener = object : MultiStageSelectPopupWindow.OnResetListener {
            override fun onReset(defaultTab: MultiStageSelectPopupWindow.TabModel?, defaultOne: MultiStageSelectPopupWindow.LevelOneModel?, defaultTwo: MultiStageSelectPopupWindow.LevelTwoModel?) {
                // todo reset
            }
        }
    }

    private fun showSingleWindow() {
        if (singleSelectPopupWindow.isShowing) {
            singleSelectPopupWindow.dismiss()
            return
        }
        val singleList = mutableListOf<SingleModel>()
        (0 until 30).forEach {
            singleList.add(SingleModel("$it", it))
        }
        singleModel.clear()
        singleModel.addAll(singleList)
        singleSelectPopupWindow.showAsDropDown(findViewById(R.id.btn_show_single))
    }

    private fun showMultiStageWindow() {
        if (multiStageSelectPopupWindow.isShowing) {
            multiStageSelectPopupWindow.dismiss()
            return
        }
        val oneList = mutableListOf<OneModel>()
        (0 until 10).forEach {
            val twoList = mutableListOf<TwoModel>()
            (0 until 20).forEach {
                val threeList = mutableListOf<ThreeModel>()
                (0 until 30).forEach {
                    threeList.add(ThreeModel("$it", it))
                }
                twoList.add(TwoModel("$it", it, threeList))
            }
            oneList.add(OneModel("$it", it, twoList))
        }
        multiModel.oneModel.clear()
        multiModel.oneModel.addAll(oneList)
        multiStageSelectPopupWindow.showAsDropDown(findViewById(R.id.btn_show_multi))
    }
}
