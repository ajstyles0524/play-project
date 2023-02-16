import javax.inject.Inject

import play.filters.cors.CORSFilter
import play.api.http.DefaultHttpFilters
import play.api.http.EnabledFilters

class Filters @Inject() (enabledFilters: EnabledFilters, corsFilter: CORSFilter)
  extends DefaultHttpFilters(enabledFilters.filters :+ corsFilter: _*)