/*

*/
import jenkins.model.*
import hudson.model.*
import jenkins.security.*
import org.apache.commons.io.FileUtils

User u = User.get("jenkins")
ApiTokenProperty t = u.getProperty(ApiTokenProperty.class)
def token = t.getApiTokenInsecure()

FileUtils.writeStringToFile(new File("/tmp/jenkins_token.token"), "${token}");
