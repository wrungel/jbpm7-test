#!/usr/bin/bash
set -e
mvn clean package
WILDFLY_HOME="/c/DEV/tmp/jbpm7/wildfly-12.0.0.Final"
if [ ! -d ${WILDFLY_HOME} ]; then
    echo "${WILDFLY_HOME} not found"
    exit
fi
    echo "Server home: ${WILDFLY_HOME}"
WMIC PROCESS WHERE "COMMANDLINE LIKE '%wildfly-12.0.0.Final%'" CALL TERMINATE
(cd $WILDFLY_HOME && rm -rf standalone/tmp/* standalone/log/* standalone/deployments/*)
cp target/jbpm7-1.0-SNAPSHOT.war ${WILDFLY_HOME}/standalone/deployments/
(cd $WILDFLY_HOME/bin && JAVA_HOME='C:\DEV\java\jdk-1.8.0-66_H03-win_x64' ./standalone.bat -Djboss.socket.binding.port-offset=0 -Dorg.kie.executor.pool.size=2 --debug --server-config=standalone-full.xml)
    
