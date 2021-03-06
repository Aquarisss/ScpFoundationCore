package ru.kuchanov.scpcore.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.kuchanov.scpcore.db.DbProviderFactory;
import ru.kuchanov.scpcore.di.module.AppModule;
import ru.kuchanov.scpcore.di.module.HelpersModule;
import ru.kuchanov.scpcore.di.module.NetModule;
import ru.kuchanov.scpcore.di.module.NotificationModule;
import ru.kuchanov.scpcore.di.module.PresentersModule;
import ru.kuchanov.scpcore.di.module.StorageModule;
import ru.kuchanov.scpcore.monetization.util.admob.AdmobInterstitialAdListener;
import ru.kuchanov.scpcore.monetization.util.appodeal.MyAppodealInterstitialCallbacks;
import ru.kuchanov.scpcore.monetization.util.mopub.MopubInterstitialAdListener;
import ru.kuchanov.scpcore.monetization.util.playmarket.InAppHelper;
import ru.kuchanov.scpcore.receivers.ReceiverBoot;
import ru.kuchanov.scpcore.receivers.ReceiverTimer;
import ru.kuchanov.scpcore.service.DownloadAllServiceDefault;
import ru.kuchanov.scpcore.service.MyFirebaseMessagingService;
import ru.kuchanov.scpcore.ui.activity.ArticleActivity;
import ru.kuchanov.scpcore.ui.activity.GalleryActivity;
import ru.kuchanov.scpcore.ui.activity.MainActivity;
import ru.kuchanov.scpcore.ui.activity.MaterialsActivity;
import ru.kuchanov.scpcore.ui.activity.SplashActivity;
import ru.kuchanov.scpcore.ui.activity.SubscriptionsActivity;
import ru.kuchanov.scpcore.ui.activity.TagSearchActivity;
import ru.kuchanov.scpcore.ui.adapter.ArticleAdapter;
import ru.kuchanov.scpcore.ui.adapter.ArticlesListAdapter;
import ru.kuchanov.scpcore.ui.adapter.ImagesPagerAdapter;
import ru.kuchanov.scpcore.ui.adapter.SettingsSpinnerAdapter;
import ru.kuchanov.scpcore.ui.adapter.SettingsSpinnerCardDesignAdapter;
import ru.kuchanov.scpcore.ui.dialog.AdsSettingsBottomSheetDialogFragment;
import ru.kuchanov.scpcore.ui.dialog.CC3LicenseDialogFragment;
import ru.kuchanov.scpcore.ui.dialog.NewVersionDialogFragment;
import ru.kuchanov.scpcore.ui.dialog.SettingsBottomSheetDialogFragment;
import ru.kuchanov.scpcore.ui.dialog.TextSizeDialogFragment;
import ru.kuchanov.scpcore.ui.fragment.article.ArticleFragment;
import ru.kuchanov.scpcore.ui.fragment.articleslists.FavoriteArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.articleslists.Objects1ArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.articleslists.Objects2ArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.articleslists.Objects3ArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.articleslists.Objects4ArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.articleslists.Objects5ArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.articleslists.ObjectsRuArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.articleslists.OfflineArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.articleslists.RatedArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.articleslists.ReadArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.articleslists.RecentArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.materials.MaterialsArchiveFragment;
import ru.kuchanov.scpcore.ui.fragment.materials.MaterialsExperimentsFragment;
import ru.kuchanov.scpcore.ui.fragment.materials.MaterialsIncidentsFragment;
import ru.kuchanov.scpcore.ui.fragment.materials.MaterialsInterviewsFragment;
import ru.kuchanov.scpcore.ui.fragment.materials.MaterialsJokesFragment;
import ru.kuchanov.scpcore.ui.fragment.materials.MaterialsOtherFragment;
import ru.kuchanov.scpcore.ui.fragment.monetization.FreeAdsDisableActionsFragment;
import ru.kuchanov.scpcore.ui.fragment.monetization.LeaderboardFragment;
import ru.kuchanov.scpcore.ui.fragment.monetization.ReadHistoryFragment;
import ru.kuchanov.scpcore.ui.fragment.monetization.SubscriptionsFragment;
import ru.kuchanov.scpcore.ui.fragment.search.SiteSearchArticlesFragment;
import ru.kuchanov.scpcore.ui.fragment.tags.TagsSearchFragment;
import ru.kuchanov.scpcore.ui.fragment.tags.TagsSearchResultsArticlesFragment;
import ru.kuchanov.scpcore.ui.holder.article.ArticleImageHolder;
import ru.kuchanov.scpcore.ui.holder.article.ArticleSpoilerHolder;
import ru.kuchanov.scpcore.ui.holder.article.ArticleTableHolder;
import ru.kuchanov.scpcore.ui.holder.article.ArticleTabsHolder;
import ru.kuchanov.scpcore.ui.holder.article.ArticleTagsHolder;
import ru.kuchanov.scpcore.ui.holder.article.ArticleTextHolder;
import ru.kuchanov.scpcore.ui.holder.article.ArticleTitleHolder;
import ru.kuchanov.scpcore.ui.holder.article.NativeAdsArticleListHolder;
import ru.kuchanov.scpcore.ui.holder.articlelist.HolderMin;
import ru.kuchanov.scpcore.ui.util.URLImageParser;

