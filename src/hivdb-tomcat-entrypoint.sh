#! /bin/bash
set -e
case $1 in
    inspect)
        /bin/bash
        ;;
    dev)
        cp /root/web.dev.xml /usr/share/tomcat/conf/web.xml
        /usr/share/tomcat/bin/catalina.sh run
        ;;
    prod)
        cp /root/web.prod.xml /usr/share/tomcat/conf/web.xml
        /usr/share/tomcat/bin/catalina.sh run
        ;;
    *)
        echo "Usage: docker run hivdb/tomcat-with-nucamino:latest {inspect|dev|prod}"
        exit 1
        ;;
esac
