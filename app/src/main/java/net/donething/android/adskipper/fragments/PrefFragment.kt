package net.donething.android.adskipper.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import net.donething.android.adskipper.R

class PrefFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}