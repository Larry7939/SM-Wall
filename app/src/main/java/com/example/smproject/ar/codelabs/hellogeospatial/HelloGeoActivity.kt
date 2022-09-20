/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//package com.google.ar.core.codelabs.hellogeospatial
package com.example.smproject.ar.codelabs.hellogeospatial

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.example.smproject.ar.codelabs.hellogeospatial.helpers.ARCoreSessionLifecycleHelper
import com.example.smproject.ar.codelabs.hellogeospatial.helpers.GeoPermissionsHelper
import com.example.smproject.ar.codelabs.hellogeospatial.helpers.HelloGeoView
import com.example.smproject.ar.examples.java.common.helpers.FullScreenHelper
import com.example.smproject.ar.examples.java.common.helpers.TapHelper
import com.example.smproject.ar.examples.java.common.samplerender.SampleRender
import com.example.smproject.config.ApplicationClass
import com.example.smproject.src.main.getPostApi.GetPostListService
import com.example.smproject.src.main.getPostApi.GetPostListView
import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableApkTooOldException
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException
import com.google.ar.core.exceptions.UnavailableSdkTooOldException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import androidx.fragment.app.Fragment
import com.example.smproject.src.main.MainActivity
import io.github.sceneview.SceneView

class HelloGeoActivity : AppCompatActivity(){
  companion object {
    private const val TAG = "HelloGeoActivity"
  }

  lateinit var arCoreSessionHelper: ARCoreSessionLifecycleHelper
  lateinit var view: HelloGeoView
  lateinit var renderer: HelloGeoRenderer
  lateinit var tapHelper : TapHelper


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Setup ARCore session lifecycle helper and configuration.
    arCoreSessionHelper = ARCoreSessionLifecycleHelper(this)
    // If Session creation or Session.resume() fails, display a message and log detailed
    // information.
    arCoreSessionHelper.exceptionCallback =
      { exception ->
        val message =
          when (exception) {
            is UnavailableUserDeclinedInstallationException ->
              "Please install Google Play Services for AR"
            is UnavailableApkTooOldException -> "Please update ARCore"
            is UnavailableSdkTooOldException -> "Please update this app"
            is UnavailableDeviceNotCompatibleException -> "This device does not support AR"
            is CameraNotAvailableException -> "Camera not available. Try restarting the app."
            else -> "Failed to create AR session: $exception"
          }
        Log.e(TAG, "ARCore threw an exception", exception)
        view.snackbarHelper.showError(this, message)
      }

    // Configure session features.
    arCoreSessionHelper.beforeSessionResume = ::configureSession
    lifecycle.addObserver(arCoreSessionHelper)

    // Sets up an example renderer using our HelloGeoRenderer.


    // Set up Hello AR UI.
    view = HelloGeoView(this)
    lifecycle.addObserver(view)
    setContentView(view.root)

    tapHelper = TapHelper(this)
    view.surfaceView.setOnTouchListener(tapHelper)


    renderer = HelloGeoRenderer(this, tapHelper)
    lifecycle.addObserver(renderer)

    // Set up the Hello AR renderer.

    SampleRender(view.surfaceView, renderer, assets)
  }


  // Configure the session, setting the desired options according to your usecase.
  fun configureSession(session: Session) {
    // TODO: Configure ARCore to use GeospatialMode.ENABLED.
    session.configure(
      session.config.apply {
        // Enable Geospatial Mode.
        geospatialMode = Config.GeospatialMode.ENABLED
      }
    )
  }


  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus)
  }
}
