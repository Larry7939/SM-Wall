package com.example.smproject.src.main.post

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.children
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.*
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import com.example.smproject.BuildConfig
import com.example.smproject.R
import com.example.smproject.config.ApplicationClass
import com.example.smproject.config.BaseFragment
import com.example.smproject.databinding.FragmentPostBinding
import com.example.smproject.src.main.post.models.PostPostingRequest
import com.example.smproject.src.main.post.models.PostPostingResponse
import com.example.smproject.util.BitmapConverter
import com.example.smproject.util.CurrentLocation
import com.google.android.material.chip.Chip
import java.io.File
import java.io.IOException


class PostFragment : BaseFragment<FragmentPostBinding>(FragmentPostBinding::bind,R.layout.fragment_post),PostFragmentPostingView {
    private lateinit var swShare:SwitchCompat
    private lateinit var tvShare:AppCompatTextView
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private var bitmapConverter = BitmapConverter()
    //해시태그 문자열
    private lateinit var hashString:String
    private var hashList:ArrayList<String> = ArrayList() //chip들의 text가 추가/삭제되는 리스트
    private  var hashStringToPost:String ="" //hashList를 String으로 바꿔서 서버에 보내기 위함
    //이미지 배열
    private var postImages:ArrayList<String> = ArrayList()
    //공개여부
    private var isPrivate:Int = 0
    //위도 경도
    private var lat:String=""
    private var lng:String=""
    //게시물 내용
    private var contents:String = ""
    //칩의 개수 카운트
    private var chipNum:Int =0
    //camera 코드
    private val REQ_CAMERA = 100

    private lateinit var cameraImagePath :String

    fun newInstance(): Fragment {
        return PostFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swShare = binding.postSwitch
        tvShare = binding.postSwitchTv

        swShare.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //체크된 상태로 만들 시 코드
                tvShare.text = "전체 공개"
                isPrivate = 0
            } else {
                //체크된 상태 취소 시 코드
                tvShare.text = "비공개"
                isPrivate = 1
            }
        }
        context?.let { setPostedImage(it) }
        hashTag()

        //서버 통신 성공 시 이 정보들을 보내야함.
        binding.postFinish.setOnClickListener {
            postPosting() //포스팅
        }
        imgCancel() // 이미지 업로드 취소
   }
    @SuppressLint("Range")
    private fun setPostedImage(context: Context){
        //launcher선언(앨범 열기용)
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                // RESULT_OK일 때 실행할 코드...
                val uri: Uri? = data?.data
                //iv1이 비어있으면 iv1에 이미지 set
                //iv1이 차있고 iv2도 차있으면 toast 메시지
                //iv1이 차있고 iv2가 비어있으면 iv2에 이미지 set
                if(binding.postPostedIv1.drawable==null){
                    binding.postPostedCancel1.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(uri)
                        .into(binding.postPostedIv1)
                    if (uri != null) {
                        // 사진 가져오기
                        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                        // 사진의 회전 정보 가져오기
                        val orientation = getOrientationOfImage(uri).toFloat()
                        // 이미지 회전하기
                        val newBitmap:Bitmap? = getRotatedBitmap(bitmap, orientation)
//                        // 회전된 이미지로 imaView 설정
                        binding.postPostedIv1.setImageBitmap(newBitmap)
//                        postImages.add("data:image/png;base64,${bitmapConverter.bitmapToBase64(newBitmap)}")
                        uriToFile(newBitmap?.let { bitmapConverter.getImageUri(context, it) } ,context) //업로드 및 list에 filename 추가

                    }
                }
                else if(binding.postPostedIv1.drawable!=null){
                    if(binding.postPostedIv2.drawable!=null){
                        showCustomToast("사진은 최대 2장까지만 첨부할 수 있습니다.")
                    }
                    else {
                        binding.postPostedCancel2.visibility = View.VISIBLE
                        Glide.with(context)
                            .load(uri)
                            .into(binding.postPostedIv2)
//                        Uri -> Bitmap -> Base64인코딩
                        if (uri != null) {
                            // 사진 가져오기
                            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                            // 사진의 회전 정보 가져오기
                            val orientation = getOrientationOfImage(uri).toFloat()
                            // 이미지 회전하기
                            val newBitmap = getRotatedBitmap(bitmap, orientation)
//                            // 회전된 이미지로 imaView 설정
                            binding.postPostedIv2.setImageBitmap(newBitmap)
//                            postImages.add("data:image/png;base64,${bitmapConverter.bitmapToBase64(newBitmap)}")
                            uriToFile(newBitmap?.let { bitmapConverter.getImageUri(context, it) } ,context) //업로드 및 list에 filename 추가


                        }


                    }
                }
            }
        }
        // 이미지 추가 버튼 눌렀을 때 갤러리 실행
        binding.postAddImg.setOnClickListener { v ->
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            launcher.launch(intent)
        }
        binding.postAddCamera.setOnClickListener {
            showCamera()
        }
