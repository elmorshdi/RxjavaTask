package com.elmorshdi.internTask.domain.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.elmorshdi.internTask.datasource.roomdatabase.MyDataBase
import com.elmorshdi.internTask.helper.Constant.APP_SETTINGS
import com.elmorshdi.internTask.helper.Constant.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            APP_SETTINGS,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


    @Singleton
    @Provides
    fun provideEditor(sharedPref: SharedPreferences): SharedPreferences.Editor = sharedPref.edit()


    @Singleton
    @Provides
    fun provideYourDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        MyDataBase::class.java,
        DB_NAME
    ).build()

    @Singleton
    @Provides
    fun provideYourDao(db: MyDataBase) = db.getDao()
}

