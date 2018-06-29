set -e
taskId=$1
baseUri=http://127.0.0.1:8080/jbpm7-1.0-SNAPSHOT/rest
#echo "-- deploy"
#curl -X PUT -i -H 'Content-Type: text/plain' ${baseUri}/deploymentService/deploy/frol/frol/1.0/SimpleProcessWithPotentionalOwner.bpmn2 -T src/main/resources/assets/SimpleProcessWithPotentionalOwner.bpmn2
#echo "-- start process"
curl -X POST -i                              ${baseUri}/processService/frol:frol:1.0/frol.jbpm7test.SimpleProcessWithPotentionalOwner/start

#echo "-- get task ${taskId}"
#curl -X GET -i                             ${baseUri}/userTaskService/${taskId}?userId=frol
#echo "claim task $taskId"
#curl -X POST -i ${baseUri}/userTaskService/1/claim?userId=frol
echo "start task ${taskId}"
curl -X POST -i                             ${baseUri}/userTaskService/${taskId}/start?userId=frol
echo "complete task ${taskId}"
curl -X POST -i                             ${baseUri}/userTaskService/${taskId}/complete?userId=frol
