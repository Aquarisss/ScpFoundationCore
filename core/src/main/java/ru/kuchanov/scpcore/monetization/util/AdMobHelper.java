package ru.kuchanov.scpcore.monetization.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import com.google.android.gms.ads.AdRequest;

import ru.kuchanov.scpcore.BuildConfig;
import ru.kuchanov.scpcore.util.SystemUtils;

/**
 * Created by mohax on 03.08.2017.
 * <p>
 * for ScpCore
 */
public class AdMobHelper {

    public static AdRequest buildAdRequest(Context context) {
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        if (BuildConfig.FLAVOR.equals("dev")) {
            @SuppressLint("HardwareIds")
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            String deviceId;
            deviceId = SystemUtils.MD5(androidId);
            if (deviceId != null) {
                deviceId = deviceId.toUpperCase();
                adRequestBuilder.addTestDevice(deviceId);
            }
            adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        }
        return adRequestBuilder.build();
    }
}