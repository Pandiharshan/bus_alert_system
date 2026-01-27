package com.campusbussbuddy.data.repository;

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
  private final Provider<FirebaseAuthRepository> firebaseAuthRepositoryProvider;

  public AuthRepositoryImpl_Factory(
      Provider<FirebaseAuthRepository> firebaseAuthRepositoryProvider) {
    this.firebaseAuthRepositoryProvider = firebaseAuthRepositoryProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(firebaseAuthRepositoryProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(
      Provider<FirebaseAuthRepository> firebaseAuthRepositoryProvider) {
    return new AuthRepositoryImpl_Factory(firebaseAuthRepositoryProvider);
  }

  public static AuthRepositoryImpl newInstance(FirebaseAuthRepository firebaseAuthRepository) {
    return new AuthRepositoryImpl(firebaseAuthRepository);
  }
}
