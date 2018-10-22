[![Build Status](https://travis-ci.org/navicore/naviblob.svg?branch=master)](https://travis-ci.org/navicore/naviblob)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6b9e137785184eb4b91048e8da24a0e9)](https://www.codacy.com/app/navicore/naviblob?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=navicore/naviblob&amp;utm_campaign=Badge_Grade)

# Read Blob Storage into Akka Streams

This connector is for when I want to replay historical data-at-rest into an
existing code base that had been designed for streaming.  The initial use
case is to replay Azure Eventhubs "capture" avro data back into other Eventhubs,
allowing me to back-test new streaming code.

## Current Storage Sources
1.  Azure Blobs with Avro created by Azure Eventhubs Capture
2.  Other cloud storage implementations TBD

## USAGE

update your `build.sbt` dependencies with:

```scala
// https://mvnrepository.com/artifact/tech.navicore/naviblob
libraryDependencies += "tech.navicore" %% "naviblob" % "TBD"
```

create a config, a connector, and a source via:

```scala
    val consumer = ... // some Sink
    ...
    ...
    ...
    implicit val cfg: BlobConfig = BlobConfig(STORAGEACCOUNT, STORAGEKEY, CONTAINERNAME, STORAGEPATH)
    val connector: ActorRef = actorSystem.actorOf(AvroConnector.props[EhRecord])  // pass case class for avro data deserialize
    val src = NaviBlob[EhRecord](connector)
    ...
    ...
    ...
    src.runWith(consumer)
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
