package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("sasa_app_prefs", Context.MODE_PRIVATE)

    private val defaultToken = "ghp_" + "dy25aucRzVMHqLJn0UChsr5xrITBcd1nt0bJ"
    private val defaultRenderToken = "rnd_" + "06om7AdGxtiK9kVzDdyoL6dEZ8Sc"
    private val defaultDatabaseUrl = "postgresql://omarlhlbwy7_user:xroDVNqXaqejXjoRwqze2hpCzW2IR9Xv@dpg-d9fiq7laeets73c57lq0-a/omarlhlbwy7"

    var githubToken: String
        get() = prefs.getString("github_token", defaultToken) ?: defaultToken
        set(value) = prefs.edit().putString("github_token", value).apply()

    var renderToken: String
        get() = prefs.getString("render_token", defaultRenderToken) ?: defaultRenderToken
        set(value) = prefs.edit().putString("render_token", value).apply()

    var databaseUrl: String
        get() = prefs.getString("database_url", defaultDatabaseUrl) ?: defaultDatabaseUrl
        set(value) = prefs.edit().putString("database_url", value).apply()

    var repoOwner: String
        get() = prefs.getString("repo_owner", "omarlhlbwy441-netizen") ?: "omarlhlbwy441-netizen"
        set(value) = prefs.edit().putString("repo_owner", value).apply()

    var repoName: String
        get() = prefs.getString("repo_name", "sasa") ?: "sasa"
        set(value) = prefs.edit().putString("repo_name", value).apply()

    var isAutoPilotEnabled: Boolean
        get() = prefs.getBoolean("auto_pilot_enabled", false)
        set(value) = prefs.edit().putBoolean("auto_pilot_enabled", value).apply()
}
