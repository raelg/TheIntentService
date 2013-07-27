--
TheIntentService 
-- 

This is an exmaple project showing how to combine an IntentService, a ResultReceiver and a Handler to create a framework for RESTful services such that the RESTful controllers can be Unit tested in a regular JVM and run in the IDE. It also shows the corresponding Integration test, and how to invoke the service in an activity to update the UI. 

Most of the core code is in Scala, and the included IntelliJ iml project files are configured to run ProGuard before deploying. The baseUrl can be overriden in a local environment by creating "local.properties" in the assets directory. When run from Maven in a CI environment, the baseUrl can be set by filtering project.properties and supplying an additional command line argument with the relevant baseUrl. 






