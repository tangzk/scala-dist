package sbaz.messages;

import scala.xml._;


// A message from the server to the client listing all packages
// currently listed on the server.
case class LatestPackages(packages: AvailableList)
extends Message {
  override def toXML: Node = 
<latestpackages>
  { packages.toOldXML }
</latestpackages> ;
}


object LatestPackagesUtil {
  def fromXML(node: Node) = {
    val packsXML = (node \ "packageset")(0) ;
    val packs = AvailableListUtil.fromXML(packsXML);
    new LatestPackages(packs)
  }
}
