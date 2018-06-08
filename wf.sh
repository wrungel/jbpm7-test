set -e
WILDFLY_HOME=/cygdrive/d/wildfly/wildfly-12.0.0.Final.original
if [ ! -d ${WILDFLY_HOME} ]; then
    echo "${WILDFLY_HOME} not found"
    exit
fi
echo "Server home: ${WILDFLY_HOME}"
WMIC PROCESS WHERE "COMMANDLINE LIKE '%wildfly-12.0.0.Final.original%'" CALL TERMINATE
(cd $WILDFLY_HOME && rm -rf standalone/tmp/* standalone/log/* standalone/deployments/*)
(cd $WILDFLY_HOME/bin && cygstart c:/windows/system32/cmd /c 'standalone.bat -Djboss.socket.binding.port-offset=100 --debug --server-config=standalone-full.xml')
    
