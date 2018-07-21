# Material Contextual Action Bar

<img src="https://raw.githubusercontent.com/afollestad/material-cab/master/art/newshowcase.png" width="400px" />

Material CAB allows you to implement a customizable and flexible contextual action bar in your app.
The traditional stock CAB on Android is limited to being placed at the top of your Activity,
and the navigation drawer cannot go over it. This library lets you choose its exact location,
and a toolbar is used, allowing views to be be placed over and under it.

## Gradle Dependency

[ ![jCenter](https://api.bintray.com/packages/drummer-aidan/maven/material-cab/images/download.svg) ](https://bintray.com/drummer-aidan/maven/material-cab/_latestVersion)
[![Build Status](https://img.shields.io/travis/afollestad/material-cab.svg?style=flat-square)](https://travis-ci.org/afollestad/material-cab)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)

Add Material CAB to your module's `build.gradle` dependencies block:

```Gradle
dependencies {

    implementation 'com.afollestad:material-cab:1.0.0'
}
```

---

## Attaching

This library attaches to your `Activity` by taking the place of a `ViewStub` in your Activity layout.
For an example, this is the main layout of the sample project:

```xml
<LinearLayout
  xmlns:android="http://schemas.android.com/apk/res/android"
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

```kotlin
MaterialCab.attach(this, R.id.cab_stub)

val active = MaterialCab.isActive // true
```

`R.id.cab_stub` references the `ViewStub`, which is replaced with the CAB toolbar.

In addition, you can also pass the ID of a `ViewGroup` (such as a `FrameLayout`). The CAB will
get added as a child to that view group.

---

## Configuration

You can configure various properties about your CAB during attachment:

```kotlin
MaterialCab.attach(this, R.id.cab_stub) {
    title = "Title Hardcoded"
    titleRes(R.string.title_resource)
    titleColor = Color.WHITE
    titleColorRes(R.color.white)
    popupTheme = R.style.ThemeOverlay_AppCompat_Light
    contentInsetStart = 120
    contentInsetStartRes(R.dimen.mcab_default_content_inset)
    menuRes = R.menu.cab_menu_items
    backgroundColor = Color.DKGRAY
    backgroundColorRes(R.color.dark_gray)
    closeDrawableRes = R.drawable.back_arrow

    onCreate { cab, menu ->
      ...
    }
    onSelection { item ->
      ...
      true // allow selection?
    }
    onDestroy { cab ->
      ...
      true // allow destruction?
    }
}
```

---

## Saving and Restoring States

In order to keep the CAB active, and maintain all of its current properties, you have to save and restore
the CAB state during configuration changes.

It works like this in an Activity:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)

  // Returns true if a CAB was restored and is now visible
  MaterialCab.tryRestore(this, savedInstanceState)
}

override fun onSaveInstanceState(outState: Bundle) {
  super.onSaveInstanceState(outState)

  // If any CAB is visible, all of its properties are pushed into the Bundle
  MaterialCab.saveState(outState)
}
```

---

## Destroying the CAB

The navigation icon in your CAB toolbar (far left button) will trigger this method, but you
can manually call it whenever you'd like as well:


```kotlin
MaterialCab.destroy()

val active = MaterialCab.isActive // false
```

This will invoke the onDestroy callback. If the callback returns true, any visible CAB will be
hidden.