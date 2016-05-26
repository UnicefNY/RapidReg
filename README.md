### RapidReg

**RapidReg** is an Android-based mobile application that lets aid workers collect, 
sort and share information about children in emergency situations.

RapidReg is specifically designed to streamline and speed up Family Tracing and Reunification 
efforts both in the immediate aftermath of a crisis and during ongoing recovery efforts.

RapidReg allows for quick input of essential data about a child on a mobile phone, 
including a photograph, the child's age, family, health status and location information. 
Data is saved automatically and uploaded to a central database whenever network access 
becomes available. Registered aid workers will be able to create and modify entries for children 
in their care as well as search all existing records in order to help distressed parents 
find information about their missing children. Because RapidFTR is designed specifically to collect 
and distribute information about children, data security is extremely important.

#### Signing apk in release mode
Currently the keystore is located under the root directory, it's protected by password.
All you need to do is give correct values to *storePassword*, *keyAlias*, *keyPassword* 
in build.gradle.

Note: Never commit the real password into the codebase.
 
#### Setting up the CI environment
The project runs in docker virtual environment for every build. The *dockerfile* includes all
necessary libraries and Android SDK but source code. You need to mount source code into the docker
container.

Here is a Jenkins sample:
``` docker
docker rm -f $JOB_NAME || echo `no container to delete`
docker build -t primero/$JOB_NAME .
docker run --name $JOB_NAME \
-v "$WORKSPACE:/opt/project" \
-e "STORE_PASSWORD=$STORE_PASSWORD" \
-e "KEY_PASSWORD=$KEY_PASSWORD" \
-e "KEY_ALIAS=$KEY_ALIAS" \
```
