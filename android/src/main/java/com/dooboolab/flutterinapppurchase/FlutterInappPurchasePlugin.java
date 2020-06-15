package com.dooboolab.flutterinapppurchase;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterInappPurchasePlugin */
public class FlutterInappPurchasePlugin implements MethodCallHandler {
  static AndroidInappPurchasePlugin androidPlugin;
  static AmazonInappPurchasePlugin amazonPlugin;
  private static  Registrar mRegistrar;

  FlutterInappPurchasePlugin() {
    androidPlugin = new AndroidInappPurchasePlugin();
    amazonPlugin = new AmazonInappPurchasePlugin();
  }

  // Plugin registration.
  public static void registerWith(Registrar registrar) {
    mRegistrar = registrar;

    final boolean playStore = isPackageInstalled(mRegistrar.context(), "com.android.vending");
    final boolean amazonAppStore = isPackageInstalled(mRegistrar.context(), "com.amazon.venezia");

    if (amazonAppStore && playStore){
      if (isAppInstalledFrom(mRegistrar.context(), "amazon")){
        amazonPlugin.registerWith(registrar);
      } else {
        androidPlugin.registerWith(registrar);
      }
    } else if (playStore) {
      androidPlugin.registerWith(registrar);
    } else if (amazonAppStore){
      amazonPlugin.registerWith(registrar);
    }
  }

  @Override
  public void onMethodCall(final MethodCall call, final Result result) {
    if(isPackageInstalled(mRegistrar.context(), "com.android.vending")) {
      androidPlugin.onMethodCall(call, result);
    } else if(isPackageInstalled(mRegistrar.context(), "com.amazon.venezia")) {
      amazonPlugin.onMethodCall(call, result);
    } else result.notImplemented();
  }

  public static final boolean isPackageInstalled(Context ctx, String packageName) {
    try {
      ctx.getPackageManager().getPackageInfo(packageName, 0);
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }
    return true;
  }

  public static final boolean isAppInstalledFrom(Context ctx, String installer) {
    String installerPackageName = ctx.getPackageManager().getInstallerPackageName(
            ctx.getPackageName());
    if (installer != null && installerPackageName.contains(installer)){
      return true;
    }
    return false;
  }
}
