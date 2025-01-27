package id.ardev.keretakita.model.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.ardev.keretakita.model.firebase.FirebaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseClient() : FirebaseClient {
        return FirebaseClient()
    }
}