[![Build Status](https://travis-ci.org/navicore/naviblob.svg?branch=master)](https://travis-ci.org/navicore/naviblob)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6b9e137785184eb4b91048e8da24a0e9)](https://www.codacy.com/app/navicore/naviblob?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=navicore/naviblob&amp;utm_campaign=Badge_Grade)

# Read Blob Storage into Akka Streams

Why?  Usually I'll use Akka Streams with streaming sources.  This connector is
for when I want to replay historical streaming data into an already existing
code base that was designed for streaming.  The initial use case is to replay
Azure Eventhubs "capture" avro data back into other Eventhubs.

## Current Storage Sources
 1. Azure Blobs with Avro created by Azure Eventhubs Capture.
 2. S3 <TBD>
 3. GS <TBD>
 4. Other cloud storage implementations TBD.

## USAGE

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
