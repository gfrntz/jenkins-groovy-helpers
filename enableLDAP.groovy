/*
Original source: https://issues.jenkins-ci.org/browse/JENKINS-29733
*/

import jenkins.model.*
import hudson.security.*
import org.jenkinsci.plugins.*

String server = 'ldaps://'
String rootDN = 'dc=example,dc=com'
String userSearchBase = 'ou=sampleUsers'
String userSearch = 'uid={0}'
String groupSearchBase = ''
String managerDN = 'cn=manage,dc=example,dc=com'
String managerPassword = 'secret pass'
boolean inhibitInferRootDN = false

SecurityRealm ldap_realm = new LDAPSecurityRealm(server, rootDN, userSearchBase, userSearch, groupSearchBase, managerDN, managerPassword, inhibitInferRootDN)
Jenkins.instance.setSecurityRealm(ldap_realm)
Jenkins.instance.save()
