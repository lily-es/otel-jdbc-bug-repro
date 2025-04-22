import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.opentelemetry.instrumentation.hikaricp.v3_0.HikariTelemetry
import io.opentelemetry.instrumentation.jdbc.datasource.JdbcTelemetry
import io.opentelemetry.sdk.OpenTelemetrySdk
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.MountableFile

class Test {
    @Test
    fun `Select`() {
        transaction(db) {
            val result = TestTable.selectAll().map { row -> row[TestTable.test_row] }
            assertEquals("TestHappy", result.first())
        }
    }

    @Test
    fun `Insert`() {
        transaction(db) {
            TestTable.insert {
                it[test_row] = "TestSad"
            }
        }
    }


    @Test
    fun `Create table`() {
        transaction(db) {
            SchemaUtils.create(NewTable)
        }
    }

    companion object {
        object TestTable: Table("test") {
            val test_row = varchar("test_row", 100)

            override val primaryKey: PrimaryKey = PrimaryKey(test_row)
        }

        object NewTable: Table("new") {
            val test_row = varchar("test_row", 100)

            override val primaryKey: PrimaryKey = PrimaryKey(test_row)
        }

        private val postgres = PostgreSQLContainer("postgres:17.4").withUsername("test").withPassword("pass")
            .withCopyFileToContainer(MountableFile.forClasspathResource("init-db.sql"), "/docker-entrypoint-initdb.d/")


        private lateinit var pool: HikariDataSource
        private lateinit var db: Database

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            postgres.start()
            val telemetry = OpenTelemetrySdk.builder().build()
            pool = configurePool(telemetry)
            val database = Database.connect(JdbcTelemetry.create(telemetry).wrap(pool))
            db = database
        }

        private fun configurePool(openTelemetrySdk: OpenTelemetrySdk): HikariDataSource {
            val config = HikariConfig()

            config.username = postgres.username
            config.password = postgres.password
            config.jdbcUrl = postgres.getJdbcUrl()

            val hikariTelemetry = HikariTelemetry.create(openTelemetrySdk)
            config.metricsTrackerFactory = hikariTelemetry.createMetricsTrackerFactory()

            return HikariDataSource(config)
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            try {
                pool.close()
            } finally {
                postgres.stop()
            }
        }
    }
}