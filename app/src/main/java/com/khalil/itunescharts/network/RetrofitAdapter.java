package com.khalil.itunescharts.network;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.khalil.itunescharts.entities.Music;
import com.khalil.itunescharts.entities.MusicChartsList;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 5/17/17.
 */

public final class RetrofitAdapter implements Interceptor {

    private Retrofit retrofit;
    private ServiceProtocol serviceProtocol;

    public RetrofitAdapter(ServiceProtocol serviceProtocol) {
        this.serviceProtocol = serviceProtocol;
        initialize();
    }

    private void initialize() {

        if(retrofit != null) {
            return;
        }
        retrofit = new Retrofit.Builder()
                .baseUrl(serviceProtocol.getAPIURL())
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .addCallAdapterFactory(new ServiceCallAdapterFactory(serviceProtocol))
                .build();
    }

    private Gson getGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
                .registerTypeAdapter(MusicChartsList.class, new RSSFeedDeserializer<Music>())
                .create();
    }

    private OkHttpClient getOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient();
        return okHttpClient.newBuilder()
                .addInterceptor(this)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public <S> S loadService(Class<S> serviceClass) {
        if(retrofit == null) {
            initialize();
        }
        return retrofit.create(serviceClass);
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Response response;
        try {
            response = chain.proceed (modifyRequest(request));
        }
        catch (ProtocolException e) {
            response = new Response.Builder ().request (request).code (204).protocol (Protocol.HTTP_1_1).build ();
        }

        return response;
    }

    private Request modifyRequest(Request request) {
        return serviceProtocol.addHeaders(request.newBuilder())
                .build();
    }
}
