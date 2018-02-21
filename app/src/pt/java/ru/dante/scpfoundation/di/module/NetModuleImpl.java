package ru.dante.scpfoundation.di.module;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import javax.inject.Named;

import dagger.Module;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import ru.dante.scpfoundation.ConstantValuesImpl;
import ru.dante.scpfoundation.api.ApiClientImpl;
import ru.kuchanov.scp.downloads.ConstantValues;
import ru.kuchanov.scpcore.api.ApiClient;
import ru.kuchanov.scpcore.di.module.NetModule;
import ru.kuchanov.scpcore.manager.MyPreferenceManager;

/**
 * Created by mohax on 13.07.2017.
 * <p>
 * for ScpFoundationRu
 */
@Module(includes = NetModule.class)
public class NetModuleImpl extends NetModule {

    @Override
    protected ApiClient getApiClient(
            @NonNull final OkHttpClient okHttpClient,
            @Named("vps") @NonNull final Retrofit vpsRetrofit,
            @Named("scp") @NonNull final Retrofit scpRetrofit,
            @NonNull final MyPreferenceManager preferencesManager,
            @NonNull final Gson gson,
            @NonNull final ConstantValues constantValues
            ) {
        return new ApiClientImpl(okHttpClient, vpsRetrofit, scpRetrofit, preferencesManager, gson, constantValues);
    }

    @Override
    protected ConstantValues getConstants() {
        return new ConstantValuesImpl();
    }
}