@Singleton
@Component(modules = {
        AppModule.class,
        StorageModule.class,
        PresentersModule.class,
        NetModule.class,
        NotificationModule.class,
        HelpersModule.class
})
public interface AppComponent {

    void inject(ArticleActivity activity);

    void inject(MaterialsActivity activity);

    void inject(MainActivity activity);

    void inject(GalleryActivity activity);

    void inject(TagSearchActivity activity);

    void inject(SplashActivity activity);

    void inject(SubscriptionsActivity activity);

    void inject(ArticleFragment fragment);

    void inject(RecentArticlesFragment fragment);

    void inject(RatedArticlesFragment fragment);

    void inject(FavoriteArticlesFragment fragment);

    void inject(OfflineArticlesFragment fragment);

    void inject(Objects1ArticlesFragment fragment);

    void inject(Objects2ArticlesFragment fragment);

    void inject(Objects3ArticlesFragment fragment);

    void inject(ObjectsRuArticlesFragment fragment);

    void inject(SiteSearchArticlesFragment fragment);

    void inject(MaterialsExperimentsFragment fragment);

    void inject(MaterialsInterviewsFragment fragment);

    void inject(MaterialsIncidentsFragment fragment);

    void inject(MaterialsOtherFragment fragment);

    void inject(MaterialsArchiveFragment fragment);

    void inject(MaterialsJokesFragment fragment);

    void inject(Objects4ArticlesFragment fragment);

    void inject(Objects5ArticlesFragment fragment);

    void inject(TagsSearchFragment fragment);

    void inject(TagsSearchResultsArticlesFragment fragment);

    void inject(ReadArticlesFragment fragment);

    void inject(SubscriptionsFragment fragment);

    void inject(FreeAdsDisableActionsFragment fragment);

    void inject(LeaderboardFragment leaderboardFragment);

    void inject(ReadHistoryFragment readHistoryFragment);

    void inject(TextSizeDialogFragment dialogFragment);

    void inject(NewVersionDialogFragment dialogFragment);

    void inject(SettingsBottomSheetDialogFragment dialogFragment);

    void inject(CC3LicenseDialogFragment dialogFragment);

    void inject(ArticlesListAdapter adapter);

    void inject(ArticleAdapter adapter);

    void inject(SettingsSpinnerAdapter adapter);

    void inject(SettingsSpinnerCardDesignAdapter adapter);

    void inject(ImagesPagerAdapter adapter);

    void inject(ArticleImageHolder holder);

    void inject(ArticleTagsHolder holder);

    void inject(ArticleTitleHolder holder);

    void inject(ArticleTextHolder holder);

    void inject(ArticleSpoilerHolder holder);

    void inject(HolderMin holder);

    void inject(ArticleTableHolder holder);

    void inject(NativeAdsArticleListHolder holder);

    void inject(ArticleTabsHolder holder);

    void inject(ReceiverTimer receiver);

    void inject(ReceiverBoot receiver);

    void inject(AdmobInterstitialAdListener adListener);

    void inject(MopubInterstitialAdListener mopubInterstitialAdListener);

    void inject(MyAppodealInterstitialCallbacks callbacks);

    void inject(DownloadAllServiceDefault service);

    void inject(MyFirebaseMessagingService myFirebaseMessagingService);

    void inject(URLImageParser urlImageParser);

    DbProviderFactory getDbProviderFactory();

    void inject(AdsSettingsBottomSheetDialogFragment dialogFragment);

    void inject(InAppHelper inAppHelper);
}