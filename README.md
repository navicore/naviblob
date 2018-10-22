Read Blob Storage into Akka Streams
---

### Current Storage Sources:
1. Azure Blobs with Avro created by Azure Eventhubs Capture.
1. ...
1. ...
1. Other cloud storage implementations TBD.

# USAGE

update your `build.sbt` dependencies with:

```scala
// https://mvnrepository.com/artifact/tech.navicore/naviblob
libraryDependencies += "tech.navicore" %% "naviblob" % "TBD"
```

create a config, a connector, and a graph stage via:

```scala
    val consumer = ... // some Sink
    ...
    ...
    ...
    implicit val cfg: AzureBlobConfig = AzureBlobConfig(storageAccount, storageKey, containerName, storagePath)
    val connector: ActorRef = actorSystem.actorOf(EhCaptureConnector.props(cfg))
    val srcGraph = new NaviBlob(connector)

    // then create a back-pressured stream that will read data from blobs found in storagePath

    val r: Future[Done] = Source.fromGraph(srcGraph).runWith(consumer)
    ...
    ...
    ...
```

## OPS

### publish local

```console
sbt +publishLocalSigned
```

### publish to nexus staging

```console
export GPG_TTY=$(tty)
sbt +publishSigned
sbt sonatypeReleaseAll
```

---
