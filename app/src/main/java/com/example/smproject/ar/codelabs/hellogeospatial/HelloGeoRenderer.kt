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
package com.example.smproject.ar.codelabs.hellogeospatial

import android.opengl.GLU
import android.opengl.Matrix
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.smproject.ar.codelabs.hellogeospatial.helpers.GetPostListArView
import com.google.ar.core.Anchor
import com.google.ar.core.TrackingState
import com.example.smproject.ar.examples.java.common.helpers.DisplayRotationHelper
import com.example.smproject.ar.examples.java.common.helpers.TrackingStateHelper
import com.example.smproject.ar.examples.java.common.helpers.TapHelper
import com.example.smproject.ar.examples.java.common.samplerender.Framebuffer
import com.example.smproject.ar.examples.java.common.samplerender.Mesh
import com.example.smproject.ar.examples.java.common.samplerender.SampleRender
import com.example.smproject.ar.examples.java.common.samplerender.Shader
import com.example.smproject.ar.examples.java.common.samplerender.Texture
import com.example.smproject.ar.examples.java.common.samplerender.arcore.BackgroundRenderer
import com.example.smproject.config.ApplicationClass
import com.example.smproject.src.main.getArPostApi.models.GetArPostListResponse
import com.example.smproject.src.main.getArPostApi.models.LocationObj
import com.example.smproject.src.main.getArPostApi.GetArPostListService
import com.example.smproject.src.main.getArPostApi.models.GetArPostListRequest
//import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
//import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
//import com.example.smproject.src.main.getPostApi.GetPostListService
//import com.example.smproject.src.main.getPostApi.models.GetPostListRequest
//import com.example.smproject.src.main.getPostApi.models.GetPostListResonse
//import com.example.smproject.src.main.getPostApi.models.LocationObj
import com.example.smproject.util.PostedDialog
import com.google.ar.core.exceptions.CameraNotAvailableException
import java.io.IOException
import kotlin.collections.ArrayList

data class MarkerObj(
  val id: Int,
  val locationObj: LocationObj,
  var anchor: Anchor?
)

data class CurrentDevicePose(
  val id: Int,
  val pose: DevicePose
)

data class DevicePose(
  val x: Float,
  val y: Float
)

