package ru.dante.scpfoundation.mvp.presenter;

import android.support.annotation.NonNull;

import ru.dante.scpfoundation.mvp.contract.ObjectsJpArticles;
import ru.kuchanov.scpcore.ConstantValues;
import ru.kuchanov.scpcore.api.ApiClient;
import ru.kuchanov.scpcore.db.DbProviderFactory;
import ru.kuchanov.scpcore.db.model.Article;
import ru.kuchanov.scpcore.manager.MyPreferenceManager;
import ru.kuchanov.scpcore.monetization.util.playmarket.InAppHelper;
import ru.kuchanov.scpcore.mvp.presenter.articleslists.BaseObjectsArticlesPresenter;

public class ObjectsJpArticlesPresenter
        extends BaseObjectsArticlesPresenter<ObjectsJpArticles.View>
        implements ObjectsJpArticles.Presenter {

    public ObjectsJpArticlesPresenter(
            final MyPreferenceManager myPreferencesManager,
            final DbProviderFactory dbProviderFactory,
            final ApiClient apiClient,
            @NonNull final ConstantValues constantValues,
            final InAppHelper inAppHelper
    ) {
        super(myPreferencesManager, dbProviderFactory, apiClient, constantValues, inAppHelper);
    }

    @Override
    protected String getObjectsInDbFieldName() {
        return Article.FIELD_IS_IN_OBJECTS_JP;
    }

    @Override
    protected String getObjectsLink() {
        return mConstantValues.getObjectsJp();
    }
}