package com.campusbussbuddy.di;

import com.campusbussbuddy.data.remote.FirebaseService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AppModule_ProvideFirebaseServiceFactory implements Factory<FirebaseService> {
  @Override
  public FirebaseService get() {
    return provideFirebaseService();
  }

  public static AppModule_ProvideFirebaseServiceFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseService provideFirebaseService() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideFirebaseService());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideFirebaseServiceFactory INSTANCE = new AppModule_ProvideFirebaseServiceFactory();
  }
}
