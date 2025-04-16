package repository

import com.github.tminglei.slickpg._


//for json conversion
trait ExtendedPostgresProfile extends ExPostgresProfile
  with PgJsonSupport
  with PgPlayJsonSupport
  with PgArraySupport
  with PgHStoreSupport
  with PgDate2Support {
  override val api = ExtendedPostgresAPI

  def pgjson = "jsonb"

  object ExtendedPostgresAPI extends API with JsonImplicits
}

object ExtendedPostgresProfile extends ExtendedPostgresProfile