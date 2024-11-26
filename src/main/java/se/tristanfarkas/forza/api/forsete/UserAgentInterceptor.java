package se.tristanfarkas.forza.api.forsete;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class UserAgentInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader("User-Agent", "Forza/1.0.0")
                .build();
        return chain.proceed(request);
    }
}
