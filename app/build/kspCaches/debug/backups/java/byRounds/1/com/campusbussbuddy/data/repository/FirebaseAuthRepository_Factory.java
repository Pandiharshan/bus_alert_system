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
public final class FirebaseAuthRepository_Factory implements Factory<FirebaseAuthRepository> {
  private final Provider<FirebaseService> firebaseServiceProvider;

  public FirebaseAuthRepository_Factory(Provider<FirebaseService> firebaseServiceProvider) {
    this.firebaseServiceProvider = firebaseServiceProvider;
  }

  @Override
  public FirebaseAuthRepository get() {
    return newInstance(firebaseServiceProvider.get());
  }

  public static FirebaseAuthRepository_Factory create(
      Provider<FirebaseService> firebaseServiceProvider) {
    return new FirebaseAuthRepository_Factory(firebaseServiceProvider);
  }

  public static FirebaseAuthRepository newInstance(FirebaseService firebaseService) {
    return new FirebaseAuthRepository(firebaseService);
  }
}
