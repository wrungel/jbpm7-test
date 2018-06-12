http://127.0.0.1:8080/jbpm7-1.0-SNAPSHOT/rest/deploymentService/deployedUnits
curl -X PUT -i -H 'Content-Type: text/plain' http://127.0.0.1:8080/jbpm7-1.0-SNAPSHOT/rest/deploymentService/deploy/frol/jbpm7-test/1.0/SimpleTask.bpmn2 -T src/main/resources/assets/SimpleTask.bpmn2
curl -X GET -i http://127.0.0.1:8080/jbpm7-1.0-SNAPSHOT/rest/processService/frol:jbpm7-test:1.0/frol.jbpm7test.SimpleTask/start
curl -X GET -i http://127.0.0.1:8080/jbpm7-1.0-SNAPSHOT/rest/runtimeDataService/getProcessInstances