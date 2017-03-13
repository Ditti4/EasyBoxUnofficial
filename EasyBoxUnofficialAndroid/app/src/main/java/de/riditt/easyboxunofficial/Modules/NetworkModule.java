package de.riditt.easyboxunofficial.Modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.riditt.easyboxunofficial.Api.EasyBoxApi;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@Module
public class NetworkModule {
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl fullUrl, List<Cookie> cookies) {
                        cookieStore.put(fullUrl.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl fullUrl) {
                        List<Cookie> cookies = cookieStore.get(fullUrl.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .addInterceptor(logging)
                .build();
    }

    @Provides
    @Singleton
    EasyBoxApi provideEasyBoxApi(OkHttpClient client) {
        return new EasyBoxApi(client);
    }
}
