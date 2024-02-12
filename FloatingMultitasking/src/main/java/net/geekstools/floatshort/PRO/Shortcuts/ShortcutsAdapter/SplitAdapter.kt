/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 11/11/20 10:47 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Shortcuts.ShortcutsAdapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsApplications
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FloatingServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.imageview.customshapes.ShapesImage

class SplitAdapter(private val context: Context,
                   private val adapterItemsApplications: ArrayList<AdapterItemsApplications>) : RecyclerView.Adapter<SplitAdapter.ViewHolder>() {

    private val functionsClassLegacy: FunctionsClassLegacy = FunctionsClassLegacy(context)
    private val fileIO: FileIO = FileIO(context)
    private val floatingServices: FloatingServices = FloatingServices(context)

    private var layoutInflater = 0
    private var idRippleShape = 0

    private var recoveryIndicatorDrawable: LayerDrawable? = null

    init {
        PublicVariable.floatingSizeNumber = functionsClassLegacy.readDefaultPreference("floatingSize", 39)
        PublicVariable.floatingViewsHW = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PublicVariable.floatingSizeNumber.toFloat(), context.resources.displayMetrics).toInt()

        recoveryIndicatorDrawable = context.getDrawable(R.drawable.draw_recovery_indicator) as LayerDrawable?
        val backgroundTemporary: Drawable? = recoveryIndicatorDrawable?.findDrawableByLayerId(R.id.backgroundTemporary)
        backgroundTemporary?.setTint(PublicVariable.primaryColor)

        when (functionsClassLegacy.shapesImageId()) {
            1 -> {
                layoutInflater = R.layout.item_card_hybrid_droplet
                idRippleShape = R.drawable.ripple_effect_shape_droplet
            }
            2 -> {
                layoutInflater = R.layout.item_card_hybrid_circle
                idRippleShape = R.drawable.ripple_effect_shape_circle
            }
            3 -> {
                layoutInflater = R.layout.item_card_hybrid_square
                idRippleShape = R.drawable.ripple_effect_shape_square
            }
            4 -> {
                layoutInflater = R.layout.item_card_hybrid_squircle
                idRippleShape = R.drawable.ripple_effect_shape_squircle
            }
            0 -> {
                layoutInflater = R.layout.item_card_hybrid_noshape
                idRippleShape = R.drawable.ripple_effect_no_bound
            }
            else -> {
                layoutInflater = R.layout.item_card_hybrid_noshape
                idRippleShape = R.drawable.ripple_effect_no_bound
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(viewHolderBinder: ViewHolder, position: Int) {

        viewHolderBinder.appIconView.setImageDrawable(adapterItemsApplications[position].AppIcon)
        viewHolderBinder.appNameView.text = adapterItemsApplications[position].AppName
        viewHolderBinder.appNameView.setTextColor(PublicVariable.colorLightDarkOpposite)

        val drawItemRippleDrawable = context.getDrawable(idRippleShape) as RippleDrawable?
        drawItemRippleDrawable?.setColor(ColorStateList.valueOf(adapterItemsApplications[position].AppIconDominantColor))
        viewHolderBinder.fullItemView.background = drawItemRippleDrawable

        viewHolderBinder.fullItemView.setOnClickListener {

            val splitSingle: Intent = Intent()
            splitSingle.setClassName(adapterItemsApplications[position].PackageName, adapterItemsApplications[position].ClassName)
            splitSingle.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(splitSingle)

        }

    }

    override fun getItemCount(): Int {

        return adapterItemsApplications.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var fullItemView: RelativeLayout = view.findViewById<RelativeLayout>(R.id.fullItemView) as RelativeLayout
        var appIconView: ShapesImage = view.findViewById<ShapesImage>(R.id.appIconView) as ShapesImage
        var appNameView: TextView = view.findViewById<TextView>(R.id.appNameView) as TextView
        var recoveryIndicator: Button = view.findViewById<Button>(R.id.recoveryIndicator) as Button
    }
}