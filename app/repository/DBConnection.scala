package repository

import com.typesafe.config.{Config, ConfigFactory}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import slick.jdbc.JdbcBackend.Database
import utils.Logging

object DBConnection extends Logging {
  //load config file
  val config: Config = ConfigFactory.load()
  //  create HikariCP config
  private val hikariConfig = new HikariConfig()
  hikariConfig.setJdbcUrl(config.getString("db.uri"))
  hikariConfig.setUsername(config.getString("db.user"))
  hikariConfig.setPassword(config.getString("db.secret"))
  hikariConfig.setMaximumPoolSize(config.getInt("db.maximum_pool_size")) // Set the maximum number of connections in the pool
  hikariConfig.setMinimumIdle(config.getInt("db.minimum_idle_connection")) // Set the minimum number of idle connections in the pool
  //create datasource
  private val dataSource = new HikariDataSource(hikariConfig)
  // Create the database connection using the data source
  val dbConnection: Database = Database.forDataSource(dataSource, Some(config.getInt("db.maximum_pool_size")))
  logger.info("database connection established")

  // Function to close the database connection
  def closeConnection(): Unit = {
    dbConnection.close()
    dataSource.close()
    logger.info("database connection closed")
  }

  val handleDB = new OperationHandler(dbConnection)
}

