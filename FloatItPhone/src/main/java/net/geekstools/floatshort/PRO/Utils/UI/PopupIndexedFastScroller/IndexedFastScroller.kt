package net.geekstools.floatshort.PRO.Utils.UI.PopupIndexedFastScroller

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.databinding.FastScrollerIndexViewBinding
import java.util.*
import kotlin.collections.ArrayList


class IndexedFastScroller(private val context: Context,
                          private val functionsClass: FunctionsClass,
                          private val layoutInflater: LayoutInflater,
                          private val rootView: ViewGroup,
                          private val scrollView: ScrollView,
                          private val recyclerView: RecyclerView,
                          private val fastScrollerIndexViewBinding: FastScrollerIndexViewBinding) {

    var popupOffset: Int = functionsClass.DpToInteger(7)

    fun initializeIndexView(paddingTop: Int, paddingBottom: Int,
                            paddingStart: Int, paddingEnd: Int) : IndexedFastScroller {

        fastScrollerIndexViewBinding.indexView.removeAllViews()

        val layoutParams = fastScrollerIndexViewBinding.root.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, rootView.id)

        fastScrollerIndexViewBinding.root.layoutParams = layoutParams

        fastScrollerIndexViewBinding.root
                .setPadding(paddingStart, paddingTop,
                        paddingEnd, paddingBottom)

        return this@IndexedFastScroller
    }

    fun loadApplicationsIndexData(mapIndexFirstItem: LinkedHashMap<String, Int>, mapIndexLastItem: LinkedHashMap<String, Int>,
                                  mapRangeIndex: LinkedHashMap<Int, String>,
                                  indexList: ArrayList<String?>) = CoroutineScope(SupervisorJob() + Dispatchers.Main).async {

        withContext(Dispatchers.Default) {
            val indexCount = indexList.size

            for (indexNumber in 0 until indexCount) {
                val indexText = indexList[indexNumber]!!

                if (mapIndexFirstItem[indexText] == null /*avoid duplication*/) {
                    mapIndexFirstItem[indexText] = indexNumber
                }

                mapIndexLastItem[indexText] = indexNumber
            }
        }

        var sideIndexItem = layoutInflater.inflate(R.layout.side_index_item, null) as TextView
        val indexListFinal: List<String> = ArrayList(mapIndexFirstItem.keys)

        indexListFinal.forEach { indexText ->
            sideIndexItem = layoutInflater.inflate(R.layout.side_index_item, null) as TextView
            sideIndexItem.text = indexText.toUpperCase(Locale.getDefault())
            sideIndexItem.setTextColor(PublicVariable.colorLightDarkOpposite)

            fastScrollerIndexViewBinding.indexView.addView(sideIndexItem)
        }

        val finalTextView = sideIndexItem

        delay(777)

        var upperRange = (fastScrollerIndexViewBinding.indexView.y - finalTextView.height).toInt()

        for (number in 0 until fastScrollerIndexViewBinding.indexView.childCount) {
            val indexText = (fastScrollerIndexViewBinding.indexView.getChildAt(number) as TextView).text.toString()
            val indexRange = (fastScrollerIndexViewBinding.indexView.getChildAt(number).y + fastScrollerIndexViewBinding.indexView.y + finalTextView.height).toInt()

            for (jRange in upperRange..indexRange) {
                mapRangeIndex[jRange] = indexText
            }

            upperRange = indexRange
        }

        setupFastScrollingIndexing(mapIndexFirstItem,
                mapRangeIndex)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupFastScrollingIndexing(mapIndexFirstItem: LinkedHashMap<String, Int>,
                                           mapRangeIndex: LinkedHashMap<Int, String>) = CoroutineScope(Dispatchers.Main).launch {

        val popupIndexBackground = context.getDrawable(R.drawable.ic_launcher_balloon)?.mutate()
        popupIndexBackground?.setTint(PublicVariable.primaryColorOpposite)
        fastScrollerIndexViewBinding.popupIndex.background = popupIndexBackground

        fastScrollerIndexViewBinding.nestedIndexScrollView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
        fastScrollerIndexViewBinding.nestedIndexScrollView.visibility = View.VISIBLE

        val popupIndexOffsetY = (PublicVariable.statusBarHeight + PublicVariable.actionBarHeight
                + popupOffset).toFloat()

        fastScrollerIndexViewBinding.nestedIndexScrollView.setOnTouchListener { view, motionEvent ->

            when(motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                    if (!functionsClass.litePreferencesEnabled()) {
                        val indexText = mapRangeIndex[motionEvent.y.toInt()]

                        if (indexText != null) {
                            fastScrollerIndexViewBinding.popupIndex.y = motionEvent.rawY - popupIndexOffsetY
                            fastScrollerIndexViewBinding.popupIndex.text = indexText
                            fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
                            fastScrollerIndexViewBinding.popupIndex.visibility = View.VISIBLE
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!functionsClass.litePreferencesEnabled()) {
                        val indexText = mapRangeIndex[motionEvent.y.toInt()]

                        if (indexText != null) {
                            if (!fastScrollerIndexViewBinding.popupIndex.isShown) {
                                fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
                                fastScrollerIndexViewBinding.popupIndex.visibility = View.VISIBLE
                            }
                            fastScrollerIndexViewBinding.popupIndex.y = motionEvent.rawY - popupIndexOffsetY
                            fastScrollerIndexViewBinding.popupIndex.text = indexText
                            try {
                                scrollView.smoothScrollTo(
                                        0,
                                        recyclerView.getChildAt(mapIndexFirstItem[mapRangeIndex[motionEvent.y.toInt()]]!!).y.toInt()
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            if (fastScrollerIndexViewBinding.popupIndex.isShown) {
                                fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
                                fastScrollerIndexViewBinding.popupIndex.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (functionsClass.litePreferencesEnabled()) {
                        try {
                            scrollView.smoothScrollTo(
                                    0,
                                    recyclerView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        if (fastScrollerIndexViewBinding.popupIndex.isShown) {
                            try {
                                scrollView.smoothScrollTo(
                                        0,
                                        recyclerView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
                            fastScrollerIndexViewBinding.popupIndex.visibility = View.INVISIBLE
                        }
                    }
                }
            }
            true
        }

        fastScrollerIndexViewBinding.root.bringToFront()
        fastScrollerIndexViewBinding.popupIndex.bringToFront()
    }
}