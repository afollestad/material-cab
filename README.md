# Material Contextual Action Bar

![Showcase Image](https://raw.githubusercontent.com/afollestad/material-cab/master/art/screenshot2.png)

Material CAB allows you to implement a customizable and flexible contextual action bar in your app.
The traditional stock CAB on Android is limited to being placed at the top of your Activity, and the navigation drawer 
cannot go over it. This library lets you choose its exact location, and a toolbar is used allowing views
to be be placed over and under it.

Not only that, the stock CAB only allows you to specify theme properties from styles.xml, this library
lets you dynamically change theme properties at runtime from code.

## Gradle Dependency

This goes in your dependencies (in addition to your other dependencies obviously):

```Gradle
dependencies {
    compile 'com.afollestad:material-cab:0.1.5'
}
```

[ ![Download](https://api.bintray.com/packages/drummer-aidan/maven/material-cab/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-cab/_latestVersion)

If you have issues resolving the library, add this to your Gradle file too:

```Gradle
repositories {
    maven { url 'https://dl.bintray.com/drummer-aidan/maven' }
}
```

## Attacher

This library attaches to your `Activity` by taking the place of a `ViewStub` in your Activity layout.
For an example, this is the main layout of the sample project:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <android.support.v7.widget.Toolbar
            android:id="@+id/main_toolbar"
            android:layout_width="match_parent"
            android:layout_height="?actionBarSize"
            android:background="?colorPrimary"
            android:elevation="@dimen/mcab_toolbar_elevation"
            android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
            app:contentInsetStart="@dimen/mcab_default_content_inset"
            app:popupTheme="@style/ThemeOverlay.AppCompat.Light"
            tools:ignore="UnusedAttribute" />

        <ViewStub
            android:id="@+id/cab_stub"
            android:layout_width="match_parent"
            android:layout_height="?actionBarSize" />

    </FrameLayout>

    <android.support.v7.widget.RecyclerView
        android:id="@+id/list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scrollbars="vertical" />

</LinearLayout>
```

You attach a Material CAB to the Activity like this:

```java
MaterialCab cab = new MaterialCab(this, R.id.cab_stub)
    .start(this);
```

R.id.cab_stub references the `ViewStub`, which is replaced with the CAB toolbar when `start()` is called.

Note that the parameter in `start()` is a Callback interface implementer which receives CAB events.

## Callback

Whether it's an Activity that implements the Callback interface, or an inline callback, it implements
these methods:

```java
new MaterialCab.Callback() {
            @Override
            public boolean onCabCreated(MaterialCab cab, Menu menu) {
                // The CAB was started, return true to allow creation to continue.
                return true; 
            }

            @Override
            public boolean onCabItemClicked(MenuItem item) {
                // An item in the toolbar or overflow menu was tapped.
                return true;
            }

            @Override
            public boolean onCabFinished(MaterialCab cab) {
                // The CAB was finished, return true to allow destruction to continue.
                return true;
            }
        };
```

## Properties

This code chains calls to properties that would be commonly used:

```java
MaterialCab cab = new MaterialCab(this, R.id.cab_stub)
    .setTitleRes(R.string.cab_title)
    .setMenu(R.menu.cab_menu)
    .setPopupMenuTheme(R.style.ThemeOverlay_AppCompat_Light)
    .setContentInsetStartRes(R.dimen.mcab_default_content_inset)
    .setBackgroundColorRes(R.color.indigo_500)
    .setCloseDrawableRes(R.drawable.abc_ic_ab_back_mtrl_am_alpha)
    .start(this);
```

Note that *most* of the property setters have different variations for literal values, dimension resources,
and attribute IDs.

You can also check whether or not the CAB is currently started:

```java
MaterialCab cab = // ...

if (cab.isActive()) {
    // Do something
}
```

## Global Theming

```xml
<style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">

    <!-- Sets a default title for all CABs in the Activity -->
    <item name="mcab_title">@string/hello_world</item>
    
    <!-- Sets a default inflated menu for all CABs in the Activity -->
    <item name="mcab_menu">@menu/menu_cab</item>
    
    <!-- 
        Changes the default content inset for all CABs in the Activity.
        Defaults to 72dp.
    -->
    <item name="mcab_contentinset_start">72dp</item>
    
    <!-- 
        Changes the default CAB background color for all CABs in the Activity.
        Defaults to the default value of ?colorPrimary (the AppCompat theme attribute).
    -->
    <item name="mcab_background_color">?colorAccent</item>
    
    <!-- 
        Changes the default CAB close drawable for all CABs in the Activity.
        Defaults to the AppCompat R.drawable.abc_ic_ab_back_mtrl_am_alpha back arrow.
    -->
    <item name="mcab_close_drawable">@drawable/abc_ic_ab_back_mtrl_am_alpha</item>
    
    <!-- 
        Changes the default overflow popup theme for all CABs in the Activity.
        Defaults to @style/ThemeOverlay.AppCompat.Light.
    -->
    <item name="mcab_popup_theme">@style/ThemeOverlay.AppCompat.Dark</item>

</style>
```

## Saving and Restoring States

In order to keep the CAB active, and maintain all of its current properties, you have to save and restore
the CAB state during configuration changes.

It works like this in an Activity:

```java
private MaterialCab mCab;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // ... other initialization for an Activity

    if (savedInstanceState != null) {
        // Restore the CAB state, save a reference to mCab.
        mCab = MaterialCab.restoreState(savedInstanceState, this, this);
    } else {
        // No previous state, first creation.
    }
}

@Override
protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (mCab != null) {
        // If the CAB isn't null, save it's state for restoration in onCreate()
        mCab.saveState(outState);
    }
}
```

## Finishing the CAB

The icon on the left of the CAB toolbar (the close drawable) will cause the CAB to be finished,
but you can also manually finish the CAB:

```java
MaterialCab cab = // ... initialize
cab.finish();
```
