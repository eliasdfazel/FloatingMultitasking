package net.geekstools.floatshort.PRO.SearchEngine.UI

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import net.geekstools.floatshort.PRO.R
import net.geekstools.floatshort.PRO.SearchEngine.Data.Filter.SearchResultType
import net.geekstools.floatshort.PRO.SearchEngine.UI.Adapter.SearchEngineAdapter
import net.geekstools.floatshort.PRO.SecurityServices.Authentication.PinPassword.HandlePinPassword
import net.geekstools.floatshort.PRO.Utils.AdapterItemsData.AdapterItemsSearchEngine
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClass
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassRunServices
import net.geekstools.floatshort.PRO.Utils.Functions.FunctionsClassSecurity
import net.geekstools.floatshort.PRO.Utils.Functions.PublicVariable
import net.geekstools.floatshort.PRO.Utils.IAP.InAppBilling
import net.geekstools.floatshort.PRO.Utils.IAP.billing.BillingManager
import net.geekstools.floatshort.PRO.Utils.UI.CustomIconManager.LoadCustomIcons
import net.geekstools.floatshort.PRO.Widgets.RoomDatabase.WidgetDataInterface
import net.geekstools.floatshort.PRO.databinding.SearchEngineViewBinding

class InitializeSearchEngine(private val activity: AppCompatActivity, private val context: Context,
                             private val searchEngineViewBinding: SearchEngineViewBinding,
                             private val functionsClass: FunctionsClass,
                             private val functionsClassRunServices: FunctionsClassRunServices,
                             private val functionsClassSecurity: FunctionsClassSecurity,
                             private val customIcons: LoadCustomIcons?,
                             private val firebaseAuth: FirebaseAuth) {

    private val inputMethodManager: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    fun loadSearchEngineData() = CoroutineScope(SupervisorJob() + Dispatchers.Default).async {
        var searchAdapterItems: ArrayList<AdapterItemsSearchEngine> = ArrayList<AdapterItemsSearchEngine>()

        if (SearchEngineAdapter.allSearchData.isEmpty()) {

            //Loading Applications
            val applicationInfoList = context.packageManager.queryIntentActivities(Intent().apply {
                this.action = Intent.ACTION_MAIN
                this.addCategory(Intent.CATEGORY_LAUNCHER)
            }, PackageManager.GET_RESOLVED_FILTER)
            val applicationInfoListSorted = applicationInfoList.sortedWith(ResolveInfo.DisplayNameComparator(context.packageManager))

            applicationInfoListSorted.asFlow()
                    .filter {

                        (context.packageManager.getLaunchIntentForPackage(it.activityInfo.packageName) != null)
                    }
                    .map {

                        it
                    }
                    .collect {
                        val installedPackageName = it.activityInfo.packageName
                        val installedClassName = it.activityInfo.name
                        val installedAppName = functionsClass.activityLabel(it.activityInfo)

                        val installedAppIcon = if (functionsClass.loadCustomIcons()) {
                            customIcons?.getDrawableIconForPackage(installedPackageName, functionsClass.shapedAppIcon(it.activityInfo))
                        } else {
                            functionsClass.shapedAppIcon(it.activityInfo)
                        }

                        searchAdapterItems.add(AdapterItemsSearchEngine(installedAppName, installedPackageName, installedClassName, installedAppIcon, SearchResultType.SearchShortcuts))
                    }

            //Loading Folders
            try {
                context.getFileStreamPath(".categoryInfo").readLines().forEach {
                    try {
                        searchAdapterItems.add(AdapterItemsSearchEngine(it, functionsClass.readFileLine(it), SearchResultType.SearchFolders))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            //Loading Widgets
            if (context.getDatabasePath(PublicVariable.WIDGET_DATA_DATABASE_NAME).exists()) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val widgetDataInterface = Room.databaseBuilder(context, WidgetDataInterface::class.java, PublicVariable.WIDGET_DATA_DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .addCallback(object : RoomDatabase.Callback() {
                            override fun onCreate(supportSQLiteDatabase: SupportSQLiteDatabase) {
                                super.onCreate(supportSQLiteDatabase)
                            }

                            override fun onOpen(supportSQLiteDatabase: SupportSQLiteDatabase) {
                                super.onOpen(supportSQLiteDatabase)
                            }
                        })
                        .build()
                val widgetDataModels = widgetDataInterface.initDataAccessObject().getAllWidgetDataSuspend()

                if (widgetDataModels.isNotEmpty()) {
                    widgetDataModels.forEach { widgetDataModel ->
                        try {
                            val appWidgetId: Int = widgetDataModel.WidgetId
                            val packageName: String = widgetDataModel.PackageName
                            val className: String = widgetDataModel.ClassNameProvider
                            val configClassName: String? = widgetDataModel.ConfigClassName

                            if (functionsClass.appIsInstalled(packageName)) {

                                val appWidgetProviderInfo = appWidgetManager.getAppWidgetInfo(appWidgetId)
                                val newAppName = functionsClass.appName(packageName)
                                val appIcon = if (functionsClass.loadCustomIcons()) {
                                    customIcons?.getDrawableIconForPackage(packageName, functionsClass.shapedAppIcon(packageName))
                                } else {
                                    functionsClass.shapedAppIcon(packageName)
                                }

                                searchAdapterItems.add(AdapterItemsSearchEngine(
                                        newAppName,
                                        packageName,
                                        className,
                                        configClassName,
                                        widgetDataModel.WidgetLabel,
                                        appIcon,
                                        appWidgetProviderInfo,
                                        appWidgetId,
                                        SearchResultType.SearchWidgets
                                ))
                            } else {
                                widgetDataInterface.initDataAccessObject().deleteByWidgetClassNameProviderWidgetSuspend(packageName, className)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            val searchRecyclerViewAdapter = SearchEngineAdapter(context, searchAdapterItems)

            withContext(Dispatchers.Main) {
                activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

                if (searchAdapterItems.size > 0) {
                    setupSearchView(searchRecyclerViewAdapter)
                }
            }
        } else {
            searchAdapterItems = SearchEngineAdapter.allSearchData

            val searchRecyclerViewAdapter = SearchEngineAdapter(context, searchAdapterItems)

            withContext(Dispatchers.Main) {
                activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

                if (searchAdapterItems.size > 0) {
                    setupSearchView(searchRecyclerViewAdapter)
                }
            }
        }
    }

    private fun setupSearchView(searchRecyclerViewAdapter: SearchEngineAdapter) {
        searchEngineViewBinding.searchView.setAdapter(searchRecyclerViewAdapter)

        searchEngineViewBinding.searchView.setDropDownBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        searchEngineViewBinding.searchView.isVerticalScrollBarEnabled = false
        searchEngineViewBinding.searchView.scrollBarSize = 0

        searchEngineViewBinding.searchView.setTextColor(PublicVariable.colorLightDarkOpposite)
        searchEngineViewBinding.searchView.compoundDrawableTintList = ColorStateList.valueOf(functionsClass.setColorAlpha(PublicVariable.colorLightDarkOpposite, 175f))

        val layerDrawableSearchIcon = context.getDrawable(R.drawable.search_icon) as RippleDrawable?
        val backgroundTemporarySearchIcon = layerDrawableSearchIcon?.findDrawableByLayerId(R.id.backgroundTemporary)
        val frontTemporarySearchIcon = layerDrawableSearchIcon?.findDrawableByLayerId(R.id.frontTemporary)
        val frontDrawableSearchIcon = layerDrawableSearchIcon?.findDrawableByLayerId(R.id.frontDrawable)
        backgroundTemporarySearchIcon?.setTint(PublicVariable.colorLightDarkOpposite)
        frontTemporarySearchIcon?.setTint(PublicVariable.colorLightDark)
        frontDrawableSearchIcon?.setTint(if (PublicVariable.themeLightDark) {
            functionsClass.manipulateColor(PublicVariable.primaryColor, 0.90f)
        } else {
            functionsClass.manipulateColor(PublicVariable.primaryColor, 3.00f)
        })

        layerDrawableSearchIcon?.setLayerInset(2,
                functionsClass.DpToInteger(13), functionsClass.DpToInteger(13), functionsClass.DpToInteger(13), functionsClass.DpToInteger(13))

        searchEngineViewBinding.root.visibility = View.VISIBLE

        searchEngineViewBinding.searchIcon.setImageDrawable(layerDrawableSearchIcon)
        searchEngineViewBinding.searchIcon.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
        searchEngineViewBinding.searchIcon.visibility = View.VISIBLE

        searchEngineViewBinding.textInputSearchView.hintTextColor = ColorStateList.valueOf(PublicVariable.primaryColorOpposite)
        searchEngineViewBinding.textInputSearchView.boxStrokeColor = PublicVariable.primaryColor

        val layerDrawableBackgroundInput = context.getDrawable(R.drawable.background_search_input) as LayerDrawable?
        val backgroundTemporaryInput = layerDrawableBackgroundInput?.findDrawableByLayerId(R.id.backgroundTemporary) as GradientDrawable?
        backgroundTemporaryInput?.setTint(PublicVariable.colorLightDark)
        searchEngineViewBinding.textInputSearchView.background = layerDrawableBackgroundInput

        searchEngineViewBinding.searchIcon.setOnClickListener {
            val bundleSearchEngineUsed = Bundle()
            bundleSearchEngineUsed.putParcelable("USER_USED_SEARCH_ENGINE", firebaseAuth.currentUser)
            bundleSearchEngineUsed.putInt("TYPE_USED_SEARCH_ENGINE", SearchResultType.SearchFolders)

            val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
            firebaseAnalytics.logEvent(SearchEngineAdapter.SEARCH_ENGINE_USED_LOG, bundleSearchEngineUsed)

            if (functionsClass.securityServicesSubscribed()) {
                if (functionsClass.readPreference(".Password", "Pin", "0") == "0" && functionsClass.securityServicesSubscribed()) {
                    activity.startActivity(Intent(context, HandlePinPassword::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }, ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
                } else {
                    if (!SearchEngineAdapter.alreadyAuthenticatedSearchEngine) {
                        if (functionsClass.securityServicesSubscribed()) {
                            FunctionsClassSecurity.AuthOpenAppValues.authComponentName = context.getString(R.string.securityServices)
                            FunctionsClassSecurity.AuthOpenAppValues.authSecondComponentName = context.packageName
                            FunctionsClassSecurity.AuthOpenAppValues.authSearchEngine = true

                            functionsClassSecurity.openAuthInvocation()

                            val intentFilter = IntentFilter()
                            intentFilter.addAction("SEARCH_ENGINE_AUTHENTICATED")
                            val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                                override fun onReceive(context: Context, intent: Intent) {
                                    if (intent.action == "SEARCH_ENGINE_AUTHENTICATED") {
                                        backgroundTemporaryInput?.let { gradientDrawable -> performSearchEngine(gradientDrawable) }
                                    }
                                }
                            }
                            context.registerReceiver(broadcastReceiver, intentFilter)
                        }
                    } else {
                        backgroundTemporaryInput?.let { gradientDrawable -> performSearchEngine(gradientDrawable) }
                    }
                }
            } else {
                backgroundTemporaryInput?.let { gradientDrawable -> performSearchEngine(gradientDrawable) }
            }
        }
    }

    private fun performSearchEngine(finalBackgroundTemporaryInput: GradientDrawable) = CoroutineScope(Dispatchers.Main).launch {
        delay(99)

        if (functionsClass.searchEngineSubscribed()) {
            searchEngineViewBinding.textInputSearchView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
            searchEngineViewBinding.textInputSearchView.visibility = View.VISIBLE

            searchEngineViewBinding.searchIcon.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
            searchEngineViewBinding.searchIcon.visibility = View.INVISIBLE

            val valueAnimatorCornerDown = ValueAnimator.ofInt(functionsClass.DpToInteger(51), functionsClass.DpToInteger(7))
            valueAnimatorCornerDown.duration = 777
            valueAnimatorCornerDown.addUpdateListener { animator ->
                val animatorValue = animator.animatedValue as Int

                searchEngineViewBinding.textInputSearchView.setBoxCornerRadii(animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat())
                finalBackgroundTemporaryInput.cornerRadius = animatorValue.toFloat()
            }
            valueAnimatorCornerDown.start()

            val valueAnimatorScalesUp = ValueAnimator.ofInt(functionsClass.DpToInteger(51), (searchEngineViewBinding.root.width / 2))
            valueAnimatorScalesUp.duration = 777
            valueAnimatorScalesUp.addUpdateListener { animator ->
                val animatorValue = animator.animatedValue as Int
                searchEngineViewBinding.textInputSearchView.layoutParams.width = animatorValue
                searchEngineViewBinding.textInputSearchView.requestLayout()
            }
            valueAnimatorScalesUp.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    searchEngineViewBinding.searchView.requestFocus()

                    inputMethodManager.showSoftInput(searchEngineViewBinding.searchView, InputMethodManager.SHOW_IMPLICIT)

                    Handler().postDelayed({
                        searchEngineViewBinding.searchFloatIt.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up_bounce_interpolator))
                        searchEngineViewBinding.searchFloatIt.visibility = View.VISIBLE

                        searchEngineViewBinding.searchClose.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_up_bounce_interpolator))
                        searchEngineViewBinding.searchClose.visibility = View.VISIBLE
                    }, 555)
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
            valueAnimatorScalesUp.start()

            searchEngineViewBinding.searchFloatIt.setOnClickListener {
                if (searchEngineViewBinding.searchView.text.toString().isNotEmpty() && SearchEngineAdapter.allSearchResults.size > 0 && searchEngineViewBinding.searchView.text.toString().length >= 2) {
                    SearchEngineAdapter.allSearchResults.forEach { searchResultItem ->
                        when (searchResultItem.searchResultType) {
                            SearchResultType.SearchShortcuts -> {
                                functionsClassRunServices.runUnlimitedShortcutsService(searchResultItem.PackageName!!, searchResultItem.ClassName!!)
                            }
                            SearchResultType.SearchFolders -> {
                                functionsClass.runUnlimitedFolderService(searchResultItem.folderName)
                            }
                            SearchResultType.SearchWidgets -> {
                                functionsClass
                                        .runUnlimitedWidgetService(searchResultItem.appWidgetId!!,
                                                searchResultItem.widgetLabel)
                            }
                        }
                    }
                }
            }

            searchEngineViewBinding.searchView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(text: CharSequence, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(text: Editable) {

                }
            })

            searchEngineViewBinding.searchView.setOnEditorActionListener { textView, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (SearchEngineAdapter.allSearchResults.size == 1 && !searchEngineViewBinding.searchView.text.toString().isEmpty() && searchEngineViewBinding.searchView.text.toString().length >= 2) {
                        when (SearchEngineAdapter.allSearchResults[0].searchResultType) {
                            SearchResultType.SearchShortcuts -> {
                                functionsClassRunServices.runUnlimitedShortcutsService(SearchEngineAdapter.allSearchResults[0].PackageName!!, SearchEngineAdapter.allSearchResults[0].ClassName!!)
                            }
                            SearchResultType.SearchFolders -> {
                                functionsClass.runUnlimitedFolderService(SearchEngineAdapter.allSearchResults[0].folderName)
                            }
                            SearchResultType.SearchWidgets -> {
                                functionsClass
                                        .runUnlimitedWidgetService(SearchEngineAdapter.allSearchResults[0].appWidgetId!!,
                                                SearchEngineAdapter.allSearchResults[0].widgetLabel)
                            }
                        }

                        searchEngineViewBinding.searchView.setText("")

                        inputMethodManager.hideSoftInputFromWindow(searchEngineViewBinding.searchView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                        val valueAnimatorCornerUp = ValueAnimator.ofInt(functionsClass.DpToInteger(7), functionsClass.DpToInteger(51))
                        valueAnimatorCornerUp.duration = 777
                        valueAnimatorCornerUp.addUpdateListener { animator ->
                            val animatorValue = animator.animatedValue as Int
                            searchEngineViewBinding.textInputSearchView.setBoxCornerRadii(animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat())
                            finalBackgroundTemporaryInput.cornerRadius = animatorValue.toFloat()
                        }
                        valueAnimatorCornerUp.start()

                        val valueAnimatorScales = ValueAnimator.ofInt(searchEngineViewBinding.textInputSearchView.width, functionsClass.DpToInteger(51))
                        valueAnimatorScales.duration = 777
                        valueAnimatorScales.addUpdateListener { animator ->
                            val animatorValue = animator.animatedValue as Int
                            searchEngineViewBinding.textInputSearchView.layoutParams.width = animatorValue
                            searchEngineViewBinding.textInputSearchView.requestLayout()
                        }
                        valueAnimatorScales.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {

                            }

                            override fun onAnimationEnd(animation: Animator) {
                                searchEngineViewBinding.textInputSearchView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
                                searchEngineViewBinding.textInputSearchView.visibility = View.INVISIBLE

                                searchEngineViewBinding.searchIcon.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
                                searchEngineViewBinding.searchIcon.visibility = View.VISIBLE

                                searchEngineViewBinding.searchFloatIt.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_down_zero))
                                searchEngineViewBinding.searchFloatIt.visibility = View.INVISIBLE

                                searchEngineViewBinding.searchClose.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_down_zero))
                                searchEngineViewBinding.searchClose.visibility = View.INVISIBLE
                            }

                            override fun onAnimationCancel(animation: Animator) {

                            }

                            override fun onAnimationRepeat(animation: Animator) {

                            }
                        })
                        valueAnimatorScales.start()
                    } else {
                        if (SearchEngineAdapter.allSearchResults.size > 0 && searchEngineViewBinding.searchView.text.toString().length >= 2) {
                            searchEngineViewBinding.searchView.showDropDown()
                        }
                    }
                }

                false
            }

            searchEngineViewBinding.searchClose.setOnClickListener {
                searchEngineViewBinding.searchView.setText("")

                inputMethodManager.hideSoftInputFromWindow(searchEngineViewBinding.searchView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

                val valueAnimatorCornerUp = ValueAnimator.ofInt(functionsClass.DpToInteger(7), functionsClass.DpToInteger(51))
                valueAnimatorCornerUp.duration = 777
                valueAnimatorCornerUp.addUpdateListener { animator ->
                    val animatorValue = animator.animatedValue as Int
                    searchEngineViewBinding.textInputSearchView.setBoxCornerRadii(animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat(), animatorValue.toFloat())
                    finalBackgroundTemporaryInput.cornerRadius = animatorValue.toFloat()
                }
                valueAnimatorCornerUp.start()

                val valueAnimatorScales = ValueAnimator.ofInt(searchEngineViewBinding.textInputSearchView.width, functionsClass.DpToInteger(51))
                valueAnimatorScales.duration = 777
                valueAnimatorScales.addUpdateListener { animator ->
                    val animatorValue = animator.animatedValue as Int
                    searchEngineViewBinding.textInputSearchView.layoutParams.width = animatorValue
                    searchEngineViewBinding.textInputSearchView.requestLayout()
                }
                valueAnimatorScales.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {

                    }

                    override fun onAnimationEnd(animation: Animator) {
                        searchEngineViewBinding.textInputSearchView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
                        searchEngineViewBinding.textInputSearchView.visibility = View.INVISIBLE

                        searchEngineViewBinding.searchIcon.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
                        searchEngineViewBinding.searchIcon.visibility = View.VISIBLE

                        searchEngineViewBinding.searchFloatIt.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_down_zero))
                        searchEngineViewBinding.searchFloatIt.visibility = View.INVISIBLE

                        searchEngineViewBinding.searchClose.startAnimation(AnimationUtils.loadAnimation(context, R.anim.scale_down_zero))
                        searchEngineViewBinding.searchClose.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator) {

                    }

                    override fun onAnimationRepeat(animation: Animator) {

                    }
                })
                valueAnimatorScales.start()
            }
        } else {
            InAppBilling.ItemIAB = BillingManager.iapSearchEngines

            activity.startActivity(Intent(context, InAppBilling::class.java),
                    ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())
        }

    }
}