/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 3/28/20 6:29 PM
 * Last modified 3/28/20 6:27 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Widgets.WidgetsAdapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.imageview.customshapes.ShapesImage
import java.util.*

class WidgetSectionedInstalledAdapter(private val context: Context,
                                      private val sectionLayoutResourceId: Int,
                                      private val recyclerView: RecyclerView, private val baseAdapter: RecyclerView.Adapter<InstalledWidgetsAdapter.ViewHolder>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val functionsClass: FunctionsClass = FunctionsClass(context)

    private val sectionSparseArray = SparseArray<Section?>()

    private var validate = true

    companion object {
        private const val SECTION_TYPE = 0
    }

    init {

        baseAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                validate = this@WidgetSectionedInstalledAdapter.baseAdapter.itemCount > 0
                notifyDataSetChanged()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                validate = this@WidgetSectionedInstalledAdapter.baseAdapter.itemCount > 0
                notifyItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                validate = this@WidgetSectionedInstalledAdapter.baseAdapter.itemCount > 0
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                validate = this@WidgetSectionedInstalledAdapter.baseAdapter.itemCount > 0
                notifyItemRangeRemoved(positionStart, itemCount)
            }
        })

        val layoutManager = recyclerView.layoutManager as GridLayoutManager?
        layoutManager!!.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (isSectionHeaderPosition(position)) layoutManager.spanCount else 1
            }
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, typeView: Int): RecyclerView.ViewHolder {
        return if (typeView == SECTION_TYPE) {
            val view = LayoutInflater.from(context).inflate(sectionLayoutResourceId, parent, false)
            SectionViewHolder(view)
        } else {
            baseAdapter.onCreateViewHolder(parent, typeView - 1)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        if (isSectionHeaderPosition(position)) {
            if (viewHolder is SectionViewHolder) {
                val sectionViewHolder = viewHolder
                sectionViewHolder.appName.text = sectionSparseArray[position]!!.sectionTitle
                sectionViewHolder.appName.setTextColor(if (PublicVariable.themeLightDark) context.getColor(R.color.dark) else context.getColor(R.color.light))
                sectionViewHolder.appIcon.setImageDrawable(sectionSparseArray[position]!!.appIcon)
            }
        } else {
            try {
                baseAdapter.onBindViewHolder(viewHolder as InstalledWidgetsAdapter.ViewHolder, sectionedPositionToPosition(position))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isSectionHeaderPosition(position)) SECTION_TYPE else baseAdapter.getItemViewType(sectionedPositionToPosition(position)) + 1
    }

    fun setSections(sections: Array<Section>) {
        sectionSparseArray.clear()
        Arrays.sort(sections) { o, o1 ->
            var comparison = 1
            comparison = try {
                Integer.compare(o.firstPosition, o1.firstPosition)
            } catch (e: Exception) {
                0
            }
            comparison
        }
        var offset = 0 // offset positions for the headers we're adding
        for (section in sections) {
            try {
                section.sectionedPosition = section.firstPosition + offset
                sectionSparseArray.append(section.sectionedPosition, section)
                ++offset
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        notifyDataSetChanged()
    }

    fun sectionedPositionToPosition(sectionedPosition: Int): Int {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.SCROLLBAR_POSITION_LEFT
        }
        var offset = 0
        for (i in 0 until sectionSparseArray.size()) {
            if (sectionSparseArray.valueAt(i)!!.sectionedPosition > sectionedPosition) {
                break
            }
            --offset
        }
        return sectionedPosition + offset
    }

    private fun isSectionHeaderPosition(position: Int): Boolean {
        return sectionSparseArray[position] != null
    }

    override fun getItemId(position: Int): Long {

        return (if (isSectionHeaderPosition(position)) {
            Int.MAX_VALUE - sectionSparseArray.indexOfKey(position).toLong()
        } else {
            baseAdapter.getItemId(sectionedPositionToPosition(position))
        })
    }

    override fun getItemCount(): Int {
        return if (validate) baseAdapter.itemCount + sectionSparseArray.size() else 0
    }

    class Section(var firstPosition: Int, var sectionTitle: String, var appIcon: Drawable) {
        var sectionedPosition = 0

    }

    inner class SectionViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var appIcon: ShapesImage
        var appName: TextView

        init {
            appIcon = view.findViewById<View>(R.id.appIcon) as ShapesImage
            appName = view.findViewById<View>(R.id.appName) as TextView
            appIcon.setShapeDrawable(functionsClass.shapesDrawables())
        }
    }


}