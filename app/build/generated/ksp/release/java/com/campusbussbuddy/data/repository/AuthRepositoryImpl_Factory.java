package com.campusbussbuddy.data.repository;

import com.campusbussbuddy.data.remote.FirebaseService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<FirebaseService> firebaseServiceProvider;

  public AuthRepositoryImpl_Factory(Provider<FirebaseService> firebaseServiceProvider) {
    this.firebaseServiceProvider = firebaseServiceProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(firebaseServiceProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(
      Provider<FirebaseService> firebaseServiceProvider) {
    return new AuthRepositoryImpl_Factory(firebaseServiceProvider);
  }

  public static AuthRepositoryImpl newInstance(FirebaseService firebaseService) {
    return new AuthRepositoryImpl(firebaseService);
  }
}
