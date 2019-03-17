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

import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialcab.attached.AttachedCab
import com.afollestad.materialcab.attached.RealAttachedCab
import com.afollestad.materialcab.attached.destroy
import com.afollestad.materialcab.internal.idName
import com.afollestad.materialcab.internal.inflate

/** @author Aidan Follestad (afollestad) */
object MaterialCab {
  private val activeCabs = hashMapOf<String, AttachedCab>()

  /**
   * Creates a new contextual action bar. Attaches to a view stub or into a view group
   * by ID [attachToId], inside the given [context].
   */
  fun create(
    context: AppCompatActivity,
    @IdRes attachToId: Int,
    exec: AttachedCab.() -> Unit
  ): AttachedCab {
    val attachToName = context.idName(attachToId)
    val cabKey = cabKey(context, attachToId)
    check(!activeCabs.containsKey(cabKey)) {
      "$attachToName already has an active CAB."
    }

    val attachToView = context.findViewById<View>(attachToId)
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
        context = context,
        toolbar = toolbar,
        replacedViewStub = replacedViewStub,
        key = cabKey
    ).apply {
      exec()
      show()
      activeCabs[key] = this
      onDestroy {
        activeCabs.remove(key)
        true
      }
    }
  }

  /**
   * Applies properties to update an existing context action bar, [id], in the given [context].
   */
  fun update(
    context: AppCompatActivity,
    @IdRes id: Int,
    exec: AttachedCab.() -> Unit
  ) {
    val attachToName = context.idName(id)
    val cabKey = cabKey(context, id)
    val activeCab = activeCabs[cabKey] ?: throw IllegalStateException(
        "No active CAB found in this context for ID $attachToName"
    )
    activeCab.exec()
    (activeCab as? RealAttachedCab)
        ?.updateCallbacks
        ?.invokeAll(activeCab, activeCab.getMenu())
  }

  /**
   * Destroys an existing contextual action bar, [id], in the given [context].
   */
  fun destroy(
    context: AppCompatActivity,
    @IdRes id: Int
  ) {
    val attachToName = context.idName(id)
    val cabKey = cabKey(context, id)
    val activeCab = activeCabs[cabKey] ?: throw IllegalStateException(
        "No active CAB found in this context for ID $attachToName"
    )
    (activeCab as? RealAttachedCab).destroy()
  }

  /**
   * Destroys all contextual action bars.
   */
  fun destroyAll() = synchronized(activeCabs) {
    activeCabs.values.forEach {
      (it as? RealAttachedCab).destroy()
    }
    activeCabs.clear()
  }

  private fun cabKey(context: AppCompatActivity, @IdRes id: Int): String {
    val attachToName = context.idName(id)
    val contextName = context::class.java.name
    return "$contextName:$attachToName"
  }
}
