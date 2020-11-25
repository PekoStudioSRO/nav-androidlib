package cz.pekostudio

import android.content.Context
import cz.pekostudio.api.Api

/**
 * Created by Lukas Urbanek on 12/06/2020.
 */
class ApiConfig(context: Context, config: ApiConfig.() -> Unit) {
    init {
        config()
        if (ApiConfigData.api.autoSaveUserInstance) {
            Api.loadUserInstance(context)
        }
    }

    fun api(api: ApiConfigData.Api.() -> Unit) {
        api(ApiConfigData.api)
    }
}