class HelloGeoRenderer(val activity: HelloGeoActivity, val tapHelper: TapHelper):
  SampleRender.Renderer, DefaultLifecycleObserver {
    //<editor-fold desc="ARCore initialization" defaultstate="collapsed">
    companion object {
    val TAG = "HelloGeoRenderer"

    private val Z_NEAR = 0.1f
    private val Z_FAR = 1000f
    }

    lateinit var backgroundRenderer: BackgroundRenderer
    lateinit var virtualSceneFramebuffer: Framebuffer
    var hasSetTextureNames = false

    // Virtual object (ARCore pawn)
    lateinit var virtualObjectMesh: Mesh
    lateinit var virtualObjectShader: Shader
    lateinit var virtualObjectTexture: Texture

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    val modelMatrix = FloatArray(16)
    val viewMatrix = FloatArray(16)
    val projectionMatrix = FloatArray(16)
    val modelViewMatrix = FloatArray(16) // view x model

    val modelViewProjectionMatrix = FloatArray(16) // projection x view x model

    val session
    get() = activity.arCoreSessionHelper.session

    val displayRotationHelper = DisplayRotationHelper(activity)
    val trackingStateHelper = TrackingStateHelper(activity)

    val width: Int = activity.getResources().getDisplayMetrics().widthPixels
    val height: Int = activity.getResources().getDisplayMetrics().heightPixels

    private lateinit var postedDialog: PostedDialog
    // 서버에서 가져온 게시물 리스트
    private var markerObjList: ArrayList<MarkerObj> = arrayListOf()

    private var currentDevicePoseList: ArrayList<CurrentDevicePose> = arrayListOf()

    private lateinit var existingLocation: LocationObj
    private var arView: GetPostListArView = GetPostListArView(this)
    private var first = 0
    private var checker = 0



  override fun onResume(owner: LifecycleOwner) {
      displayRotationHelper.onResume()
      hasSetTextureNames = false
    }

    override fun onPause(owner: LifecycleOwner) {
      displayRotationHelper.onPause()
    }


    override fun onSurfaceCreated(render: SampleRender) {
      // Prepare the rendering objects.
      // This involves reading shaders and 3D model files, so may throw an IOException.
      try {


        backgroundRenderer = BackgroundRenderer(render)
        virtualSceneFramebuffer = Framebuffer(render, /*width=*/ 1, /*height=*/ 1)

        // Virtual object to render (Geospatial Marker)
        virtualObjectTexture =
          Texture.createFromAsset(
            render,
            //"models/spatial_marker_baked.png",
            "models/mapPointerRed.png",
            Texture.WrapMode.CLAMP_TO_EDGE,
            Texture.ColorFormat.SRGB
          )

        //virtualObjectMesh = Mesh.createFromAsset(render, "models/geospatial_marker.obj");
//        virtualObjectMesh = Mesh.createFromAsset(render, "models/cone.obj");
        virtualObjectMesh = Mesh.createFromAsset(render, "models/middleMapPointer.obj");


        virtualObjectShader =
          Shader.createFromAssets(
            render,
            "shaders/ar_unlit_object.vert",
            "shaders/ar_unlit_object.frag",
            /*defines=*/ null
          )
            .setTexture("u_Texture", virtualObjectTexture)

        backgroundRenderer.setUseDepthVisualization(render, false)
        backgroundRenderer.setUseOcclusion(render, false)



        Log.d("width, height", "${width},${height}")


//        val view = GetPostListArView(this)
//        GetPostListService(view).tryGetPostList(GetPostListRequest("getPostList", null, null, null, null, null))

      } catch (e: IOException) {
        Log.e(TAG, "Failed to read a required asset file", e)

        showError("Failed to read a required asset file: $e")
      }
    }


//    fun onGetPostListSuccess(response: GetPostListResonse) {
//      postedDialog = PostedDialog(activity)
//
//      Log.d("AR 게시물 목록", "")
//      val responseList = response.data.list
//      for(item in responseList.iterator()){
//        markerObjList.add(MarkerObj(item.id, item.locationObj, null))
//      }
//
//      createMultiMarker(markerObjList)
////      createMarker(LatLng(37.468736, 126.895482)) // 사무실
//    }
//
//    fun onGetPostListFailure(message: String) {
//      Log.d("AR 게시물 목록 요청 실패", message)
//    }

  fun onGetArPostListSuccess(response: GetArPostListResponse) {
    postedDialog = PostedDialog(activity)

    markerObjList.clear()
    Log.d("AR 게시물 목록", "")
    val responseList = response.data.list
    for(item in responseList.iterator()){
      markerObjList.add(MarkerObj(item.id, item.locationObj, null))
    }

    createMultiMarker(markerObjList)
  }

  fun onGetArPostListFailure(message: String) {
    Log.d("AR 게시물 목록 요청 실패", message)
  }


  override fun onSurfaceChanged(render: SampleRender, width: Int, height: Int) {
      displayRotationHelper.onSurfaceChanged(width, height)
      virtualSceneFramebuffer.resize(width, height)
    }
    //</editor-fold>

    override fun onDrawFrame(render: SampleRender) {
      //Log.d("Draw_Frame", "")
      val session = session ?: return

      //<editor-fold desc="ARCore frame boilerplate" defaultstate="collapsed">
      // Texture names should only be set once on a GL thread unless they change. This is done during
      // onDrawFrame rather than onSurfaceCreated since the session is not guaranteed to have been
      // initialized during the execution of onSurfaceCreated.
      if (!hasSetTextureNames) {
        session.setCameraTextureNames(intArrayOf(backgroundRenderer.cameraColorTexture.textureId))
        hasSetTextureNames = true
      }

      // -- Update per-frame state

      // Notify ARCore session that the view size changed so that the perspective matrix and
      // the video background can be properly adjusted.
      displayRotationHelper.updateSessionIfNeeded(session)

      // Obtain the current frame from ARSession. When the configuration is set to
      // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
      // camera framerate.
      val frame =
        try {
          session.update()
        } catch (e: CameraNotAvailableException) {
          Log.e(TAG, "Camera not available during onDrawFrame", e)
          showError("Camera not available. Try restarting the app.")
          return
        }

      val camera = frame.camera

      // BackgroundRenderer.updateDisplayGeometry must be called every frame to update the coordinates
      // used to draw the background camera image.
      backgroundRenderer.updateDisplayGeometry(frame)

      // Keep the screen unlocked while tracking, but allow it to lock when tracking stops.
      trackingStateHelper.updateKeepScreenOnFlag(camera.trackingState)

      // -- Draw background
      if (frame.timestamp != 0L) {
        // Suppress rendering if the camera did not produce the first frame yet. This is to avoid
        // drawing possible leftover data from previous sessions if the texture is reused.
        backgroundRenderer.drawBackground(render)
      }

      // If not tracking, don't draw 3D objects.
      if (camera.trackingState == TrackingState.PAUSED) {
        return
      }

      // Get projection matrix.
      camera.getProjectionMatrix(projectionMatrix, 0, Z_NEAR, Z_FAR)

      // Get camera matrix and draw.
      camera.getViewMatrix(viewMatrix, 0)

      render.clear(virtualSceneFramebuffer, 0f, 0f, 0f, 0f)
      //</editor-fold>

      // TODO: Obtain Geospatial information and display it on the map.
      val earth = session.earth

      if(earth?.trackingState == TrackingState.TRACKING) {
        val lat = earth.cameraGeospatialPose.latitude
        val lng = earth.cameraGeospatialPose.longitude
        if(first == 0){
          existingLocation = LocationObj(lat.toString(), lng.toString())
          GetArPostListService(arView).tryGetArPostList(GetArPostListRequest("getArPostList", lat.toString(), lng.toString()))
          first = 1
        }else if(existingLocation.lat.toDouble() + 0.0001 < lat || existingLocation.lat.toDouble() - 0.0001 > lat
          || existingLocation.lng.toDouble() + 0.0001 < lng || existingLocation.lng.toDouble() - 0.0001 > lng ){
          checker = 1
          existingLocation = LocationObj(lat.toString(), lng.toString())
          GetArPostListService(arView).tryGetArPostList(GetArPostListRequest("getArPostList", lat.toString(), lng.toString()))
        }
      }


      // Draw the placed anchor, if it exists.
      //currentDevicePoseList.clear()
      if(markerObjList != null && checker == 0){
        if(earth?.trackingState == TrackingState.TRACKING){
          render.renderCompassAtAnchor(markerObjList)
        }
      }

      // Compose the virtual scene with the background.
      backgroundRenderer.drawVirtualScene(render, virtualSceneFramebuffer, Z_NEAR, Z_FAR)


      val tap = tapHelper.poll() ?: return
      if(tap !== null){
        Log.i(TAG,"tap")

        Log.d("tap touch", "${tap.x},${(height+100-tap.y)}")

        if(currentDevicePoseList.size > 0) {
          for(item in currentDevicePoseList.iterator()){
            var x = item.pose.x
            var y = item.pose.y
            if ((tap.x < x+200 && tap.x > x-200) && ((height+100 - tap.y) < y+200 && (height+100 - tap.y) > y-200)) {
              Log.i("tap click!!!!", "${item.id}")
              touchObject(item.id)
              break
            }
          }

          Log.d("tap markerList", "")
          for(i in currentDevicePoseList.iterator()){
            Log.d("tap position", "${i.pose.x},${i.pose.y}")
          }
        }

      }

    }

  fun touchObject(id: Int){
    activity.runOnUiThread {
      Toast.makeText(activity, "${id}", Toast.LENGTH_SHORT).show()
      ApplicationClass.postedId = id.toString()
      postedDialog.create()
      postedDialog.show()
    }
  }


  fun checkTrackState(state: TrackingState, markerList: ArrayList<MarkerObj>) {
      if(state != TrackingState.TRACKING) {
        android.os.Handler(Looper.getMainLooper()).postDelayed({
          createMultiMarker(markerList)
        }, 1000)
      }

    }


  fun createMultiMarker(markerList: ArrayList<MarkerObj>) {
//    markerList.clear()

    // TODO: place an anchor at the given position.
    val earth = session?.earth ?: return

    checkTrackState(earth.trackingState, markerList)

    if (earth.trackingState != TrackingState.TRACKING) {
      Log.i(TAG,"session not tracking")
      return
    }

    Log.i(TAG,"session is tracking")

//    earthAnchor?.detach()
//    earthAnchor2?.detach()

    // Place the earth anchor at the same altitude as that of the camera to make it easier to view
    val altitude = earth.cameraGeospatialPose.altitude - 1
    // The rotation quaternion of the anchor in the East-Up-South (EUS) coordinate system.
    val qx = 0f
    val qy = 0f
    val qz = 0f
    val qw = 1f

    // Anchor 생성
    for(marker in markerList.iterator()){
      marker.anchor = earth.createAnchor(marker.locationObj.lat.toDouble(), marker.locationObj.lng.toDouble(), altitude, qx, qy, qz, qw)
    }
    checker = 0

  }

  private fun SampleRender.renderCompassAtAnchor(markerList: ArrayList<MarkerObj>) {/* = java.util.ArrayList<com.example.smproject.ar.codelabs.hellogeospatial.MarkerObj> */

    currentDevicePoseList.clear()

    // projection을 위한 view matrix
    val view = IntArray(4)
    view[0] = 0
    view[1] = 0
    view[2] = width
    view[3] = height

    for(item in markerList.iterator()){
      val anchor = item.anchor
      // Get the current pose of the Anchor in world space. The Anchor pose is updated
      // during calls to session.update() as ARCore refines its estimate of the world.
      anchor?.pose?.toMatrix(modelMatrix, 0)

      // Calculate model/view/projection matrices
      Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
      Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0)

      // Update shader properties and draw
      virtualObjectShader.setMat4("u_ModelViewProjection", modelViewProjectionMatrix)
      draw(virtualObjectMesh, virtualObjectShader, virtualSceneFramebuffer)

      // projection해서 디바이스 상의 위치 구하기
      Log.d("getPose", "${anchor?.getPose()}")
      val pose = anchor?.getPose()
      val pos = FloatArray(4)

      if(pose != null){
        GLU.gluProject(pose.tx(), pose.ty(), pose.tz(), viewMatrix, 0, projectionMatrix, 0, view, 0, pos, 0)
        Log.d("result", "${pos[0]},${pos[1]}}")
        currentDevicePoseList.add(CurrentDevicePose(item.id, DevicePose(pos[0], pos[1])))
      }

    }
  }

    private fun showError(errorMessage: String) =
      activity.view.snackbarHelper.showError(activity, errorMessage)
  }

