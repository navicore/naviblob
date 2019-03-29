package onextent.akka.naviblob.azure.avro

/**
  * for reading data from Azure Eventhubs capture blobs
  */
case class EhRecord(SequenceNumber: Long,
                    Offset: String,
                    EnqueuedTimeUtc: String,
                    SystemProperties: Map[String, String],
                    Properties: Map[String, String],
                    Body: String)
