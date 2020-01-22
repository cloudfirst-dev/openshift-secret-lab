# Secrets Lab

This lab walks through different ways to create/manage and utilize secrets in OpenShift.

## Prerequeites

* Access to an OpenShift cluster
* Ability to create a new namespace (later referred to as project namespace)
* OpenShift CLI (oc)
* jq

## Initial setup
1. Setup environment variables
```
export PROJECT_NAMESPACE=<project namespace>
```
2. Create the project namespace
```
oc new-project $PROJECT_NAMESPACE
```
3. Deploy the sample app and build config that consumes the secrets we will be exposing by the simple API
```
oc process -p NAMESPACE=$PROJECT_NAMESPACE -f openshift/build.yml | oc apply -f -
oc process -p NAMESPACE=$PROJECT_NAMESPACE -f openshift/deploy.yml | oc apply -f -
```
4. Build the image that will consume the secrets during the lab
```
oc start-build basic-secret --from-dir="./app" --follow
```
5. The previous step will also initiate a deployment because the image stream changed as part of the s2i build
5. Validate the application is running by navigating to the output of the follow command which will get the route from openshift and you should see a "Congratulations" screen
```
oc get route basic-secret -o json | echo http://$(jq --raw-output .spec.host)
```
7. Make sure both urls as the outputs of the two commands return null as the secret values
```
oc get route basic-secret -o json | echo http://$(jq --raw-output .spec.host)/env-secret
oc get route basic-secret -o json | echo http://$(jq --raw-output .spec.host)/file-secret
```
8. Apply the first secret which is the evironment secret, and delete the current pod so it will pick up the new secret
```
oc create secret generic env-secret --from-literal=env-secret=value-is-defined-here
oc get pod | grep Running | awk '{print $1}' | xargs oc delete pod
```
9. Execute the following command to get the env secret endpoint and see that what was set above shows in the output "Secret From ENV BASIC_SECRET_ENV :  value-is-defined-here"
```
oc get route basic-secret -o json | echo http://$(jq --raw-output .spec.host)/env-secret
```
10. Apply the second secret which is the file secret, and delete the current pod so it will pick up the new secret
```
oc create secret generic file-secret --from-file=openshift/secrets
oc get pod | grep Running | awk '{print $1}' | xargs oc delete pod
```
11. Execute the following command to get the env secret endpoint and see that what was set above shows in the output "Secret From ENV BASIC_SECRET_ENV :  value-is-defined-here"
```
oc get route basic-secret -o json | echo http://$(jq --raw-output .spec.host)/file-secret
```
12. Now lets demonstrate the better approach to managing secrets, which is using a yml file, inspect the contents of openshift/secret.yml which will update our secret from step 8.  Under the data section notice the key "env-secret" and the value does not look like text, but is actually base64 encoded string.  To view its contents run this command
```
echo bmV3IHZhbHVlIGhlcmUK | base64 -D
```
13. Now lets apply this new file to our namespace and once again kill the pod to pick up the change
```
oc apply -f openshift/secret.yml
oc get pod | grep Running | awk '{print $1}' | xargs oc delete pod
```
14. Now lets run the following and open the url to view the new environment variable.  We should see "Secret From ENV BASIC_SECRET_ENV :  new value here"
```
oc get route basic-secret -o json | echo http://$(jq --raw-output .spec.host)/env-secret
```