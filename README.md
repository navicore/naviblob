[![Build Status](https://travis-ci.org/navicore/naviblob.svg?branch=master)](https://travis-ci.org/navicore/naviblob)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/6b9e137785184eb4b91048e8da24a0e9)](https://www.codacy.com/app/navicore/naviblob?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=navicore/naviblob&amp;utm_campaign=Badge_Grade)

# Read Blob Storage into Akka Streams

This connector is for when I replay historical data-at-rest into an
existing code base that had been designed for streaming.  The initial use
case is to replay Azure Eventhubs "capture" avro data back into Eventhubs
and Kafka, allowing me to back-test new streaming code.

## Current Storage Sources

1.  Azure Blobs with Avro
2.  Azure Blobs newline delimited text
3.  Azure Blobs with Gzip'd newline delimited text
4.  Other cloud storage implementations TBD

## INSTALL

Binaries available via [maven](https://mvnrepository.com/artifact/tech.navicore/naviblob): - check for latest version

Update your `build.sbt` dependencies with:
```scala
// https://mvnrepository.com/artifact/tech.navicore/naviblob
libraryDependencies += "tech.navicore" %% "naviblob" % "1.1.1"
```

## USAGE

This example reads avro data from Azure blobs.  It uses [avro4s] to create
the avro schema from a case class type parameter.

Create a config, a connector, and a source via the example below - note the
`EhRecord` type parameter can be replaced with a case class that represents your
avro schema.

```scala
    val consumer = ... // some Sink
    ...
    ...
    ...
    
    // credentials and location
    implicit val cfg: BlobConfig = BlobConfig(STORAGEACCOUNT, STORAGEKEY, CONTAINERNAME, STORAGEPATH)
    
    // type parameter for avro deserialize - in this example: `EhRecord`
    val connector: ActorRef = actorSystem.actorOf(AvroConnector.props[EhRecord])
    
    val src = NaviBlob[EhRecord](connector)

    ...
    ...
    ...
    src.runWith(consumer)
    ...
    ...
    ...
```

For line delimited text files, use the `TextBlobConnector` connector actor:

```scala
    ...
    ...
    ...
    val connector: ActorRef = actorSystem.actorOf(TextBlobConnector.props)
    ...
    ...
    ...
```

For GZIP'd jsonl text, use the `GzipTextBlobConnector` connector actor:

```scala
    ...
    ...
    ...
    val connector: ActorRef = actorSystem.actorOf(GzipTextBlobConnector.props)
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
[avro4s]:https://github.com/sksamuel/avro4s

