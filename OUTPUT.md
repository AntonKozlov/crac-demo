

Here is the output of starting a CRaC enabled container, runing the `crac-demo` application inside and triggering the checkpoint:

```shell
(main) docker run --network "host" -it --privileged --rm --name=my-crac-app -v $(pwd)/target:/opt/mnt tzolov/java_17_crac:latest /bin/bash

root@docker-desktop:/# java -XX:CRaCCheckpointTo=/opt/crac-files -jar /opt/mnt/crac-demo-1.0.0-SNAPSHOT-shaded.jar

20:12:24.781 [main] INFO  com.example.crac.CrackDemo - CRaC Demo of a processor with dependant context
20:12:27.791 [main] INFO  com.example.crac.CrackDemo - ProcessorContext STARTED!
0:1:2:3:4:5:6:7:8:9:10:11:12:13:14:15:
20:12:44.208 [Attach Listener] INFO  com.example.crac.CrackDemo - call 'beforeCheckpoint'
16:17:18:
20:12:47.213 [Attach Listener] INFO  com.example.crac.CrackDemo - ProcessorContext STOPPED!
Apr 03, 2023 8:12:47 PM jdk.internal.util.jar.PersistentJarFile beforeCheckpoint
INFO: /opt/mnt/crac-demo-1.0.0-SNAPSHOT-shaded.jar is recorded as always available on restore
Killed
```

Then try to restore from the created checkpoint:

```shell
root@docker-desktop:/# java -XX:CRaCRestoreFrom=/opt/crac-files

20:13:24.879 [Attach Listener] INFO  com.example.crac.CrackDemo - call 'afterRestore'
20:13:24.879 [main] ERROR com.example.crac.CrackDemo - ProcessorContext not initialized yet! [i =19]
Exception in thread "main" java.lang.RuntimeException: ProcessorContext not initialized yet!
    at com.example.crac.CrackDemo$Processor.next(CrackDemo.java:64)
    at com.example.crac.CrackDemo.main(CrackDemo.java:43)
```

The processor (e.g. main) call stack is restored and started before the afterRestore has completed lading to an invalid state. 