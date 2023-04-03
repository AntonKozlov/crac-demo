
- Clone the project

```
git clone git@github.com:tzolov/crac-demo.git
```

- Change directory to the `crac-demo` folder and build:

```
mvn clean install
```

under target you will see the `crac-demo-1.0.0-SNAPSHOT-shaded.jar` ubber jar.


- From within the `crac-demo` folder run the `java_17_crac` container:

```
docker run --network "host" -it --privileged --rm --name=my-crac-app -v $(pwd)/target:/opt/mnt tzolov/java_17_crac:latest /bin/bash
```

The `java_17_crac` uses Ububu22.04 and pre-installs JDK 17 CRaC + 5.

- Start the crac-demo application inside the running container:

```
java -XX:CRaCCheckpointTo=/opt/crac-files -jar /opt/mnt/crac-demo-1.0.0-SNAPSHOT-shaded.jar
```

wait for 15-20 seconds until you see printout like `0:1:2:3:4:5...`

- From another terminal trigger the CRaC checkpoint:

```
docker exec -it  --privileged -u root my-crac-app jcmd /opt/mnt/crac-demo-1.0.0-SNAPSHOT-shaded.jar JDK.checkpoint
```
later creates the checkpoint under the container's `/opt/crac-files` folder and stops the original application.

- In the first terminal (e.g. the running `my-crac-app` container) restart the application from the checkpoint:

```
java -XX:CRaCRestoreFrom=/opt/crac-files
```

