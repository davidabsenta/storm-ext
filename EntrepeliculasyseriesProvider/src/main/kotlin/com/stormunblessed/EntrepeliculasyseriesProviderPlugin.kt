package com.stormunblessed

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.BasePlugin
import android.content.Context
import com.lagradost.cloudstream3.movieproviders.EntrepeliculasyseriesProvider

@CloudstreamPlugin
class EntrepeliculasyseriesProviderPlugin: BasePlugin() {
    override fun load() {
        // All providers should be added in this manner. Please don't edit the providers list directly.
        registerMainAPI(EntrepeliculasyseriesProvider())
    }
}