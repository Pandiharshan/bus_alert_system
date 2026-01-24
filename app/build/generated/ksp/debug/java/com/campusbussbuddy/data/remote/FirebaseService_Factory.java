package com.campusbussbuddy.data.remote;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class FirebaseService_Factory implements Factory<FirebaseService> {
  @Override
  public FirebaseService get() {
    return newInstance();
  }

  public static FirebaseService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FirebaseService newInstance() {
    return new FirebaseService();
  }

  private static final class InstanceHolder {
    private static final FirebaseService_Factory INSTANCE = new FirebaseService_Factory();
  }
}
