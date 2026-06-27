package com.example.eduhub.network;

import com.example.eduhub.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = ApiConfig.BASE_URL;
    private static RetrofitClient instance;
    private static String accessToken;
    private static String refreshToken;
    private static final Object tokenLock = new Object();

    private final Retrofit retrofit;
    private final ApiService apiService;

    public static void setTokens(String access, String refresh) {
        synchronized (tokenLock) {
            accessToken = access;
            refreshToken = refresh;
        }
    }

    public static String getAccessToken() {
        synchronized (tokenLock) {
            return accessToken;
        }
    }

    public static String getRefreshToken() {
        synchronized (tokenLock) {
            return refreshToken;
        }
    }

    public static void clearTokens() {
        synchronized (tokenLock) {
            accessToken = null;
            refreshToken = null;
        }
    }

    private RetrofitClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

        logging.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BASIC
                : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(new AuthInterceptor())
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)

                .connectionPool(new ConnectionPool(8, 5, TimeUnit.MINUTES))
                .retryOnConnectionFailure(true)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }

    private static class AuthInterceptor implements Interceptor {
        private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
        private static final Gson gson = new Gson();

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String path = request.url().encodedPath();

            boolean isAuthEndpoint = path.contains("/auth/login")
                    || path.contains("/auth/refresh")
                    || path.contains("/auth/logout");

            String token = getAccessToken();
            if (token != null && !isAuthEndpoint) {
                request = request.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .build();
            }

            Response response = chain.proceed(request);

            if (response.code() == 401 && !isAuthEndpoint) {
                String refreshTok = getRefreshToken();
                if (refreshTok != null) {
                    synchronized (tokenLock) {
                        String currentRefresh = getRefreshToken();
                        if (currentRefresh == null || !currentRefresh.equals(refreshTok)) {
                            response.close();
                            String newToken = getAccessToken();
                            if (newToken != null) {
                                Request retry = request.newBuilder()
                                        .header("Authorization", "Bearer " + newToken)
                                        .build();
                                return chain.proceed(retry);
                            }
                            return response;
                        }

                        Response refreshResponse = doRefresh(refreshTok);
                        if (refreshResponse != null && refreshResponse.isSuccessful()) {
                            String body = refreshResponse.body() != null ? refreshResponse.body().string() : null;
                            refreshResponse.close();

                            if (body != null) {
                                JsonObject json = gson.fromJson(body, JsonObject.class);
                                String newAccess = json.has("token") ? json.get("token").getAsString() : null;
                                String newRefresh = json.has("refreshToken") ? json.get("refreshToken").getAsString() : null;
                                if (newAccess != null) {
                                    setTokens(newAccess, newRefresh);
                                    com.example.eduhub.EduHubApp app = com.example.eduhub.EduHubApp.getInstance();
                                    if (app != null) {
                                        com.example.eduhub.network.session.UserSessionManager
                                                .getInstance(app).saveToken(newAccess);
                                        if (newRefresh != null) {
                                            com.example.eduhub.network.session.UserSessionManager
                                                    .getInstance(app).saveRefreshToken(newRefresh);
                                        }
                                    }
                                }
                            }

                            response.close();
                            String refreshedToken = getAccessToken();
                            if (refreshedToken != null) {
                                Request retry = request.newBuilder()
                                        .header("Authorization", "Bearer " + refreshedToken)
                                        .build();
                                return chain.proceed(retry);
                            }
                        } else {
                            clearTokens();
                        }
                    }
                } else {
                    clearTokens();
                }
            }

            return response;
        }

        private Response doRefresh(String refreshTok) {
            try {
                OkHttpClient cleanClient = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                JsonObject body = new JsonObject();
                body.addProperty("refreshToken", refreshTok);

                Request refreshRequest = new Request.Builder()
                        .url(BASE_URL + "auth/refresh")
                        .post(RequestBody.create(body.toString(), JSON))
                        .build();

                return cleanClient.newCall(refreshRequest).execute();
            } catch (IOException e) {
                return null;
            }
        }
    }
}
