package com.zn.multistagepopuplist

import com.zn.popuplist.DefaultSelectedAdapter
import com.zn.popuplist.MultiStageSelectPopupWindow

/**
 * @author zhangnan
 * @date 2018/6/29
 */


/**
 * 单级列表数据模型
 */
data class SingleModel(
        val name: String, val id: Int) : DefaultSelectedAdapter.DefaultSelectedModel(name)

/**
 * 多级嵌套列表数据模型
 */
data class MultiModel(
        val oneModel: MutableList<OneModel>) : MultiStageSelectPopupWindow.MultiStageModel(oneModel)

data class OneModel(
        val oneName: String,
        val oneId: Int,
        val twoModel: MutableList<TwoModel>) : MultiStageSelectPopupWindow.TabModel(oneName, twoModel)

data class TwoModel(
        val twoName: String,
        val towId: Int,
        val threeModel: MutableList<ThreeModel>) : MultiStageSelectPopupWindow.LevelOneModel(twoName, threeModel)

data class ThreeModel(
        val threeName: String,
        val threeId: Int) : MultiStageSelectPopupWindow.LevelTwoModel(threeName)