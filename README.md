# Jenkins-mattermost plugin

This plugin allows to post build notifications to a Mattermost channel.

## Development Instructions

* Build and run the unit tests:
```
mvn clean install
```

* Install the plugin into a locally-running Jenkins:
```
mvn hpi:run
```
* Local check in:
```
http://localhost:8080/jenkins
```
