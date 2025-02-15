package us.abstracta.jmeter.javadsl.azure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static us.abstracta.jmeter.javadsl.JmeterDsl.autoStop;
import static us.abstracta.jmeter.javadsl.JmeterDsl.csvDataSet;
import static us.abstracta.jmeter.javadsl.JmeterDsl.dummySampler;
import static us.abstracta.jmeter.javadsl.JmeterDsl.testPlan;
import static us.abstracta.jmeter.javadsl.JmeterDsl.threadGroup;
import static us.abstracta.jmeter.javadsl.core.listeners.AutoStopListener.AutoStopCondition.sampleTime;

import java.time.Duration;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;
import us.abstracta.jmeter.javadsl.core.engines.AutoStoppedTestException;
import us.abstracta.jmeter.javadsl.util.TestResource;

public class AzureEngineIT {

  @Disabled("This azure test is now reporting 0 samples counts, even though we get the sample "
      + "result in results file but general dashboard in Azure is reporting 0 as well. "
      + "We will disable it until we get an answer from Azure Load Testing support.")
  @Test
  public void shouldRunTestInAzure() throws Exception {
    TestPlanStats stats = testPlan(
        threadGroup(1, 1,
            csvDataSet(new TestResource("users.csv")).randomOrder(),
            dummySampler("${USER}")
        )
    ).runIn(new AzureEngine(System.getenv("AZURE_CREDS"))
        .testName("jmeter-java-dsl")
        .testTimeout(Duration.ofMinutes(10)));
    assertThat(stats.overall().samplesCount()).isEqualTo(1);
  }

  @Disabled("For some reason when test run is cancelled, azure keeps it in cancelling state for "
      + "more than 10 minutes. Until we don't get an answer from Azure Load Testing team, and can "
      + "properly adapt associated logic, we will disable this test to avoid failing build")
  @Test
  public void shouldAutoStopTestWhenConditionIsMet() {
    assertThrows(AutoStoppedTestException.class, () ->
        testPlan(
            threadGroup(1, Duration.ofMinutes(1),
                dummySampler("OK")
                    .responseTime(Duration.ofSeconds(1))
                    .simulateResponseTime(true),
                autoStop()
                    .when(sampleTime().percentile(99).greaterThan(Duration.ZERO))
            )
        ).runIn(new AzureEngine(System.getenv("AZURE_CREDS"))
            .testName("jmeter-java-dsl")
            .testTimeout(Duration.ofMinutes(10)))
    );
  }

}
