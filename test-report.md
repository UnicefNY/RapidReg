## Test Report for Primero

###Primero test structure include:
* Unit Test
* API Test
* Functional Manual Test


### Unit Test
#### Frameworks
* Library: jUnit, robolectric, easymock, powermock
* Coverage: 50%

#### Test cases
Test cases locate at app/src/test/ under RapidReg root project.

#### How to run
$ ./gradlew testDebugUnitTest


### API Test
#### Fameworks
* Moco

#### Test cases
Repository URL:  https://bitbucket.org/sjyuan-cc/primero-mock-server.git

Test cases locate at /apis.json under primero-mock-server  root project.

#### How to run
`$ java -jar moco.jar -p 12306 -c apis.json`



