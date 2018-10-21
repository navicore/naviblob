package onextent.akka.naviblob.azure

case class AzureBlobConfig(accountName: String,
                           accountKey: String,
                           containerName: String,
                           path: Option[String])
