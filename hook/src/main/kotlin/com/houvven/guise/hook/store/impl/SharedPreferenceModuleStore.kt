package com.houvven.guise.hook.store.impl

import android.content.Context
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge
import com.houvven.guise.hook.profile.HookProfiles
import com.houvven.guise.hook.store.ModuleStore

object SharedPreferenceModuleStore {

    class Hooker(context: Context) : ModuleStore.Hooker() {

        private val prefs: YukiHookPrefsBridge = context.prefs(name)

        override val configuredPackages: Set<String>
            get() = prefs.all().keys

        override fun set(profiles: HookProfiles) {
            prefs.edit {
                with(profiles) {
                    if (isAvailable) putString(packageName!!, profiles.toJsonStr())
                    else remove(packageName!!)
                }
            }
        }

        override fun get(packageName: String): HookProfiles {
            return HookProfiles.fromJsonStr(prefs.getString(packageName))
        }

        override fun isEnabled(packageName: String): Boolean {
            return prefs.contains(packageName)
        }
    }


    class Hooked(packageParam: PackageParam) : ModuleStore.Hooked() {

        private val prefs: YukiHookPrefsBridge = packageParam.prefs(name)

        override fun get(packageName: String): HookProfiles {
            return HookProfiles.fromJsonStr(prefs.getString(packageName))
        }

        override fun isEnabled(packageName: String): Boolean {
            return prefs.contains(packageName)
        }
    }
}