//        binding.postAddCamera.setOnClickListener {
//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            if (intent.resolveActivity(context.packageManager) != null) {
//                var imageFile: File? = null
//                try {
//                    imageFile = createImageFile()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//                if (imageFile != null) {
//                    val imageUri = FileProvider.getUriForFile(
//                        context.applicationContext,
//                        //일단 아래 코드를 위 줄로 대체
////                        ApplicationProvider.getApplicationContext<Context>(),
//                        "com.example.getimage.fileprovider",
//                        imageFile
//                    )
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
//                    startActivityForResult(intent, CAMERA) // final int CAMERA = 100;
//                }
//            }
//        }

    }
//
//    @SuppressLint("SimpleDateFormat")
//    @Throws(IOException::class)
//    fun createImageFile(): File? {
//    //	이미지 파일 생성
//    	val imageDate = SimpleDateFormat("yyyyMMdd_HHmmss");
//        val timeStamp: String =
//            imageDate.format(Date()) // 파일명 중복을 피하기 위한 "yyyyMMdd_HHmmss"꼴의 timeStamp
//        val fileName = "IMAGE_$timeStamp" // 이미지 파일 명
//
//        val storageDir: File = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES).absolutePath()
//
//        val file = File.createTempFile(fileName, ".jpg", storageDir) // 이미지 파일 생성
//        imagePath = file.absolutePath // 파일 절대경로 저장하기, String
//        return file
//    }

    // uri -> real path 로 변환하는 함수 (by 다인)
    private fun getFilePathFromUri(context: Context, uri: Uri?): String? {
        uri ?: return null
        uri.path ?: return null

        var newUriString = uri.toString()
        newUriString = newUriString.replace(
            "content://com.android.providers.downloads.documents/",
            "content://com.android.providers.media.documents/"
        )
        newUriString = newUriString.replace(
            "/msf%3A", "/image%3A"
        )
        val newUri = Uri.parse(newUriString)

        var realPath = String()
        val databaseUri: Uri
        val selection: String?
        val selectionArgs: Array<String>?
        if (newUri.path?.contains("/document/image:")  == true) {
            databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            selection = "_id=?"
            selectionArgs = arrayOf(DocumentsContract.getDocumentId(newUri).split(":")[1])
        } else {
            databaseUri = newUri
            selection = null
            selectionArgs = null
        }
        try {
            val column = "_data"
            val projection = arrayOf(column)
            val cursor = context.contentResolver.query(
                databaseUri,
                projection,
                selection,
                selectionArgs,
                null
            )
            cursor?.let {
                if (it.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    realPath = cursor.getString(columnIndex)
                }
                cursor.close()
            }
        } catch (e: Exception) {
            Log.i("GetFileUri Exception:", e.message ?: "")
        }
        val path = realPath.ifEmpty {
            when {
                newUri.path?.contains("/document/raw:") == true -> newUri.path?.replace(
                    "/document/raw:",
                    ""
                )
                newUri.path?.contains("/document/primary:") == true -> newUri.path?.replace(
                    "/document/primary:",
                    "/storage/emulated/0/"
                )
                else -> return null
            }
        }
        return if (path.isNullOrEmpty()) null else path
    }

    private fun createImageFile(): File { // 사진이 저장될 폴더 있는지 체크

        var imageName = "plantdiary_${System.currentTimeMillis()}"

        var file: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(imageName,".jpg", file).apply {
            cameraImagePath = absolutePath
        }
    }
    private fun showCamera() {

        var state = Environment.getExternalStorageState()
        if (TextUtils.equals(state, Environment.MEDIA_MOUNTED)) {
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            context?.let {
                intent.resolveActivity(it.packageManager)?.let {
                    createImageFile().let {
                        var photoUri = context?.let { it1 ->
                            FileProvider.getUriForFile(
                                it1,
                                BuildConfig.APPLICATION_ID + ".provider",
                                it
                            )
                        }
                        Log.d("카메라URI-show","${photoUri}")
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        startActivityForResult(intent, REQ_CAMERA)
                    }
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var bitmap:Bitmap? = null
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_CAMERA -> {


                    val options:BitmapFactory.Options = BitmapFactory.Options()
                    options.inSampleSize = 2 //이미지 축소 정도. 원 크기에서 1/inSampleSize로 축소됨
                    bitmap = BitmapFactory.decodeFile(cameraImagePath,options)


                    // 사진의 회전 정보 가져오기
                    val orientation = getOrientationOfImage(bitmapConverter.getImageUri(context,bitmap)).toFloat()
                    Log.d("회전각도", orientation.toString())
                    // 이미지 회전하기
                    val newBitmap = getRotatedBitmap(bitmap, orientation)

                    uriToFile(newBitmap?.let { bitmapConverter.getImageUri(context, it) },context!!)
//                    postImages.add("data:image/png;base64,${bitmapConverter.bitmapToBase64(bitmap)}")

                    if(binding.postPostedIv1.drawable==null){
                        binding.postPostedIv1.setImageBitmap(bitmap)
                        binding.postPostedCancel1.visibility = View.VISIBLE
                        binding.postPostedCancel2.visibility = View.GONE
                    }
                    else if(binding.postPostedIv1.drawable!=null){
                        if(binding.postPostedIv2.drawable!=null){
                            showCustomToast("사진은 최대 2장까지만 첨부할 수 있습니다.")
                        }
                        //
                        else{
                            binding.postPostedIv2.setImageBitmap(bitmap)
                            binding.postPostedCancel1.visibility = View.VISIBLE
                            binding.postPostedCancel2.visibility = View.VISIBLE
                        }
                    }

                }
            }
        }
    }

    private fun imgCancel(){
        binding.postPostedCancel1.setOnClickListener {
            //사진 1개 & 첫번째 취소버튼
            if(postImages.size==1){
                postImages.removeAt(0)
                binding.postPostedIv1.setImageDrawable(null)
                binding.postPostedCancel1.visibility = View.GONE
                binding.postPostedCancel2.visibility = View.GONE
            }
            //사진 2개 & 첫번째 취소버튼
            else if(postImages.size==2){
                postImages.removeAt(0)
                binding.postPostedIv1.setImageDrawable(binding.postPostedIv2.drawable)
                binding.postPostedIv2.setImageDrawable(null)
                binding.postPostedCancel1.visibility = View.VISIBLE
                binding.postPostedCancel2.visibility = View.GONE
            }

        }
        binding.postPostedCancel2.setOnClickListener {
            if(postImages.size==2){
                postImages.removeAt(1)
                binding.postPostedIv2.setImageDrawable(null)
                binding.postPostedCancel1.visibility = View.VISIBLE
                binding.postPostedCancel2.visibility = View.GONE
            }

        }
    }

    // 이미지 회전 정보 가져오기
    @RequiresApi(Build.VERSION_CODES.N)
    private fun getOrientationOfImage(uri: Uri): Int {
        // uri -> inputStream
        val inputStream = context?.contentResolver?.openInputStream(uri)
        val exif: ExifInterface? = try {
            inputStream?.let { ExifInterface(it) }
        } catch (e: IOException) {
            e.printStackTrace()
            return -1
        }
        inputStream?.close()

        // 회전된 각도 알아내기
        val orientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        if (orientation != -1) {
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> return 90
                ExifInterface.ORIENTATION_ROTATE_180 -> return 180
                ExifInterface.ORIENTATION_ROTATE_270 -> return 270
            }
        }
        return 0
    }

    // 이미지 회전하기
    @Throws(Exception::class)
    private fun getRotatedBitmap(bitmap: Bitmap?, degrees: Float): Bitmap? {
        if (bitmap == null) return null
        if (degrees == 0F) return bitmap

        val m = Matrix()
        m.setRotate(degrees,bitmap.width.toFloat() / 2, bitmap.height.toFloat() / 2)
//        m.setRotate(degrees, bitmap.width.toFloat() / 2, bitmap.height.toFloat() / 2)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
    }

    private fun hashTag(){
        binding.postHashEt.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int
                ) {

                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    hashString = "#"+binding.postHashEt.text.toString()
                    if(hashString[hashString.length-1]==' '){
                        if(chipNum >4){
                            showCustomToast("해시태그는 최대 5개까지 남길 수 있습니다.")
                        }
                        else if(hashString[0]=='#' && hashString.length>2){ //해시태그 입력창에 입력된 문자열 검사 - 맨 앞이 #이고 길이가 1이상이어야함.
                            var chip = Chip(context)
                            hashString = hashString.substring(0,hashString.length-1)//replace로는 맨 끝 공백이 사라지지 않아서, substring으로 공백 없애주기!
                            chip.text = hashString
                            chip.setTextColor(resources.getColor(R.color.black))
                            chip.setChipBackgroundColorResource(R.color.basicBackground)
                            chip.setTextAppearance(R.style.TextAppearance_MaterialComponents_Chip)
                            chip.isCloseIconVisible = true
                            chip.setOnCloseIconClickListener {
                                chip.visibility = View.GONE
                                //chip을 삭제하면 hashStringSum에서 삭제된 chip의 text를 삭제함.

                                hashList.remove(chip.text.toString())
                                chipNum-=1

                            }
                            chipNum+=1
                            binding.postChipGroup.addView(chip)
                            binding.postHashEt.text = null
                            //chip을 추가하면서 List에 String추가
                            hashList.add(chip.text.toString())
                        }
                    }
                }
                override fun afterTextChanged(s: Editable?) {

                }

            }
        )
    }
    private fun postPosting(){
        showLoadingDialog(context!!,2)
        //내용
        contents = binding.postEt.text.toString()
        //해시태그 joinToString
        if(hashList.isNotEmpty()){
            hashStringToPost = hashList.joinToString(",")
            hashStringToPost = hashStringToPost.replace("#","")
        }
        CurrentLocation(requireContext()).returnLocation()
        //위도,경도
        Log.d("Post - 현재 위치","${ApplicationClass.latitude},${ApplicationClass.longtidute}")

        //error code 301 - 이미지와 내용 둘다 없는 경우
        if(postImages.size ==2){
            Log.d("s3테스트","postImages -  ${postImages[0]},${postImages[1]}")
        }


        PostPostingService(this).tryPostPosting(PostPostingRequest("createPost", contents, postImages, hashStringToPost, isPrivate, ApplicationClass.latitude.toString(),ApplicationClass.longtidute.toString()))
        binding.postPostedCancel1.visibility = View.GONE
        binding.postPostedCancel2.visibility = View.GONE
    }

    override fun onPostPostingSuccess(response: PostPostingResponse) {
        if(response.data.result){
            Log.d("포스팅 성공여부 ","성공")
        }
        if(response.data.code==200){
            binding.postEt.text = null
            binding.postPostedIv1.setImageDrawable(null)
            binding.postPostedIv2.setImageDrawable(null)
            chipNum=0
            hashList.clear()
            for(i in binding.postChipGroup.children.iterator()){
                i.visibility = View.GONE
            }
            isPrivate = 0
            tvShare.text = "전체 공개"
            swShare.isChecked = true
            showCustomToast("작성 완료")
            dismissLoadingDialog()
        }
        else if(response.data.code==301){
            showCustomToast("내용을 입력하세요")
            dismissLoadingDialog()
        }
        else{
            dismissLoadingDialog()
        }
    }
    override fun onPostPostingFailure(message: String) {
        Log.d("포스팅 성공여부 ","실패 $message")
        dismissLoadingDialog()
    }


    //uri -> realpath -> filename  -> postImages.add(filename)
    private fun uriToFile(uri:Uri?,context: Context){
        // aws s3 image upload (by 다인)
        if (uri != null) {

            // accesskey, secretkey는 깃허브에 올리면 안되서 따로 파일로 빼서 관리 부탁드립니다. -> buildConfig에서 관리(gitignore에서 local property숨김)
            val awsCredentials: AWSCredentials = BasicAWSCredentials(BuildConfig.S3_ACCESS_KEY,BuildConfig.S3_SECRET_KEY);

            // aws s3 리전은 서울(AP_NORTHEAST_2)입니다.
            val s3Client: AmazonS3Client = AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2))

            val transferUtility: TransferUtility = TransferUtility.builder().s3Client(s3Client).context(activity?.applicationContext).build()
            TransferNetworkLossHandler.getInstance(activity?.applicationContext)

            // getFilePathFromUri는 uri -> real path로 변환하는 함수로 아래 작성되어 있습니다.
            val path = getFilePathFromUri(context, uri)
            val file = File(path)

            // 버킷명: smwall
            // 파일이름: 파일이름의 경우 고유해야 해서 현재 시간을 TimeStamp로 변경한 뒤 확장자를 붙여서 사용합니다.
            val pathArr = path?.split(".")
            val type = pathArr?.get(pathArr?.size-1) // 확장자
            val filename = System.currentTimeMillis().toString() + "." + type
            val uploadObserver: TransferObserver = transferUtility.upload("smwall", filename, file) // 파일 업로드

            uploadObserver.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState?) {
                    if(state == TransferState.COMPLETED) {
                        // 이미지 업로드 성공한 경우!!
                        // createPost하는 api의 imageList에 base64대신 위에서 만들어 놓은 filename을 넣어서 보내주시면 됩니다.
                        postImages.add(filename)
//                        showCustomToast("UPLOAD_SUCCESS_STATE_CHANGED")
                    }
                }
                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
//                    showCustomToast("UPLOAD_SUCCESS_ON_PROGRESS_CHANGED")

                }


                override fun onError(id: Int, ex: java.lang.Exception?) {
                    Log.d("error", ex.toString())
//                    showCustomToast("ERROR")

                }
            })
        }
    }
}