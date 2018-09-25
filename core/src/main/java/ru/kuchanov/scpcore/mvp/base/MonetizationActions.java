package ru.kuchanov.scpcore.mvp.base;

import ru.kuchanov.scpcore.monetization.util.admob.MyAdListener;

/**
 * Created by mohax on 15.01.2017.
 * <p>
 * for scp_ru
 */
public interface MonetizationActions {

    void initAds();

    void showInterstitial();

    void showInterstitial(MyAdListener adListener, boolean showVideoIfNeedAndCan);

    boolean isTimeToShowAds();

    boolean isAdsLoaded();

    void requestNewInterstitial();

    void updateOwnedMarketItems();

    void showRewardedVideo();

    void startRewardedVideoFlow();

    boolean isBannerEnabled();

    void showOfferSubscriptionPopup();
}