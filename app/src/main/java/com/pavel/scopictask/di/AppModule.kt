package com.pavel.scopictask.di

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pavel.scopictask.data.network.datastore.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDefaultDispatcher() = Dispatchers.IO

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideOneTapClient(
        @ApplicationContext
        context: Context
    ) = Identity.getSignInClient(context)

    @Provides
    @Singleton
    fun provideFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context) = DataStoreManager(context)
}