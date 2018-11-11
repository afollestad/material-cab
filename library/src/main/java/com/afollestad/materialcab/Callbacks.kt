/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.materialcab

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewPropertyAnimator

typealias CreateUpdateCallback = (cab: MaterialCab, menu: Menu) -> Unit

typealias SelectCallback = (item: MenuItem) -> Boolean

typealias DestroyCallback = (cab: MaterialCab) -> Boolean

typealias CabAnimator = (view: View, animator: ViewPropertyAnimator) -> Unit

typealias CabApply = MaterialCab.() -> Unit
