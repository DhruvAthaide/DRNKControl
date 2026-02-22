package com.dhruvathaide.drnkcontrol.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGhostDatabase(@ApplicationContext context: Context): GhostDatabase {
        // NOTE: In a real app, the passphrase should be securely generated and stored in Android Keystore.
        // For demonstration, a placeholder is used here.
        val passphrase = "Secure_DRNK_Placeholder_Passphrase".toByteArray()
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            context,
            GhostDatabase::class.java,
            "ghost_database.db"
        )
        .openHelperFactory(factory)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideGhostMessageDao(database: GhostDatabase): GhostMessageDao {
        return database.ghostMessageDao()
    }
}
