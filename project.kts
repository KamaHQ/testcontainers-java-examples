import com.github.kama.*
import com.github.kama.tasks.*

flow {
    echo("Starting execution $executionId")
    echo("trigger: $trigger")

    node {
        echo("All execution will be performed on ${node.id}")

        checkout(repository)

        echo("All cloned.")

        parallel((0..5).map { i ->
            flow {
                echo("Processing #$i...")
                sleep(3.second)
                echo("Done #$i")
            }
        })

        val answer = input("Continue?")
        echo("Answer was: $answer")

        if (answer) {
            exec("gradle --no-daemon init")

            dir("spring-boot") {
                file("build.gradle", append = true, text = """
                    test.testLogging {
                        displayGranularity 1
                        showStandardStreams = true
                        events "STARTED", "PASSED", "FAILED", "SKIPPED"
                    }
                """)

                exec("../gradlew --no-daemon --console=rich clean test")
            }
        }
    }
}
