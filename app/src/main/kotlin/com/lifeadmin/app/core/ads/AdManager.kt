package com.lifeadmin.app.core.ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.lifeadmin.app.BuildConfig

/**
 * AdMob manager with abstraction layer
 * Handles banner and interstitial ads with proper lifecycle management
 */
class AdManager(
    private val context: Context
) {
    private var interstitialAd: InterstitialAd? = null
    
    /**
     * Initialize AdMob SDK
     * Should be called on app startup
     */
    fun initialize() {
        if (BuildConfig.ADS_ENABLED) {
            MobileAds.initialize(context) { }
        }
    }
    
    /**
     * Load a banner ad into an AdView
     */
    fun loadBanner(adView: AdView) {
        if (!BuildConfig.ADS_ENABLED) return
        
        try {
            adView.adUnitId = BuildConfig.ADMOB_BANNER_ID
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        } catch (e: Exception) {
            // Fail silently - ads are not critical
        }
    }
    
    /**
     * Preload an interstitial ad
     */
    fun preloadInterstitial() {
        if (!BuildConfig.ADS_ENABLED) return
        
        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                BuildConfig.ADMOB_INTERSTITIAL_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                    }
                }
            )
        } catch (e: Exception) {
            // Fail silently
        }
    }
    
    /**
     * Show interstitial ad if available
     * Returns true if ad was shown
     */
    fun showInterstitial(activity: android.app.Activity): Boolean {
        if (!BuildConfig.ADS_ENABLED) return false
        
        return try {
            interstitialAd?.show(activity)
            interstitialAd = null // Ad can only be shown once
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Check if interstitial ad is ready
     */
    fun isInterstitialReady(): Boolean {
        return interstitialAd != null
    }
}
