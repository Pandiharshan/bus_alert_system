package com.campusbussbuddy.di;

import com.campusbussbuddy.data.remote.FirebaseService;
import com.campusbussbuddy.data.repository.FirebaseAuthRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideFirebaseAuthRepositoryFactory implements Factory<FirebaseAuthRepository> {
  private final Provider<FirebaseService> firebaseServiceProvider;

  public AppModule_ProvideFirebaseAuthRepositoryFactory(
      Provider<FirebaseService> firebaseServiceProvider) {
    this.firebaseServiceProvider = firebaseServiceProvider;
  }

  @Override
  public FirebaseAuthRepository get() {
    return provideFirebaseAuthRepository(firebaseServiceProvider.get());
  }

  public static AppModule_ProvideFirebaseAuthRepositoryFactory create(
      Provider<FirebaseService> firebaseServiceProvider) {
    return new AppModule_ProvideFirebaseAuthRepositoryFactory(firebaseServiceProvider);
  }

  public static FirebaseAuthRepository provideFirebaseAuthRepository(
      FirebaseService firebaseService) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideFirebaseAuthRepository(firebaseService));
  }
}
