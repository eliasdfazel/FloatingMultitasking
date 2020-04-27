/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/27/20 4:41 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Utils.UI.PopupIndexedFastScroller

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
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
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassDebug
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.databinding.FastScrollerIndexViewBinding
import java.util.*

/**
 * You Must Enable ViewBinding.
 * Just Add The Below Configuration To Your App Level Gradle In Android Section
 * viewBinding { enabled = true }
 *
 *
 * @param rootView Instance Of Root (Base) View In Your Layout
 * @param nestedScrollView Follow This Hierarchy: ScrollView -> RelativeLayout -> RecyclerView
 * @param recyclerView Instance Of A RecyclerView That You Want To Populate With Items
 *
 *
 * Add Index Layout As <include /> In Your Layout. Be Careful With Layers. That's Why I Let You To Put It Manually In Layout.
 * @param fastScrollerIndexViewBinding Pass ViewBinding Instance Of Fast Scroller Layout That Added Using <include />
 **/
class IndexedFastScroller(private val context: Context,
                          private val layoutInflater: LayoutInflater,
                          private val rootView: ViewGroup,
                          private val nestedScrollView: ScrollView,
                          private val recyclerView: RecyclerView,
                          private val fastScrollerIndexViewBinding: FastScrollerIndexViewBinding) {

    /**
     * Enable Popup View Of Index Text
     **/
    var popupEnable: Boolean = true

    /**
     * Set Integer Number Of Offset For Popup View Of Index Text.
     * It Will Automatically Convert It To DP.
     *
     * Default Value Is 7dp.
     **/
    var popupOffset: Float = 7F
    private val finalPopupOffset: Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            popupOffset,
            context.resources.displayMetrics).toInt()

    /**
     * Set A Drawable As Background Of Popup View Of Index Text
     **/
    var popupBackgroundShape: Drawable? = null

    init {
        FunctionsClassDebug.PrintDebug("*** Indexed Fast Scroller Initialized ***")
    }

    fun initializeIndexView(paddingTop: Int, paddingBottom: Int,
                            paddingStart: Int, paddingEnd: Int) : Deferred<IndexedFastScroller> = CoroutineScope(SupervisorJob() + Dispatchers.Main).async {

        fastScrollerIndexViewBinding.indexView.removeAllViews()

        val layoutParams = fastScrollerIndexViewBinding.root.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, rootView.id)

        fastScrollerIndexViewBinding.root.layoutParams = layoutParams

        fastScrollerIndexViewBinding.root
                .setPadding(paddingStart, paddingTop,
                        paddingEnd, paddingBottom)

        this@IndexedFastScroller
    }

    /**
     * When Populating Your List Get First Char Of Each Item Title By itemTextTitle.substring(0, 1).toUpperCase(Locale.getDefault()).
     * & Add It To A ArrayList<String>.
     * Then Pass It As...
     * @param listOfNewCharOfItemsForIndex ArrayList<String>
     **/
    fun loadIndexData(listOfNewCharOfItemsForIndex: ArrayList<String>) = CoroutineScope(SupervisorJob() + Dispatchers.Main).async {

        val mapIndexFirstItem: LinkedHashMap<String, Int> = LinkedHashMap<String, Int>()
        val mapIndexLastItem: LinkedHashMap<String, Int> = LinkedHashMap<String, Int>()

        val mapRangeIndex: LinkedHashMap<Int, String> = LinkedHashMap<Int, String>()

        withContext(Dispatchers.Default) {

            for (indexNumber in 0 until listOfNewCharOfItemsForIndex.size) {
                val indexText = listOfNewCharOfItemsForIndex[indexNumber]

                /*Avoid Duplication*/
                if (mapIndexFirstItem[indexText] == null) {
                    mapIndexFirstItem[indexText] = indexNumber
                }

                mapIndexLastItem[indexText] = indexNumber
            }
        }

        var sideIndexItem = layoutInflater.inflate(R.layout.side_index_item, null) as TextView

        mapIndexFirstItem.keys.forEach { indexText ->
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

        this@async.launch {

            setupFastScrollingIndexing(mapIndexFirstItem,
                    mapRangeIndex)
        }.join()
    }

    /**
     * Setup Popup View Of Index With Touch On List Of Index
     **/
    @SuppressLint("ClickableViewAccessibility")
    private fun setupFastScrollingIndexing(mapIndexFirstItem: LinkedHashMap<String, Int>,
                                           mapRangeIndex: LinkedHashMap<Int, String>) {

        val popupIndexBackground: Drawable? = popupBackgroundShape?:context.getDrawable(R.drawable.ic_launcher_balloon)?.mutate()
        popupIndexBackground?.setTint(PublicVariable.primaryColorOpposite)
        fastScrollerIndexViewBinding.popupIndex.background = popupIndexBackground

        fastScrollerIndexViewBinding.nestedIndexScrollView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
        fastScrollerIndexViewBinding.nestedIndexScrollView.visibility = View.VISIBLE

        val popupIndexOffsetY = (
                PublicVariable.statusBarHeight
                        + PublicVariable.actionBarHeight
                        + finalPopupOffset
                ).toFloat()

        fastScrollerIndexViewBinding.nestedIndexScrollView.setOnTouchListener { view, motionEvent ->

            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (popupEnable) {
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
                    if (popupEnable) {
                        val indexText = mapRangeIndex[motionEvent.y.toInt()]

                        if (indexText != null) {
                            if (!fastScrollerIndexViewBinding.popupIndex.isShown) {
                                fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
                                fastScrollerIndexViewBinding.popupIndex.visibility = View.VISIBLE
                            }

                            fastScrollerIndexViewBinding.popupIndex.y = motionEvent.rawY - popupIndexOffsetY
                            fastScrollerIndexViewBinding.popupIndex.text = indexText

                            nestedScrollView.smoothScrollTo(
                                    0,
                                    recyclerView.getChildAt(mapIndexFirstItem[mapRangeIndex[motionEvent.y.toInt()]]!!).y.toInt()
                            )
                        } else {
                            if (fastScrollerIndexViewBinding.popupIndex.isShown) {
                                fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
                                fastScrollerIndexViewBinding.popupIndex.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (popupEnable) {
                        if (fastScrollerIndexViewBinding.popupIndex.isShown) {

                            nestedScrollView.smoothScrollTo(
                                    0,
                                    recyclerView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                            )

                            fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
                            fastScrollerIndexViewBinding.popupIndex.visibility = View.INVISIBLE
                        }
                    } else {
                        nestedScrollView.smoothScrollTo(
                                0,
                                recyclerView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                        )
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (popupEnable) {
                        if (fastScrollerIndexViewBinding.popupIndex.isShown) {

                            nestedScrollView.smoothScrollTo(
                                    0,
                                    recyclerView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                            )

                            fastScrollerIndexViewBinding.popupIndex.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
                            fastScrollerIndexViewBinding.popupIndex.visibility = View.INVISIBLE
                        }
                    } else {
                        nestedScrollView.smoothScrollTo(
                                0,
                                recyclerView.getChildAt(mapIndexFirstItem.get(mapRangeIndex[motionEvent.y.toInt()])!!).y.toInt()
                        )
                    }
                }
            }

            true
        }
    }
}