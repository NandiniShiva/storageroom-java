StorageRoom Java API
=========

storageroom-java is a Java binding for [StorageRoom] API. StorageRoom is an online hosted content management system, good for stashing away files and data. This API allows you to publish, query, and read data and documents posted to StorageRoom from your Java application.

Some features that you'll like

  - Ability to connect to your Account using an Application you have configured in the StorageRoom UI
  - Easy coding to traverse down the Application into the Collections, Fields and Entries
  - Good support for the verbs - PUT POST GET DELETE

Additionally, the implementation should be easy to use:

  - Given the webby nature of SR, I don't expect you are a Java expert. I tried to keep the Java syntax simple
  - Unit and integration tests are included
  - Reasonable javadoc is included
  - Sample code can be found in the unit tests and on this page
  - I tried to eliminate dependencies as best I could; really just a dependency on HttpClient is needed
  - You should be able to drop this project into your application quickly

Unfortunately, not all is rainbows and unicorns:

  - I tried not to use Java Generics, as it makes an API less approachable, but I found they were too useful to pass up
  - There are some missing features here and there (see bottom of this page)

It is easy to get started. The code below should hopefully convince you that the API is easy to work with.

*Peter Laird*
January 2013

Example: Getting the Contents of a Collection 
------

This code shows how to connect to your Account via your Application, and then navigating into a collection


    Application app = Application.getInstance("JavaClient");
    boolean success = app.connect("my_acct_id", "my_auth_token", true);
    Collections colls = app.getCollections(true);
    Collection col = colls.findCollection("ReadOnlyCollection");
    CollectionEntries entries = col.getEntries();
    PageOfEntries results = entries.queryAll();
    
    // just pick off the first entry in the page for this sample
    Entry entry = results.asList().get(0);
    for (GenericField<?> field : entry.values()) {
        String id = field.getIdentifier();
        if ("sku".equals(id)) {
            String sku = field.getValueWrapper().getInnerValue());
            ...
        }
    }

Example: Adding, Deleting, Querying an Entry in a Collection 
------

This code shows how to connect to your Account via your Application, and then manipulate an Entry in a Collection


    Application app = Application.getInstance("JavaClient");
    boolean success = app.connect("my_acct_id", "my_auth_token", true);
    Collections colls = app.getCollections(true);
    Collection col = colls.findCollection("ReadWriteCollection");
    CollectionEntries entries = col.getEntries();
	
    String insertedSKU = "JUNIT"+System.currentTimeMillis();
	
    // INSERT
    Entry newEntry = entries.createNewEntryTemplateObject();
    StringField sku = (StringField)newEntry.get("sku");
    sku.setValue(new StringValue(insertedSKU));
    IntegerField inStock = (IntegerField)newEntry.get("in_stock");
    inStock.setValue(new IntegerValue(888));
    success = entries.insertNewEntry(newEntry);

    // QUERY
    CollectionQuery queryDef = new CollectionQuery();
    queryDef.filterOptions = "sku="+insertedSKU;
    PageOfEntries results = entries.query(queryDef);

    // UPDATE
    Entry theEntry = results.asList().get(0);
    inStock = (IntegerField)theEntry.get("in_stock");
    inStock.setValue(new IntegerValue(67890));
    success = theEntry.update();
	
    // QUERY
    queryDef.filterOptions = "in_stock=67890";
    results = entries.query(queryDef);

    // DELETE
    theEntry = results.asList().get(0);
    success  = theEntry.delete();
    
Using the Library
-------

*Distribution*

The dist directory contains a built version. Feel free to just pick that up (and all the dependencies in the lib directory) and go from there.

    dist/storageroom-java-1.0.jar
    lib/*.jar

*Build the Library*

I use Gradle to build the library, but as you can see it is easy to adapt into whatever build infra you want. To use Gradle, do this once you have gradle installed:

    gradle build

That's it. That will build the JAR, and run the Junit tests.

Missing Features
-------

Sadly, I am not finished with the integration. And, given that my needs are already met, I might be slow in finishing these out. If you have a need let me know, otherwise I may not get to these any time soon.

  - Example for how to build with Maven (pom.xml) to complement the Gradle build
  - Sufficient logging; in the short term you may have to step debug if you hit problems
  - Anything to do with Web hooks
  - PUT and POST for File or Image Fields (GET works)
  - Anything with Association Fields
  - Anything interesting with deleted entries

  [StorageRoom]: http://storageroomapp.com/
    
