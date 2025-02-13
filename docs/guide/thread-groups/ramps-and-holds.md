### Thread ramps and holds

When working with many threads, it is advisable to configure a ramp-up period, to avoid starting all threads at once affecting performance metrics and generation.

You can easily configure a ramp-up with the DSL like this:

```java
threadGroup().rampTo(10, Duration.ofSeconds(5)).holdIterating(20) // ramp to 10 threads for 5 seconds (1 thread every half second) and iterating each thread 20 times
threadGroup().rampToAndHold(10, Duration.ofSeconds(5), Duration.ofSeconds(20)) //similar as above but after ramping up holding execution for 20 seconds
```

Additionally, you can use and combine these same methods to configure more complex scenarios (incremental, peak, and any other types of tests) like the following one:

```java
threadGroup()
    .rampToAndHold(10, Duration.ofSeconds(5), Duration.ofSeconds(20))
    .rampToAndHold(100, Duration.ofSeconds(10), Duration.ofSeconds(30))
    .rampTo(200, Duration.ofSeconds(10))
    .rampToAndHold(100, Duration.ofSeconds(10), Duration.ofSeconds(30))
    .rampTo(0, Duration.ofSeconds(5))
    .children(
      httpSampler("http://my.service")
    )
```

Which would translate into the following threads' timeline:

![Thread Group Timeline](./images/ultimate-thread-group-timeline.png)

Check [DslThreadGroup](/jmeter-java-dsl/src/main/java/us/abstracta/jmeter/javadsl/core/threadgroups/DslThreadGroup.java) for more details.

::: tip
To visualize the threads timeline, for complex thread group configurations like the previous one, you can get a chart like the previous one by using provided `DslThreadGroup.showTimeline()` method.
:::

::: tip
If you are a JMeter GUI user, you may even be interested in using provided `TestElement.showInGui()` method, which shows the JMeter test element GUI that could help you understand what will DSL execute in JMeter. You can use this method with any test element generated by the DSL (not just thread groups).

For example, for the above test plan you would get a window like the following one:

![UltimateThreadGroup GUI](./images/ultimate-thread-group-gui.png)
:::

::: tip
When using multiple thread groups in a test plan, consider setting a name (eg: `threadGroup("main", 1, 1, ...)`)on them to properly identify associated requests in statistics & jtl results.
:::
