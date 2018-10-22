package onextent.akka.naviblob.azure

case class BlobConfig(accountName: String,
                      accountKey: String,
                      containerName: String,
                      path: Option[String])
