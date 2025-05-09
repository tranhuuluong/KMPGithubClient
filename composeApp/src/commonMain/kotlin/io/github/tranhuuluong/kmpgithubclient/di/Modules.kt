package io.github.tranhuuluong.kmpgithubclient.di

import androidx.lifecycle.SavedStateHandle
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.tranhuuluong.kmpgithubclient.core.HttpClientFactory
import io.github.tranhuuluong.kmpgithubclient.core.util.TimeProvider
import io.github.tranhuuluong.kmpgithubclient.core.util.TimeProviderImpl
import io.github.tranhuuluong.kmpgithubclient.user.data.OfflineFirstUserRepository
import io.github.tranhuuluong.kmpgithubclient.user.data.local.DatabaseBuilderFactory
import io.github.tranhuuluong.kmpgithubclient.user.data.local.GhcDatabase
import io.github.tranhuuluong.kmpgithubclient.user.data.local.UserDao
import io.github.tranhuuluong.kmpgithubclient.user.data.remote.KtorUserRemoteDataSource
import io.github.tranhuuluong.kmpgithubclient.user.data.remote.UserRemoteDataSource
import io.github.tranhuuluong.kmpgithubclient.user.domain.UserRepository
import io.github.tranhuuluong.kmpgithubclient.user.domain.use_case.GetUserDetailUseCase
import io.github.tranhuuluong.kmpgithubclient.user.domain.use_case.GetUserListUseCase
import io.github.tranhuuluong.kmpgithubclient.user.domain.use_case.LoadMoreUsersUseCase
import io.github.tranhuuluong.kmpgithubclient.user.presentation.user_detail.UserDetailViewModel
import io.github.tranhuuluong.kmpgithubclient.user.presentation.user_list.UserListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    singleOf(::OfflineFirstUserRepository).bind<UserRepository>()
    singleOf(::KtorUserRemoteDataSource).bind<UserRemoteDataSource>()
    singleOf(::TimeProviderImpl).bind<TimeProvider>()
    single<GhcDatabase> {
        get<DatabaseBuilderFactory>()
            .create()
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single<UserDao> { get<GhcDatabase>().userDao() }
    factoryOf(::GetUserListUseCase)
    factoryOf(::LoadMoreUsersUseCase)
    factoryOf(::GetUserDetailUseCase)
    viewModelOf(::UserListViewModel)
    viewModel { (saveStateHandle: SavedStateHandle) ->
        UserDetailViewModel(
            savedStateHandle = saveStateHandle,
            getUserDetailUseCase = get(),
            timeProvider = get()
        )
    }
}