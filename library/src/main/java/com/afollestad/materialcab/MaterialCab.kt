/**
 * Designed and developed by Aidan Follestad (@afollestad)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.afollestad.materialcab

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.afollestad.materialcab.attached.AttachedCab
import com.afollestad.materialcab.attached.RealAttachedCab
import com.afollestad.materialcab.internal.idName
import com.afollestad.materialcab.internal.inflate

/**
 * Creates a new contextual action bar. Attaches to a view stub or into a view group
 * by ID [attachToId], inside the receiving [Activity].
 */
fun Activity.createCab(
  @IdRes attachToId: Int,
  exec: CabApply
): AttachedCab {
  val attachToName = idName(attachToId)
  val attachToView = findViewById<View>(attachToId)
  var replacedViewStub = true

  val toolbar: Toolbar = when (attachToView) {
    is Toolbar -> {
      // This is most likely a previous destroyed CAB that
      // was inflated into a ViewStub.
      attachToView
    }
    is ViewStub -> {
      // We assign an ID so that we can find it again later
      // when re-attaching, since destroying won't remove this,
      // only hide it.
      attachToView.inflatedId = attachToId
      attachToView.layoutResource = R.layout.mcab_toolbar
      attachToView.inflate() as Toolbar
    }
    is ViewGroup -> {
      replacedViewStub = false
      attachToView.inflate<Toolbar>(R.layout.mcab_toolbar)
          .also {
            attachToView.addView(it)
          }
    }
    else -> throw IllegalStateException(
        "Unable to attach to $attachToName, it's not a ViewStub or ViewGroup."
    )
  }

  return RealAttachedCab(
      context = this,
      toolbar = toolbar,
      replacedViewStub = replacedViewStub
  ).apply {
    exec()
    show()
  }
}

/**
 * Calls [createCab] on the Fragment's Activity.
 */
fun Fragment.createCab(
  @IdRes attachToId: Int,
  exec: CabApply
): AttachedCab {
  val context = activity ?: throw IllegalStateException(
      "Fragment ${this::class.java.name} is not attached to an Activity."
  )
  return context.createCab(attachToId, exec)
}
