package com.parishod.watomatic.adapter

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.parishod.watomatic.R
import com.parishod.watomatic.model.App
import com.parishod.watomatic.model.preferences.PreferencesManager
import com.parishod.watomatic.model.utils.Constants
import kotlinx.android.synthetic.main.supported_apps_list.view.*


class SupportedAppsAdapter(private val listType: Constants.EnabledAppsDisplayType, private var supportedAppsList: List<App>): RecyclerView.Adapter<SupportedAppsAdapter.AppsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppsViewHolder {
        val itemView = if(listType == Constants.EnabledAppsDisplayType.VERTICAL) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.supported_apps_list, parent, false)
        }else{
            LayoutInflater.from(parent.context)
                .inflate(R.layout.enabled_apps_grid_item, parent, false)
        }
        return AppsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppsViewHolder, position: Int) {
        holder.setData(supportedAppsList[position], listType)
    }

    override fun getItemCount(): Int {
        return supportedAppsList.size
    }

    fun updateList(supportedAppsList: List<App>){
        this.supportedAppsList = supportedAppsList
        notifyDataSetChanged()
    }

    class AppsViewHolder(view: View) : RecyclerView.ViewHolder(view){

        fun setData(app: App, listType: Constants.EnabledAppsDisplayType){
            try {
                val icon: Drawable = itemView.context.packageManager.getApplicationIcon(app.packageName)
                itemView.appIcon.setImageDrawable(icon)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            if(listType == Constants.EnabledAppsDisplayType.VERTICAL){
                itemView.appEnableSwitch.text = app.name
                itemView.appEnableSwitch.tag = app
                itemView.appEnableSwitch.isChecked = PreferencesManager.getPreferencesInstance(itemView.context).isAppEnabled(app)
                itemView.appEnableSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                    val preferencesManager = PreferencesManager.getPreferencesInstance(itemView.context)
                    if (!isChecked && preferencesManager.enabledApps.size <= 1) { // Keep at-least one app selected
                        // Keep at-least one app selected
                        Toast.makeText(
                            itemView.context,
                            itemView.context.resources.getString(R.string.error_atleast_single_app_must_be_selected),
                            Toast.LENGTH_SHORT
                        ).show()
                        buttonView.isChecked = true
                    }else {
                        preferencesManager.saveEnabledApps(buttonView.tag as App, isChecked)
                    }
                }
            }
        }
    }
}