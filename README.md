

1. From within the crac-demo top folder run:

```
docker run --network "host" -it --privileged --rm --name=my-crac-app -v $(pwd)/target:/opt/mnt tzolov/java_17_crac:latest /bin/bash
```

then start the application inside the running container like this:

```
java -XX:CRaCCheckpointTo=/opt/crac-files -jar /opt/mnt/crac-demo-1.0.0-SNAPSHOT-shaded.jar
```

wait for 15-20 seconds until you see printout like `0:1:2:3:4:5...`

Then from another terminal trigger the CRaC checkpoint:

```
docker exec -it  --privileged -u root my-crac-app jcmd /opt/mnt/crac-demo-1.0.0-SNAPSHOT-shaded.jar JDK.checkpoint
```
later creates the checkpoint under the container's `/opt/crac-files` folder and stops the original application.

In the terminal of the `my-crac-app` container restart the application from the checkpoint:

```
java -XX:CRaCRestoreFrom=/opt/crac-files
```

