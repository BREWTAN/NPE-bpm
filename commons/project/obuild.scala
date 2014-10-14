import sbt._
import Keys._
import IvyRepositories.{localDepRepo, localRepoArtifacts, makeLocalRepoSettings, localDepRepoCreated}


object obuild extends Build {


lazy val publishM2Configuration = 
   TaskKey[PublishConfiguration]("publish-m2-configuration", 
     "Configuration for publishing to the .m2 repository.") 

lazy val publishM2 = 
   TaskKey[Unit]("publish-m2", 
      "Publishes artifacts to the .m2 repository.") 

  lazy val m2Repo = 
    Resolver.file("publish-m2-local", 
      Path.userHome / ".m2" / "repository") 
      
  
  lazy val publishTo = Resolver.file("Local", Path.userHome / ".m2" / "repository" asFile)
  


}