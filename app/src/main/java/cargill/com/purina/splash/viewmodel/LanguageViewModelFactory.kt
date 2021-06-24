package cargill.com.purina.splash.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cargill.com.purina.splash.Repository.LanguageRepository
import java.lang.IllegalArgumentException

class LanguageViewModelFactory(private val repository: LanguageRepository, val ctx: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LanguageViewModel::class.java)){
            return LanguageViewModel(repository, ctx) as T
        }
        throw IllegalArgumentException("Unknow view model class")
    }
}