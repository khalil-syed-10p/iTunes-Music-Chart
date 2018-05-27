package com.khalil.itunescharts.network;

import android.util.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * Created on 27/01/2016.
 */
class ServiceCallAdapterFactory extends CallAdapter.Factory {

    private ServiceProtocol serviceProtocol;

    ServiceCallAdapterFactory(ServiceProtocol serviceProtocol) {

        this.serviceProtocol = serviceProtocol;
    }

	@Override
	public CallAdapter<?, ?> get (Type returnType, Annotation[] annotations, Retrofit retrofit) {

        Log.d(ServiceCallAdapterFactory.class.getName(), "get Method");
		if (getRawType (returnType) != ServiceCall.class) {
			return null;
		}
		if (!(returnType instanceof ParameterizedType)) {
			throw new IllegalStateException ("ServiceCall must have generic type (e.g., ServiceCall<ResponseBody>)");
		}
		Type responseType = getParameterUpperBound (0, (ParameterizedType) returnType);
        Executor callbackExecutor = retrofit.callbackExecutor();
		return new CustomCallAdapter(responseType, callbackExecutor);
	}

    private final class CustomCallAdapter<R> implements CallAdapter<R, ServiceCall<R>> {
        private final Type responseType;
        private final Executor callbackExecutor;

        CustomCallAdapter(Type responseType, Executor callbackExecutor) {
            this.responseType = responseType;
            this.callbackExecutor = callbackExecutor;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public ServiceCall<R> adapt(Call<R> call) {
            return new ServiceCallAdapter<>(call, callbackExecutor, serviceProtocol);
        }

    }
}
