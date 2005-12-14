package sbaz;

import java.io.{File, StringReader} ;
import scala.xml._ ;
import scala.collection.immutable._ ;

// Information about one package that is currently installed.
//
// The "complete" flag indicates whether the package is fully
// installed; it is set to false while a package
// is being installed or uninstalled, and set to true
// afterwards.
class InstalledEntry(val name:String, val version:Version,
		     val files:List[File],
		     val depends:Set[String],
		     val complete:Boolean)
{

  def this(name0:String, version0:Version, files0:List[File], depends:Set[String]) = {
    this(name0, version0, files0, depends, false);
  }

  val packageSpec = PackageSpec(name, version) ;

  // return the same entry but with complete=true
  def completed = { new InstalledEntry(name, version, files, depends, true) }

  // return the same entry but with complete=false
  def broken = { new InstalledEntry(name, version, files, depends, false) }

  def toXML:Node = {
<installedpackage>
  <name>{name}</name>
  <version>{version}</version>
  <files>{files.toList.map(f => <filename>{f.getPath()}</filename>)}</files>
  <depends>{depends.toList.map(dep => <name>{dep}</name>)}</depends> 
  {if(complete)
	List(<complete/>)
      else 
	Nil}
</installedpackage>
	  };

  override def toString() = {
    name + " " + version + 
    " (" + files.length + " files)" +
    (if(complete) "" else " (incomplete)")
  }
}


object InstalledEntryUtil {
   def fromXML(xml:Node) = {
     // XXX need to throw a reasonable error for malformed input
     val parts = xml ;
     val name = (parts \ "name")(0).child(0).toString(false) ;
     val version = new Version((parts \ "version")(0).child(0).toString(false)) ;
     val dependsList =
	  (parts \ "depends" \ "name").toList
	  .map(nod => nod(0).child(0).toString(false)) ;
     val depends = ListSet.Empty[String].incl(dependsList) ;
     val files =
          (parts \ "files" \ "filename").toList.map(s =>
                  new File(s(0).child(0).toString(false))) ;
     val complete = (parts \ "complete").length > 0 ;
     new InstalledEntry(name, version, files, depends, complete)
   }
}



object TestInstalledEntry {
  def main(args:Array[String]) = {
    val xml =
      "<installedpackage>\n" +
      "<name>foo</name>\n" +
      "<version>1.5</version>\n" +
      "<files>\n" +
      "  <filename>lib/foo.jar</filename>\n" +
      "  <filename>doc/foo/foo.html</filename>\n" +
      "</files>\n" +
      "<complete/>\n" +
      "</installedpackage>\n" ;

    val node = XML.load(new StringReader(xml)) ;
    val entry = InstalledEntryUtil.fromXML(node) ;

    Console.println(entry);
    Console.println(entry.toXML);
  }
}
