package com.example.eduhub.di;

import android.content.Context;

import com.example.eduhub.database.DBHelper;
import com.example.eduhub.network.session.UserSessionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public static DBHelper provideDBHelper(@ApplicationContext Context context) {
        return DBHelper.getInstance(context);
    }

    @Provides
    @Singleton
    public static UserSessionManager provideUserSessionManager(@ApplicationContext Context context) {
        return UserSessionManager.getInstance(context);
    }
}
