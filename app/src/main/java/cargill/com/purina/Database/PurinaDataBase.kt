package cargill.com.purina.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.dashboard.Model.ProductDetails.ImagesTypeConverter
import cargill.com.purina.dashboard.Model.Products.Product
import cargill.com.purina.splash.Model.Country

@Database(entities = arrayOf(Country::class, Animal::class, Product::class, cargill.com.purina.dashboard.Model.ProductDetails.Product::class), version = 1)
@TypeConverters(ImagesTypeConverter::class)
abstract class PurinaDataBase: RoomDatabase() {

    abstract val dao: PurinaDAO

    companion object {
        @Volatile private var instance: PurinaDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            PurinaDataBase::class.java,
            "purinaDatabase"
        ).allowMainThreadQueries().build()
    }
}