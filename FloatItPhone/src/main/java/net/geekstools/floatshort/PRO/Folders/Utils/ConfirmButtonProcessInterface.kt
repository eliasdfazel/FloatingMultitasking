package net.geekstools.floatshort.PRO.Folders.Utils

interface ConfirmButtonProcessInterface {
    /**
     * Check Increase/Decrease In Counter Of Saved Shortcuts
     */
    fun savedShortcutCounter()
    /**
     * Show Popup List Of Saved Shortcut
     */
    fun showSavedShortcutList()
    /**
     * Hide Popup List Of Saved Shortcut
     */
    fun hideSavedShortcutList()
    /**
     * Reload Apps List After Shortcut Deleted
     */
    fun shortcutDeleted()
    /**
     * Show Popup List Of Saved Shortcut To Select One For Split Shortcut
     */
    fun showSplitShortcutPicker()
}