package ru.kuchanov.scpcore.mvp.presenter.articleslists;

import android.util.Pair;

import java.util.List;

import io.realm.RealmResults;
import io.realm.Sort;
import ru.kuchanov.scpcore.api.ApiClient;
import ru.kuchanov.scpcore.db.DbProviderFactory;
import ru.kuchanov.scpcore.db.model.Article;
import ru.kuchanov.scpcore.manager.MyPreferenceManager;
import ru.kuchanov.scpcore.monetization.util.playmarket.InAppHelper;
import ru.kuchanov.scpcore.mvp.contract.articleslists.RecentArticlesMvp;
import rx.Observable;
import rx.Single;

public class MostRecentArticlesPresenter
        extends BaseListArticlesPresenter<RecentArticlesMvp.View>
        implements RecentArticlesMvp.Presenter {

    public MostRecentArticlesPresenter(
            final MyPreferenceManager myPreferencesManager,
            final DbProviderFactory dbProviderFactory,
            final ApiClient apiClient,
            final InAppHelper inAppHelper
    ) {
        super(myPreferencesManager, dbProviderFactory, apiClient, inAppHelper);
    }

    @Override
    protected Observable<RealmResults<Article>> getDbObservable() {
        return mDbProviderFactory.getDbProvider().getArticlesSortedAsync(Article.FIELD_IS_IN_RECENT, Sort.ASCENDING);
    }

    @Override
    protected Single<List<Article>> getApiObservable(final int offset) {
        return mApiClient.getRecentArticlesForOffset(offset);
    }

    @Override
    protected Single<Pair<Integer, Integer>> getSaveToDbObservable(final List<Article> data, final int offset) {
        return mDbProviderFactory.getDbProvider().saveRecentArticlesList(data, offset);
    }
}