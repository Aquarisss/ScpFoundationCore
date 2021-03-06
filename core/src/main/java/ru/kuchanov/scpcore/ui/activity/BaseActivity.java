package ru.kuchanov.scpcore.ui.activity;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.mopub.mobileads.MoPubView;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import org.jetbrains.annotations.NotNull;
import org.joda.time.Duration;
import org.joda.time.Period;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.kuchanov.scpcore.BaseApplication;
import ru.kuchanov.scpcore.BuildConfig;
import ru.kuchanov.scpcore.ConstantValues;
import ru.kuchanov.scpcore.Constants;
import ru.kuchanov.scpcore.R;
import ru.kuchanov.scpcore.R2;
import ru.kuchanov.scpcore.db.model.ArticleTag;
import ru.kuchanov.scpcore.db.model.User;
import ru.kuchanov.scpcore.manager.MyNotificationManager;
import ru.kuchanov.scpcore.manager.MyPreferenceManager;
import ru.kuchanov.scpcore.monetization.util.InappPurchaseUtil.SubscriptionType;
import ru.kuchanov.scpcore.monetization.util.InterstitialAdListener;
import ru.kuchanov.scpcore.monetization.util.admob.AdMobHelper;
import ru.kuchanov.scpcore.monetization.util.admob.AdmobInterstitialAdListener;
import ru.kuchanov.scpcore.monetization.util.mopub.MopubHelper;
import ru.kuchanov.scpcore.monetization.util.mopub.MopubInterstitialAdListener;
import ru.kuchanov.scpcore.monetization.util.mopub.MopubNativeManager;
import ru.kuchanov.scpcore.monetization.util.mopub.ScpMopubBannerAdListener;
import ru.kuchanov.scpcore.monetization.util.mopub.ScpMopubRewardedVideoAdListener;
import ru.kuchanov.scpcore.monetization.util.playmarket.InAppHelper;
import ru.kuchanov.scpcore.mvp.base.BaseActivityMvp;
import ru.kuchanov.scpcore.mvp.base.MonetizationActions;
import ru.kuchanov.scpcore.mvp.contract.DataSyncActions;
import ru.kuchanov.scpcore.ui.adapter.SocialLoginAdapter;
import ru.kuchanov.scpcore.ui.dialog.AdsSettingsBottomSheetDialogFragment;
import ru.kuchanov.scpcore.ui.dialog.NewVersionDialogFragment;
import ru.kuchanov.scpcore.ui.dialog.SettingsBottomSheetDialogFragment;
import ru.kuchanov.scpcore.ui.dialog.TextSizeDialogFragment;
import ru.kuchanov.scpcore.ui.holder.login.SocialLoginHolder;
import ru.kuchanov.scpcore.ui.util.DialogUtils;
import ru.kuchanov.scpcore.util.AttributeGetter;
import ru.kuchanov.scpcore.util.Entry;
import ru.kuchanov.scpcore.util.RemoteConfigJsonModel;
import ru.kuchanov.scpcore.util.StorageUtils;
import ru.kuchanov.scpcore.util.SystemUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static ru.kuchanov.scpcore.Constants.Firebase.Analytics.EventName;
import static ru.kuchanov.scpcore.Constants.Firebase.Analytics.EventParam;
import static ru.kuchanov.scpcore.Constants.Firebase.Analytics.StartScreen;
import static ru.kuchanov.scpcore.Constants.Firebase.Analytics.UserPropertyKey;
import static ru.kuchanov.scpcore.Constants.Firebase.RemoteConfigKeys.NATIVE_ADS_LISTS_ENABLED;
import static ru.kuchanov.scpcore.manager.MyPreferenceManager.IMAGES_DISABLED_PERIOD;
import static ru.kuchanov.scpcore.ui.activity.MainActivity.EXTRA_SHOW_DISABLE_ADS;

/**
 * Created by mohax on 31.12.2016.
 */
