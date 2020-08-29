/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 8/29/20 3:58 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */
package net.geekstools.floatshort.PRO.Folders.FoldersApplicationsSelectionProcess.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.geekstools.floatshort.PRO.Folders.UI.AppsConfirmButton
import net.geekstools.floatshort.PRO.Folders.Utils.ConfirmButtonProcessInterface
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SecurityServices.AuthenticationProcess.Utils.SecurityFunctions
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItems
import net.geekstools.floatshort.PRO.Utils.Functions.FileIO
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassLegacy
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.databinding.AdvanceAppSelectionListBinding
import net.geekstools.imageview.customshapes.ShapesImage
import java.util.*
import kotlin.math.abs

class AppSelectionListAdapter(private val context: Context,
                              private val functionsClassLegacy: FunctionsClassLegacy,
                              private val advanceAppSelectionListBinding: AdvanceAppSelectionListBinding,
                              private val adapterItems: ArrayList<AdapterItems>,
                              private val appsConfirmButton: AppsConfirmButton,
                              private val confirmButtonProcessInterface: ConfirmButtonProcessInterface) : RecyclerView.Adapter<AppSelectionListAdapter.ViewHolder>() {

    private val securityFunctions: SecurityFunctions = SecurityFunctions(context)

    private val fileIO: FileIO = FileIO(context)

    var temporaryFallingIcon: ImageView = functionsClassLegacy.initShapesImage(advanceAppSelectionListBinding.temporaryFallingIcon)

    object PickedAttribute {
        var fromX: Float = 0f
        var fromY: Float = 0f

        var toX: Float = 0f
        var toY: Float = 0f

        var dpHeight: Float = 0f
        var dpWidth: Float = 0f

        var systemUiHeight: Float = 0f

        var animationType: Int = 0
    }

    var layoutInflaterView = 0

    lateinit var loadCustomIcons: LoadCustomIcons

    init {
        val displayMetrics = context.resources.displayMetrics

        PickedAttribute.dpHeight = displayMetrics.heightPixels.toFloat()
        PickedAttribute.dpWidth = displayMetrics.widthPixels.toFloat()

        PickedAttribute.systemUiHeight = PublicVariable.actionBarHeight.toFloat()

        PickedAttribute.toX = PublicVariable.confirmButtonX
        PickedAttribute.fromX = PickedAttribute.toX

        PickedAttribute.toY = PublicVariable.confirmButtonY

        PickedAttribute.animationType = Animation.ABSOLUTE

        when (functionsClassLegacy.shapesImageId()) {
            1 -> layoutInflaterView = R.layout.selection_item_card_list_droplet
            2 -> layoutInflaterView = R.layout.selection_item_card_list_circle
            3 -> layoutInflaterView = R.layout.selection_item_card_list_square
            4 -> layoutInflaterView = R.layout.selection_item_card_list_squircle
            0 -> layoutInflaterView = R.layout.selection_item_card_list_noshape
        }

        if (functionsClassLegacy.customIconsEnable()) {
            loadCustomIcons = LoadCustomIcons(context, functionsClassLegacy.customIconPackageName())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(layoutInflaterView, parent, false))
    }

    override fun getItemCount(): Int {

        return adapterItems.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(viewHolderBinder: ViewHolder, position: Int) {

        val pickedPackageNameList = adapterItems[position].packageName
        val pickedPackageNameFileList = context.getFileStreamPath(pickedPackageNameList + PublicVariable.folderName)

        viewHolderBinder.checkboxSelectItem.isChecked = false
        viewHolderBinder.checkboxSelectItem.isChecked = pickedPackageNameFileList.exists()

        if (PublicVariable.themeLightDark) {
            viewHolderBinder.checkboxSelectItem.buttonTintList = ColorStateList.valueOf(context.getColor(R.color.dark))
        } else if (!PublicVariable.themeLightDark) {
            viewHolderBinder.appName.setTextColor(context.getColor(R.color.light))
            viewHolderBinder.checkboxSelectItem.buttonTintList = ColorStateList.valueOf(context.getColor(R.color.light))
        }

        viewHolderBinder.appIcon.setImageDrawable(adapterItems[position].appIcon)
        viewHolderBinder.appName.text = adapterItems[position].appName

        viewHolderBinder.item.setOnTouchListener { view, motionEvent ->

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    PickedAttribute.fromY = -(PickedAttribute.dpHeight - motionEvent.rawY - PickedAttribute.systemUiHeight)
                }
                MotionEvent.ACTION_UP -> {
                    val pickedPackageName = adapterItems[position].packageName
                    val pickedPackageNameFile = context.getFileStreamPath(pickedPackageName + PublicVariable.folderName)

                    if (pickedPackageNameFile.exists()) {
                        context.deleteFile(pickedPackageName + PublicVariable.folderName)
                        fileIO.removeLine(PublicVariable.folderName, adapterItems[position].packageName)

                        viewHolderBinder.checkboxSelectItem.isChecked = false

                        confirmButtonProcessInterface.savedShortcutCounter()
                        confirmButtonProcessInterface.hideSavedShortcutList()
                        appsConfirmButton.makeItVisible()

                        if (securityFunctions.isAppLocked(PublicVariable.folderName)) {
                            securityFunctions.doUnlockApps(adapterItems[position].packageName)
                        }

                    } else {
                        fileIO.saveFile(pickedPackageName + PublicVariable.folderName, pickedPackageName)
                        fileIO.saveFileAppendLine(PublicVariable.folderName, pickedPackageName)

                        viewHolderBinder.checkboxSelectItem.isChecked = true
                        if (securityFunctions.isAppLocked(PublicVariable.folderName)) {
                            securityFunctions.doLockApps(adapterItems[position].packageName)
                        }

                        val translateAnimation = TranslateAnimation(PickedAttribute.animationType, PickedAttribute.fromX,
                                PickedAttribute.animationType, PickedAttribute.toX,
                                PickedAttribute.animationType, PickedAttribute.fromY,
                                PickedAttribute.animationType, PickedAttribute.toY)

                        translateAnimation.duration = abs(PickedAttribute.fromY).toLong()

                        temporaryFallingIcon.setImageDrawable(if (functionsClassLegacy.customIconsEnable()) {
                            loadCustomIcons.getDrawableIconForPackage(adapterItems[position].packageName, functionsClassLegacy.shapedAppIcon(adapterItems[position].packageName))
                        } else {
                            functionsClassLegacy.shapedAppIcon(adapterItems[position].packageName)
                        })
                        temporaryFallingIcon.startAnimation(translateAnimation)

                        translateAnimation.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation) {
                                confirmButtonProcessInterface.hideSavedShortcutList()
                                appsConfirmButton.makeItVisible()
                                temporaryFallingIcon.visibility = View.VISIBLE
                            }

                            override fun onAnimationEnd(animation: Animation) {
                                temporaryFallingIcon.visibility = View.INVISIBLE
                                
                                appsConfirmButton.startCustomAnimation(null)
                                confirmButtonProcessInterface.savedShortcutCounter()
                            }

                            override fun onAnimationRepeat(animation: Animation) {}
                        })
                    }
                }
            }
            true
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var item: RelativeLayout = view.findViewById<View>(R.id.fullItemView) as RelativeLayout
        var appIcon: ShapesImage = view.findViewById<View>(R.id.appIconView) as ShapesImage
        var appName: TextView = view.findViewById<View>(R.id.appNameView) as TextView
        var checkboxSelectItem: CheckBox = view.findViewById<View>(R.id.checkboxSelectItem) as CheckBox
    }
}