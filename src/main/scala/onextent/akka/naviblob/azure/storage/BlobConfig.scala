package onextent.akka.naviblob.azure.storage

case class BlobConfig(accountName: String,
                      accountKey: String,
                      containerName: String,
                      path: Option[String])
