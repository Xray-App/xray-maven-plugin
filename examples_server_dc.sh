#!/bin/bash

# abort on error
set -e
# print commands
set -x

mvn clean compile install

# import xray
#mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=xray -Dxray.reportFile=examples/server_datacenter/specific_reports/xray.json

# import xray, multipart
mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=xray -Dxray.reportFile=examples/server_datacenter/specific_reports/xray.json -Dxray.testExecInfoJson=examples/server_datacenter/specific_reports/testExecInfo.json

# import junit
mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=junit -Dxray.reportFile=examples/reports/junit.xml

# import junit, multipart
mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=junit -Dxray.reportFile=examples/reports/junit.xml -Dxray.testExecInfoJson=examples/server_datacenter/specific_reports/testExecInfo.json

# import xunit
mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=xunit -Dxray.reportFile=examples/reports/xunit.xml

# import xunit, multipart
mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=xunit -Dxray.reportFile=examples/reports/xunit.xml -Dxray.testExecInfoJson=examples/server_datacenter/specific_reports/testExecInfo.json

# import nunit
mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=nunit -Dxray.reportFile=examples/reports/nunit.xml

# import nunit, multipart
mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=nunit -Dxray.reportFile=examples/reports/nunit.xml -Dxray.testExecInfoJson=examples/server_datacenter/specific_reports/testExecInfo.json

# import testng
mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=testng -Dxray.reportFile=examples/reports/testng.xml

# import testng, multipart
mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=testng -Dxray.reportFile=examples/reports/testng.xml -Dxray.testExecInfoJson=examples/server_datacenter/specific_reports/testExecInfo.json


# import robot
mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=robot -Dxray.reportFile=examples/reports/robot.xml

# import robot, multipart
mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=robot -Dxray.reportFile=examples/reports/robot.xml -Dxray.testExecInfoJson=examples/server_datacenter/specific_reports/testExecInfo.json

# import cucumber features
mvn com.xblend:xray-maven-plugin:import-features -Dxray.cloud=false -Dxray.inputFeatures=features/ 

# export cucumber features
mvn com.xblend:xray-maven-plugin:export-features -Dxray.cloud=false -Dxray.issueKeys=CALC-1 -Dxray.outputDir=features/

# import cucumber results
mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=cucumber -Dxray.reportFile=examples/reports/cucumber.json

# import cucumber results, multipart
mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=cucumber -Dxray.reportFile=examples/reports/cucumber.json -Dxray.testExecInfoJson=examples/server_datacenter/specific_reports/testExecInfo.json

# import behave results
mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=behave -Dxray.reportFile=examples/reports/behave.json

# import behave results, multipart
mvn com.xblend:xray-maven-plugin:import-results -Dxray.cloud=false -Dxray.reportFormat=behave -Dxray.reportFile=examples/reports/behave.json -Dxray.testExecInfoJson=examples/server_datacenter/specific_reports/testExecInfo.json