public abstract class BaseActivity<V extends BaseActivityMvp.View, P extends BaseActivityMvp.Presenter<V>>
        extends MvpActivity<V, P>
        implements BaseActivityMvp.View,
        SharedPreferences.OnSharedPreferenceChangeListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String EXTRA_ARTICLES_URLS_LIST = "EXTRA_ARTICLES_URLS_LIST";

    public static final String EXTRA_POSITION = "EXTRA_POSITION";

    public static final String EXTRA_TAGS = "EXTRA_TAGS";

    //google login
    public static final int RC_SIGN_IN = 5555;

    protected GoogleApiClient mGoogleApiClient;
    ///////////

    @BindView(R2.id.root)
    protected View mRoot;

    @BindView(R2.id.content)
    protected View mContent;

    @Nullable
    @BindView(R2.id.toolBar)
    protected Toolbar mToolbar;

    @Nullable
    @BindView(R2.id.banner)
    protected AdView mAdView;

    @Nullable
    @BindView(R2.id.mopubBanner)
    protected MoPubView mopubBanner;

    @Inject
    protected P mPresenter;

    @Inject
    protected MyPreferenceManager mMyPreferenceManager;

    @Inject
    protected MyNotificationManager mMyNotificationManager;

    @Inject
    protected ConstantValues mConstantValues;

    @Inject
    protected DialogUtils mDialogUtils;

    @Inject
    protected ru.kuchanov.scpcore.downloads.DialogUtils mDownloadAllChooser;

    @Inject
    protected InAppHelper mInAppHelper;

    @Inject
    protected MopubNativeManager mopubNativeManager;

    @Inject
    protected FirebaseRemoteConfig remoteConfig;

    //admob
    private InterstitialAd mAdmobInterstitialAd;

    //mopub
    private MoPubInterstitial mMopubInterstitialAd;

    @NonNull
    @Override
    public P createPresenter() {
        return mPresenter;
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        Timber.d("onCreate");
        callInjections();
        if (mMyPreferenceManager.isNightMode()) {
            setTheme(R.style.SCP_Theme_Dark);
        } else {
            setTheme(R.style.SCP_Theme_Light);
        }
        super.onCreate(savedInstanceState);

        //remote config
        initAndUpdateRemoteConfig();

        setContentView(getLayoutResId());
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        //google login
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_application_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mPresenter.onCreate();

        //setAlarm for notification
        mMyNotificationManager.checkAlarm();

        //ads
        initAds();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        //just log fcm token for test purposes
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(
                        instanceIdResult -> Timber.d(
                                "FCM result: %s",
                                instanceIdResult.getToken()
                        )
                );

        mInAppHelper.onActivate(this);
    }

    @Override
    public void showOfferLoginForLevelUpPopup() {
        mDialogUtils.showOfferLoginForLevelUpPopup(this);
    }

    @Override
    public void showLoginProvidersPopup() {
        final List<Constants.Firebase.SocialProvider> providers = new ArrayList<>(Arrays.asList(Constants.Firebase.SocialProvider.values()));
        if (!getResources().getBoolean(R.bool.social_login_vk_enabled)) {
            providers.remove(Constants.Firebase.SocialProvider.VK);
        }
        final SocialLoginAdapter adapter = new SocialLoginAdapter();
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_social_login_title)
                .items(providers)
                .adapter(adapter, new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
                .positiveText(android.R.string.cancel)
                .build();
        adapter.setItemClickListener(data -> {
            startLogin(data.getSocialProvider());
            dialog.dismiss();
        });
        adapter.setData(SocialLoginHolder.SocialLoginModel.getModels(providers));
        dialog.getRecyclerView().setOverScrollMode(View.OVER_SCROLL_NEVER);
        dialog.show();
    }

    @Override
    public void startLogin(final Constants.Firebase.SocialProvider provider) {
        switch (provider) {
            case VK:
                VKSdk.login(this, VKScope.EMAIL, VKScope.GROUPS, VKScope.WALL);
                break;
            case GOOGLE:
                final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            case FACEBOOK:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
                break;
            default:
                throw new RuntimeException("unexpected provider");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        MoPub.onStop(this);
        //unsubscribe from firebase;
        mPresenter.onActivityStopped();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MoPub.onStart(this);
        //subscribe from firebase;
        mPresenter.onActivityStarted();
    }

    @Override
    public void initAds() {
        //disable ads for first 3 days
        if (mMyPreferenceManager.getLastTimeAdsShows() == 0) {
            //so it's first time we check it after install (or app data clearing)
            //so add 3 day to current time to disable it for 3 days to increase user experience
            //3 days, as we use 2 day interval before asking for review
            final long initialAdsDisablePeriodInMillis = Period.days(3).toStandardDuration().getMillis();
            mMyPreferenceManager.setLastTimeAdsShows(System.currentTimeMillis() + initialAdsDisablePeriodInMillis);
            //also disable banners for same period
            mMyPreferenceManager.setTimeForWhichBannersDisabled(System.currentTimeMillis() + initialAdsDisablePeriodInMillis);

            //also disable images
            mMyPreferenceManager.setFirstLaunchTime(System.currentTimeMillis());
        } else {
            //also fix images disabling for old users
            //If there is already LastTimeAdsShows so it's not first launch, so if FirstLaunchTime is not set - set it
            if (mMyPreferenceManager.getFirstLaunchTime() == 0) {
                long curTime = System.currentTimeMillis();
                mMyPreferenceManager.setFirstLaunchTime(curTime - IMAGES_DISABLED_PERIOD);
            }
        }

        //init frameworks
        MobileAds.initialize(getApplicationContext(), getString(R.string.ads_app_id));

        mAdmobInterstitialAd = new InterstitialAd(this);
        mAdmobInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id_interstitial));
        mAdmobInterstitialAd.setAdListener(new AdmobInterstitialAdListener());

        //todo rewarded video by admob
        //and native, if they do it

        //admob END

        //mopub
        MoPub.onCreate(this);
        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(MopubHelper.getBannerAdId())
                .withLogLevel(MoPubLog.LogLevel.NONE)
                .withLegitimateInterestAllowed(false)
                .build();

        MoPub.initializeSdk(this, sdkConfiguration, () -> {
            Timber.d("Mopub initialized");
            //MoPub SDK initialized.
            //Check if you should show the consent dialog here, and make your ad requests.

            //banner
            if (mopubBanner != null) {
                mopubBanner.loadAd();
            }

            //interstitial
            if (!isAdsLoaded() && mMyPreferenceManager.isTimeToLoadAds()) {
                requestNewInterstitial();
            }

            //native ads
            if (remoteConfig.getBoolean(NATIVE_ADS_LISTS_ENABLED)
                    && mMyPreferenceManager.isTimeToShowBannerAds()
                    && !isBannerEnabled()) {
                mopubNativeManager.activate();
            }
        });

        //banner
        if (mopubBanner != null) {
            mopubBanner.setBannerAdListener(new ScpMopubBannerAdListener());
            mopubBanner.setAdUnitId(MopubHelper.getBannerAdId());
            mopubBanner.setTesting(BaseApplication.isTestingMode());
        }

        //interstitial
        mMopubInterstitialAd = new MoPubInterstitial(this, MopubHelper.getInterstitialAdId());
        mMopubInterstitialAd.setInterstitialAdListener(new MopubInterstitialAdListener());

        //rewarded video
        MoPubRewardedVideos.loadRewardedVideo(MopubHelper.getRewardedVideoAdId());
        MoPubRewardedVideos.setRewardedVideoListener(new ScpMopubRewardedVideoAdListener() {
            @Override
            public void onRewardedVideoCompleted(@NotNull Set<String> adUnitIds, @NotNull MoPubReward reward) {
                super.onRewardedVideoCompleted(adUnitIds, reward);

                final long numOfMillis = FirebaseRemoteConfig.getInstance()
                        .getLong(Constants.Firebase.RemoteConfigKeys.REWARDED_VIDEO_COOLDOWN_IN_MILLIS);
                final long hours = Duration.millis(numOfMillis).toStandardHours().getHours();
                showMessage(getString(R.string.ads_reward_gained, hours));

                FirebaseAnalytics.getInstance(BaseActivity.this).logEvent(
                        Constants.Firebase.Analytics.EventType.REWARD_GAINED,
                        null
                );

                @DataSyncActions.ScoreAction final String action = DataSyncActions.ScoreAction.REWARDED_VIDEO;
                mPresenter.updateUserScoreForScoreAction(action);

                mRoot.postDelayed(() -> mMyPreferenceManager.applyAwardFromAds(), Constants.POST_DELAYED_MILLIS);
            }
        });

        //mopub END

        if (!isAdsLoaded() && mMyPreferenceManager.isTimeToLoadAds()) {
            requestNewInterstitial();
        }

        setUpBanner();
    }

    private void setUpBanner() {
//        Timber.d("setUpBanner");
//        Timber.d("mMyPreferenceManager.isHasAnySubscription(): %s", mMyPreferenceManager.isHasAnySubscription());
//        Timber.d("!isBannerEnabled(): %s", !isBannerEnabled());
//        Timber.d("!mMyPreferenceManager.isTimeToShowBannerAds(): %s", !mMyPreferenceManager.isTimeToShowBannerAds());
//        Timber.d("mAdView != null: %s", mAdView != null);
//        Timber.d("mMyPreferenceManager.isHasAnySubscription()\n|| !isBannerEnabled()\n || !mMyPreferenceManager.isTimeToShowBannerAds(): %s",
//                mMyPreferenceManager.isHasAnySubscription()
//                || !isBannerEnabled()
//                || !mMyPreferenceManager.isTimeToShowBannerAds()
//        );
        if (mMyPreferenceManager.isHasAnySubscription()
                || !isBannerEnabled()
                || !mMyPreferenceManager.isTimeToShowBannerAds()) {
            if (mAdView != null) {
                mAdView.setEnabled(false);
                mAdView.setVisibility(View.GONE);
            }

            if (mopubBanner != null) {
                mopubBanner.setEnabled(false);
                mopubBanner.setVisibility(View.GONE);
            }
        } else {
            switch (mMyPreferenceManager.getCommonAdsSource()) {
                case MOPUB:
                    if (mopubBanner != null) {
                        mopubBanner.setEnabled(true);
                        mopubBanner.setVisibility(View.VISIBLE);
                    }
                    if (mAdView != null) {
                        mAdView.setEnabled(false);
                        mAdView.setVisibility(View.GONE);
                    }
                    break;
                case ADMOB:
                    if (mAdView != null) {
//                      Timber.d("Enable banner! mAdView.isLoading(): %s", mAdView.isLoading());
                        mAdView.setEnabled(true);
                        mAdView.setVisibility(View.VISIBLE);
                        if (!mAdView.isLoading()) {
                            mAdView.loadAd(AdMobHelper.buildAdRequest());
                        }
                    }
                    if (mopubBanner != null) {
                        mopubBanner.setEnabled(false);
                        mopubBanner.setVisibility(View.GONE);
                    }
                    break;
                default:
                    Timber.wtf("UNEXPECTED AD SOURCE!!!");
                    break;
            }
        }
    }

    @Override
    public void startRewardedVideoFlow() {
        if (mMyPreferenceManager.isRewardedDescriptionShown()) {
            showRewardedVideo();
        } else {
            showRewardedVideoFlowDescription();
        }
    }

    private void showRewardedVideoFlowDescription() {
        new MaterialDialog.Builder(this)
                .title(R.string.ads_reward_description_title)
                .content(R.string.ads_reward_description_content)
                .positiveText(R.string.ads_reward_ok)
                .onPositive((dialog, which) -> {
                    mMyPreferenceManager.setRewardedDescriptionIsNotShown();
                    startRewardedVideoFlow();
                })
                .show();
    }

    @Override
    public void showRewardedVideo() {
        switch (mMyPreferenceManager.getCommonAdsSource()) {
            case MOPUB:
                if (MoPubRewardedVideos.hasRewardedVideo(MopubHelper.getRewardedVideoAdId())) {
                    FirebaseAnalytics.getInstance(BaseActivity.this).logEvent(
                            Constants.Firebase.Analytics.EventType.REWARD_REQUESTED,
                            null
                    );
                    MoPubRewardedVideos.showRewardedVideo(MopubHelper.getRewardedVideoAdId());
                } else {
                    showMessage(R.string.reward_not_loaded_yet);
                    MoPubRewardedVideos.loadRewardedVideo(MopubHelper.getRewardedVideoAdId());
                }
                break;
            case ADMOB:
                //todo rewarded video by admob
                showMessage("Not implemented yet, sorry");
                break;
            default:
                Timber.wtf("UNEXPECTED AD SOURCE!!!");
                break;
        }
    }

    @Override
    public boolean isTimeToShowAds() {
        return !mMyPreferenceManager.isHasAnySubscription() && mMyPreferenceManager.isTimeToShowAds();
    }

    @Override
    public boolean isAdsLoaded() {
        switch (mMyPreferenceManager.getCommonAdsSource()) {
            case MOPUB:
                return mMopubInterstitialAd.isReady();
            case ADMOB:
                return mAdmobInterstitialAd.isLoaded();
            default:
                Timber.wtf("UNEXPECTED AD SOURCE!!!");
                return false;
        }
    }

    /**
     * ads adsListener with showing SnackBar after ads closing
     * and calls {@link MonetizationActions#showInterstitial(InterstitialAdListener, boolean)}
     */
    @Override
    public void showInterstitial() {
        final InterstitialAdListener adListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialClosed(@NotNull MyPreferenceManager preferences) {
                showSnackBarWithAction(Constants.Firebase.CallToActionReason.REMOVE_ADS);

                @DataSyncActions.ScoreAction final String action = DataSyncActions.ScoreAction.INTERSTITIAL_SHOWN;
                mPresenter.updateUserScoreForScoreAction(action);
            }
        };
        showInterstitial(adListener, true);
    }

    /**
     * checks if it's time to show rewarded instead of simple interstitial and it's ready and shows rewarded video or interstitial
     */
    @Override
    public void showInterstitial(
            @NotNull final InterstitialAdListener adListener,
            final boolean showVideoIfNeedAndCan
    ) {
        //reset offer shown state to notify user before next ad will be shown
        mMyPreferenceManager.setOfferAlreadyShown(false);
        switch (mMyPreferenceManager.getCommonAdsSource()) {
            case MOPUB:
                mMopubInterstitialAd.setInterstitialAdListener(new MopubInterstitialAdListener() {
                    @Override
                    public void onInterstitialClosed(@NotNull MyPreferenceManager preferences) {
                        super.onInterstitialClosed(preferences);
                        adListener.onInterstitialClosed(preferences);
                    }
                });
                mMopubInterstitialAd.show();
                break;
            case ADMOB:
                //add score in activity, that will be shown from close callback of listener
                mAdmobInterstitialAd.setAdListener(new AdmobInterstitialAdListener() {
                    @Override
                    public void onInterstitialClosed(@NotNull MyPreferenceManager preferences) {
                        super.onInterstitialClosed(preferences);
                        adListener.onInterstitialClosed(preferences);
                    }
                });
                mAdmobInterstitialAd.show();
                break;
            default:
                Timber.wtf("UNEXPECTED AD SOURCE!!!");
                break;
        }
    }

    @Override
    public void showSnackBarWithAction(@NonNull final Constants.Firebase.CallToActionReason reason) {
        Timber.d("showSnackBarWithAction: %s", reason);
        final Snackbar snackbar;
        switch (reason) {
            case REMOVE_ADS:
                snackbar = Snackbar.make(
                        mRoot,
                        SystemUtils.coloredTextForSnackBar(this, R.string.remove_ads),
                        Snackbar.LENGTH_LONG
                );
                snackbar.setAction(R.string.yes_bliad, v -> {
                    snackbar.dismiss();
                    SubscriptionsActivity.start(this);

                    final Bundle bundle = new Bundle();
                    bundle.putString(EventParam.PLACE, StartScreen.SNACK_BAR);
                    FirebaseAnalytics.getInstance(BaseActivity.this).logEvent(EventName.SUBSCRIPTIONS_SHOWN, bundle);
                });
                break;
            case ENABLE_FONTS:
                snackbar = Snackbar.make(
                        mRoot,
                        SystemUtils.coloredTextForSnackBar(this, R.string.only_premium),
                        Snackbar.LENGTH_LONG
                );
                snackbar.setAction(R.string.activate, action -> {
                    SubscriptionsActivity.start(this);

                    final Bundle bundle = new Bundle();
                    bundle.putString(EventParam.PLACE, StartScreen.FONT);
                    FirebaseAnalytics.getInstance(BaseActivity.this).logEvent(EventName.SUBSCRIPTIONS_SHOWN, bundle);
                });
                break;
            case ENABLE_AUTO_SYNC:
                snackbar = Snackbar.make(
                        mRoot,
                        SystemUtils.coloredTextForSnackBar(this, R.string.auto_sync_disabled),
                        Snackbar.LENGTH_LONG
                );
                snackbar.setAction(R.string.turn_on, v -> {
                    snackbar.dismiss();
                    SubscriptionsActivity.start(this);

                    final Bundle bundle = new Bundle();
                    bundle.putString(EventParam.PLACE, StartScreen.AUTO_SYNC_SNACKBAR);
                    FirebaseAnalytics.getInstance(BaseActivity.this).logEvent(EventName.SUBSCRIPTIONS_SHOWN, bundle);
                });
                break;
            case SYNC_NEED_AUTH:
                snackbar = Snackbar.make(
                        mRoot,
                        SystemUtils.coloredTextForSnackBar(this, R.string.sync_need_auth),
                        Snackbar.LENGTH_LONG
                );
                snackbar.setAction(R.string.authorize, v -> {
                    snackbar.dismiss();
                    showLoginProvidersPopup();
                });
                break;
            case ADS_WILL_SHOWN_SOON:
                snackbar = Snackbar.make(mRoot, SystemUtils.coloredTextForSnackBar(this, R.string.ads_will_be_shown_soon), Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.yes, action -> {
                    final int modificator = mMyPreferenceManager.getOfferSubscriptionInsteadOfRewardedVideoModificator();
                    final int randomInt = new Random().nextInt(modificator);
                    if (randomInt > 1) {
                        presenter.onPurchaseClick(
                                mInAppHelper.getNewSubsSkus().get(0),
                                true
                        );
                    } else {
                        //show rewarded video after description
                        showRewardedVideoFlowDescription();
                    }
                });
                final View snackbarView = snackbar.getView();
                final TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setMaxLines(5);
                break;
            default:
                throw new IllegalArgumentException("unexpected callToActionReason");
        }
        snackbar.setActionTextColor(AttributeGetter.getColor(this, R.attr.snackbarActionTextColor));
        snackbar.show();
    }

    @Override
    public void requestNewInterstitial() {
        switch (mMyPreferenceManager.getCommonAdsSource()) {
            case MOPUB:
                Timber.d("requestNewInterstitial isReady: %s", mMopubInterstitialAd.isReady());
                if (mMopubInterstitialAd.isReady()) {
                    Timber.d("loading already done");
                } else {
                    mMopubInterstitialAd.load();
                }
                break;
            case ADMOB:
                Timber.d("requestNewInterstitial loading/loaded: %s/%s", mAdmobInterstitialAd.isLoading(), mAdmobInterstitialAd.isLoaded());
                if (mAdmobInterstitialAd.isLoading() || mAdmobInterstitialAd.isLoaded()) {
                    Timber.d("loading already in progress or already done");
                } else {
                    mAdmobInterstitialAd.loadAd(AdMobHelper.buildAdRequest());
                }
                break;
            default:
                Timber.wtf("UNEXPECTED AD SOURCE!!!");
                break;
        }
    }

    @Override
    public void updateOwnedMarketItems() {
        Timber.d("updateOwnedMarketItems");
        mInAppHelper
                .validateSubsObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        validatedItems -> {
                            @SubscriptionType final int type = mInAppHelper.getSubscriptionTypeFromItemsList(validatedItems);
                            switch (type) {
                                case SubscriptionType.NONE:
                                    break;
                                case SubscriptionType.NO_ADS:
                                case SubscriptionType.FULL_VERSION: {
                                    //remove banner
                                    if (mAdView != null) {
                                        mAdView.setEnabled(false);
                                        mAdView.setVisibility(View.GONE);
                                    }
                                    if (mopubBanner != null) {
                                        mopubBanner.setEnabled(false);
                                        mopubBanner.setVisibility(View.GONE);
                                    }
                                    break;
                                }
                                default:
                                    throw new IllegalArgumentException("unexpected type: " + type);
                            }
                        },
                        e -> Timber.e(e, "error while getting owned items")
                );
        //also check if user joined app vk group
        mPresenter.checkIfUserJoinedAppVkGroup();
    }

    /**
     * @return id of activity layout
     */
    protected abstract int getLayoutResId();

    /**
     * inject DI here
     */
    protected abstract void callInjections();

    /**
     * Override it to add menu or return 0 if you don't want it
     */
    protected abstract int getMenuResId();

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (getMenuResId() != 0) {
            getMenuInflater().inflate(getMenuResId(), menu);
        }
        return true;
    }

    /**
     * workaround from http://stackoverflow.com/a/30337653/3212712 to show menu icons
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(final View view, final Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    @SuppressLint("PrivateApi") final Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (final Exception e) {
                    Timber.e(e, "onMenuOpened...unable to set icons for overflow menu");
                }
            }

            final boolean nightModeIsOn = mMyPreferenceManager.isNightMode();
            final MenuItem themeMenuItem = menu.findItem(R.id.night_mode_item);
            if (themeMenuItem != null) {
                if (nightModeIsOn) {
                    themeMenuItem.setIcon(R.drawable.ic_brightness_low_white_24dp);
                    themeMenuItem.setTitle(R.string.day_mode);
                } else {
                    themeMenuItem.setIcon(R.drawable.ic_brightness_3_white_24dp);
                    themeMenuItem.setTitle(R.string.night_mode);
                }
            }

            for (int i = 0; i < menu.size(); i++) {
                final MenuItem item = menu.getItem(i);
                final Drawable icon = item.getIcon();
                if (icon != null) {
                    applyTint(icon);
                    item.setIcon(icon);
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    void applyTint(final Drawable icon) {
        icon.setColorFilter(new PorterDuffColorFilter(
                ContextCompat.getColor(this, R.color.material_blue_gray_50),
                PorterDuff.Mode.SRC_IN
        ));
    }

    @Override
    public void showError(final Throwable throwable) {
        Snackbar.make(mRoot, SystemUtils.coloredTextForSnackBar(this, throwable.getMessage()), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(final String message) {
        Timber.d("showMessage: %s", message);
        Snackbar.make(mRoot, SystemUtils.coloredTextForSnackBar(this, message), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(@StringRes final int message) {
        showMessage(getString(message));
    }

    @Override
    public void showMessageLong(final String message) {
        Snackbar.make(mRoot, SystemUtils.coloredTextForSnackBar(this, message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showMessageLong(@StringRes final int message) {
        showMessageLong(getString(message));
    }

    @Override
    public void showProgressDialog(final String title) {
        if (!isDestroyed() || !isFinishing()) {
            mDialogUtils.showProgressDialog(this, title);
        }
    }

    @Override
    public void showProgressDialog(@StringRes final int title) {
        if (!isDestroyed() || !isFinishing()) {
            mDialogUtils.showProgressDialog(this, getString(title));
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (!isDestroyed() || !isFinishing()) {
            mDialogUtils.dismissProgressDialog();
        }
    }

    @Override
    public void showNeedLoginPopup() {
        new MaterialDialog.Builder(this)
                .title(R.string.need_login)
                .content(R.string.need_login_content)
                .positiveText(R.string.authorize)
                .onPositive((dialog, which) -> showLoginProvidersPopup())
                .negativeText(android.R.string.cancel)
                .onNegative((dialog, which) -> dialog.dismiss())
                .build()
                .show();
    }

    @Override
    public void showOfferLoginPopup(final MaterialDialog.SingleButtonCallback cancelCallback) {
        if (!hasWindowFocus() || isDestroyed() || isFinishing()) {
            return;
        }
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_offer_login_to_gain_score_title)
                .content(R.string.dialog_offer_login_to_gain_score_content)
                .positiveText(R.string.authorize)
                .onPositive((dialog, which) -> showLoginProvidersPopup())
                .negativeText(android.R.string.cancel)
                .onNegative(cancelCallback)
                .build()
                .show();
    }

    @Override
    public void showOfferSubscriptionPopup() {
        if (!hasWindowFocus() || isDestroyed() || isFinishing()) {
            return;
        }
        Timber.d("showOfferSubscriptionPopup");
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_offer_subscription_title)
                .content(R.string.dialog_offer_subscription_content)
                .positiveText(R.string.yes_bliad)
                .onPositive((dialog, which) -> {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.Firebase.Analytics.EventParam.PLACE, Constants.Firebase.Analytics.StartScreen.AFTER_LEVEL_UP);
                    FirebaseAnalytics.getInstance(this).logEvent(Constants.Firebase.Analytics.EventName.SUBSCRIPTIONS_SHOWN, bundle);

                    SubscriptionsActivity.start(BaseActivity.this);
                })
                .negativeText(android.R.string.cancel)
                .onNegative((dialog, which) -> dialog.dismiss())
                .build()
                .show();
    }

    @Override
    public void showOfferFreeTrialSubscriptionPopup() {
        if (!hasWindowFocus() || isDestroyed() || isFinishing()) {
            return;
        }
        showProgressDialog(R.string.wait);
        mInAppHelper.getSubsListToBuyObservable(mInAppHelper.getFreeTrailSubsSkus())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        subscriptions -> {
                            dismissProgressDialog();
                            if (!subscriptions.isEmpty()) {
                                mDialogUtils.showFreeTrialSubscriptionOfferDialog(this, subscriptions);
                            }
                        },
                        e -> {
                            Timber.e(e);
                            dismissProgressDialog();
                            showError(e);
                        }
                );
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int i = item.getItemId();
        if (i == R.id.settings) {
            final BottomSheetDialogFragment settingsDF = SettingsBottomSheetDialogFragment.newInstance();
            settingsDF.show(getSupportFragmentManager(), settingsDF.getTag());
            return true;
        } else if (i == R.id.subscribe) {
            SubscriptionsActivity.start(this);

            final Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, StartScreen.MENU);
            FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            return true;
        } else if (i == R.id.night_mode_item) {
            mMyPreferenceManager.setIsNightMode(!mMyPreferenceManager.isNightMode());
            return true;
        } else if (i == R.id.text_size) {
            final BottomSheetDialogFragment fragmentDialogTextAppearance = TextSizeDialogFragment.newInstance(TextSizeDialogFragment.TextSizeType.ALL);
            fragmentDialogTextAppearance.show(getSupportFragmentManager(), TextSizeDialogFragment.TAG);
            return true;
        } else if (i == R.id.info) {
            final DialogFragment dialogFragment = NewVersionDialogFragment.newInstance(getString(R.string.app_info));
            dialogFragment.show(getFragmentManager(), NewVersionDialogFragment.TAG);
            return true;
        } else if (i == R.id.menuItemDownloadAll) {
            mDownloadAllChooser.showDownloadDialog(this);
            return true;
        } else if (i == R.id.faq) {
            mDialogUtils.showFaqDialog(this);
            return true;
        } else if (i == R.id.appLangVersions) {
            mDialogUtils.showAllAppLangVariantsDialog(this);
            return true;
        } else if (i == R.id.removeAds) {
            final BottomSheetDialogFragment subsDF = AdsSettingsBottomSheetDialogFragment.newInstance();
            subsDF.show(getSupportFragmentManager(), subsDF.getTag());
            return true;
        } else {
            Timber.wtf("unexpected id: %s", item.getItemId());
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MoPub.onRestart(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MoPub.onResume(this);

        if (!isAdsLoaded() && mMyPreferenceManager.isTimeToLoadAds()) {
            requestNewInterstitial();
        }
        //notify user about soon ads showing and offer him appodeal or subscription
        if (mMyPreferenceManager.isTimeToNotifyAboutSoonAdsShowing()) {
            Timber.d("isTime to notify about ads");
            showSnackBarWithAction(Constants.Firebase.CallToActionReason.ADS_WILL_SHOWN_SOON);
            mMyPreferenceManager.setOfferAlreadyShown(true);
        } else {
            Timber.wtf("is NOT time to notify about ads");
        }

        if (mMyPreferenceManager.isTimeToValidateSubscriptions()) {
            updateOwnedMarketItems();
        }

        setUpBanner();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        mInAppHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MoPub.onPause(this);
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        //ignore facebook analytics log spam
        if (key.startsWith("com.facebook")) {
            return;
        }
        Timber.d("onSharedPreferenceChanged with key: %s", key);
        switch (key) {
            case MyPreferenceManager.Keys.NIGHT_MODE:
                recreate();
                break;
            case MyPreferenceManager.Keys.ADS_BANNER_IN_ARTICLE:
            case MyPreferenceManager.Keys.ADS_BANNER_IN_ARTICLES_LISTS:
            case MyPreferenceManager.Keys.TIME_FOR_WHICH_BANNERS_DISABLED:
                //check if there is banner in layout
                setUpBanner();
                break;
            case MyPreferenceManager.Keys.HAS_SUBSCRIPTION:
            case MyPreferenceManager.Keys.HAS_NO_ADS_SUBSCRIPTION:
                FirebaseAnalytics.getInstance(BaseActivity.this).setUserProperty(
                        UserPropertyKey.SUBSCRIBED,
                        String.valueOf(mMyPreferenceManager.isHasAnySubscription())
                );
                break;
            default:
                break;
        }
    }

    @Override
    public void showFreeAdsDisablePopup() {
        SubscriptionsActivity.start(this, SubscriptionsActivity.TYPE_DISABLE_ADS_FOR_FREE);
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
        if (mopubBanner != null) {
            mopubBanner.destroy();
        }
        mMopubInterstitialAd.destroy();
        MoPub.onDestroy(this);

        mInAppHelper.onActivityDestroy(this);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Timber.d("onActivityResult called in BaseActivity");

        if (!presenter.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void updateUser(final User user) {
        //nothing to do here
    }

    private void initAndUpdateRemoteConfig() {
        Timber.d("initAndUpdateRemoteConfig");
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();

        // Create Remote Config Setting to enable developer mode.
        // Fetching configs from the server is normally limited to 5 requests per hour.
        // Enabling developer mode allows many more requests to be made per hour, so developers
        // can test different config values during development.
        //noinspection ConstantConditions
        final FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.FLAVOR_mode.equals("dev"))
                .setFetchTimeoutInSeconds(Constants.Firebase.RemoteConfigKeys.CACHE_EXPIRATION_SECONDS)
                .setMinimumFetchIntervalInSeconds(Period.minutes(60).toStandardSeconds().getSeconds())
                .build();
        remoteConfig.setConfigSettings(configSettings);

        // Set default Remote Config values. In general you should have in app defaults for all
        // values that you may configure using Remote Config later on. The idea is that you
        // use the in app defaults and when you need to adjust those defaults, you set an updated
        // value in the App Manager console. Then the next time you application fetches from the
        // server, the updated value will be used. You can set defaults via an xml file like done
        // here or you can set defaults inline by using one of the other setDefaults methods.S
        // [START set_default_values]
        //this is not working for some reason
//        remoteConfig.setDefaults(R.xml.remote_config_defaults);
        //this woks
        try {
            final Map<String, Object> defaults = new HashMap<>();
            final RemoteConfigJsonModel remoteConfigJsonModel = new Gson().fromJson(
                    StorageUtils.readFromAssets(this, mConstantValues.getAppLang() + ".json"),
                    RemoteConfigJsonModel.class
            );
            for (final Entry entry : remoteConfigJsonModel.getDefaultsMap().getEntry()) {
                defaults.put(entry.getKey(), entry.getValue());
            }
            remoteConfig.setDefaults(defaults);
        } catch (final IOException e) {
            Timber.e(e);
        }

        //comment this if you want to use local data
        remoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Timber.d("Fetch succeeded. Updated: %s", task.getResult());
            } else {
                Timber.e("Fetch Failed");
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        Timber.e("onConnectionFailed: %s", connectionResult);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MoPub.onBackPressed(this);
    }

    public void startArticleActivity(final List<String> urls, final int position) {
        final Intent intent = new Intent(this, getArticleActivityClass());
        intent.putExtra(EXTRA_ARTICLES_URLS_LIST, new ArrayList<>(urls));
        intent.putExtra(EXTRA_POSITION, position);

        if (isTimeToShowAds()) {
            if (isAdsLoaded()) {
                showInterstitial(new InterstitialAdListener() {
                    @Override
                    public void onInterstitialClosed(@NotNull MyPreferenceManager preferences) {
                        intent.putExtra(EXTRA_SHOW_DISABLE_ADS, true);
                        startActivity(intent);
                    }
                }, true);
                return;
            } else {
                Timber.d("Ads not loaded yet");
            }
        } else {
            Timber.d("it's not time to showInterstitial ads");
        }
        startActivity(intent);
    }

    public void startArticleActivity(final String url) {
        startArticleActivity(Collections.singletonList(url), 0);
    }

    public void startMaterialsActivity() {
        final Intent intent = new Intent(BaseActivity.this, getMaterialsActivityClass());

        if (isTimeToShowAds()) {
            if (isAdsLoaded()) {
                showInterstitial(new InterstitialAdListener() {
                    @Override
                    public void onInterstitialClosed(@NotNull MyPreferenceManager preferences) {
                        intent.putExtra(EXTRA_SHOW_DISABLE_ADS, true);
                        startActivity(intent);
                    }
                }, true);
                return;
            } else {
                Timber.d("Ads not loaded yet");
            }
        } else {
            Timber.d("it's not time to showInterstitial ads");
        }
        startActivity(intent);
    }

    public void startTagsSearchActivity(final List<ArticleTag> tagList) {
        final Intent intent = new Intent(BaseActivity.this, getTagsSearchActivityClass());
        intent.putExtra(EXTRA_TAGS, new ArrayList<>(ArticleTag.getStringsFromTags(tagList)));

        if (isTimeToShowAds()) {
            if (isAdsLoaded()) {
                showInterstitial(new InterstitialAdListener() {
                    @Override
                    public void onInterstitialClosed(@NotNull MyPreferenceManager preferences) {
                        intent.putExtra(EXTRA_SHOW_DISABLE_ADS, true);
                        startActivity(intent);
                    }
                }, true);
                return;
            } else {
                Timber.d("Ads not loaded yet");
            }
        } else {
            Timber.d("it's not time to showInterstitial ads");
        }
        startActivity(intent);
    }

    public void startTagsSearchActivity() {
        final Intent intent = new Intent(this, getTagsSearchActivityClass());

        if (isTimeToShowAds()) {
            if (isAdsLoaded()) {
                showInterstitial(new InterstitialAdListener() {
                    @Override
                    public void onInterstitialClosed(@NotNull MyPreferenceManager preferences) {
                        intent.putExtra(EXTRA_SHOW_DISABLE_ADS, true);
                        startActivity(intent);
                    }
                }, true);
                return;
            } else {
                Timber.d("Ads not loaded yet");
            }
        } else {
            Timber.d("it's not time to showInterstitial ads");
        }
        startActivity(intent);
    }

    protected Class getTagsSearchActivityClass() {
        return TagSearchActivity.class;
    }

    protected Class getMaterialsActivityClass() {
        return MaterialsActivity.class;
    }

    protected Class getArticleActivityClass() {
        return ArticleActivity.class;
    }
}
