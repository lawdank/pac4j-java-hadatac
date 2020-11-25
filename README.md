# HaDatAc Upgrade

## System Requirements
**Java** - **openjdk 11**

Steps : Open Terminal (mac)/ putty(windows) & excute these commands.

      brew tap AdoptOpenJDK/openjdk
      brew cask install adoptopenjdk11
      java -version //Check the downloaded version is correct. Below should be the expected result
          openjdk version "11.0.2" 2019-01-15_     
          OpenJDK Runtime Environment AdoptOpenJDK (build 11.0.2+9)     
          OpenJDK 64-Bit Server VM AdoptOpenJDK (build 11.0.2+9, mixed mode)

**Solr** -  **8.6.1**
Config Files in the code will take care of it

**Play** - **2.8.2** 
Config Files in the code will take care of it

**Pac4j** - **4.0.3** 
Config Files in the code will take care of it

**play-pac4j** - **10.0.1** 
Config Files in the code will take care of it

## Code Changes
Comprehensive list of play framework related changes is [here.](https://www.playframework.com/documentation/2.8.x/Requirements)
| Original Code        | New Code           | Details  |
| ------------- |:-------------:| -----:|
| File file = (File)uploadedfile.getFile();| import play.libs.Files.TemporaryFile;      TemporaryFile temporaryFile = (TemporaryFile) uploadedfile.getRef();     File file = temporaryFile.path().toFile(); | getFile() method is now implemented in two steps, as latest play version provided access to temporaryfile. |
| Request request      | Http.Request request      |    |
| session() | request.session(); [Add Http.Request request in the method as parameter]*      |    Eg: testApi(){session();} -> testApi(Http.Request request) {request.session();} |
|response().setHeader("Content-disposition", String.format("attachment; filename=%s", dataFile.getFileName()));|return ok(new File(dataFile.getAbsolutePath())).withHeader("Content-disposition", String.format("attachment; filename=%s", dataFile.getFileName()));||
|bindFromRequest();|bindFromRequest(request); [Add Http.Request request in the method as parameter]*|Eg: testApi(){bindFromRequest ();} -> testApi(Http.Request request) { bindFromRequest (request);}|
|request().body()|request.body() [Add Http.Request request in the method as parameter]|Eg: testApi(){request().body();} -> testApi(Http.Request request) {request.body();}|
|Back to main menu (in scala template)|Portal.index()|Application.formIndex()|



#### *Update _routes.conf_ parameters accordingly, Http.Request should be added in routes as **request:Request**
Build the project and launch the app at [http://localhost:9000](http://localhost:9000)
