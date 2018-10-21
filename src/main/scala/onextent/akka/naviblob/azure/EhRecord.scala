package onextent.akka.naviblob.azure

case class EhRecord(SequenceNumber: Long,
                    Offset: String,
                    EnqueuedTimeUtc: String,
                    SystemProperties: Map[String, String],
                    Properties: Map[String, String],
                    Body: String)
