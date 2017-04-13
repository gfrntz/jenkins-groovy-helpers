import jenkins.model.*
import hudson.model.*
import hudson.scm.*
import hudson.security.*
import jenkins.security.*
import com.cloudbees.plugins.credentials.*

String[] usersArray = ['put users list here']

def instance = Jenkins.getInstance()

println "=== Enable ProjectMatrixAuthorizationStrategy ===".center(100,'=')

def strategy = new hudson.security.ProjectMatrixAuthorizationStrategy()
instance.setAuthorizationStrategy(strategy)

println "=== Add admins to jenkins ===".center(100,'=')

usersArray.each{
  instance.authorizationStrategy.add(Hudson.ADMINISTER, "${it}")
  instance.authorizationStrategy.add(Hudson.READ, "${it}")
  instance.authorizationStrategy.add(Hudson.RUN_SCRIPTS, "${it}")

  instance.authorizationStrategy.add(Item.WORKSPACE, "${it}")
  instance.authorizationStrategy.add(Item.BUILD, "${it}")
  instance.authorizationStrategy.add(Item.DELETE, "${it}")
  instance.authorizationStrategy.add(Item.CONFIGURE, "${it}")
  instance.authorizationStrategy.add(Item.CANCEL, "${it}")
  instance.authorizationStrategy.add(Item.DISCOVER, "${it}")
  instance.authorizationStrategy.add(Item.READ, "${it}")
  instance.authorizationStrategy.add(Item.CREATE, "${it}")

  instance.authorizationStrategy.add(Run.DELETE, "${it}")
  instance.authorizationStrategy.add(Run.UPDATE, "${it}")

  instance.authorizationStrategy.add(SCM.TAG, "${it}")

  instance.authorizationStrategy.add(Computer.BUILD, "${it}")
  instance.authorizationStrategy.add(Computer.CONFIGURE, "${it}")
  instance.authorizationStrategy.add(Computer.CONNECT, "${it}")
  instance.authorizationStrategy.add(Computer.CREATE, "${it}")
  instance.authorizationStrategy.add(Computer.DELETE, "${it}")
  instance.authorizationStrategy.add(Computer.DISCONNECT, "${it}")

  instance.authorizationStrategy.add(CredentialsProvider.CREATE, "${it}")
  instance.authorizationStrategy.add(CredentialsProvider.DELETE, "${it}")
  instance.authorizationStrategy.add(CredentialsProvider.UPDATE, "${it}")
  instance.authorizationStrategy.add(CredentialsProvider.VIEW, "${it}")

  instance.authorizationStrategy.add(View.CONFIGURE, "${it}")
  instance.authorizationStrategy.add(View.CREATE, "${it}")
  instance.authorizationStrategy.add(View.DELETE, "${it}")
  instance.authorizationStrategy.add(View.READ, "${it}")
}

println "=== Add anonymous Overall Read Permission ===".center(100,'=')

instance.authorizationStrategy.add(hudson.model.Hudson.READ,'anonymous')

instance.save()
